/*
 * Copyright 2020 looseBoxes.com
 *
 * Licensed under the looseBoxes Software License (the "License");
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

import com.bc.safecontent.Likelihood;
import com.bc.safecontent.SensitiveWords;
import com.bc.safecontent.StandardFlags;
import com.bc.safecontent.googlecloud.vision.SafeSearchService;
import com.bc.safecontent.util.CollectIntoBuffer;
import com.bc.safecontent.util.Collector;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;

/**
 * @author USER
 */
public class ContentFlaggingServiceImpl implements ContentFlaggingService{

    private static final Logger LOG = LoggerFactory.getLogger(ContentFlaggingServiceImpl.class);
    
    private final SafeSearchService safeSearchService;
    
    private final SensitiveWords sensitiveWords;
    
    private final Set<Likelihood> unsafeLikelihoods;
    
    private boolean shuttingDown;
    private boolean shutdown;
    
    public ContentFlaggingServiceImpl(
            SafeSearchService safeSearchService, SensitiveWords sensitiveWords) {
        
        this(safeSearchService, sensitiveWords, EnumSet.of(
                Likelihood.POSSIBLE, Likelihood.LIKELY, Likelihood.VERY_LIKELY));
    }
    
    public ContentFlaggingServiceImpl(
            SafeSearchService safeSearchService, 
            SensitiveWords sensitiveWords,
            Set<Likelihood> unsafeLikelihoods) {
        
        this.safeSearchService = Objects.requireNonNull(safeSearchService);
        
        this.sensitiveWords = Objects.requireNonNull(sensitiveWords);
        
        this.unsafeLikelihoods = Collections.unmodifiableSet(unsafeLikelihoods);
    }
    
    @Cacheable(value = "bcsafecontentservice_contentFlagCache", key="#content", sync=true)
    @Override
    public String flag(Content content, long timeoutMillis) {
        final StringBuilder appendTo = new StringBuilder(100);
        this.appendFlags(appendTo, content, timeoutMillis);
        return appendTo.toString();
    }

    @Override
    public State getState() {
        return new StateImpl(this.shuttingDown, this.shutdown);
    }

    @Override
    public State shutdown() {
        this.shuttingDown = true;
        this.shutdown = true;
        this.shuttingDown = false;
        return this.getState();
    }

    private void appendFlags(StringBuilder appendTo, Content content, long timeoutMillis) {
        
        final Collection<String> imageurls = content.getImageUrls();
        final Collection<String> text = content.getText();

        this.appendGoogleSafeSearchFlags(appendTo, null, StandardFlags.IMAGE_SUFFIX, imageurls, timeoutMillis);

        LOG.debug("Google SafeSearch flags: {}, urls: {}", appendTo, imageurls);

        //@TODO timeout does not yet take the text portion into consideration
        if(text != null && !text.isEmpty()) {
            this.appendSafeTextFlags(appendTo, null, StandardFlags.TEXT_SUFFIX, text);
        }
    }

    private void appendGoogleSafeSearchFlags(
            StringBuilder builder, String prefix, String suffix, 
            Collection<String> imageurls, long timeoutMillis) {
        if(imageurls == null || imageurls.isEmpty()) {
            return;
        }
        final Collector<String, StringBuilder> collector = new CollectIntoBuffer(builder, prefix, suffix);
        if(imageurls.size() == 1) {
            this.requestGoogleSafeSearchFlags(imageurls.iterator().next(), collector, timeoutMillis);
        }else if(imageurls.size() > 1) {
            //@TODO make this a configuration/property
            final int processors = Runtime.getRuntime().availableProcessors();
            final int parallelism = Math.min(processors, imageurls.size());
            final ExecutorService execSvc = Executors.newWorkStealingPool(parallelism);
            for(String imageurl : imageurls) {
                execSvc.submit(new Runnable(){
                    @Override
                    public void run() {
                        requestGoogleSafeSearchFlags(imageurl, collector, timeoutMillis);
                    }
                });
            }
            //@TODO use best-practices shutdown logic
            execSvc.shutdown();
            try{
                execSvc.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS);
            }catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                LOG.warn("Exeception waiting for Executor to terminate: " + execSvc, e);
            }finally{
                execSvc.shutdownNow();
            }
        }
    }
    
    private void requestGoogleSafeSearchFlags(
            String imageurl, Collector<String, StringBuilder> collector, long timeoutMillis) {
        
        if(imageurl != null && ! imageurl.isEmpty()) {
            
            final Map data = this.safeSearchService.requestAnnotation(
                    imageurl, timeoutMillis, TimeUnit.MILLISECONDS);

            this.safeSearchService.collectFlags(data, unsafeLikelihoods, collector);
        }
    }
    
    private void appendSafeTextFlags(StringBuilder builder, String prefix, String suffix, Collection<String> text) {
        if(text != null && !text.isEmpty()) {
            final Collector<String, StringBuilder> collector = new CollectIntoBuffer(builder, prefix, suffix);
            this.appendSafeTextFlags(text, collector);
        }
    }

    private void appendSafeTextFlags(Collection<String> text, Collector<String, StringBuilder> collector) {
        if(text != null && !text.isEmpty()) {
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
            result = this.sensitiveWords.matchesAny(text, new String[]{flag}, unsafeLikelihoods);
            
            LOG.trace("Is flagged: {}, text: {}", result, text);
        }
        return result;
    }
    
    private static final class StateImpl implements State, Serializable{
        
        private final boolean shuttingDown;
        private final boolean shutdown;

        public StateImpl(boolean shuttingDown, boolean shutdown) {
            this.shuttingDown = shuttingDown;
            this.shutdown = shutdown;
        }

        @Override
        public boolean isShuttingDown() {
            return shuttingDown;
        }

        @Override
        public boolean isShutdown() {
            return shutdown;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 11 * hash + (this.shuttingDown ? 1 : 0);
            hash = 11 * hash + (this.shutdown ? 1 : 0);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final StateImpl other = (StateImpl) obj;
            if (this.shuttingDown != other.shuttingDown) {
                return false;
            }
            if (this.shutdown != other.shutdown) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "StateImpl{" + "shuttingDown=" + shuttingDown + ", shutdown=" + shutdown + '}';
        }
    }
}
