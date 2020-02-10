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
package com.bc.safecontent;

import com.bc.safecontent.googlecloud.vision.SafeSearchService;
import com.bc.safecontent.service.ContentFlaggingService;
import com.bc.safecontent.service.ContentFlaggingServiceImpl;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author USER
 */
@Configuration
public class AppConfiguration {
    
    public AppConfiguration() { }
    
    @Bean @Scope("singleton") public ContentFlaggingService contentFlaggingService() {
        return new ContentFlaggingServiceImpl(this.safeSearchService(), this.sensitiveWords());
    }

    @Bean @Scope("singleton") public SafeSearchService safeSearchService() {
        return new SafeSearchService();
    }
    
    @Bean @Scope("singleton") public SensitiveWords sensitiveWords() {
        try{
            return new SensitiveWordsImpl();
        }catch(IOException e) {
//            return SensitiveWords.NO_OP;
            throw new UncheckedIOException(e);
        }
    }

    @Bean public TerminateBean terminateBean() {
        return new TerminateBean();
    }
}
