/*
 * Copyright 2018 NUROX Ltd.
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

package com.bc.safecontent.service;

import com.bc.safecontent.util.CollectIntoBuffer;
import com.bc.safecontent.util.Collector;
import com.bc.safecontent.googlecloud.vision.SafeSearchService;
import com.bc.safecontent.SensitiveWords;
import com.bc.safecontent.Likelihood;
import com.bc.safecontent.SensitiveWordsImpl;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.safecontent.StandardFlags;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 22, 2018 5:02:51 AM
 * @deprecated Rather use {@link com.bc.safecontent.service.ContentFlaggingServiceImpl ContentFlaggingServiceImpl}
 */
@Deprecated
public final class SafeContentServiceImpl implements SafeContentService {

    private transient static final Logger LOG = Logger.getLogger(SafeContentServiceImpl.class.getName());

    private final SafeSearchService safeSearchService;
    
    private final Set<Likelihood> safeSearchLikelihoods;
    
    private final SensitiveWords sensitiveWords;
    
    private boolean shuttingDown;
    
    private boolean shutdown;
    
    public SafeContentServiceImpl() {
        
        this.safeSearchService = new SafeSearchService();
        
        this.safeSearchLikelihoods = EnumSet.of(
                Likelihood.POSSIBLE, Likelihood.LIKELY, Likelihood.VERY_LIKELY);
        
        this.sensitiveWords = this.createSensitiveWords();
    }
    
    private SensitiveWords createSensitiveWords() {
        try{
            return new SensitiveWordsImpl();
        }catch(IOException e) {
            LOG.log(Level.WARNING, "Failed to initialize instance of: " + SensitiveWordsImpl.class, e);
            return SensitiveWords.NO_OP;
        }
    }

    @Override
    public boolean isShuttingDown() {
        return this.shuttingDown;
    }

    @Override
    public boolean isShutdown() {
        return this.shutdown;
    }

    @Override
    public void shutdown() {
        if(this.isShuttingDown() || this.isShutdown()) {
            return;
        }
        try{
            this.shuttingDown = true;
        }finally{
            this.shuttingDown = false;
            this.shutdown = true;
        }
    }

    @Override
    public String requestFlags(String... text) {
        
        return this.requestFlags(null, text);
    }
    
    @Override
    public String requestFlags(String imageurl, String... text) {
        final StringBuilder builder = new StringBuilder();
        this.appendFlags(builder, imageurl, text);
        return builder.toString();
    }
    
    @Override
    public void appendFlags(StringBuilder appenndTo, String...text) {
        
        this.appendFlags(appenndTo, null, text);
    }
    
    @Override
    public void appendFlags(StringBuilder appendTo, String imageurl, String...text) {
        if(imageurl != null && !imageurl.isEmpty()) {
            this.appendGoogleSafeSearchFlags(appendTo, null, StandardFlags.IMAGE_SUFFIX, imageurl);
        }
        if(LOG.isLoggable(Level.FINEST)) {
            LOG.log(Level.FINEST, "Google SafeSearch flags: {0}, url: {1}", new Object[]{appendTo, imageurl});
        }

        if(text != null && text.length > 0) {
            this.appendSafeTextFlags(appendTo, null, StandardFlags.TEXT_SUFFIX, text);
        }
    }

    private void appendGoogleSafeSearchFlags(StringBuilder builder, String prefix, String suffix, String imageurl) {
        if(imageurl != null && ! imageurl.isEmpty()) {
            final Collector<String, StringBuilder> collector = new CollectIntoBuffer(builder, prefix, suffix);
            this.requestGoogleSafeSearchFlags(imageurl, collector);
        }
    }
    
    private void requestGoogleSafeSearchFlags(String imageurl, Collector<String, StringBuilder> collector) {
        
        if(imageurl != null && ! imageurl.isEmpty()) {
            
            final Map data = this.safeSearchService.requestAnnotation(imageurl);

            this.safeSearchService.collectFlags(data, safeSearchLikelihoods, collector);
        }
    }
    
    private void appendSafeTextFlags(StringBuilder builder, String prefix, String suffix, String [] text) {
        if(text != null && text.length > 0) {
            final Collector<String, StringBuilder> collector = new CollectIntoBuffer(builder, prefix, suffix);
            this.appendSafeTextFlags(text, collector);
        }
    }

    private void appendSafeTextFlags(String [] text, Collector<String, StringBuilder> collector) {
        
        if(text != null && text.length > 0) {
            final String [] flags = StandardFlags.ALL;
            for(String flag : flags) {
                for(String part : text) {
                    if(part == null || part.isEmpty()) {
                        continue;
                    }
                    if(this.matches(part, flag)) {

                        collector.accept(flag.toLowerCase());
                    }
                }
            }
        }
    }
    
    private boolean matches(String text, String flag) {
        final boolean result;
        if(text == null || text.isEmpty()) {
            result = false;
        }else{
            result = this.sensitiveWords.matchesAny(text, new String[]{flag}, this.safeSearchLikelihoods);
            if(LOG.isLoggable(Level.FINEST)) {
                LOG.log(Level.FINEST, "Flag: {0}, text: {1}", 
                        new Object[]{(result ? flag.toLowerCase() : "none"), text});
            }    
        }
        return result;
    }
}
