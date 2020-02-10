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
import com.bc.safecontent.service.controllers.response.ResponseImpl;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import org.springframework.context.annotation.Import;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

/**
 * @author USER
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(MyTestConfiguration.class)
@TestMethodOrder(OrderAnnotation.class)
public class HttpRequestIT {
    
    @Autowired private TestUrls testUrls;
    
    @Autowired private EndpointRequestParams reqParams;

    @LocalServerPort private int port;

    @Autowired private TestRestTemplate restTemplate;

    @Test
    public void issafeRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("issafeRequest_ShouldReturnSuccessfully");
        
        this.givenEndpoint_ShouldReturn(Endpoints.ISSAFE, 200, true);
    }
    
    @Test
    public void flagRequest_GivenNegativeTimeout_ShouldReturnSuccessfully() throws Exception {
        System.out.println("flagRequest_GivenNegativeTimeout_ShouldReturnSuccessfully");
    
        final Map<String, String> params = reqParams.forEndpoint(Endpoints.FLAG);
        
        params.put(ParamNames.TIMEOUT, String.valueOf(Integer.MIN_VALUE));
        
        final String url = testUrls.getEndpointUrlWithParams(port, Endpoints.FLAG, params);
        
        this.givenUrl_ShouldReturn(url, 200, true);
    }

    @Test
    public void flagRequest_ShouldReturnSuccessfully() throws Exception {
        System.out.println("flagRequest_ShouldReturnSuccessfully");
    
        this.givenEndpoint_ShouldReturn(Endpoints.FLAG, 200, true);
    }

    private ResponseEntity<ResponseImpl> givenEndpoint_ShouldReturn(String endpoint, 
            int code, boolean success) throws Exception {
        
        return this.givenEndpoint_ShouldReturn(endpoint, Collections.EMPTY_LIST, code, success);
    }
    
    private ResponseEntity<ResponseImpl> givenEndpoint_ShouldReturn(String endpoint, List<String> cookies, 
            int code, boolean success) throws Exception {
        
        final String url = testUrls.getEndpointUrlWithDefaultParams(port, endpoint);
        
        return this.givenUrl_ShouldReturn(url, cookies, code, success);
    }

    private ResponseEntity<ResponseImpl> givenUrl_ShouldReturn(String url, 
            int code, boolean success) throws Exception {
        
        return this.givenUrl_ShouldReturn(url, Collections.EMPTY_LIST, code, success);
    }
    
    private ResponseEntity<ResponseImpl> givenUrl_ShouldReturn(
            String url, List<String> cookies, int code, boolean success) {
        
        ResponseEntity<ResponseImpl> result = null;
        try{
            
            System.out.println("----------------------------------");
            System.out.println("Executing request to: " + url);
            System.out.println("    With cookies: " + cookies);
            System.out.println("----------------------------------");

            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Cookie",cookies.stream().collect(Collectors.joining(";")));
            final HttpEntity<String> entity = new HttpEntity<>(headers);
            result = restTemplate.exchange(url, HttpMethod.GET, entity, ResponseImpl.class);

            final List<String> cookiesReceived = getCookies(result.getHeaders());
            System.out.println("Cookies received: " + cookiesReceived);
            System.out.println("----------------------------------");

            assertThat(result.getBody())
                .matches((r) -> r.isSuccess() == success && r.getCode() == code,
                        "{success="+success+", code="+code+"}");

        }catch(Exception e) {
            e.printStackTrace();
            fail(e.toString());
        }
        
        return result;
    }
    
    private List<String> getCookies(HttpHeaders headers) {
        final List<String> cookies = headers.get("Set-Cookie");
        return cookies == null ? Collections.EMPTY_LIST : cookies;
    }
}