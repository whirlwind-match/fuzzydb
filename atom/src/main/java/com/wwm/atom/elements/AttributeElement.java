/*********************************import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;

import org.fuzzydb.dto.attributes.Attribute;
se, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.atom.elements;

import javax.xml.namespace.QName;

import org.apache.abdera.factory.Factory;
import org.apache.abdera.model.Element;
import org.fuzzydb.dto.attributes.Attribute;


public abstract class AttributeElement extends NamedElement {

	public AttributeElement(Element internal) {
		super(internal);
	}

	public AttributeElement(Factory factory, QName qname) {
		super(factory, qname);
	}

	public abstract Attribute<?> getAttribute();
}
