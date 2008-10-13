package com.wwm.db.userobjects;

import java.io.Serializable;
import java.util.Date;

import com.wwm.db.Ref;
import com.wwm.db.Transaction;
import com.wwm.db.core.exceptions.ArchException;
import com.wwm.db.marker.ExpirableExec;

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
		try {
			t.delete(dependant);
			t.create(offspring);
		} catch (ArchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public Date getExpiryTime() {
		return expiryTime;
	}

	public void setExpiryTime(Date expiryTime) {
		this.expiryTime = expiryTime;
	}

}
