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

import com.bc.diskcache.DiskLruCacheIx;
import com.bc.safecontent.util.CollectIntoBuffer;
import com.bc.safecontent.util.Collector;
import com.bc.safecontent.googlecloud.vision.SafeSearchService;
import com.bc.safecontent.SensitiveWords;
import com.bc.safecontent.Likelihood;
import com.bc.safecontent.SensitiveWordsImpl;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.bc.safecontent.StandardFlags;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 22, 2018 5:02:51 AM
 */
public class SafeContentServiceImpl implements SafeContentService {

    private transient static final Logger LOG = Logger.getLogger(SafeContentServiceImpl.class.getName());

    private final SafeSearchService safeSearchService;
    
    private final List<String> safeSearchLikelihoods;
    
    private final SensitiveWords sensitiveWords;
    
    private final File cacheDir;
    
    private final int maxCacheSizeBytes;
    
    private boolean shuttingDown;
    
    public SafeContentServiceImpl(File cacheDir, int maxCacheSizeBytes) {
        
        this.safeSearchService = new SafeSearchService();
        
        this.safeSearchLikelihoods = Arrays.asList(
                Likelihood.POSSIBLE.name(), Likelihood.LIKELY.name(), Likelihood.VERY_LIKELY.name());
        
        this.sensitiveWords = this.createSensitiveWords();
        
        this.cacheDir = Objects.requireNonNull(cacheDir);
        
        this.maxCacheSizeBytes = maxCacheSizeBytes;
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
        return this.getCache().isClosed();
    }

    @Override
    public void shutdown() {
        if(this.isShuttingDown() || this.isShutdown()) {
            return;
        }
        try{
            this.shuttingDown = true;
            final DiskLruCacheIx cache = this.getCache();
            try{
                cache.flush();
            }catch(IOException e) {
                LOG.log(Level.WARNING, "{0}", e);
                LOG.log(Level.FINE, "Exception flushing Cache", e);
            }
            try{
                cache.close();
            }catch(IOException e) {
                LOG.log(Level.WARNING, "{0}", e);
                LOG.log(Level.FINE, "Exception closing Cache", e);
            }
        }finally{
            this.shuttingDown = false;
        }
    }

    @Override
    public String requestFlags(String imageurl, String... text) {
        final StringBuilder builder = new StringBuilder();
        this.appendFlags(builder, imageurl, text);
        return builder.toString();
    }
    
    
    @Override
    public void appendFlags(StringBuilder result, String imageurl, String...text) {
        
        if(imageurl != null && !imageurl.isEmpty()) {
            this.appendGoogleSafeSearchFlags(result, null, StandardFlags.IMAGE_SUFFIX, imageurl);
        }
            
        if(LOG.isLoggable(Level.FINER)) {
            LOG.log(Level.FINER, "Google SafeSearch flags: {0}, url: {1}", new Object[]{result, imageurl});
        }

        if(text != null && text.length > 0) {
            this.appendSafeTextFlags(result, null, StandardFlags.TEXT_SUFFIX, text);
        }
    }

    public void appendGoogleSafeSearchFlags(StringBuilder builder, String prefix, String suffix, String imageurl) {
        if(imageurl != null && ! imageurl.isEmpty()) {
            final Collector<String, StringBuilder> collector = new CollectIntoBuffer(builder, prefix, suffix);
            this.requestGoogleSafeSearchFlags(imageurl, collector);
        }
    }
    
    public void requestGoogleSafeSearchFlags(String imageurl, Collector<String, StringBuilder> collector) {
        
        if(imageurl != null && ! imageurl.isEmpty()) {
            
            final DiskLruCacheIx cache = this.getCache();

            Map data = null;
            try{
                data = (Map)cache.getObject(imageurl, null);
            }catch(IOException | ClassNotFoundException e) {
                LOG.log(Level.WARNING, "{0}", e);
                LOG.log(Level.FINE, "Exception retrieving from Cache. Value for key: " + imageurl, e);
            }

            if(data == null || data.isEmpty()) {
                data = this.safeSearchService.requestAnnotation(imageurl);
                if(data != null && !data.isEmpty()) {
                    try{
                        cache.putIfNone(imageurl, data);
                    }catch(IOException e) {
                        LOG.log(Level.WARNING, "{0}", e);
                        LOG.log(Level.FINE, "Exception adding to Cache. Value for key: " + imageurl, e);
                    }
                }
            }

            this.safeSearchService.collectFlags(data, safeSearchLikelihoods, collector);
        }
    }
    
    public void appendSafeTextFlags(StringBuilder builder, String prefix, String suffix, String [] text) {
        if(text != null && text.length > 0) {
            final Collector<String, StringBuilder> collector = new CollectIntoBuffer(builder, prefix, suffix);
            this.appendSafeTextFlags(text, collector);
        }
    }

    public void appendSafeTextFlags(String [] text, Collector<String, StringBuilder> collector) {
        
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
            result = this.sensitiveWords.matchesAny(text, new String[]{flag}, 
                Likelihood.VERY_LIKELY, Likelihood.LIKELY, Likelihood.POSSIBLE);
            if(LOG.isLoggable(Level.FINER)) {
                LOG.log(Level.FINER, "Flag: {0}, text: {1}", 
                        new Object[]{(result ? flag.toLowerCase() : "none"), text});
            }    
        }
        return result;
    }
    
    
    private static final Lock CACHE_LOCK = new ReentrantLock();
    private static DiskLruCacheIx _dc; 
    public DiskLruCacheIx getCache() {
        try{
            CACHE_LOCK.lock();
            if(_dc == null || _dc.isClosed()) {
                com.bc.diskcache.SimpleDiskLruCache.removeCacheDir(cacheDir);
                _dc = this.openCache();
            }
            return _dc;
        }finally{
            CACHE_LOCK.unlock();
        }
    }
    
    private DiskLruCacheIx openCache() {
        try{
            LOG.log(Level.INFO, "Opening disk LRU cache for: {0}", this.getClass());
            return com.bc.diskcache.SimpleDiskLruCache.open(cacheDir, 1, this.maxCacheSizeBytes);
        }catch(Exception e) {
            LOG.log(Level.WARNING, "Failed to open Cache at: " + cacheDir, e);
            return com.bc.diskcache.DiskLruCacheIx.NO_OP;
        }
    }
}
