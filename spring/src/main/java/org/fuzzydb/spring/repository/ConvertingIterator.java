package org.fuzzydb.spring.repository;

import java.util.Iterator;


public abstract class ConvertingIterator<FROM,TO> implements Iterator<TO> {
	private final Iterator<FROM> iterator;

	ConvertingIterator(Iterator<FROM> iterator) {
		this.iterator = iterator;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public TO next() {
		FROM resultInternal = iterator.next();
		TO result = convert(resultInternal);
		return result;
	}
	
	abstract protected TO convert(FROM internal);

	@Override
	public void remove() {
		iterator.remove(); // Generally we'd not expect this to be supported
	}
}