package org.fuzzydb.util;

import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Map;

import org.fuzzydb.util.CsvReader;
import org.fuzzydb.util.CsvReader.GarbageLineException;
import org.fuzzydb.util.CsvReader.NoSuchColumnException;
import org.fuzzydb.util.CsvReader.UnsupportedTypeException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;


public class CsvReaderTest {

	@Test
	public void readWithHeaders() throws IOException, UnsupportedTypeException,
			GarbageLineException, NoSuchColumnException {
		CsvReader csvr = new CsvReader(
				"CsvReaderTest.csv",
				false, true, true);
		csvr.setColumn("Number", String.class);
		csvr.setColumn("GivenName", String.class);
		Map<String, Object> line = csvr.readLine();
		assertThat(line.size(), CoreMatchers.is(2));
	}
}
