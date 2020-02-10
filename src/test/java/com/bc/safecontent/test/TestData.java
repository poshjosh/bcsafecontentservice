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
package com.bc.safecontent.test;

import com.bc.safecontent.service.controllers.response.Response;
import com.bc.safecontent.service.controllers.response.ResponseImpl;
import javax.servlet.http.HttpServletResponse;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * @author USER
 */
public class TestData {
    
    private final int successCode = HttpServletResponse.SC_OK;
    
    public int getTimeout() {
        return 10_000;
    }
    
    public String getImageurl() {
        return "http://www.buzzwears.com/local/images/fashion/2018/03/2_16257b445c0.jpg";
    }
    
    public String [] getUnsafeText() {
        final String [] text = {
            "This is some good content which we hope will not be flagged because I have a pussy cat",
            "Please don't flag me because I use the word sexist to describe him",
            "He is a fucking pervert and a loser. Will you flag me?",
            "He fingered the perpetrator. I hope you don't flag me",
            "Blood and gore is not the name of any game I know",
            "Bloodied limbs was scattered everywhere after the explosion"
        };
        return text;
    }

    public void validateSuccessResponse(Response response) { 
        final int expectedCode = successCode;
        assertThat("code != " + expectedCode, response.getCode(), equalTo(expectedCode));
        assertThat("success != true", response.isSuccess(), equalTo(true));
    }
    
    public Response createSuccessResponse() {
        final ResponseImpl res = new ResponseImpl();
        res.setCode(successCode);
        res.setSuccess(true);
        res.setMessage("success");
        return res;
    }
}
