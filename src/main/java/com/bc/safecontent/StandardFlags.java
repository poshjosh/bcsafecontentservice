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

/**
 * @author Chinomso Bassey Ikwuagwu on Jan 29, 2019 7:38:30 PM
 */
public interface StandardFlags {
    
    String ADULT = "adult";
    String VIOLENCE = "violence";
    String RACY = "racy";
    String MEDICAL = "medical";
    String SPOOF = "spoof";
    String GRAPHIC = "graphic";
    
    String TEXT_SUFFIX = "-text";
    String IMAGE_SUFFIX = "-image";
    String VIDEO_SUFFIX = "-video";
    String AUDIO_SUFFIX = "-audio";
    
    String [] BASE_FLAGS = {ADULT, VIOLENCE, RACY, SPOOF, MEDICAL, GRAPHIC};
    
    String [] TEXT_FLAGS = {
        ADULT+TEXT_SUFFIX, VIOLENCE+TEXT_SUFFIX, RACY+TEXT_SUFFIX, 
        SPOOF+TEXT_SUFFIX, MEDICAL+TEXT_SUFFIX, GRAPHIC+TEXT_SUFFIX};
    
    String [] IMAGE_FLAGS = {
        ADULT+IMAGE_SUFFIX, VIOLENCE+IMAGE_SUFFIX, RACY+IMAGE_SUFFIX, 
        SPOOF+IMAGE_SUFFIX, MEDICAL+IMAGE_SUFFIX, GRAPHIC+IMAGE_SUFFIX};
    
    String [] ALL = {
        
        ADULT, VIOLENCE, RACY, SPOOF, MEDICAL, GRAPHIC,
        
        ADULT+TEXT_SUFFIX, VIOLENCE+TEXT_SUFFIX, RACY+TEXT_SUFFIX,
        SPOOF+TEXT_SUFFIX, MEDICAL+TEXT_SUFFIX, GRAPHIC+TEXT_SUFFIX,
        
        ADULT+IMAGE_SUFFIX, VIOLENCE+IMAGE_SUFFIX, RACY+IMAGE_SUFFIX,
        SPOOF+IMAGE_SUFFIX, MEDICAL+IMAGE_SUFFIX, GRAPHIC+IMAGE_SUFFIX
    };
}
