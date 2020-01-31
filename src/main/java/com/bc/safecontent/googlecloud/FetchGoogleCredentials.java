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

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @see https://cloud.google.com/docs/authentication/production#obtaining_and_providing_service_account_credentials_manually
 * @author Chinomso Bassey Ikwuagwu on Nov 21, 2018 12:54:42 PM
 */
public class FetchGoogleCredentials {

    private transient static final Logger LOG = Logger.getLogger(FetchGoogleCredentials.class.getName());
    
    private static final transient Properties properties = new Properties();
    
    public char [] getApiKey(char [] outputIfNone) throws IOException, EOFException {
        return this.getApiKey(this.getFileResource(".properties"), outputIfNone);
    }
    
    public char [] getApiKey(File file, char [] outputIfNone) throws IOException, EOFException {
        if(properties.stringPropertyNames().isEmpty()) {
            try(final InputStream in = new FileInputStream(file)) {
                properties.load(in);
            }
            if(properties.isEmpty()) {
                throw new EOFException();
            }
        }
        
        final String api_key = properties.getProperty("api_key", null);
        
        return api_key == null ? outputIfNone : api_key.toCharArray();
    }
    
    private File getFileResource(String ext) {
        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final URL url = classLoader.getResource(this.getResourceName(ext));
        try{
            return new File(url.toURI());
        }catch(URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    
    private String getResourceName(String ext) {
        
        if(!ext.startsWith(".")) {
            ext = "." + ext;
        }
        
        return "META-INF/secure/com.bc.safecontent.googlecloud.application_credentials" + ext;
    }
}
/**
 * 
    
    @Override
    public Optional<GoogleCredentials> get() {
        GoogleCredentials credentials = null;
        try{
            credentials = this.execute();
        }catch(IOException e) {
            LOG.log(Level.WARNING, null, e);
        }
        return Optional.of(credentials);
    }

    public GoogleCredentials execute() throws IOException {
        return this.execute(this.getFileResource(".json"));
    }
    
    public GoogleCredentials execute(File file) throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(file))
            .createScoped(Lists.newArrayList("https://www.googleapis.com/auth/cloud-platform"));
        return credentials;
    }
 * 
 */