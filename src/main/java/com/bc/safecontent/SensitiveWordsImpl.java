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

package com.bc.safecontent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 22, 2018 2:40:21 AM
 */
public class SensitiveWordsImpl implements SensitiveWords {

    private transient static final Logger LOG = Logger.getLogger(SensitiveWordsImpl.class.getName());
    
    private final Map<String, Pattern> patterns;
    
    public SensitiveWordsImpl() throws IOException {
        this(getDefaultFile());
    }
    
    private static File getDefaultFile() {
        try{
            return new File(Thread.currentThread().getContextClassLoader()
                .getResource("META-INF/com.bc.safecontent.sensitivewords.properties").toURI());
        }catch(URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    public SensitiveWordsImpl(File file) throws IOException {
        final Properties properties = new Properties();
        try(InputStream in = new FileInputStream(file)) {
            properties.load(in);
        }
        this.patterns = Collections.unmodifiableMap(this.buildPatterns(properties));
    }
    
    public SensitiveWordsImpl(Properties properties) {
        this.patterns = Collections.unmodifiableMap(this.buildPatterns(properties));
    }

    private Map<String, Pattern> buildPatterns(Properties properties) {
        final String [] flags = StandardFlags.ALL;
        final Likelihood [] likelihoods = Likelihood.values();
        final Map<String, Pattern> result = new LinkedHashMap<>(flags.length * likelihoods.length, 1.0f);
        for(String flag : flags) {
            for(Likelihood hood : likelihoods) {

                final String key = this.buildKey(flag, hood);
                final String val = properties.getProperty(key, null);
                if(LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, "{0} = {1}", new Object[]{key, val});
                }

                if(val != null && !val.isEmpty()) {
//                    final Pattern pattern = Pattern.compile(val.replace(',', '|'));
//                    result.put(key, pattern);
                    final String [] parts = val.split(",");
                    
                    if(LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "Split parts: {0}", Arrays.toString(parts));
                    }
                    
                    if(parts != null && parts.length > 0) {
                        
                        final StringBuilder builder = new StringBuilder();
                        final boolean usingCharCausesError = true;
                        final String WDBD = "\\b";
                        for(int i=0; i < parts.length; i++) {
                            builder.append(WDBD).append(parts[i]).append(WDBD);
                            if(i >= 0 && i < parts.length - 1) {
                                builder.append("|");
                            }
                        }
                        
                        final String regex = builder.toString();
                        
                        if(LOG.isLoggable(Level.FINE)) {
                            LOG.log(Level.FINE, "{0} = {1}", new Object[]{key, regex});
                        }
                        
                        result.put(key, Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public boolean matchesAny(String text, Likelihood... likelihoods) {
        return this.matchesAny(text, StandardFlags.ALL, likelihoods);
    }
    
    @Override
    public boolean matchesAny(String text, String [] flags, Likelihood... likelihoods) {
        boolean result = false;
        for(String flag : flags) {
            for(Likelihood l : likelihoods) {
                final String key = this.buildKey(flag, l);
                final Pattern pattern = this.patterns.get(key);
                if(LOG.isLoggable(Level.FINER)) {
                    LOG.log(Level.FINER, "{0} = {1}", new Object[]{key, (pattern==null?null:pattern.pattern())});
                }
                final Matcher matcher = pattern == null ? null : pattern.matcher(text);
                if(matcher != null && matcher.find()) {
                    if(LOG.isLoggable(Level.FINE)) {
                        LOG.log(Level.FINE, "Found: {0} in text: {1} using pattern: {2}", 
                                new Object[]{matcher.group(), text, pattern.pattern()});
                    }
                    result = true;
                    break;
                }else{
                    if(LOG.isLoggable(Level.FINER)) {
                        LOG.log(Level.FINER, "Pattern not found in text: {0}, Pattern. name: {1}, value: {2}", 
                                new Object[]{text, key, (pattern==null?null:pattern.pattern())});
                    }
                }
            }
        }
        return result;
    }
    
    private String buildKey(String flag, Likelihood likelihood) {
        return "sensitivewords."+flag.toLowerCase()+'.'+likelihood.name().toLowerCase();
    }
}
