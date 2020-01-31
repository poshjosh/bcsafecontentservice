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

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Josh
 */
public class SensitiveWordsImplTest {
    
    private static SensitiveWordsImpl instance;
    
    public SensitiveWordsImplTest() { }
    
    @BeforeClass
    public static void setUpClass() {
        try{
            instance = new SensitiveWordsImpl();
        }catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Test of matchesAny method, of class SensitiveWordsImpl.
     */
    @Test
    public void testMatchesAny_3args() {
        System.out.println("matchesAny");
        final String [] flags = StandardFlags.ALL;
        final Likelihood[] likelihoods = {Likelihood.VERY_LIKELY, Likelihood.LIKELY, Likelihood.POSSIBLE};
        this.test("No modicum of respect", flags, likelihoods, false);
        this.test("He hinted that her cum was quite thick", flags, likelihoods, true);
        this.test("This is the pussy cat", flags, likelihoods, true);
        this.test("This is the octopussy", flags, likelihoods, false);
        this.test("Don't deny them their human right to live!", flags, likelihoods, false);
        this.test("She is a bitch", flags, likelihoods, false);
        this.test("UK APC seeks support for Sanwo-Olu", flags, likelihoods, false);
    }
    
    public void test(String text, String[] flags, Likelihood[] likelihoods, boolean expResult) {
        boolean result = instance.matchesAny(text, flags, likelihoods);
        System.out.println("Result. Expected: " + expResult + ", found: " + result + ". Text: " + text);
        assertEquals(expResult, result);
    }
}
