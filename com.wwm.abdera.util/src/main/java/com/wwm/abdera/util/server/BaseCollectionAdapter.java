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
package com.wwm.abdera.util.server;

import java.io.IOException;
import java.util.Collection;

import javax.activation.MimeType;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.Abdera;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.i18n.iri.IRI;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.model.Entry;
import org.apache.abdera.model.Feed;
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
import org.apache.abdera.util.Constants;
import org.apache.abdera.util.EntityTag;
import org.apache.abdera.util.MimeTypeHelper;
import org.apache.abdera.writer.StreamWriter;

import com.wwm.abdera.util.AtomUtils;

/**
 * Provides common processing required for handling of Abdera requests, and allows
 * application-specific functionality to be implemented by internal protected methods, (somewhat)
 * using the "open for extension, closed for modification" pattern that the Spring Framework uses.
 */
public abstract class BaseCollectionAdapter implements CollectionAdapter {

    // protected static Logger log = LogFactory.getLogger(BaseProviderImpl.class);

    public BaseCollectionAdapter() {
        super();
    }

    /**
     * Implements fixed aspects of dealing with a create request, such as validating Atom XML
     * conformity. createEntry handles POST to a COLLECTION
     */
    @SuppressWarnings("unchecked")
    public final ResponseContext postEntry(RequestContext request) {
        Parser parser = getParser(request);
        /* Factory factory = */getAndInitFactory(request);
        try {
            MimeType contentType = request.getContentType();
            String ctype = (contentType != null) ? contentType.toString() : null;
            if (ctype != null && !MimeTypeHelper.isAtom(ctype)
                    && !MimeTypeHelper.isXml(ctype)) {
                return makeExceptionResponse(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE,
                    "Content-Type must be application/atom+xml or application/xml");
            }

            Document<Entry> entryDoc = (Document<Entry>) request.getDocument(parser).clone();
            if (entryDoc != null) {
                Entry entry = entryDoc.getRoot();
                if (!ProviderHelper.isValidEntry(entry)) {
                    return makeExceptionResponse(HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid Atom document. Must have valid entries for id, author, title and updated");
                }

                createEntryInternal(request, entryDoc, entry);

                /*
                 * Sort out the response, including giving the location which will be used to GET
                 * the document
                 */
                BaseResponseContext<Base> rc = new BaseResponseContext<Base>(entry);
                IRI baseUri = ProviderHelper.resolveBase(request);
                IRI editLinkResolvedHref = entry.getEditLinkResolvedHref();
                if (editLinkResolvedHref == null) {
                    throw new Error("createEntryInternal() must set the edit link href");
                }
                rc.setLocation(baseUri.resolve(editLinkResolvedHref).toString());
                rc.setContentLocation(rc.getLocation().toString());
                // ETag is hash of entry, so HTTP caching can identify changed content
                rc.setEntityTag(calculateEntityTag(entry));
                rc.setStatus(HttpServletResponse.SC_CREATED);
                return rc;
            } else {
                return makeExceptionResponse(HttpServletResponse.SC_BAD_REQUEST, "Missing Document");
            }
        } catch (ParseException pe) {
            return makeExceptionResponse(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, pe);
        } catch (ClassCastException cce) {
            return makeExceptionResponse(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, cce);
        } catch (BadRequestException e) {
            return makeExceptionResponse(HttpServletResponse.SC_BAD_REQUEST, e);
        } catch (Exception e) {
            e.printStackTrace(); // UNEXPECTED EXCEPTION
            return makeExceptionResponse(HttpServletResponse.SC_BAD_REQUEST, e);
        }
    }

    abstract protected void createEntryInternal(RequestContext request, Document<Entry> entryDoc,
            Entry entry)
            throws Exception;

    public ResponseContext deleteEntry(RequestContext request) {
        String entryId = getEntryID(request);
        try {
            deleteEntryInternal(request, entryId);
        } catch (NotFoundException e) {
            return makeExceptionResponse(HttpServletResponse.SC_NOT_FOUND, 
            "No entry for id " + entryId);
        } catch (BadRequestException e) {
            return makeExceptionResponse(HttpServletResponse.SC_BAD_REQUEST, e);
        }

        EmptyResponseContext emptyResponseContext = new EmptyResponseContext(HttpServletResponse.SC_NO_CONTENT);
        emptyResponseContext.setContentLength(0);
        return emptyResponseContext;
    }

    abstract protected void deleteEntryInternal(RequestContext request, String entryId)
            throws NotFoundException, BadRequestException;

    /**
     * Implements GET on an ENTRY
     */
    public ResponseContext getEntry(RequestContext request) {
        Entry entry;
        String entryId = getEntryID(request);
        try {
            entry = getEntryInternal(request, entryId);
            Document<Entry> entryDoc = entry.getDocument();
            AbstractResponseContext rc = new BaseResponseContext<Document<Entry>>(entryDoc);
            rc.setEntityTag(calculateEntityTag(entry));
            return rc;
        } catch (NotFoundException e) {
            return makeExceptionResponse(HttpServletResponse.SC_NOT_FOUND,
                "No entry for id " + entryId);
        } catch (BadRequestException e) {
            return makeExceptionResponse(HttpServletResponse.SC_BAD_REQUEST, e);
        }
    }

    abstract protected Entry getEntryInternal(RequestContext request, String entryId)
            throws NotFoundException, BadRequestException;

    /**
     * Performs an update of an Entry Handles PUT to an ENTRY
     * 
     * @param request
     * @return
     */
    @SuppressWarnings("unchecked")
    public ResponseContext putEntry(RequestContext request) {
        Parser parser = getParser(request);
        /* Factory factory = */getAndInitFactory(request);

        try {
            MimeType contentType = request.getContentType();
            if (contentType != null
                    && !MimeTypeHelper.isAtom(contentType.toString())) {
                EmptyResponseContext emptyResponseContext = new EmptyResponseContext(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
                emptyResponseContext.setContentLength(0);
                return emptyResponseContext;
            }

            Document<Entry> updatedDoc = (Document<Entry>) request.getDocument(parser).clone();
            if (updatedDoc == null || !ProviderHelper.isValidEntry(updatedDoc.getRoot())) {
                return makeExceptionResponse(HttpServletResponse.SC_BAD_REQUEST, "");
            } else {
                updateEntryInternal(request, updatedDoc);

                EmptyResponseContext emptyResponseContext = new EmptyResponseContext(HttpServletResponse.SC_NO_CONTENT);
                emptyResponseContext.setContentLength(0);
                return emptyResponseContext;
            }
        } catch (ParseException pe) {
            return makeExceptionResponse(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, pe);
        } catch (ClassCastException cce) {
            return makeExceptionResponse(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE, cce);
        } catch (Exception e) {
            return makeExceptionResponse(HttpServletResponse.SC_BAD_REQUEST, e);
        }
    }

    abstract protected void updateEntryInternal(RequestContext request,
            Document<? extends Entry> doc) throws IOException;

    private Parser getParser(RequestContext request) {
        Abdera abdera = request.getAbdera();
        Parser parser = abdera.getParser();
        return parser;
    }

    private Factory getAndInitFactory(RequestContext request) {
        Factory factory = request.getAbdera().getFactory();
        registerExtensionsInternal(factory);
        return factory;
    }

    /**
     * If any extensions need registering, then this method should be overridden and calls made to
     * registerExtension() in {@link Factory}
     * 
     * @param factory
     */
    protected void registerExtensionsInternal(Factory factory) {
    }

    /**
     * Implements GET on a COLLECTION
     */
    public ResponseContext getFeed(RequestContext request) {
        /* Factory factory = */getAndInitFactory(request);
        Feed feed;
        try {
            int length = ProviderHelper.getPageSize(request, "count", 25);
            int offset = ProviderHelper.getOffset(request, "page", length);
            String _page = request.getParameter("page");
            int page =(_page != null) ? Integer.parseInt(_page) : 0;
            feed = getFeedInternal(request, offset, length);
        } catch (BadRequestException e) {
            return makeExceptionResponse(HttpServletResponse.SC_BAD_REQUEST, e);
        }

        Document<Feed> doc = feed.getDocument();

        AbstractResponseContext rc = new BaseResponseContext<Document<Feed>>(doc);
        rc.setEntityTag(calculateEntityTag(feed));
        return rc;
    }

    protected Feed getFeedInternal(RequestContext request, int offset, int length) throws BadRequestException {
        // default impl is to ignore page requests
        return getFeedInternal(request);
    }

    // FIXME: Make this abstract
    protected Feed getFeedInternal(RequestContext request) throws BadRequestException {
//        dummyImpl(request);
        return null;
    }

    public ResponseContext headEntry(RequestContext request) {
        return ProviderHelper.notsupported(request);
    }

    public ResponseContext optionsEntry(RequestContext request) {
        return ProviderHelper.notsupported(request);
    }

    /**
     * Return the different categories we support (we use categories for different types of
     * document)
     */
    public ResponseContext getCategories(RequestContext request) {
        return new StreamWriterResponseContext(request.getAbdera()) {
            @Override
            protected void writeTo(StreamWriter sw) throws IOException {
                sw.startDocument().startCategories(false);
                Collection<String> cats = getCategoriesInternal();
                if (cats != null) {
                    for (String cat : cats) {
                        sw.writeCategory(cat);
                    }
                }
                sw.endCategories().endDocument();
            }
        }
            .setStatus(HttpServletResponse.SC_OK)
            .setContentType(Constants.CAT_MEDIA_TYPE);
    }

    /**
     * Implement this to return a collection of strings which are then formed into the categories
     * document
     * 
     * @return a collection, or null for an empty collection
     */
    abstract protected Collection<String> getCategoriesInternal();

    protected String getEntryID(RequestContext request) {
        if (request.getTarget().getType() != TargetType.TYPE_ENTRY) {
            return null;
        }

        String path = request.getUri().toString();
        String[] segments = path.split("/");
        return segments[segments.length - 1];
    }

    private EntityTag calculateEntityTag(Base base) {
        String id = null;
        String modifiedDate = null;
        if (base instanceof Entry) {
            id = ((Entry) base).getId().toString();
            modifiedDate = ((Entry) base).getUpdatedElement().getText();
        } else if (base instanceof Feed) {
            id = ((Feed) base).getId().toString();
            modifiedDate = ((Feed) base).getUpdatedElement().getText();
        }
        return EntityTag.generate(id, modifiedDate);
    }

    // ================================================================
    // Misc dev stuff
    // ================================================================
    private ResponseContext dummyImpl(RequestContext request) {
        try {
            System.out.println(request);
            // String[] parameters = request.getParameterNames();
            Document<Element> doc = request.getDocument();
            if (doc != null) {
                AtomUtils.prettyPrint(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // throw new Error(e);
        }
        // throw new UnsupportedOperationException();
        return null;
    }

    final private ResponseContext makeExceptionResponse(int statusCode, Exception e) {
        EmptyResponseContext rc = new EmptyResponseContext(statusCode);
        rc.setContentLength(0);
        decorateExceptionResponse(rc, e);
        return rc;
    }

    abstract protected void decorateExceptionResponse(EmptyResponseContext rc, Exception e);

    final private ResponseContext makeExceptionResponse(int statusCode, String error) {
        EmptyResponseContext rc = new EmptyResponseContext(statusCode);
        rc.setContentLength(0);
        decorateErrorResponse(rc, error);
        return rc;
    }

    abstract protected void decorateErrorResponse(EmptyResponseContext rc, String error);

    public CollectionAdapter getCollectionAdapter(RequestContext request) {
        return this;
    }

    public ResponseContext extensionRequest(RequestContext request) {
        // TODO Auto-generated method stub
        return null;
    }

    protected void addEditLinkToEntry(Entry entry) throws Exception {
        if (ProviderHelper.getEditUriFromEntry(entry) == null) {
            entry.addLink(entry.getId().toString(), "edit");
        }
    }

    protected void setEntryIdIfNull(RequestContext request, Entry entry) throws Exception {
        // if there is no id in Entry, assign one.
        if (entry.getId() != null) {
            return;
        }
        String uuidUri = request.getAbdera().getFactory().newUuidUri();
        String[] segments = uuidUri.split(":");
        String entryId = segments[segments.length - 1];
        entry.setId(createEntryIdUri(entryId));
    }

    protected String createEntryIdUri(String entryId) throws Exception {
        return getFeedUri() + "/" + entryId;
    }
    
    /** 
     * This must be overridden to give the feed uri, which must NOT end in a trailing '/'
     * @return e.g. "atom/feed"
     */
    abstract protected String getFeedUri();

}