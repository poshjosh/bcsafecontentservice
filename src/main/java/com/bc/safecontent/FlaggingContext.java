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
package com.bc.safecontent;

import com.bc.safecontent.util.Collector;

/**
 * @author Chinomso Bassey Ikwuagwu on Jan 30, 2019 12:04:39 PM
 */
public interface FlaggingContext<T> {

    String FLAG_SEPARATOR = ",";
    
    /**
     * Remove all current flags and update the input with the specified flags.
     * @param input The input whose flags will be updated
     * @param flags The flags to use in updating the input
     * @return The updated input resulting from replacing existing flags with specified flags
     */
    T updateFlags(T input, String... flags);

    T addFlags(T input, String... flags);

    String getFlags(T input, String resultIfNone);
    
    void collectFlagsFrom(Collector<String, ?> collector, T input, String... flags);
    
    boolean isFlaggedImage(T input);
    
    boolean isFlaggedText(T input);

    boolean isFlagged(T input);
    
    T removeFlags(T input, String... flags);
}
