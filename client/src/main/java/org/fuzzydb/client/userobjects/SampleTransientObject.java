package org.fuzzydb.client.userobjects;

import java.io.Serializable;
import java.util.Date;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.client.marker.ExpirableExec;


public class SampleTransientObject implements ExpirableExec, Serializable{

	private static final long serialVersionUID = 3L;

	Ref dependant;
	SampleKeyedObject offspring;

	private Date expiryTime;
	
	public SampleTransientObject(Ref dependant, SampleKeyedObject offspring) {
		super();
		this.dependant = dependant;
		this.offspring = offspring;
	}

	public void onExpiry(Transaction t) {
		t.delete(dependant);
		t.create(offspring);
	}

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}

}
