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
package com.bc.safecontent.service;

import com.bc.safecontent.SensitiveWordsImpl;
import com.bc.safecontent.googlecloud.vision.SafeSearchService;
import com.bc.safecontent.service.ContentFlaggingService.Content;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Josh
 */
public class ContentFlaggingServiceImplTest {
    
    private static ContentFlaggingService service;
    
    public ContentFlaggingServiceImplTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        try{
            service = new ContentFlaggingServiceImpl(new SafeSearchService(), new SensitiveWordsImpl());
        }catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
        if(service != null && !service.getState().isShutdown()) {
            service.shutdown();
        }
    }

    /**
     * Test of requestFlags method, of class ContentFlaggingServiceImpl.
     */
    @Test
    public void testRequestFlags() {
        System.out.println("requestFlags");
        
        // boko haram
        // https://www.worldbulletin.net/images/resize/100/656x400/haberler/news/2015/06/17/boko-haram.jpg
        this.testRequestFlags(
                "https://www.worldbulletin.net/images/resize/100/656x400/haberler/news/2015/06/17/boko-haram.jpg", 
                "This is not sensitive text");
        
        this.testRequestFlags("https://bellard.org/bpg/2.png", "This is boko haram");
        
        if(true) {
            return;
        }
        
        // burning fire
        //http://www.aitonline.tv/pix/NewsImages/14460.jpg
        this.testRequestFlags(
                "https://storage.googleapis.com/gweb-cloudblog-publish/images/inappropriate-content-detection-11wmcp.max-400x400.PNG", 
                "This is the pussy cat");
        
        this.testRequestFlags(
                "https://bellard.org/bpg/2.png", 
                "Don't deny them their human right to live!");
        
        this.testRequestFlags(
                "https://bellard.org/bpg/2.png", 
                "She is a bitch");
        
        this.testRequestFlags(
                "https://storage.googleapis.com/gweb-cloudblog-publish/images/inappropriate-content-detection-11wmcp.max-400x400.PNG", 
                "UK APC seeks support for Sanwo-Olu");
    }
    
    private void testRequestFlags(String imageurl, String... text) {
        final Content content = new ContentImpl(
                imageurl==null?null:Collections.singletonList(imageurl),
                text==null?null:Arrays.asList(text)
        );
        try{
            final String flags = service.flag(content, 0);
            System.out.println("Flags: " + flags + ", content" + content);
        }catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
