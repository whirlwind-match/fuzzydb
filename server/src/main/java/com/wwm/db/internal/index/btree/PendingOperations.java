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
package com.wwm.db.internal.index.btree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.fuzzydb.client.Ref;



public class PendingOperations implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;

	private final HashMap<Comparable<Object>, ArrayList<Object>> pendingInsertions = new HashMap<Comparable<Object>, ArrayList<Object>>();
	private int pendingInsertCount = 0;

	private final HashMap<Comparable<Object>, ArrayList<Ref>> pendingRemovals = new HashMap<Comparable<Object>, ArrayList<Ref>>();
	private int pendingRemovalCount = 0;
	
	public PendingOperations() {
	}
	
	// FIXME: Adrian, the rationale here needs documenting (deep vs shallow etc)
	// TODO: Consider standardising as implementing clone() directly, and calling super.clone() as part of that.
	private PendingOperations(PendingOperations clonee) {
		for(Entry<Comparable<Object>, ArrayList<Object>> entry : clonee.pendingInsertions.entrySet()) {
			Comparable<Object> key = entry.getKey();
			ArrayList<Object> values = new ArrayList<Object>();
			values.addAll(entry.getValue());
			pendingInsertions.put(key, values);
		}
		this.pendingInsertCount = clonee.pendingInsertCount;
		
		for(Entry<Comparable<Object>, ArrayList<Ref>> entry : clonee.pendingRemovals.entrySet()) {
			Comparable<Object> key = entry.getKey();
			ArrayList<Ref> values = new ArrayList<Ref>();
			values.addAll(entry.getValue());
			pendingRemovals.put(key, values);
		}
		this.pendingRemovalCount = clonee.pendingRemovalCount;
	}
	
	@Override
	public PendingOperations clone() {
		return new PendingOperations(this);
	}
	
	public void addPendingOps(PendingOperations ops) {
		addPendingRemovals(ops.pendingRemovals);
		addPendingInserts(ops.pendingInsertions);
	}
	
	public void addPendingInserts(HashMap<Comparable<Object>, ArrayList<Object>> inserts) {
		for (Entry<Comparable<Object>, ArrayList<Object>> entry : inserts.entrySet()) {
			Comparable<Object> key = entry.getKey();
			ArrayList<Object> listToAdd = entry.getValue();
			ArrayList<Object> existingList = pendingInsertions.get(key);
			if (existingList == null) {
				pendingInsertions.put(key, listToAdd);
			} else {
				existingList.addAll(listToAdd);
			}
			pendingInsertCount += listToAdd.size();
		}
	}

	public void addPendingRemovals(HashMap<Comparable<Object>, ArrayList<Ref>> removals) {
		for (Entry<Comparable<Object>, ArrayList<Ref>> entry : removals.entrySet()) {
			Comparable<Object> key = entry.getKey();
			ArrayList<Ref> listToRemove = entry.getValue();
			
			// remove the removal from the inserts if need be
			ArrayList<Object> inserts = pendingInsertions.get(key);
			if (inserts != null) {
				Iterator<Ref> i = listToRemove.iterator();
				while (i.hasNext()) {
					Ref removal = i.next();
					
					boolean removed = false;
					for (int index = 0; index < inserts.size(); index++) {
						Object o = inserts.get(index);
						if (o instanceof RefdObject) {
							if (((RefdObject)o).ref.equals(removal)) {
								inserts.remove(index);
								removed = true;
								break;
							}
						} else if (((Ref)o).equals(removal)) {
							inserts.remove(index);
							removed = true;
							break;
						}
					}
					
					
					if (removed) {
						pendingInsertCount--;
						i.remove();
						if (inserts.size() == 0) {
							pendingInsertions.remove(key);
							break;
						}
					}
				}
			}
			
			if (listToRemove.size() > 0) {
				ArrayList<Ref> existingList = pendingRemovals.get(key);
				if (existingList == null) {
					pendingRemovals.put(key, listToRemove);
				} else {
					existingList.addAll(listToRemove);
				}
				pendingRemovalCount += listToRemove.size();
			}
		}
	}

	public int getPendingOpCount() {
		return pendingInsertCount + pendingRemovalCount;
	}
	
	public int getPendingInsertCount() {
		return pendingInsertCount;
	}
	
	public HashMap<Comparable<Object>, ArrayList<Object>> getInserts() {
		return pendingInsertions;
	}

	public HashMap<Comparable<Object>, ArrayList<Ref>> getRemovals() {
		return pendingRemovals;
	}
	
	public PendingOperations extractLeft(Comparable<Object> key) {
		PendingOperations ops = new PendingOperations();
		HashMap<Comparable<Object>, ArrayList<Object>> inserts = new HashMap<Comparable<Object>, ArrayList<Object>>();
		HashMap<Comparable<Object>, ArrayList<Ref>> removals = new HashMap<Comparable<Object>, ArrayList<Ref>>();
		
		Iterator<Entry<Comparable<Object>, ArrayList<Object>>> i = pendingInsertions.entrySet().iterator();
		while (i.hasNext()) {
			Entry<Comparable<Object>, ArrayList<Object>> entry = i.next();
			if (entry.getKey().compareTo(key) <= 0) {
				pendingInsertCount -= entry.getValue().size();
				inserts.put(entry.getKey(), entry.getValue());
				i.remove();
			}
		}
		
		Iterator<Entry<Comparable<Object>, ArrayList<Ref>>> j = pendingRemovals.entrySet().iterator();
		while (j.hasNext()) {
			Entry<Comparable<Object>, ArrayList<Ref>> entry = j.next();
			if (entry.getKey().compareTo(key) <= 0) {
				pendingRemovalCount -= entry.getValue().size();
				removals.put(entry.getKey(), entry.getValue());
				j.remove();
			}
		}
		
		ops.addPendingRemovals(removals);
		ops.addPendingInserts(inserts);
		
		return ops;
		
	}
	
}
