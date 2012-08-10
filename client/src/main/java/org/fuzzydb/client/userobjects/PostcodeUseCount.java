package org.fuzzydb.client.userobjects;

import java.io.Serializable;

import com.wwm.stats.counters.Count;

public class PostcodeUseCount implements Serializable, Count {

	private static final long serialVersionUID = 1L;

	long count = 0;

	/* (non-Javadoc)
	 * @see org.fuzzydb.client.userobjects.Count#getCount()
	 */
	public long getCount() {
		return count;
	}

	/* (non-Javadoc)
	 * @see org.fuzzydb.client.userobjects.Count#setCount(long)
	 */
	public void setCount(long count) {
		this.count = count;
	}
}
