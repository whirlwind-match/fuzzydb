package org.fuzzydb.spring.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

public abstract class PageUtils {


	@SuppressWarnings("unchecked")
	static public <T> Page<T> getPage(Iterator<T> iterator, Pageable pageable) {
		// See if we have the requested page by skipping past those we don't need
		int i = 0;
		int pageStartCount = pageable.getPageNumber() * pageable.getPageSize();
		for ( ; i < pageStartCount; i++) {
			if (!iterator.hasNext()) {
				return new PageImpl<T>((List<T>)Collections.emptyList(), pageable, i);
			}
			iterator.next();
		}
		
		ArrayList<T> resultsPage = new ArrayList<T>(pageable.getPageSize());
		for ( ; i < pageStartCount + pageable.getPageSize(); i++) {
			if (!iterator.hasNext()) {
				return new PageImpl<T>(resultsPage, pageable, i); // i = element where not found
			}
			resultsPage.add(iterator.next());
		}
		// If this was the last item, we know total count, otherwise we use max value as don't know size, just that there are more.
		return new PageImpl<T>(resultsPage, pageable, iterator.hasNext() ? Long.MAX_VALUE : pageStartCount + pageable.getPageSize());
	}
}