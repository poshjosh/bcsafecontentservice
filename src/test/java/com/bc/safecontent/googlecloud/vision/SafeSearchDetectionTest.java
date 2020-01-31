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

import com.bc.safecontent.googlecloud.vision.SafeSearchService;
import com.bc.safecontent.googlecloud.GoogleCloudResponse;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 21, 2018 5:30:06 PM
 */
public class SafeSearchDetectionTest {

//    @Test
    public void test() {
        
        final String imageurl = "https://storage.googleapis.com/gweb-cloudblog-publish/images/inappropriate-content-detection-11wmcp.max-400x400.PNG";

        final GoogleCloudResponse res = new SafeSearchService().request(imageurl);

        if(res.isError()) {
            final Object err = res.getErrorResponse(null);
            System.err.println(err);
        }else if(res.isEmpty()){

        }else {    
            final int count = res.getCount();
            for(int i=0; i<count; i++) {
                final Map resItem = (Map)res.getSingleItemResponse(i, "safeSearchAnnotation", null);
                if(resItem != null) {
                    System.out.println("Medical: " + resItem.get("medical"));
                    System.out.println("Spoof: " + resItem.get("spoof"));
                    System.out.println("Violence: " + resItem.get("violence"));
                    System.out.println("Racy: " + resItem.get("racy"));
                    System.out.println("Adult: " + resItem.get("adult"));
                }
            }
        }
    }
}
