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
package com.wwm.attrs.internal.xstream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.core.io.DefaultResourceLoader;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.path.Path;
import com.wwm.attrs.AttributeDefinitionService;
import com.wwm.attrs.WWConfigHelper;
import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.attrs.enums.EnumExclusiveScorerPreference;
import com.wwm.attrs.enums.EnumExclusiveValue;
import com.wwm.attrs.enums.EnumPreferenceMap;
import com.wwm.util.DynamicRef;

public class TableToPreferenceMapConverter implements Converter {

    private final DynamicRef<? extends AttributeDefinitionService> mgr;

	public TableToPreferenceMapConverter(DynamicRef<? extends AttributeDefinitionService> wrapper) {
		mgr = wrapper;
	}

    public boolean canConvert(@SuppressWarnings("rawtypes") Class clazz) {
        return clazz.equals(EnumPreferenceMap.class) || clazz.equals(HtmlTableReader.class);
    }

    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        // UNSUPPORTED
        assert false;
	}

	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		
		ConfigurablePropertyAccessor contextBean = PropertyAccessorFactory.forDirectFieldAccess(context);
		@SuppressWarnings("unchecked")
		Map<Path,Object> map = (Map<Path, Object>) contextBean.getPropertyValue("values");
		EnumExclusiveScorerPreference object = (EnumExclusiveScorerPreference) map.get(new Path("/ScoreConfiguration/EnumScoresMapScorer"));
		
		String node = reader.getNodeName();

		if (node.equals("map")) {
			// Determine what the rows and columns mean
			final EnumDefinition scorerEnumDef = mgr.getObject().getEnumDefinition( reader.getAttribute("scorerEnumDefinition") );
			final EnumDefinition otherEnumDef = mgr.getObject().getEnumDefinition( reader.getAttribute("otherEnumDefinition") );
			
			// If a URL return the HtmlTableReader element from parsing that
			String url = reader.getAttribute("url");

			HtmlTableReader table = (url != null) ? readUrl(url) : readInlineTable(reader);
			return getTableAsEnumPreferenceMap(object.getScorerAttrId(), object.getOtherAttrId(), scorerEnumDef, otherEnumDef, table);
		}
		else {
			// For a raw HTML document, we return the reader result, which then gets processed the line above
			return readInlineTable(reader);
		}
		
		
    }


	private HtmlTableReader readUrl(String url) {
		InputStream inputStream = null;
		try {
			inputStream = new DefaultResourceLoader().getResource(url).getInputStream();
			return WWConfigHelper.readEnumDefs(mgr, inputStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
				// do nothing
			}
		}
	}

	private EnumPreferenceMap getTableAsEnumPreferenceMap( 
			final int scorerAttrId, final int otherAttrId, final EnumDefinition scorerEnumDef, final EnumDefinition otherEnumDef, HtmlTableReader table) {
		
		final EnumPreferenceMap map = new EnumPreferenceMap();
		table.foreachCell( new CellCallback(){
			public void doCell(int row, String rowHeading, int col, String colHeading, String value) {
				EnumExclusiveValue scorerAttr = scorerEnumDef.getEnumValue(rowHeading, scorerAttrId); 
				EnumExclusiveValue otherAttr = otherEnumDef.getEnumValue(colHeading, otherAttrId); 
				map.add(scorerAttr, otherAttr, Float.valueOf(value));
			}});
		return map;
	}

	private HtmlTableReader readInlineTable(HierarchicalStreamReader reader) {
		HtmlTableReader table = new HtmlTableReader(reader);
		table.read();
		return table;
	}
}
