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

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Chinomso Bassey Ikwuagwu on Nov 21, 2018 9:22:15 PM
 */
public final class GoogleCloudResponse {

    private final List<Map> responseList;
    
    public GoogleCloudResponse(Map responseData) {
        this(responseData.get("responses") == null ? 
                Collections.EMPTY_LIST :
                (List<Map>)responseData.get("responses"));
    }
    
    public GoogleCloudResponse(List<Map> responseList) {
        this.responseList = Collections.unmodifiableList(responseList);
    }

    public boolean isError() {
        return this.getErrorResponse(null) != null;
    }
    
    public Object getErrorResponse(Object outputIfNone) {
        return this.isEmpty() ? outputIfNone : this.getSingleItemResponse(0, "error", outputIfNone);
    }

    public Integer getErrorCode(Integer outputIfNone) {
        return (Integer)this.getSingleMapResponseValue(0, "error", "code", outputIfNone);
    }
    
    public String getErrorMessage(Integer outputIfNone) {
        return (String)this.getSingleMapResponseValue(0, "error", "message", outputIfNone);
    }

    public Object getSingleMapResponseValue(int index, String arg0, String arg1, Object outputIfNone) {
        final Object resItem = this.getSingleItemResponse(index, arg0, null);
        Object result;
        if(resItem instanceof Map) {
            result = ((Map)resItem).get(arg1);
        }else{
            result = null;
        }
        return result == null ? outputIfNone : result;
    }
    
    public Object getSingleItemResponse(int index, String key, Object outputIfNone) {
        final Map first = responseList.isEmpty() ? null : responseList.get(index);
        final Object errRes = first == null ? null : first.get(key);
        return errRes == null ? outputIfNone : errRes;
    }
    
    public boolean isEmpty() {
        return responseList.isEmpty();
    }
    
    public int getCount() {
        return responseList.size();
    }

    public List<Map> getResponseList() {
        return responseList;
    }
}
