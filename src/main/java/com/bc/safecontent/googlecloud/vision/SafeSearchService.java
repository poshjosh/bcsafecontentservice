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

package com.bc.safecontent.googlecloud.vision;

import com.bc.safecontent.Likelihood;
import com.bc.safecontent.util.CollectIntoBuffer;
import com.bc.safecontent.util.Collector;
import com.bc.safecontent.googlecloud.GoogleCloudResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 22, 2018 12:06:07 AM
 */
public final class SafeSearchService {

    private transient static final Logger LOG = Logger.getLogger(SafeSearchService.class.getName());
    
    private final SingleImageFeatureRequestBuilder singleImgFeatureReq;

    private final VisionRequestExecutor visionReqExec;

    public SafeSearchService() {
        this.singleImgFeatureReq = new SingleImageFeatureRequestBuilder();
        this.visionReqExec = new VisionRequestExecutor();
    }
    
    public String requestFlags(String imageurl, Collection<Likelihood> likelihoods, 
            long timeout, TimeUnit timeUnit, String outputIfNone) {
        
        final Map resItem = this.requestAnnotation(imageurl, timeout, timeUnit);
        
        return this.buildFlags(resItem, likelihoods, outputIfNone);
    }
    
    public String buildFlags(Map resItem, Collection<Likelihood> likelihoods, String outputIfNone) {
        
        final String result;
        
        if(resItem == null || resItem.isEmpty()) {
            result = null;
        }else{
            final Collector<String, StringBuilder> collector = new CollectIntoBuffer();
            this.collectFlags(resItem, likelihoods, collector);
            result = collector.isEmpty() ? null : collector.getCollection().toString();
        }
        
        return result == null ? outputIfNone : result;
    }

    public void collectFlags(Map resItem, Collection<Likelihood> likelihoods, 
            Collector<String, StringBuilder> collector) {
        if(resItem != null && ! resItem.isEmpty()) {
            final Set<String> keys = resItem.keySet();
            for(String key : keys) {
                final Object value = resItem.get(key);
                if(this.contains(likelihoods, (String)value)) {
            
                    collector.accept(key);
                }
            }
        }
    }
    
    private boolean contains(Collection<Likelihood> likelihoods, String s) {
        for(Likelihood likelihood : likelihoods) {
            if(likelihood.name().equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    public Map requestAnnotation(String imageurl, long timeout, TimeUnit timeUnit) {
        
        final GoogleCloudResponse res = this.request(imageurl, timeout, timeUnit);
        
        Map result;
        
        if(res.isError()) {
            result = null;
        }else if(res.isEmpty()) {    
            result = null;
        }else{
            result = (Map)res.getSingleItemResponse(0, "safeSearchAnnotation", null);
        }
        
        return result == null || result.isEmpty() ? Collections.EMPTY_MAP : result;
    }

    public GoogleCloudResponse request(String imageurl, long timeout, TimeUnit timeUnit) {

        final String featureType = "SAFE_SEARCH_DETECTION";

        GoogleCloudResponse res = null;
        try{
            
            final String requestJson = singleImgFeatureReq.buildJson(imageurl, featureType);

            res = visionReqExec.request(requestJson, timeout, timeUnit, null);
            
        }catch(IOException | java.text.ParseException e) {
            LOG.log(Level.WARNING, "{0}", e.toString());
            LOG.log(Level.FINE, "Exception requesting " + featureType + " from Google Could Vision", e);
        }
        
        return res == null ? new GoogleCloudResponse(Collections.EMPTY_LIST) : res;
    }
}
