/*******************************************import org.fuzzydb.attrs.simple.FloatRangePreference;
import org.fuzzydb.client.whirlwind.internal.IAttribute;
import com.wwm.postcode.model.attributes.Attribute;
import com.wwm.postcode.model.attributes.FloatRangeAttribute;
r the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.indexer.db.converters;


import org.fuzzydb.attrs.simple.FloatRangePreference;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.dto.attributes.Attribute;
import org.fuzzydb.dto.attributes.FloatRangeAttribute;



public class FloatRangeConverter implements AttributeConverter {

    private static final FloatRangeConverter instance = new FloatRangeConverter();
    
    public static FloatRangeConverter getInstance() {
    	return instance;
    }

    public Class<FloatRangePreference> getIAttributeClass() {
        return FloatRangePreference.class;
    }

    public Class<FloatRangeAttribute> getObjectClass() {
        return FloatRangeAttribute.class;
    }

    public Attribute<?> convert(String name, IAttribute attribute) {
        FloatRangePreference pref = (FloatRangePreference) attribute;
        // NOTE: name=null, as we don't need to use it in this direction
        return new FloatRangeAttribute(name, pref.getMin(), pref.getPreferred(), pref.getMax());
    }

    public FloatRangePreference convertToInternal(int attrid, Attribute<?> object) {
        FloatRangeAttribute fr = (FloatRangeAttribute) object;
        return new FloatRangePreference(attrid, fr.getMin(), fr.getPref(), fr.getMax());
    }
}
