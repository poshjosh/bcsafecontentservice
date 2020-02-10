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
package com.bc.safecontent.service;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author USER
 */
public final class ContentImpl implements ContentFlaggingService.Content, Serializable{

    private final List<String> imageUrls;
    private final List<String> text;

    public ContentImpl(String... text) {
        this((String)null, text);
    }
    
    public ContentImpl(String imageUrl, String... text) {
        this(imageUrl==null ? Collections.EMPTY_LIST : Collections.singletonList(imageUrl), 
                text==null || text.length == 0 ? Collections.EMPTY_LIST : Arrays.asList(text));
    }
    
    public ContentImpl(String [] imageUrl, String [] text) {
        this(imageUrl==null ? Collections.EMPTY_LIST : Arrays.asList(imageUrl), 
                text == null ? Collections.EMPTY_LIST : Arrays.asList(text));
    }
    
    public ContentImpl(List<String> imageUrl, List<String> text) {
        this.imageUrls = imageUrl;
        this.text = text;
    }

    @Override
    public List<String> getImageUrls() {
        return imageUrls;
    }

    @Override
    public List<String> getText() {
        return text;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.imageUrls);
        hash = 37 * hash + Objects.hashCode(this.text);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContentImpl other = (ContentImpl) obj;
        if (!Objects.equals(this.imageUrls, other.imageUrls)) {
            return false;
        }
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ContentImpl{\n" + "imageUrls=" + imageUrls + "\ntext=" + text + "\n}";
    }
}
