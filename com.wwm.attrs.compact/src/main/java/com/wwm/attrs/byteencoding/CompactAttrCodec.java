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
package com.wwm.attrs.byteencoding;

import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.db.core.LogFactory;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.util.ByteArray;

public abstract class CompactAttrCodec {

	public enum AttrFunction {
		attribute, constraint
	}


	public static final int NOT_FOUND = -1;

	protected static final int ATTR_ID_OFFSET = 0;
	protected static final int ATTR_ID_SIZE = 2;
	protected static final int PAYLOAD_OFFSET = ATTR_ID_OFFSET + ATTR_ID_SIZE;
	private static final int LENGTH_OFFSET = PAYLOAD_OFFSET; // for those that have a length it must be the first byte
	
	// TODO: Change this to a single bit for Attr/Constraint
	private static final int FUNCTION_MASK = 0x00008000;
	private static final int FN_ATTRIBUTE  = 0x00008000;
	protected static final int FN_CONSTRAINT = 0x00000000;
	
	// IncludesNotSpecified encoding
	protected static final int INCL_NOT_SPECD_MASK = 0x00004000;
	protected static final int INCL_NOT_SPECD_YES  = 0x00004000;
	protected static final int INCL_NOT_SPECD_NO   = 0x00000000;
	
	/**
	 * Responsible for getting decoded version of that type of attribute,
	 * Where the type is Boolean or Enum or quantised in a way that means that we are likely to 
	 * have many attributes of the same value, cache the attribute, and use the cached instance
	 * thereby ensuring that we only have one instance, and saving on memory, cpu effort 
	 * (doing the create) and garbage collection.
	 */
	abstract public IAttribute getDecoded(ByteArray bytes, int index);

	/**
	 * Encode the given object into the byte array.
	 * 
	 * This must handle replacing an existing object of the same attrId, and
	 * also the possibility that encoding may require a varying size.
	 * 
	 * Algo:
	 * - find in byte array
	 * - if not there, add on to end
	 * - if there, and same size, replace in situ
	 * - if there, but different size, null out existing object, and add to end.
	 * - Dead space can be compacted on serialisation by overriding writeObject(), to first compact it.
	 * @param bytes
	 * @param attrId
	 * @param value
	 */
	abstract public void encodeToByteArray(ByteArray bytes, int attrId, Object value);

	
	/**
	 * NOTE: THis isn't general purpose as far as IDs go, it's specific to the CompactCodecs.
	 * Constraints and attributes are stored in different maps, so should use the same ID.
	 * ONly when decodin compactAttrMaps do we need this function.
	 * @param attrId
	 * @return
	 */
	public static AttrFunction getAttrFunction(int attrId) {
		switch (attrId & FUNCTION_MASK) {
			case FN_ATTRIBUTE:	
				return AttrFunction.attribute;
			case FN_CONSTRAINT:				
				return AttrFunction.constraint;
		default:
			return null;
		}
	}

	
	static public int getLength(ByteArray bytes, int index, int headerWord) {
		// FIXME: WE NEED TO SUPPORT CONSTRAINTS HERE TOO, so need to look at headerWord, and return diff
		// size if it's a constraint (or even same size) e.g. for Boolean.
		boolean isConstraint = CompactAttrCodec.getAttrFunction(headerWord) == AttrFunction.constraint;
		switch (AttrDefinitionMgr.getAttrType(headerWord)){
		case booleanValue:
			return isConstraint ? BooleanConstraintCodec.BOOLEAN_SIZE : BooleanCodec.BOOLEAN_SIZE;
		case enumExclusiveValue:
			return isConstraint ? bytes.getByte( index + LENGTH_OFFSET ) : EnumCodec.EXCLUSIVE_ENUM_SIZE;
		case floatValue:
			return isConstraint ? bytes.getByte( index + LENGTH_OFFSET ) : FloatCodec.FLOAT_SIZE;
		
		case floatRangePrefValue: // could have fixed size
			// FALLTHRU
		case enumMultiValue: // could have fixed size
			// FALLTHRU
		default: // all remainder have variable size
			int length = bytes.getByte( index + LENGTH_OFFSET );
			return length;
		}
	}

	static public int getHeaderWord(ByteArray bytes, int index) {
		int header = bytes.getShort( index + ATTR_ID_OFFSET );
		return header;
	}
	
	static public int getAttrId( int headerWord ){
		return AttrDefinitionMgr.getAttrIndexAndType(headerWord);
	}

	
	static public void setAttrId(ByteArray bytes, int index, int attrId) {
		assert(attrId < 65536);
		bytes.putShort( index + ATTR_ID_OFFSET, (short)(attrId | FN_ATTRIBUTE ) );
	}


	/**
	 * Find the location of a given attribute within the given ByteArray.
	 * @return -1 if not found, index of start of attribute if found
	 */
	static public int findAttrInBuf(ByteArray bytes, int attrId) {
		int i = 0;
		while( i < bytes.size() ) {
			int headerWord = getHeaderWord( bytes, i);
			int length = getLength(bytes, i, headerWord);
			if (length == 0){ // shouldn't be needed, but was in past.  Would be good to remove this. FIXME For now, just log
				LogFactory.getLogger(CompactAttrCodec.class).error("Got zero length. Wasn't expecting it");
				break;
			}
			int bufAttrId = getAttrId( headerWord );
			if ( attrId == bufAttrId ) {
				return i;
			}
			i += length;
		}
		return NOT_FOUND; // not found
	}

    public static boolean isConstraint(int headerWord) {
        int function = headerWord & FUNCTION_MASK;
        return (function == FN_CONSTRAINT);
    }
}
