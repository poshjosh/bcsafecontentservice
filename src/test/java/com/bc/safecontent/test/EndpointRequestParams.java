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
package com.bc.safecontent.test;

import com.bc.safecontent.service.controllers.Endpoints;
import com.bc.safecontent.service.controllers.ParamNames;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author USER
 */
public class EndpointRequestParams {
    
    private final TestData testData;

    public EndpointRequestParams(TestData testData) {
        this.testData = Objects.requireNonNull(testData);
    }
    
    public Map<String, String> forEndpoint(String endpoint) {
        final String imageurl = testData.getImageurl();
        final String [] arr = testData.getUnsafeText();
        final String text = Arrays.asList(arr).stream().collect(Collectors.joining(","));
        final Map params = new HashMap<>();
        switch(endpoint) {
            case Endpoints.ISSAFE:
                params.put(ParamNames.IMAGE_URL, imageurl);
                params.put(ParamNames.TEXT, text);
                break;
            case Endpoints.FLAG:
                params.put(ParamNames.IMAGE_URL, imageurl);
                params.put(ParamNames.TEXT, text);
                break;
            default:
                throw new UnsupportedOperationException("Unexpected endpoint: " + endpoint);
        }        
        return params;
    }
}
