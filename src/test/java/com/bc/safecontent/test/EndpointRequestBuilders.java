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

import java.util.Map;
import java.util.Objects;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Build endpoint requests with custom request parameters for tests.
 * Expected endpoints are defined in the 
 * {@link com.looseboxes.cometd.chat.service.controllers.Endpoints Endpoints} interface.
 * @author USER
 */
public class EndpointRequestBuilders {
    
    private final EndpointRequestParams params;

    public EndpointRequestBuilders(EndpointRequestParams params) {
        this.params = Objects.requireNonNull(params);
    }

    public MockHttpServletRequestBuilder from(String endpoint) {
        final Map<String, String> pairs = params.forEndpoint(endpoint);
        return from(endpoint, pairs);
    }

    public MockHttpServletRequestBuilder from(String endpoint, Map<String, String> pairs) {
        final MockHttpServletRequestBuilder builder = get(endpoint);
        pairs.forEach((k, v) -> {
            builder.param(k, v);
        });
        return builder;
    }
}
