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

package com.bc.safecontent.util;

/**
 * @author Chinomso Bassey Ikwuagwu on Jan 25, 2019 2:25:42 PM
 */
public interface Collector<E, COLLECTION_TYPE> {
    
    boolean isEmpty();

    boolean accept(E key);
    
    COLLECTION_TYPE getCollection();
    
    int getCollected();
}
