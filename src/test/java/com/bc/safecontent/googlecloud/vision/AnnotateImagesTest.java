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

import com.bc.safecontent.OkHttp;
import com.bc.safecontent.googlecloud.FetchGoogleCredentials;
import java.io.IOException;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.Test;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 21, 2018 1:58:02 PM
 */
public class AnnotateImagesTest {
    
    private final String json = 
            "{\n" +
            "  \"requests\": [\n" +
            "    {\n" +
            "      \"image\": {\n" +
            "        \"source\": {\n" +
            "          \"imageUri\": \"https://cloud.google.com/vision/images/rushmore.jpg\"\n" +
            "        }\n" +
            "      },\n" +
            "      \"features\": [\n" +
            "        {\n" +
            "          \"type\": \"LANDMARK_DETECTION\",\n" +
            "          \"maxResults\": 1\n" +
            "        },\n" +
            "        {\n" +
            "          \"type\": \"WEB_DETECTION\",\n" +
            "          \"maxResults\": 2\n" +
            "        }\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    
    @Test
    public void request() {
        try{
            
            final char [] key_chars = new FetchGoogleCredentials().getApiKey(null);
            Objects.requireNonNull(key_chars);
            
            final String api_key = new String(key_chars);
            
//            System.out.println("api_key="+api_key);
            
            try(final Response response = this.request(
                    "https://vision.googleapis.com/v1/images:annotate?key=" + api_key, json)) {
            
            }
            
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public Response request(String url, String json) throws IOException {
  
        final RequestBody jsonBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"), json);
        
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .post(jsonBody)
                .url(url)
                .build();
        
        final OkHttpClient client = OkHttp.getInstance().getClient();

        final Response response = client.newCall(request).execute();
        
        return response;
    }
}
