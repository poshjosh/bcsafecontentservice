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

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 22, 2018 5:28:27 AM
 * @deprecated Rather use {@link com.bc.safecontent.service.ContentFlaggingService ContentFlaggingService}
 * @see com.bc.safecontent.service.ContentFlaggingServiceImpl
 */
@Deprecated
public interface SafeContentService {

    void appendFlags(StringBuilder appendTo, String imageurl, String... text);

    void appendFlags(StringBuilder appenndTo, String... text);

    String requestFlags(String imageurl, String... text);

    String requestFlags(String... text);
        
    boolean isShuttingDown();

    boolean isShutdown();
    
    void shutdown();
}
