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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.json.simple.JSONValue;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 21, 2018 7:55:09 PM
 */
public class SingleImageFeatureRequestBuilder {
    
    public String buildJson(String imageSource, String featureType) {
        
        final Map requestBody = this.build(imageSource, featureType);
        
        return JSONValue.toJSONString(requestBody);
    }
    
    public Map build(String imageSource, String featureType) {
        
        final Map requestData = this.buildData(imageSource, featureType);
        
        final List requestList = Collections.singletonList(requestData);
        
        final Map requestBody = Collections.singletonMap("requests", requestList);
        
        return requestBody;
    }
    
    private Map buildData(String imageSource, String featureType) {
        final Map imageSourceMap = Collections.singletonMap("imageUri", imageSource);
        final Map imageMap = Collections.singletonMap("source", imageSourceMap);
        final Map requestMap = new LinkedHashMap<>(4, 0.75f);
        requestMap.put("image", imageMap);
        
        final Map featureMap = Collections.singletonMap("type", featureType);
        final List featuresList = Collections.singletonList(featureMap);
        requestMap.put("features", featuresList);
        return requestMap;
    }
}
