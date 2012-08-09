package org.fuzzydb.spring.repository;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

public class PrimaryKeyedItem implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	private String email;

	private String passHash;
	
	public PrimaryKeyedItem(String email, String passHash) {
		this.email = email;
		this.passHash = passHash;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassHash() {
		return passHash;
	}
}
