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
package com.bc.safecontent.service.controllers;

import com.bc.safecontent.service.ContentFlaggingService;
import com.bc.safecontent.service.ContentImpl;
import com.bc.safecontent.service.controllers.response.Response;
import com.bc.safecontent.service.controllers.response.ResponseBuilder;
import java.util.List;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author USER
 */
@RestController
public class ContentFlaggingController{
    
    private static final Logger LOG = LoggerFactory.getLogger(ContentFlaggingController.class);
    
    @Autowired private ContentFlaggingService service;
    
    @Autowired private ResponseBuilder resBuilder;
    
    @RequestMapping(value=Endpoints.ISSAFE, produces={"application/json;charset=utf-8"})
    public Response isSafe(
            @RequestParam(value=ParamNames.IMAGE_URLS, required=false) List<String> imageurls,
            @RequestParam(value=ParamNames.TEXT, required=false) List<String> text,
            @RequestParam(value=ParamNames.TIMEOUT, required=false, defaultValue="0") long timeout) {
      
        return requestFlags(Endpoints.ISSAFE, imageurls, text,
                timeout, (flags) -> flags == null || flags.isEmpty(),
                "Content is safe", "Content is unsafe");
    }

    @RequestMapping(value=Endpoints.FLAG, produces={"application/json;charset=utf-8"})
    public Response flag(
            @RequestParam(value=ParamNames.IMAGE_URLS, required=false) List<String> imageurls,
            @RequestParam(value=ParamNames.TEXT, required=false) List<String> text,
            @RequestParam(value=ParamNames.TIMEOUT, required=false, defaultValue="0") long timeout) {
        
        return requestFlags(Endpoints.FLAG, imageurls, text, timeout, 
                (flags) -> flags, "No flags", "Content flagged");
    }

    private Response requestFlags(
            String endpoint, List<String> imageurls, List<String> text,
            long timeoutMillis, Function<String, Object> action,
            String success, String error) {
        
        Response res;
        try{
            
            if((imageurls == null || imageurls.isEmpty()) &&
                    (text == null || text.isEmpty())) {
            
                res = resBuilder.buildResponse("No content", endpoint, "", false);
                
            }else{

                final String flags = service.flag(new ContentImpl(imageurls, text), timeoutMillis);

                final Object result = action.apply(flags);

                LOG.trace("Result: {}, flags: {}\nEndpoint: {}, images: {}, text: {}", 
                        result, flags, endpoint, imageurls, text);

                final String message = flags == null || flags.isEmpty() ?
                        success : error;
                
                res = resBuilder.buildResponse(message, endpoint, result, false);
            }
        }catch(Exception e) {
            
            LOG.warn("Exception executing request: " + endpoint, e);
        
            res = resBuilder.buildErrorResponse(e);
        }
        
        LOG.debug("{}\nEndpoint: {}, images: {}, text: {}", 
                res, endpoint, imageurls, text);
        
        return res;
    }
}
