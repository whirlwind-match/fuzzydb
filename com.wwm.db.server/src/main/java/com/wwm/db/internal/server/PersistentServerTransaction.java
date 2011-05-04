/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.db.internal.server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.exceptions.UnknownObjectException;
import com.wwm.db.exceptions.UnknownQueryException;
import com.wwm.db.exceptions.UnknownStoreException;
import com.wwm.db.internal.MetaObject;
import com.wwm.db.internal.RefImpl;
import com.wwm.db.internal.RetrieveSpecResultImpl;
import com.wwm.db.internal.comms.messages.CommitCmd;
import com.wwm.db.internal.comms.messages.CountClassCmd;
import com.wwm.db.internal.comms.messages.CountClassRsp;
import com.wwm.db.internal.comms.messages.ListNamespacesCmd;
import com.wwm.db.internal.comms.messages.ListNamespacesRsp;
import com.wwm.db.internal.comms.messages.QueryCmd;
import com.wwm.db.internal.comms.messages.QueryFetchCmd;
import com.wwm.db.internal.comms.messages.QueryRsp;
import com.wwm.db.internal.comms.messages.RetrieveByKeyCmd;
import com.wwm.db.internal.comms.messages.RetrieveByRefCmd;
import com.wwm.db.internal.comms.messages.RetrieveByRefsCmd;
import com.wwm.db.internal.comms.messages.RetrieveBySpecCmd;
import com.wwm.db.internal.comms.messages.RetrieveBySpecRsp;
import com.wwm.db.internal.comms.messages.RetrieveFirstOfCmd;
import com.wwm.db.internal.comms.messages.RetrieveMultiRsp;
import com.wwm.db.internal.comms.messages.RetrieveSingleRsp;
import com.wwm.db.internal.comms.messages.WWSearchCmd;
import com.wwm.db.internal.comms.messages.WWSearchFetchCmd;
import com.wwm.db.internal.comms.messages.WWSearchNomineeOkayRsp;
import com.wwm.db.internal.comms.messages.WWSearchOkayRsp;
import com.wwm.db.internal.index.Query;
import com.wwm.db.internal.search.NextItem;
import com.wwm.db.internal.search.Search;
import com.wwm.db.internal.table.UserTable;
import com.wwm.db.marker.IWhirlwindItem;
import com.wwm.db.query.RetrieveSpec;
import com.wwm.db.query.RetrieveSpecItem;
import com.wwm.io.core.MessageSink;
import com.wwm.io.core.messages.ErrorRsp;
import com.wwm.io.core.messages.Response;
import com.wwm.model.attributes.Score;
import com.wwm.util.TupleKey;


public class PersistentServerTransaction extends ServerTransaction {

    public static class Key {

        private final MessageSink source;
        private final int tid;

        public Key(final MessageSink source, final int tid) {
            this.source = source;
            this.tid = tid;
        }

        @Override
        public int hashCode() {
            return source.hashCode() + tid;
        }

        @Override
        public boolean equals(Object o) {
        	if (!(o instanceof Key)){ // handles null too
        		return false;
        	}
            Key rhs = (Key)o;
            return this.source == rhs.source && this.tid == rhs.tid;
        }

        public MessageSink getSource() {
            return source;
        }

        public int getTid() {
            return tid;
        }
    }

    private final Date startedTime = new Date();
    private Date lastUsedTime = new Date();
    private final Key key;
    private final ServerStore store;
    private int busy = 0;

    /** Searches in progress FIXME (NU->AC): We currently don't time these out :) */
    private final Map<TupleKey<MessageSink, Integer>, Search> searches
    = new TreeMap<TupleKey<MessageSink, Integer>, Search>();

    private final Map<TupleKey<MessageSink, Integer>, Query> queries
    = new TreeMap<TupleKey<MessageSink, Integer>, Query>();

    public PersistentServerTransaction(ServerTransactionCoordinator stc, MessageSink source, int tid, int storeId) throws UnknownStoreException {
        super(stc, source);
        this.key = new Key(source, tid);
        store = stc.getRepository().getStore(storeId);
    }

    public Key getKey() {
        return key;
    }

    public Date getLastUsedTime() {
        return lastUsedTime;
    }

    public void touchLastUsedTime() {
        lastUsedTime = new Date();
    }

    public Date getStartedTime() {
        return startedTime;
    }

    public void cmdCommitCmd(int storeId, int cid, MessageSink source, CommitCmd command, ByteBuffer packet) {
        setWriteCommand(command, packet);
        commit();
    }

    public void cmdRetrieveByRefCmd(int storeId, int cid, MessageSink source, RetrieveByRefCmd command, ByteBuffer packet) throws UnknownObjectException {
        RefImpl<?> ref = (RefImpl<?>)command.getRef();
        RetrieveSingleRsp rsp = new RetrieveSingleRsp(storeId, cid, getByRef(ref));
        sendResponse(rsp);
    }

    public void cmdRetrieveByRefsCmd(int storeId, int cid, MessageSink source, RetrieveByRefsCmd command, ByteBuffer packet) throws UnknownObjectException {
        ArrayList<Object> objects = new ArrayList<Object>();
        for (RefImpl<?> ref : command.getRefs()) {
            objects.add(getByRef(ref) );
        }

        RetrieveMultiRsp rsp = new RetrieveMultiRsp(storeId, cid, objects.toArray());
        sendResponse(rsp);
    }

    @SuppressWarnings("unchecked")
    public void cmdRetrieveBySpecCmd(int storeId, int cid, MessageSink source, RetrieveBySpecCmd command, ByteBuffer packet) {
        RetrieveSpecResultImpl result = new RetrieveSpecResultImpl();
        RetrieveSpec spec = command.getSpec();
        Namespace namespace = store.getNamespace(command.getNamespace());
        if (namespace != null) {
            Indexes indexes = namespace.getIndexes();

            Set<Class<? extends Object>> classes = spec.getClasses();

            for (Class clazz : classes) {
                Map<String, RetrieveSpecItem> items = spec.getSpecs(clazz);
                for (RetrieveSpecItem item : items.values()) {
                    String fieldName = item.getFieldName();
                    Set<Object> keys = item.getKeys();

                    for (Object key : keys) {
                        MetaObject mo = indexes.lookup(clazz, fieldName, (Comparable<?>)key);
                        if (mo != null) {
                            result.put(clazz, key, mo);
                        }
                    }
                }
            }


        }
        RetrieveBySpecRsp rsp = new RetrieveBySpecRsp(storeId, cid, result);
        sendResponse(rsp);
    }

    private Object getByRef(RefImpl<?> ref) throws UnknownObjectException {
        Namespace namespace = store.getNamespace(ref);
        Object object = namespace.getObject(ref);
        return object;
    }



    private void sendResponse(Response response) {
        try {
            source.send(response);
        } catch (IOException e1) {
            source.close();	// Problem writing a response to the comms, close the connection
        }

    }

    @Override
    protected void doCommitChecks() {
        CommitCmd cmd = (CommitCmd)command;

        //=================================================================
        // For each created object, check that an object of it's object id
        // (oid) doesn't already exist the relevant table.
        // If it does, throw ObjectExistsException
        //=================================================================
        Map<String, ArrayList<MetaObject<?>>> created = cmd.getCreated();
        if (created != null) {
            for (Entry<String, ArrayList<MetaObject<?>>> entry : created.entrySet()) {
            	String namespace = entry.getKey();
                ArrayList<MetaObject<?>> objects = entry.getValue(); // FIXME: Should be Array (for each table being modified).
                Namespace ns = store.getNamespace(namespace);
                for (MetaObject<?> mo : objects) {
                    // FIXME: Add KeyCollision support.
                    // Need to get array of collision refs, and allow to add if ALL of colliding objects are being deleted.
                    ns.testCanCreate(mo); // FIXME: (perf: this repeatedly calls getTable() .. so hashmap lookup... we prob want to batch these by table) and lookup only once
                }
            }
        }

        //=================================================================
        // For each object being updated, check that the object being
        // modified is the latest version.  If it isn't, someone modified
        // it before us, so throw a WriteCollisionException
        //=================================================================
        ArrayList<MetaObject<?>> updated = cmd.getUpdated();
        if (updated != null) {
            for (MetaObject<?> mo : updated) {
                Namespace ns = store.getNamespace(mo.getRef());
                ns.testCanUpdate(mo);
            }
        }

        //=================================================================
        // For each object being deleted, check that it still exists.
        // If not, someone else deleted it, so throw UnknownObjectException
        //=================================================================
        ArrayList<RefImpl<?>> deleted = cmd.getDeleted();
        if (deleted != null) {
            for (RefImpl<?> ref : deleted) {
                Namespace ns = store.getNamespace(ref);
                ns.testCanDelete(ref);
            }
        }
    }

    @Override
    protected void doCommit() {
        CommitCmd cmd = (CommitCmd)command;

        //=================================================================
        // Create new objects
        //=================================================================
        Map<String, ArrayList<MetaObject<?>>> created = cmd.getCreated();
        if (created != null) {
            for (Entry<String, ArrayList<MetaObject<?>>> entry : created.entrySet()) {
            	String namespace = entry.getKey();
                ArrayList<MetaObject<?>> objects = entry.getValue(); 
                Namespace ns = store.getNamespace(namespace);
                for (MetaObject<?> mo : objects) {
                    ns.create(mo);
                }
            }
        }

        //=================================================================
        // Update objects
        //=================================================================
        ArrayList<MetaObject<?>> updated = cmd.getUpdated();
        if (updated != null) {
            for (MetaObject<?> mo : updated) {
                Namespace ns = store.getNamespace(mo.getRef());
                ns.update(mo);
            }
        }

        //=================================================================
        // Delete objects
        //=================================================================
        ArrayList<RefImpl<?>> deleted = cmd.getDeleted();
        if (deleted != null) {
            for (RefImpl<?> ref : deleted) {
                Namespace ns = store.getNamespace(ref);
                ns.delete(ref);
            }
        }
    }


    public synchronized void markIdle() {
        busy--;
        assert(busy >= 0);
    }

    public synchronized void markBusy() {
        busy++;
    }

    public synchronized boolean isBusy() {
        return busy > 0;
    }


    void cmdWWSearchCmd(int storeId, int cid, MessageSink source, WWSearchCmd command, ByteBuffer packet) throws UnknownStoreException, IOException {

        // Create a key we can lookup this result with cmdWWSearchFetchCmd
        TupleKey<MessageSink, Integer> key = new TupleKey<MessageSink, Integer>(source, command.getQueryId());

        Namespace ns = repository.getStore(storeId).getNamespace(command.getNamespace());
        Search search = ns.search(command.getSearchSpec(), command.getWantNominee());
        searches.put(key, search);
        //		try {
        doSearchFetch(storeId, cid, source, search, command.getFetchSize());
        //		} catch (Throwable e) { // User defined scorers are called here, which could do all sorts of nasties, so we catch and report
        //			throw e; // TODO: Think we're okay here, as parent deals with it.
        //		}
    }


    void cmdWWSearchFetchCmd(int storeId, int cid, MessageSink source, WWSearchFetchCmd command, ByteBuffer packet) throws IOException {

        TupleKey<MessageSink, Integer> key = new TupleKey<MessageSink, Integer>(source, command.getQueryId());

        Search search = searches.get(key);
        if (search == null){
            source.send( new ErrorRsp(storeId, cid, new ArchException( "Unknown Query - timed out?" ) ) );
        }
        else {
            doSearchFetch(storeId, cid, source, search, command.getFetchSize());
        }
    }

    void cmdRetrieveByKeyCmd(int storeId, int cid, MessageSink source, RetrieveByKeyCmd command, ByteBuffer packet) {
        MetaObject<?> mo = null;
        Namespace namespace = store.getNamespace(command.getNamespace());
        if (namespace != null) {
            Indexes indexes = namespace.getIndexes();
            mo = indexes.lookup(command.getForClass(), command.getFieldName(), command.getKey());
        }
        RetrieveSingleRsp rsp = new RetrieveSingleRsp(storeId, cid, mo);
        sendResponse(rsp);
    }

    void cmdListNamespacesCmd(int storeId, int cid, MessageSink source, ListNamespacesCmd command, ByteBuffer packet) {
        ListNamespacesRsp rsp = new ListNamespacesRsp(storeId, cid, store.getNamespaces());
        sendResponse(rsp);
    }

    @SuppressWarnings("unchecked")
	void cmdRetrieveFirstOfCmd(int storeId, int cid, MessageSink source, RetrieveFirstOfCmd command, ByteBuffer packet) {
        MetaObject<?> mo = null;
        Namespace namespace = store.getNamespace(command.getNamespace());
        if (namespace != null) {
            UserTable<?> ut = namespace.getTable(command.getForClass());
            if (ut != null) {
                Iterator<MetaObject<?>> i = (Iterator)ut.iterator();
                if (i.hasNext()) {
                    mo = i.next();
                }
            }
        }
        RetrieveSingleRsp rsp = new RetrieveSingleRsp(storeId, cid, mo);
        sendResponse(rsp);
    }

    void cmdQueryCmd(int storeId, int cid, MessageSink source, QueryCmd command, ByteBuffer packet) {
        // Begin a new query
        Namespace namespace = store.getNamespace(command.getNamespace());

        int qid = command.getQid();

        Query query = new Query(this, namespace, command.getForClass(), command.getIndex(), command.getExpr(), command.getFetchSize());

        queries.put(new TupleKey<MessageSink, Integer>(source, qid), query);

        ArrayList<Object> results = query.fetch();
        boolean moreResults = query.isMoreResults();

        QueryRsp rsp = new QueryRsp(storeId, cid, results, moreResults);
        sendResponse(rsp);
    }

    void cmdQueryFetchCmd(int storeId, int cid, MessageSink source, QueryFetchCmd command, ByteBuffer packet) {
        // Progress an existing query
        int qid = command.getQid();
        TupleKey<MessageSink, Integer> key = new TupleKey<MessageSink, Integer>(source, qid);
        Query query = queries.get(key);

        if (query == null) {
            throw new UnknownQueryException(qid);
        }

        ArrayList<Object> results = query.fetch();
        boolean moreResults = query.isMoreResults();

        QueryRsp rsp = new QueryRsp(storeId, cid, results, moreResults);
        sendResponse(rsp);
    }


    void cmdCountClassCmd(int storeId, int cid, MessageSink source, CountClassCmd command, ByteBuffer packet) {
        long count = 0;
        Namespace namespace = store.getNamespace(command.getNamespace());

        if (namespace != null)
        {
            UserTable<?> ut = namespace.getTable(command.getClazz());

            if (ut != null)
            {
                count = ut.getElementCount();
            }
        }
        CountClassRsp rsp = new CountClassRsp(storeId, cid, count);
        sendResponse(rsp);
    }


    /**
     * Implements doing search and returning results for both intial search, WWSearchCmd and WWSearchFetchCmd
     * @throws IOException
     */
    private void doSearchFetch(int storeId, int cid, MessageSink source, Search search, int fetchSize) throws IOException {
        assert (search != null);
        if (search.isNominee()) {
            // Nominee version
            ArrayList<Object> results = new ArrayList<Object>();
            ArrayList<Score> scores = new ArrayList<Score>();
            ArrayList<NextItem> items = search.getNextResults(fetchSize);
            for (NextItem ni : items) {
                Object nominee = ni.getItem().getNominee();
                results.add( nominee );
                scores.add( ni.getScore() );
            }
            source.send( new WWSearchNomineeOkayRsp(storeId, cid, results, scores, search.isMoreResults() ) );
        } else {
            // Item version
            ArrayList<IWhirlwindItem> results = new ArrayList<IWhirlwindItem>();
            ArrayList<Score> scores = new ArrayList<Score>();
            ArrayList<NextItem> items = search.getNextResults(fetchSize);
            for (NextItem ni : items) {
                IWhirlwindItem item = ni.getItem();
                results.add(item);
                scores.add(ni.getScore());
            }
            source.send( new WWSearchOkayRsp(storeId, cid, results, scores, search.isMoreResults()) );
        }
    }

}
