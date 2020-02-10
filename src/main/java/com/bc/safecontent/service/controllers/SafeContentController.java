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

import com.bc.safecontent.service.controllers.response.Response;
import com.bc.safecontent.service.controllers.response.ResponseBuilder;
import com.bc.safecontent.service.SafeContentService;
import java.util.Arrays;
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
public class SafeContentController{

    private static final Logger LOG = LoggerFactory.getLogger(SafeContentController.class);
    
    @Autowired private SafeContentService service;
    
    @Autowired private ResponseBuilder resBuilder;
    
    @RequestMapping(value=Endpoints.ISSAFE, produces={"application/json;charset=utf-8"})
    public Response isSafe(
            @RequestParam(value=ParamNames.IMAGE_URL, required=false) String imageurl,
            @RequestParam(value=ParamNames.TEXT, required=false) String [] text) {
      
        return requestFlags(Endpoints.ISSAFE, imageurl, text,
                (flags) -> flags == null || flags.isEmpty());
    }

    @RequestMapping(value=Endpoints.FLAG, produces={"application/json;charset=utf-8"})
    public Response flag(
            @RequestParam(value=ParamNames.IMAGE_URL, required=false) String imageurl,
            @RequestParam(value=ParamNames.TEXT, required=false) String [] text) {
        
        return requestFlags(Endpoints.FLAG, imageurl, text, (flags) -> flags);
    }

    private Response requestFlags(
            String endpoint, String imageurl, String[] text,
            Function<String, Object> action) {
        
        Response res;
        try{
            
            final String flags = service.requestFlags(imageurl, text);
            
            final Object result = action.apply(flags);

            LOG.trace("Result: {}, flags: {}\nEndpoint: {}, image: {}, text: {}", 
                    result, flags, endpoint, imageurl, (text==null?null:Arrays.toString(text)));
            
            res = resBuilder.buildResponse("success", endpoint, result, false);
            
        }catch(Exception e) {
            
            LOG.warn("Exception executing request: " + endpoint, e);
        
            res = resBuilder.buildErrorResponse(e);
        }
        
        LOG.debug("{}\nEndpoint: {}, image: {}, text: {}", 
                res, endpoint, imageurl, (text==null?null:Arrays.toString(text)));
        
        return res;
    }
}
