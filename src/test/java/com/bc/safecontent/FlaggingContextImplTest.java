/*
 * Copyright 2019 NUROX Ltd.
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

import com.bc.safecontent.util.CollectIntoBuffer;
import com.bc.safecontent.util.Collector;
import java.util.Arrays;
import java.util.Objects;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Josh
 */
public class FlaggingContextImplTest {
    
    private final String sampleInput = "boy,girl,father,adul,adulte,adult-,adult-nonsense,-adult,nonsense-adult";
    
    public FlaggingContextImplTest() { }
    
    public FlaggingContext<String> getInstance() {
        return new FlaggingContextImpl();
    }

    /**
     * Test of addFlags method, of class FeedFlaggingContext.
     */
    @Test
    public void testAddFlags() {
        this.testAddFlags(StandardFlags.BASE_FLAGS);
        this.testAddFlags(StandardFlags.ALL);
    }

    public void testAddFlags(String... flags) {
        System.out.println("\naddFlags");
        final String text = sampleInput;
        System.out.println("Flags: " + Arrays.toString(flags));
        System.out.println(" Text: " + text);
        final FlaggingContext<String> instance = this.getInstance();
        String expResult = this.compose(flags, text);
        String result = instance.addFlags(text, flags);
        System.out.println("Expected: " + expResult);
        System.out.println("  Result: " + result);
        assertEqualsLessSpaceChars(expResult, result);
    }

    /**
     * Test of appendFlagsFromText method, of class FeedFlaggingContext.
     */
    @Test
    public void testCollectFlagsFrom() {
        this.testCollectFlagsFrom(StandardFlags.BASE_FLAGS);
        this.testCollectFlagsFrom(StandardFlags.ALL);
    }

    public void testCollectFlagsFrom(String... flags) {
        System.out.println("\ncollectFlagsFrom");
        final String text = sampleInput;
        System.out.println("Flags: " + Arrays.toString(flags));
        System.out.println(" Text: " + text);
        final FlaggingContext<String> instance = this.getInstance();
        final String flaggedText = instance.addFlags(text, flags);
        final Collector<String, StringBuilder> collector = new CollectIntoBuffer();
        instance.collectFlagsFrom(collector, flaggedText, flags);
        final String expResult = this.flatten(flags);
        final String result = collector.getCollection().toString();
        System.out.println("Expected: " + expResult);
        System.out.println("  Result: " + result);
        assertEqualsLessSpaceChars(expResult, result);
    }
    /**
     * Test of removeFlags method, of class FeedFlaggingContext.
     */
    @Test
    public void testRemoveFlags() {
        this.testRemoveFlags(StandardFlags.BASE_FLAGS);
        this.testRemoveFlags(StandardFlags.ALL);
    }

    /**
     * Test of removeFlags method, of class FeedFlaggingContext.
     */
    public void testRemoveFlags(String... flags) {
        System.out.println("\nremoveFlags(" + Arrays.toString(flags) + ")");
        final String expResult = sampleInput;
        final String text = this.compose(flags, expResult);
        System.out.println("Flags: " + Arrays.toString(flags));
        System.out.println(" Text: " + text);
        final FlaggingContext<String> instance = this.getInstance();
        final String result = instance.removeFlags(text, flags);
        System.out.println("Expected: " + expResult);
        System.out.println("  Result: " + result);
        assertEqualsLessSpaceChars(expResult, result);
    }
    
    public String compose(String [] arr, String text) {
        Objects.requireNonNull(text);
        if(text.length() == 0) {
            throw new IllegalArgumentException();
        }
        return this.flatten(arr) + ',' + text;
    }
    
    public String flatten(String [] arr) {
        final StringBuilder builder = new StringBuilder();
        for(String s : arr) {
            if(builder.length() != 0) {
                builder.append(',');
            }
            builder.append(s);
        }
        return builder.toString();
    }
    
    public void assertEqualsLessSpaceChars(String [] lhs, String [] rhs) {
        this.assertEqualsLessSpaceChars(this.flatten(lhs), this.flatten(rhs));
    }

    public void assertEqualsLessSpaceChars(String [] lhs, String rhs) {
        this.assertEqualsLessSpaceChars(this.flatten(lhs), rhs);
    }

    public void assertEqualsLessSpaceChars(String lhs, String rhs) {
        assertEquals(lhs.replaceAll("\\s", ""), rhs.replaceAll("\\s", ""));
    }
}
