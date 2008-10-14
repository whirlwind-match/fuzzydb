/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.attrs.internal;

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.wwm.db.whirlwind.internal.AttributeCache;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.db.whirlwind.internal.IMergeable;


public class AttributeMap<T extends IAttribute> implements Cloneable, Serializable, IAttributeMap<T> {

    private static final long serialVersionUID = 3761969345898689587L;
    static final int INITIAL_SIZE = 4;

    private com.archopolis.db.whirlwind.internal.IAttribute attributes[] = new IAttribute[INITIAL_SIZE];

    private int count = 0;



    /**
     * Deep clone constructor based on interface
     *
     * FIXME: Need to explain why we deep clone the attributes
     *
     * @param clonee something that implements IAttributeMap<IAttribute>
     */
    public AttributeMap(IAttributeMap<IAttribute> clonee) {
        super();
        this.attributes = new IAttribute[clonee.size()];
        int i = 0;
        for (IAttribute attr : clonee ) {
            try {
                if (attr != null) {
                    this.attributes[i++] = attr.clone();
                }
            } catch (CloneNotSupportedException e) {
                throw new Error(e);
            }
        }
        this.count = i;
    }


    public AttributeMap() {
        super();
    }

    /**
     * Re-assign attribute in this attribute map, to identical objects, where found,
     * in cache.<br>
     * Note: Not all attributes are cached.
     * Those that are include:<br>
     * <li>BooleanValue</li>
     * <li>EnumExclusiveValue</li>
     * <li>StringValue - where we index String, we expect it to be from a categories list</li>
     * <li>StringMultiValue - because we expect to usually have only one option selected</li>
     * @param cache - cache to look in for identical objects.
     */
    public void switchTo(AttributeCache cache) {
        for (int i=0; i < attributes.length; i++) {
            if ( attributes[i] instanceof IMergeable ) {
                attributes[i] = cache.switchTo((IAttribute) attributes[i]);
            }
        }
    }

    /**
     * Find attribute given the attribute Id
     * @param attrId
     * @return
     */
    @SuppressWarnings("unchecked")
    public T findAttr(int attrId) {
        for (int i = 0; i < count; i++) {
            if ( ((IAttribute) attributes[i]).getAttrId() == attrId ) {
                return (T)attributes[i];
            }
        }

        return null;  // null if couldn't find
    }



    public T put(int attrId, T value) {
        // set attr Id so we can find it by attrId
        value.setAttrId(attrId);
        assert( value.getAttrId() != 0 ); // Should have been set to something.
        return putAttr(value);
    }

    /**
     * TODO Rename to set()
     * @param a
     * @return previous value if there was already one of the same attrId.
     */
    @SuppressWarnings("unchecked")
    public T putAttr(T a) {
        assert a != null;

        // Get assigned Id for this attribute.
        int attrId = a.getAttrId();
        assert( attrId != 0 && attrId != -1 );


        // See if we already have this one, by spinning down attrIds[]
        // and if we do, replace it.
        T prev;
        for (int i = 0; i < count; i++) {
            if ( ((IAttribute) attributes[i]).getAttrId() == attrId ){
                prev = (T) attributes[i];
                attributes[i] = a;
                return prev;
            }
        }

        // Resize the array if needed
        if ( count == attributes.length) {
            int newSize = attributes.length * 3 / 2 + 1;
            IAttribute resizeAttributes[] = new IAttribute[newSize];
            System.arraycopy(attributes, 0, resizeAttributes, 0, attributes.length);
            // TODO: Migrate to following
            // IAttribute resizeAttributes[] = Arrays.copyOf(attributes, newSize);
            attributes = resizeAttributes;
        }

        // if it failed, add to end, if we can.
        assert( count <= attributes.length );  // assert that we can... otherwise expand array if needed.
        attributes[count++] = a;
        return null; // there wasn't a previous
    }

    /**
     * Implement required method for Iterable<IAttribute> thus
     * allowing <code>this</code> to be used in for loop.
     */
    public Iterator<T> iterator() {
        return new Itr();
    }


    public int size() {
        return count;
    }


    @Override
    public String toString(){
        StringBuffer str = new StringBuffer();

        for (IAttribute attr : this ) {
            //            String key = AttributeIdMap.instance.getName( attr.getAttrId() );
            int key = attr.getAttrId();
            str.append( key + ":" + attr.toString() );
            str.append(", ");
        }
        return str.toString();
    }


    /**
     * [Shallow clone is okay, as referencing same attributes in array is fine.]
     * FIXME: Neale changed this to a deep clone!! (and doesn't remember why!)
     */
    @SuppressWarnings("unchecked")
    @Override
    public IAttributeMap<T> clone() {
        try {

            AttributeMap<T> clone = (AttributeMap) super.clone();
            clone.attributes = new IAttribute[attributes.length];
            for (int i = 0; i < count; i++) {
                // clone.attributes[i] = attributes[i]; // shallow
                if (attributes[i] != null) {
                    clone.attributes[i] = ((IAttribute) attributes[i]).clone();
                }
            }
            clone.count = count;
            return clone;

        } catch (CloneNotSupportedException e) {
            throw new Error(e);
        }
    }


    class Itr implements Iterator<T> {

        private int index = 0;

        public Itr() {
            super();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return (index < count);
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        @SuppressWarnings("unchecked")
        public T next() {
            if (index < count) {
                return (T) attributes[index++];
            }

            throw new NoSuchElementException();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }


    /**
     * Do opposite of add()
     * Remove given attribute
     * @param attrId
     * @return removed item
     */
    @SuppressWarnings("unchecked")
    public T removeAttr(int attrId) {
        for (int i = 0; i < count; i++) {
            if ( ((IAttribute) attributes[i]).getAttrId() == attrId ) {
                count--;
                attributes[i] = attributes[count];
                IAttribute prev = (IAttribute) attributes[count];
                attributes[count] = null;
                return (T) prev;
            }
        }
        return null;
    }


    /**
     * Does what used to be done in Branch.qualify
     */
    public boolean consistentFor(IAttributeConstraint constraint, int splitId) {
        IAttribute att = findAttr(splitId);
        if (att == null) {
            if (constraint == null) {
                return true;	// no attribute + no constraint, this is the right branch
            }
            return false; // no attribute but there is a constraint, wrong branch - need the one with no contraint
        }
        if (constraint == null) {
            return false;	// there is an attribute matching split id, must select a constrained branch
        }

        return constraint.consistent(att);
    }
}
