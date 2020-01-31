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

package com.bc.safecontent.googlecloud;

import com.bc.safecontent.OkHttp;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.simple.JSONValue;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 21, 2018 8:21:32 PM
 */
public class RequestExecutor {
    
    private final String endpoint;

    public RequestExecutor(String endpoint) {
        this.endpoint = Objects.requireNonNull(endpoint);
    }
    
    public GoogleCloudResponse request(String json, GoogleCloudResponse outputIfNone) 
            throws IOException, java.text.ParseException {
        final Map jsonData = (Map)this.requestJsonData(json, null);
        return jsonData == null ? outputIfNone : new GoogleCloudResponse(jsonData);
    }

    public Object requestJsonData(String json, Object outputIfNone) 
            throws IOException, java.text.ParseException {
        return this.requestJsonData(this.buildUrl(), json, null);
    }

    public String requestJson(String json, String outputIfNone) throws IOException {
        return this.requestJson(this.buildUrl(), json, outputIfNone);
    }

    public Response execute(String json) throws IOException {
        return this.execute(this.buildUrl(), json);
    }
    
    public GoogleCloudResponse request(String url, String json, GoogleCloudResponse outputIfNone) 
            throws IOException, java.text.ParseException {
        final Map jsonData = (Map)this.requestJsonData(url, json, null);
        return jsonData == null ? outputIfNone : new GoogleCloudResponse(jsonData);
    }

    public Object requestJsonData(String url, String json, Object outputIfNone) 
            throws IOException, java.text.ParseException {
        
        final String jsonResponse = this.requestJson(url, json, null);
        
        try{
            return jsonResponse == null ? outputIfNone : JSONValue.parseWithException(jsonResponse);
        }catch(org.json.simple.parser.ParseException e) {
            throw new java.text.ParseException(jsonResponse, e.getPosition());
        }
    }

    public String requestJson(String url, String json, String outputIfNone) throws IOException {
        
        try(final Response response = this.execute(url, json)) {
        
            final ResponseBody responseBody = response == null ? null : response.body();
            
            final String jsonString = responseBody == null ? outputIfNone : responseBody.string();
            
//            System.out.println("URL: " + url + ", Response:\n" + jsonString);
            
            return jsonString;
        }
    }
    
    public Response execute(String url, String json) throws IOException {
  
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
    
    public String buildUrl() throws IOException {
        
        final char [] key_chars = new FetchGoogleCredentials().getApiKey(null);
        
        Objects.requireNonNull(key_chars);

        final String api_key = new String(key_chars);

        return endpoint + "?key=" + api_key;
    }
}
