/*
 * Copyright 2019 NUROX Ltd.
 *
 * Licensed under the NUROX Ltd Software License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.looseboxes.com/legal/licenses/software.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bc.safecontent.util;

import com.bc.safecontent.FlaggingContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author Chinomso Bassey Ikwuagwu on Jan 25, 2019 2:43:51 PM
 */
public final class CollectIntoBuffer implements Collector<String, StringBuilder>, Serializable {
    
    private final StringBuilder collection;
    private final String separator;
    private final String prefix;
    private final String suffix;
    private final boolean acceptDuplicates;
    private int collected;
    private final Set<String> set;
    
    public CollectIntoBuffer() {
        this(new StringBuilder(), null, null);
    }
    
    public CollectIntoBuffer(StringBuilder buffer, String prefix, String suffix) {
        this(buffer, FlaggingContext.FLAG_SEPARATOR, prefix, suffix, false);
    }
    
    public CollectIntoBuffer(String separator, boolean acceptDuplicates) {
        this(new StringBuilder(), separator, null, null, acceptDuplicates);
    }
    
    public CollectIntoBuffer(StringBuilder buffer, String separator, String prefix, String suffix, boolean acceptDuplicates) {
        this.collection = Objects.requireNonNull(buffer);
        this.separator = Objects.requireNonNull(separator);
        this.prefix = prefix;
        this.suffix = suffix;
        this.acceptDuplicates = acceptDuplicates;
        this.set = this.acceptDuplicates ? Collections.EMPTY_SET : new HashSet<>();
    }
    
    public void reset() {
        this.collection.setLength(0);
        this.collected = 0;
    }
    
    public boolean contains(String s) {
        return collection.indexOf(s) != -1;
    }
    
    @Override
    public boolean accept(String value) {
        
        if(set.contains(value)) {
            return false;
        }
    
        if(collection.length() != 0 && separator != null) {
            collection.append(separator);
        }
        
        if(prefix != null) {
            collection.append(prefix);
        }
        
        collection.append(value);
        if( ! acceptDuplicates) {
            set.add(value);
        }
        
        if(suffix != null) {
            collection.append(suffix);
        }
        
        ++collected;
        
        return true;
    }
    
    @Override
    public boolean isEmpty() {
        return getCollected() == 0;
    }

    @Override
    public final StringBuilder getCollection() {
        return collection;
    }
    
    @Override
    public final int getCollected() {
        return collected;
    }

    public final String getPrefix() {
        return prefix;
    }

    public final String getSuffix() {
        return suffix;
    }

    public final boolean isAcceptDuplicates() {
        return acceptDuplicates;
    }
}
