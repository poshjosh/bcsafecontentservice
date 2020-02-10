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

import com.bc.safecontent.service.SafeContentService;
import com.bc.safecontent.service.SafeContentServiceImpl;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @author USER
 */
@Configuration
public class AppConfiguration {
    
    public AppConfiguration() { }
    
    @Bean @Scope("singleton") public SafeContentService safeContentService(
            @Autowired SafeContentProperties props) {
        return this.safeContentService(Paths.get(props.getCacheDir()), props.getMaxSizeBytes());
    }
    
    @Bean @Scope("singleton") public SafeContentService safeContentService(
        Path safeContentCacheDir, int maxSizeBytes) {
        return new SafeContentServiceImpl(safeContentCacheDir.toFile(), maxSizeBytes);
    }
    
    @Bean public TerminateBean terminateBean() {
        return new TerminateBean();
    }
}
