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

package com.bc.safecontent;

import com.bc.safecontent.util.CollectIntoBuffer;
import com.bc.safecontent.util.Collector;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Jan 29, 2019 9:53:58 PM
 */
public class FlaggingContextImpl implements Serializable, FlaggingContext<String> {
    
    private transient static final Logger LOG = Logger.getLogger(FlaggingContextImpl.class.getName());

    public FlaggingContextImpl() { }

    /**
     * Remove all current flags and update the input with the specified flags.
     * @param input The input whose flags will be updated
     * @param flags The flags to use in updating the input
     * @return The updated input resulting from replacing existing flags with specified flags
     */
    @Override
    public String updateFlags(String input, String... flags) {
        input = this.removeFlags(input, StandardFlags.ALL);
        return this.addFlags(input, flags);
    }
    
    @Override
    public boolean isFlagged(String input) {
        return this.getFlags(input, null) != null;
    }

    @Override
    public boolean isFlaggedImage(String input) {
        return this.getFlags(input, "").contains("image");
    }

    @Override
    public boolean isFlaggedText(String input) {
        return this.getFlags(input, "").contains("text");
    }

    @Override
    public String getFlags(String input, String resultIfNone) {
        final Collector<String, StringBuilder> collector = new CollectIntoBuffer();
        this.collectFlagsFrom(collector, input, StandardFlags.ALL);
        return collector.isEmpty() ? resultIfNone : collector.getCollection().toString();
    }
    
    @Override
    public void collectFlagsFrom(Collector<String, ?> collector, String input, String... flags) {
        
        if(input != null && !input.isEmpty()) {
            
            if(flags != null && flags.length != 0) {
                
                final String [] parts = input.split(FLAG_SEPARATOR);

                for(String flag : flags) {

                    if(this.indexOf(parts, flag) != -1) {

                        collector.accept(flag);
                    }
                }
            }
        }
    }
    
    @Override
    public String addFlags(String input, String... flags) {
        
        final String result;
        
        if(input == null || input.isEmpty()) {
            result = input;
        }else{    
            
            final String [] parts = input.split(FLAG_SEPARATOR);
            
            final StringBuilder builder = new StringBuilder();
            
            for(String flag : flags) {
                
                if(flag == null || flag.isEmpty()) {
                    continue;
                }
                
                if(this.indexOf(parts, flag) == -1) {
                    
                    if(builder.length() != 0) {
                        builder.append(FLAG_SEPARATOR);
                    }
                    
                    builder.append(flag);
                }
            }
            
            for(String part : parts) {
                
                if(part == null || part.isEmpty()) {
                    continue;
                }
                
                if(builder.length() != 0) {
                    builder.append(FLAG_SEPARATOR);
                }

                builder.append(part);
            }
            
            result = builder.toString();
        }
        
        if(LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Flags: {0}, Input: {1}, Output: {2}", 
                    new Object[]{(flags==null?null:Arrays.toString(flags)), input, result});
        }
        return result;
    }

    @Override
    public String removeFlags(String input, String... flags) {
        final String result;
        if(input == null || input.isEmpty()) {
            result = input;
        }else{
            
            final String [] parts = input.split(FLAG_SEPARATOR);
            
            final StringBuilder builder = new StringBuilder();
            
            for(String part : parts) {
                
                if(part == null || part.isEmpty()) {
                    continue;
                }
                
                if(this.indexOf(flags, part) == -1) {
                    
                    if(builder.length() != 0) {
                        builder.append(FLAG_SEPARATOR);
                    }
                    
                    builder.append(part);
                }
            }
            
            result = builder.toString();
        }
        
        if(LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Input: {0}, Output: {1}", new Object[]{input, result});
        }
        
        return result;
    }

    public int indexOf(String [] arr, String e) {
        for(int i=0; i<arr.length; i++) {
            if(Objects.equals(arr[i], e)) {
                return i;
            }
        }
        return -1;
    }
}
