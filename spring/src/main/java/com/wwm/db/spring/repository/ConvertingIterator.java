package com.wwm.db.spring.repository;

import java.util.Iterator;


public abstract class ConvertingIterator<FROM,TO> implements Iterator<TO> {
	private final Iterator<FROM> iterator;

	ConvertingIterator(Iterator<FROM> iterator) {
		this.iterator = iterator;
	}

	public boolean hasNext() {
		return iterator.hasNext();
	}

	public TO next() {
		FROM resultInternal = iterator.next();
		TO result = convert(resultInternal);
		return result;
	}
	
	abstract protected TO convert(FROM internal);

	public void remove() {
		iterator.remove(); // Generally we'd not expect this to be supported
	}
}