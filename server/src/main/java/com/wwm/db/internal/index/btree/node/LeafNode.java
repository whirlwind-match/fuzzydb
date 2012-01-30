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
package com.wwm.db.internal.index.btree.node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.wwm.db.Ref;
import com.wwm.db.internal.index.btree.IndexPointerStyle;
import com.wwm.db.internal.index.btree.LeafNodeW;
import com.wwm.db.internal.index.btree.PendingOperations;
import com.wwm.db.internal.index.btree.RefdObject;


class LeafNode extends Node implements LeafNodeW {

	private static final long serialVersionUID = 1L;

	private TreeMap<Comparable<Object>, ArrayList<Object>> data = new TreeMap<Comparable<Object>, ArrayList<Object>>();
	private int count = 0; // total number of objects in above collection
	
	LeafNode() {
		
	}
	
	private LeafNode(LeafNode node) {
		super(node);
		if (node.data != null) {
			data = new TreeMap<Comparable<Object>, ArrayList<Object>>();
			data.putAll(node.data);
		}
		count = node.count;
	}

	public ArrayList<Object> getChildren(Comparable<Object> key) {
		return data.get(key);
	}
	
	public LeafNodeW clone() {
		return new LeafNode(this);
	}


	public int getCount() {
		return count;
	}
	
	public int getKeyCount() {
		return data.size();
	}

	public TreeMap<Comparable<Object>, ArrayList<Object>> splitOutLeft() {
		TreeMap<Comparable<Object>, ArrayList<Object>> rval = new TreeMap<Comparable<Object>, ArrayList<Object>>();
		assert(data.size() > 1);
		int thresh = data.size()/2;
		
		Iterator<Entry<Comparable<Object>, ArrayList<Object>>> i = data.entrySet().iterator();
		for (int j = 0; j < thresh; j++) {
			Entry<Comparable<Object>, ArrayList<Object>> entry = i.next();
			Comparable<Object> key = entry.getKey();
			ArrayList<Object> value = entry.getValue();
			count -= value.size();
			rval.put(key, value);
			i.remove();
		}
		return rval;
	}
	
	public void insertData(IndexPointerStyle style, PendingOperations ops) {
		HashMap<Comparable<Object>, ArrayList<Ref>> removals = ops.getRemovals();
		for (Entry<Comparable<Object>, ArrayList<Ref>> entry : removals.entrySet()) {
			boolean removed = false;
			Comparable<Object> key = entry.getKey();
			ArrayList<Ref> values = entry.getValue();
			ArrayList<Object> existingData = data.get(key);
			for (Ref refToRemove : values) {
				Iterator<Object> i = existingData.iterator();
				while (i.hasNext()) {
					Object o = i.next();
					if (style == IndexPointerStyle.Copy) {
						RefdObject ro = (RefdObject) o;
						if (ro.ref.equals(refToRemove)) {
							i.remove();
							count--;
							removed = true;
						}
					}
				}
			}
			if (existingData.size() == 0) {
				data.remove(key);
			}
			assert(removed);
		}
		
		HashMap<Comparable<Object>, ArrayList<Object>> inserts = ops.getInserts();
		for (Entry<Comparable<Object>, ArrayList<Object>> entry : inserts.entrySet()) {
			Comparable<Object> key = entry.getKey();
			ArrayList<Object> values = entry.getValue();
			
			ArrayList<Object> existingList = data.get(key);
			if (existingList == null) {
				data.put(key, values);
				count += values.size();
			} else {
				existingList.addAll(values);
			}
		}
	}

	public void insertPeerData(TreeMap<Comparable<Object>, ArrayList<Object>> inserts) {
		for (Entry<Comparable<Object>, ArrayList<Object>> entry : inserts.entrySet()) {
			Comparable<Object> key = entry.getKey();
			ArrayList<Object> values = entry.getValue();
			for (Object o : values) {
				ArrayList<Object> al = data.get(key);
				if (al == null) {
					al = new ArrayList<Object>();
					data.put(key, al);
				}
				al.add(o);
				count++;
			}
		}
	}

	public Comparable<Object> getMaxKey() {

		return data.lastKey();
	}
	
}
