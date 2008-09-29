/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  The ASF licenses this file to You
 * under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.  For additional information regarding
 * copyright in this work, please see the NOTICE file in the top level
 * directory of this distribution.
 * 
 * THIS FILE IS THE SUPPLIED: org.apache.abdera.examples.appserver.SimpleProvider.java
 */
package com.wwm.atom.server;

import java.io.IOException;
import java.util.Date;

import javax.activation.MimeType;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Collection;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
import org.apache.abdera.model.Service;
import org.apache.abdera.model.Workspace;
import org.apache.abdera.parser.ParseException;
import org.apache.abdera.parser.Parser;
import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.ProviderHelper;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.AbstractResponseContext;
import org.apache.abdera.protocol.server.context.BaseResponseContext;
import org.apache.abdera.protocol.server.context.EmptyResponseContext;
import org.apache.abdera.protocol.server.context.StreamWriterResponseContext;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceProvider;
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.StreamWriter;

public class ReferenceProviderImpl extends AbstractWorkspaceProvider implements CollectionAdapter {

    private EntityTag service_etag = new EntityTag("simple");
    private Document<Service> service_doc;
    private Document<Feed> feed_doc;

    public ReferenceProviderImpl() {
        super();
    }

    private Document<Service> init_service_doc(Abdera abdera) {
        Factory factory = abdera.getFactory();
        Service service = factory.newService();
        Workspace workspace = service.addWorkspace("Simple");
        try {
            Collection collection = workspace.addCollection("Simple", "atom/feed");
            collection.setAccept("entry");
            collection.addCategories().setFixed(false);
        } catch (Exception e) { e.printStackTrace(); } // FIXME: Document this exception
        return service.getDocument();
    }

    private Document<Feed> init_feed_doc(Abdera abdera) {
        Factory factory = abdera.getFactory();
        Feed feed = factory.newFeed();
        try {
            feed.setId("tag:example.org,2006:feed");
            feed.setTitle("Simple");
            feed.setUpdated(new Date());
            feed.addLink("");
            feed.addLink("", "self");
            feed.addAuthor("Simple");
        } catch (Exception e) { e.printStackTrace(); } // FIXME: Document this exception
        return feed.getDocument();
    }

    private synchronized Document<Service> get_service_doc(Abdera abdera) {
        if (service_doc == null) {
            service_doc = init_service_doc(abdera);
        }
        return service_doc;
    }

    private synchronized Document<Feed> get_feed_doc(Abdera abdera) {
        if (feed_doc == null) {
            feed_doc = init_feed_doc(abdera);
        }
        return feed_doc;
    }

    public ResponseContext getService(RequestContext request) {
        Abdera abdera = request.getAbdera();
        Document<Service> service = get_service_doc(abdera);
        AbstractResponseContext rc;
        rc = new BaseResponseContext<Document<Service>>(service);
        rc.setEntityTag(service_etag);
        return rc;
    }

    public ResponseContext getFeed(RequestContext request) {
        Abdera abdera = request.getAbdera();
        Document<Feed> feed = get_feed_doc(abdera);
        AbstractResponseContext rc;
        rc = new BaseResponseContext<Document<Feed>>(feed);
        rc.setEntityTag(calculateEntityTag(feed.getRoot()));
        return rc;
    }

    @SuppressWarnings("unchecked")
    public ResponseContext postEntry(RequestContext request) {
        Abdera abdera = request.getAbdera();
        Parser parser = abdera.getParser();
        try {
            MimeType contentType = request.getContentType();
            String ctype = (contentType != null) ? contentType.toString()
                    : null;
            if (ctype != null && !MimeTypeHelper.isAtom(ctype)
                    && !MimeTypeHelper.isXml(ctype)) {
                return new EmptyResponseContext(415);
            }

            Document<Entry> entry_doc = (Document<Entry>) request.getDocument(
                    parser).clone();
            if (entry_doc != null) {
                Entry entry = entry_doc.getRoot();
                if (!ProviderHelper.isValidEntry(entry)) {
                    return new EmptyResponseContext(400);
                }
                entry.setUpdated(new Date());
                // NOTE: WE KEEP THE UUID
                //				entry.getIdElement().setValue(factory.newUuidUri());
                entry.addLink("feed/" + entry.getId().toString(), "edit");
                Feed feed = get_feed_doc(abdera).getRoot();
                feed.insertEntry(entry);
                feed.setUpdated(new Date());
                BaseResponseContext rc = new BaseResponseContext(entry);
                IRI baseUri = ProviderHelper.resolveBase(request);
                rc.setLocation(baseUri.resolve(entry.getEditLinkResolvedHref())
                        .toString());
                rc.setContentLocation(rc.getLocation().toString());
                rc.setEntityTag(calculateEntityTag(entry));
                rc.setStatus(201);
                return rc;
            } else {
                return new EmptyResponseContext(400);
            }
        } catch (ParseException pe) {
            return new EmptyResponseContext(415);
        } catch (ClassCastException cce) {
            return new EmptyResponseContext(415);
        } catch (Exception e) {
            return new EmptyResponseContext(400);
        }
    }

    public ResponseContext deleteEntry(RequestContext request) {
        Entry entry = getAbderaEntry(request);
        if (entry != null) {
            entry.discard();
        }
        return new EmptyResponseContext(204);
    }

    public ResponseContext getEntry(RequestContext request) {
        Entry entry = getAbderaEntry(request);
        if (entry != null) {
            Feed feed = entry.getParentElement();
            entry = (Entry) entry.clone();
            entry.setSource(feed.getAsSource());
            Document<Entry> entry_doc = entry.getDocument();
            AbstractResponseContext rc = new BaseResponseContext<Document<Entry>>(
                    entry_doc);
            rc.setEntityTag(calculateEntityTag(entry));
            return rc;
        } else {
            return new EmptyResponseContext(404);
        }
    }

    @SuppressWarnings("unchecked")
    public ResponseContext putEntry(RequestContext request) {
        Abdera abdera = request.getAbdera();
        Parser parser = abdera.getParser();
        Factory factory = abdera.getFactory();
        Entry orig_entry = getAbderaEntry(request);
        if (orig_entry != null) {
            try {
                MimeType contentType = request.getContentType();
                if (contentType != null
                        && !MimeTypeHelper.isAtom(contentType.toString())) {
                    return new EmptyResponseContext(415);
                }

                Document<Entry> entry_doc = (Document<Entry>) request
                .getDocument(parser).clone();
                if (entry_doc != null) {
                    Entry entry = entry_doc.getRoot();
                    if (!entry.getId().equals(orig_entry.getId())) {
                        return new EmptyResponseContext(409);
                    }
                    if (!ProviderHelper.isValidEntry(entry)) {
                        return new EmptyResponseContext(400);
                    }
                    entry.setUpdated(new Date());
                    entry.getIdElement().setValue(factory.newUuidUri());
                    entry.addLink("atom/feed/" + entry.getId().toString(),
                    "edit");
                    orig_entry.discard();
                    Feed feed = get_feed_doc(abdera).getRoot();
                    feed.insertEntry(entry);
                    feed.setUpdated(new Date());
                    return new EmptyResponseContext(204);
                } else {
                    return new EmptyResponseContext(400);
                }
            } catch (ParseException pe) {
                return new EmptyResponseContext(415);
            } catch (ClassCastException cce) {
                return new EmptyResponseContext(415);
            } catch (Exception e) {
                return new EmptyResponseContext(400);
            }
        } else {
            return new EmptyResponseContext(404);
        }
    }

    private EntityTag calculateEntityTag(Base base) {
        String id = null;
        String modified = null;
        if (base instanceof Entry) {
            id = ((Entry) base).getId().toString();
            modified = ((Entry) base).getUpdatedElement().getText();
        } else if (base instanceof Feed) {
            id = ((Feed) base).getId().toString();
            modified = ((Feed) base).getUpdatedElement().getText();
        }
        return EntityTag.generate(id, modified);
    }

    private Entry getAbderaEntry(RequestContext request) {
        Abdera abdera = request.getAbdera();
        String entry_id = getEntryID(request);
        Document<Feed> feed = get_feed_doc(abdera);
        try {
            return feed.getRoot().getEntry(entry_id);
        } catch (Exception e) {
            // ignore
        }
        return null;
    }

    private String getEntryID(RequestContext request) {
        if (request.getTarget().getType() != TargetType.TYPE_ENTRY) {
            return null;
        }
        String path = request.getUri().toString();
        String[] segments = path.split("/");
        return segments[segments.length - 1];
    }

	@Override
	public CollectionAdapter getCollectionAdapter(RequestContext request) {
		return this;
	}

	public ResponseContext headEntry(RequestContext request) {
		return ProviderHelper.notsupported(request);
	}

	public ResponseContext optionsEntry(RequestContext request) {
		return ProviderHelper.notsupported(request);
	}
	
    public ResponseContext getCategories(RequestContext request) {
    	return new StreamWriterResponseContext(request.getAbdera()) {
    		@Override
    		protected void writeTo(StreamWriter sw) throws IOException {
    			sw.startDocument()
    			.startCategories(false)
    			.writeCategory("a category")
    			.endCategories()
    			.endDocument();
    		}
    	}
    	.setStatus(HttpServletResponse.SC_OK)
    	.setContentType(Constants.CAT_MEDIA_TYPE);    
    }

	@Override
	public ResponseContext extensionRequest(RequestContext request) {
		// TODO Auto-generated method stub
		return null;
	}
}
