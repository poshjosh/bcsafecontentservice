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
package com.bc.safecontent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

/**
 *
 * @author Josh
 */
public class OkHttp {
    
    private final OkHttpClient client;
    
    private final CookieJar cookieJar;

    private final Map<String, List<Cookie>> cookies = new HashMap<>();
    
    private OkHttp() { 
        this.cookieJar = new CookieJar() {
            @Override
            public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                final Collection<Cookie> sessionCookies = this.getCookies(url, true);
                sessionCookies.addAll(cookies);
            }
            @Override
            public List<Cookie> loadForRequest(HttpUrl url) {
                final List<Cookie> sessionCookies = this.getCookies(url, false);
                return sessionCookies == null ? Collections.EMPTY_LIST : sessionCookies;
            }
            public List<Cookie> getCookies(HttpUrl url, boolean createIfNone) {
                final String key = url.topPrivateDomain() == null ? url.host() : url.topPrivateDomain();
                List<Cookie> sessionCookies = cookies.get(key);
                if(sessionCookies == null && createIfNone) {
                    sessionCookies = new ArrayList<>();
                    cookies.put(key, sessionCookies);
                }
                return sessionCookies;
            }
        };
        this.client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .cookieJar(this.cookieJar).build();
    }
    
    public static OkHttp getInstance() {
        return OkHttpHolder.INSTANCE;
    }
    
    private static class OkHttpHolder {
        private static final OkHttp INSTANCE = new OkHttp();
    }

    public final OkHttpClient getClient() {
        return client;
    }

    public final CookieJar getCookieJar() {
        return cookieJar;
    }

    public final Map<String, List<Cookie>> getCookies() {
        return cookies;
    }
}
