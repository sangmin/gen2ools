/*
Copyright (c) 2003, Dennis M. Sosnoski
All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.
 * Neither the name of JiBX nor the names of its contributors may be used
   to endorse or promote products derived from this software without specific
   prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package com.eucalyptus.gen2ools;

import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Hashmap with insertion ordering. This adds a simple list of keys to a
 * conventional hashmap. In order to keep things reasonably simple this does not
 * implement removal support.
 *
 * @author Dennis M. Sosnoski
 * @version 1.0
 */

public class ListMap extends HashMap
{
    private ArrayList m_keys;
    
    public ListMap() {
        m_keys = new ArrayList();
    }
    
    public ListMap(Map map) {
        super(map.size());
        m_keys = new ArrayList();
        for (Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            m_keys.add(entry.getKey());
            put(entry.getKey(), entry.getValue());
        }
    }
    /* (non-Javadoc)
     * @see java.util.Map#clear()
     */
    public void clear() {
        m_keys.clear();
        super.clear();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        return new ListMap(this);
    }
    /* (non-Javadoc)
     * @see java.util.Map#entrySet()
     */
    public Set entrySet() {
        return new EntrySet();
    }
    /* (non-Javadoc)
     * @see java.util.Map#keySet()
     */
    public Set keySet() {
        return new KeySet();
    }
    /* (non-Javadoc)
     * @see java.util.Map#put(java.lang.Object, java.lang.Object)
     */
    public Object put(Object key, Object value) {
        if (!containsKey(key)) {
            m_keys.add(key);
        }
        return super.put(key, value);
    }
    /* (non-Javadoc)
     * @see java.util.Map#putAll(java.util.Map)
     */
    public void putAll(Map m) {
        for (Iterator iter = m.entrySet().iterator(); iter.hasNext();) {
            Map.Entry entry = (Map.Entry)iter.next();
            put(entry.getKey(), entry.getValue());
        }
    }
    /* (non-Javadoc)
     * @see java.util.Map#remove(java.lang.Object)
     */
    public Object remove(Object key) {
        if (containsKey(key)) {
            m_keys.remove(key);
        }
        return super.remove(key);
    }
    /* (non-Javadoc)
     * @see java.util.Map#values()
     */
    public Collection values() {
        // TODO Auto-generated method stub
        return super.values();
    }
    
    /**
     * Set of keys that just uses the order defined in the array list.
     */
    
    private class KeySet extends AbstractSet
    {
        /* (non-Javadoc)
         * @see java.util.AbstractCollection#size()
         */
        public int size() {
            return m_keys.size();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#iterator()
         */
        public Iterator iterator() {
            return m_keys.iterator();
        }
    }
    
    /**
     * Set of entries that works off the list of keys.
     */
    
    private class EntrySet extends AbstractSet
    {
        /* (non-Javadoc)
         * @see java.util.AbstractCollection#size()
         */
        public int size() {
            return m_keys.size();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#iterator()
         */
        public Iterator iterator() {
            return new EntryIterator();
        }
    }
    
    /**
     * Iterator for entries in map. This just goes by position within the actual
     * list of keys.
     */
    
    private class EntryIterator implements Iterator
    {
        private int m_index = -1;
        private boolean m_valid;
        
        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            if (m_valid) {
                ListMap.this.remove(m_keys.get(m_index));
                m_keys.remove(m_index--);
                m_valid = false;
            } else {
                throw new IllegalStateException("Iterator not at entry");
            }
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return m_index+1 < m_keys.size();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Object next() {
            if (hasNext()) {
                m_valid = true;
                return new Entry(m_keys.get(++m_index));
            } else {
                throw new NoSuchElementException("Past end of iteration range");
            }
        }
    }
    
    /**
     * Entry returned by entry set iterator. This just wraps the current key,
     * useing the hashmap directly for all operations.
     */
    
    private class Entry implements Map.Entry
    {
        private Object m_key;
        
        private Entry(Object key) {
            m_key = key;
        }
        
        /* (non-Javadoc)
         * @see java.util.Map.Entry#getKey()
         */
        public Object getKey() {
            return m_key;
        }

        /* (non-Javadoc)
         * @see java.util.Map.Entry#getValue()
         */
        public Object getValue() {
            return get(m_key);
        }

        /* (non-Javadoc)
         * @see java.util.Map.Entry#setValue(java.lang.Object)
         */
        public Object setValue(Object value) {
            return put(m_key, value);
        }
    }
    
    /**
     * Collection of values that works off the list of keys.
     */
    
    private class ValueCollection extends AbstractCollection
    {
        /* (non-Javadoc)
         * @see java.util.AbstractCollection#size()
         */
        public int size() {
            return m_keys.size();
        }

        /* (non-Javadoc)
         * @see java.util.AbstractCollection#iterator()
         */
        public Iterator iterator() {
            return new ValueIterator();
        }
    }
    
    /**
     * Iterator for values in map. This just goes by position within the actual
     * list of keys. The value iterator does not support removal.
     */
    
    private class ValueIterator implements Iterator
    {
        private int m_index = -1;
        private boolean m_valid;
        
        /* (non-Javadoc)
         * @see java.util.Iterator#remove()
         */
        public void remove() {
            if (m_valid) {
                ListMap.this.remove(m_keys.get(m_index));
                m_keys.remove(m_index--);
                m_valid = false;
            } else {
                throw new IllegalStateException("Iterator not at entry");
            }
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#hasNext()
         */
        public boolean hasNext() {
            return m_index+1 < m_keys.size();
        }

        /* (non-Javadoc)
         * @see java.util.Iterator#next()
         */
        public Object next() {
            if (hasNext()) {
                m_valid = true;
                return get(m_keys.get(++m_index));
            } else {
                throw new NoSuchElementException("Past end of iteration range");
            }
        }
    }
}