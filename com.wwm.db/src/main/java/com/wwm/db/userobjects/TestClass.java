package com.wwm.db.userobjects;

import java.io.Serializable;
import java.util.Date;

import com.wwm.db.annotations.Key;

@SuppressWarnings("serial")
public class TestClass implements Serializable {

	@SuppressWarnings("unused") // It's used via reflection
	@Key private int a;

	@SuppressWarnings("unused") // It's used via reflection
	@Key(unique=true) private int b;
	
	@SuppressWarnings("unused") // It's used via reflection
	private @Key int c;
	
	@SuppressWarnings("unused") // It's used via reflection
	private @Key Date date;
	
	
}
