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

import com.bc.safecontent.googlecloud.vision.SingleImageFeatureRequestBuilder;
import com.bc.safecontent.googlecloud.vision.VisionRequestExecutor;
import com.bc.safecontent.googlecloud.GoogleCloudResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 21, 2018 8:12:43 PM
 */
public class LabelDetectionTest {

//    @Test
    public void test() {
        
        try{
        
            final String requestJson = new SingleImageFeatureRequestBuilder().buildJson(
                    "https://cloud.google.com/vision/docs/images/ferris-wheel.jpg", 
                    "LABEL_DETECTION");

            final GoogleCloudResponse res = new VisionRequestExecutor().request(
                    requestJson, 10_000, TimeUnit.MILLISECONDS, null);
            
            if(res.isError()) {
                final Object err = res.getErrorResponse(null);
                System.err.println(err);
            }else{
                final int count = res.getCount();
                for(int i=0; i<count; i++) {
                    final Object resItem = res.getSingleItemResponse(i, "labelAnnotations", null);
                    System.out.println(resItem);
                }
            }
        }catch(IOException | java.text.ParseException e) {
            
            e.printStackTrace();
        }
    }
}
