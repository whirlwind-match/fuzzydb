package org.fuzzydb.client.internal.comms.messages;

import org.fuzzydb.client.IndexDefinition;

public class EnsureIndexCmd extends TransactionCommand {

	private static final long serialVersionUID = 1L;

	private String namespace;
	private IndexDefinition def;

	public EnsureIndexCmd(int storeId, int cid, int tid, String namespace,
			IndexDefinition def) {
		super(storeId, cid, tid);
		this.namespace = namespace;
		this.def = def;
	}

	public String getNamespace() {
		return namespace;
	}
	
	public IndexDefinition getDef() {
		return def;
	}
}
