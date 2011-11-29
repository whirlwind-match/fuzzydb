/**
 * 
 */
package com.wwm.db.internal;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.internal.comms.messages.OkRsp;
import com.wwm.db.internal.comms.messages.WWSearchCmd;
import com.wwm.db.internal.comms.messages.WWSearchFetchCmd;
import com.wwm.db.internal.comms.messages.WWSearchNomineeOkayRsp;
import com.wwm.db.internal.comms.messages.WWSearchOkayRsp;
import com.wwm.db.query.Result;
import com.wwm.db.query.ResultIterator;
import com.wwm.db.whirlwind.SearchSpec;
import com.wwm.io.core.messages.Command;
import com.wwm.io.core.messages.Response;

public class WWResultSetIterator<T extends Object> implements ResultIterator<Result<T>> {

		private final Class<T> clazz; // Used on interface to ensure we specify the type we expect to get returned.

		private final WWResultSet<T> parentResultSet;
		private final StoreImpl store;
		private int qid;
		private final SearchSpec search;
		private boolean active = false;
		private final int tid;
		private final ArrayList<Result<T>> results = new ArrayList<Result<T>>();
		private boolean moreResults = true;
		private final TransactionImpl transaction;	// Not needed for access, but it shuld be held alive to stop it being finalized while a result set is in use
		private final int fetchSize;
		private boolean moreResultsKnown = false;
		private final boolean wantNominee;
		
		WWResultSetIterator(WWResultSet<T> parentResultSet, Class<T> clazz, StoreImpl store, TransactionImpl transaction, int tid, SearchSpec search, int fetchSize, boolean wantNominee) {
			this.parentResultSet = parentResultSet;
			this.tid = tid;
			this.store = store;
			this.transaction = transaction;
			this.search = search;
			this.clazz = clazz;
			this.fetchSize = fetchSize;
			this.wantNominee = wantNominee;
		}
		
		private void activate() {
			if (!active) {
				int cid = store.getNextId();
				this.qid = store.getNextId();
				Command command = new WWSearchCmd(store.getStoreId(), cid, tid, transaction.getNamespace(), qid, wantNominee, fetchSize, search);
				Response response = transaction.execute(command);
				if (response instanceof OkRsp) {
					active = true;
					handleResults((OkRsp)response);
					return;
				}
				throw new ArchException("Unexpected: " + response);
			}
		}
		
		public boolean hasNext() {
			parentResultSet.throwIfDisposed();
			if (!moreResultsKnown) fetch();
			return ( (results.size() > 0) || moreResults);
		}

		public Result<T> next() {
			parentResultSet.throwIfDisposed();
			fetch();
			if (results.size() > 0) {
				Result<T> result = results.remove(0);
//FIXME				transaction.setResolutionProvider(result.getItem());
				return result;
			}
			throw new NoSuchElementException();
		}

		private void fetch() {
			Response response = null;
			activate();	// Start the query if need be
			if (results.size() > 0) return;	// don't fetch if we already have data
			if (!moreResults) return;	// don't fetch if we were already told no more
			WWSearchFetchCmd command = new WWSearchFetchCmd( store.getStoreId(), store.getNextId(), tid, qid, fetchSize);
			response = transaction.execute(command);
			if (response instanceof OkRsp) handleResults((OkRsp)response);
		}
		
		private void handleResults(OkRsp response) {
			if (response instanceof WWSearchOkayRsp) {
				WWSearchOkayRsp fetch = (WWSearchOkayRsp) response;
				fetch.getResults(results, store);
				moreResults = fetch.isMoreResults();
				moreResultsKnown = true;
			} else if (response instanceof WWSearchNomineeOkayRsp) {
				WWSearchNomineeOkayRsp fetch = (WWSearchNomineeOkayRsp) response;
				fetch.getResults(clazz, results);
				moreResults = fetch.isMoreResults();
				moreResultsKnown = true;
			} else {
				throw new RuntimeException("Unfinished code");	//FIXME: handle the rest
			}
		}
		
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		public void dispose() {
			parentResultSet.dispose();
		}

        public long count() {
            throw new UnsupportedOperationException("count() not valid for Whirlwind results");
        }
	}