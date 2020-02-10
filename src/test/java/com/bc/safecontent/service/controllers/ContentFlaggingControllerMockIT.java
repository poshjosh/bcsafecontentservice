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
package com.bc.safecontent.service.controllers;

import com.bc.safecontent.StandardFlags;
import com.bc.safecontent.service.ContentFlaggingService;
import com.bc.safecontent.service.ContentFlaggingService.Content;
import com.bc.safecontent.service.controllers.response.ResponseBuilder;
import com.bc.safecontent.test.EndpointRequestBuilders;
import com.bc.safecontent.test.EndpointRequestParams;
import com.bc.safecontent.test.Mocker;
import com.bc.safecontent.test.MyTestConfiguration;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.times;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Web MVC test which starts the spring application context without the web
 * server, thus narrowing the tests to only the web layer.
 * Uses @ExtendsWith(SpringExtention.class) which is JUnit5 construct for 
 * JUnit4 @RunWith(SpringRunner.class)
 * <p>
 * We provide mock objects to the spring application context by using the 
 * @MockBean annotation. @MockBean automatically replaces the bean of the same 
 * type in the application context with a Mockito mock.
 * </p>
 * @author USER
 */
// To start the web server use the commented out annotations, as opposed to the currently used.
//@SpringBootTest
//@AutoConfigureMockMvc
//@Import(MyTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ContentFlaggingController.class)
@Import(MyTestConfiguration.class)
public class ContentFlaggingControllerMockIT {

    private final boolean debug = true;
    
    private static final Logger LOG = LoggerFactory.getLogger(ContentFlaggingControllerMockIT.class);
    
    @Autowired private Mocker mocker;
    
    @Autowired private EndpointRequestParams endpointReqParams;
    
    @Autowired private EndpointRequestBuilders endpointReqBuilders;
    
    @Autowired private MockMvc mockMvc;

    /** Required by the Controller being tested */
    @MockBean private ContentFlaggingService service;
    
    /** Required by the Controller being tested */
    @MockBean private ResponseBuilder resBuilder;

    @Test
    public void issafeRequest_ShouldReturnSuccessfully() throws Exception{
        System.out.println("issafeRequest_ShouldReturnSuccessfully");
        
        this.endpointRequest_ShouldReturn(Endpoints.ISSAFE, 200, false);
    }

    @Test
    public void flagRequest_ShouldReturnSuccessfully() throws Exception{
        System.out.println("flagRequest_ShouldReturnSuccessfully");
        
        this.endpointRequest_ShouldReturn(Endpoints.FLAG, 200, false);
    }
    
    protected <T> void endpointRequest_ShouldReturn(
            String endpoint, int code, boolean err) throws Exception{
    
        final String flags = StandardFlags.ADULT;
        
        this.mocker.mock(service, flags);
        this.mocker.mock(resBuilder, code);

        this.mockMvc.perform(endpointReqBuilders.from(endpoint))
                .andDo(debug ? print() : (mvcResult) -> {})
                .andExpect(status().is(code));

        this.verifyService(endpoint);
        this.verifyResBuilder(endpoint, err);
    }
    
    private void verifyService(String endpoint) {
        final ArgumentCaptor<List<String>> imageCaptor = ArgumentCaptor.forClass(List.class);
        final ArgumentCaptor<List<String>> textCaptor = ArgumentCaptor.forClass(List.class);
        final ArgumentCaptor<Content> contentCaptor = ArgumentCaptor.forClass(Content.class);
        final ArgumentCaptor<Long> timeoutCaptor = ArgumentCaptor.forClass(long.class);
        
        final Map<String, String> params = endpointReqParams.forEndpoint(endpoint);
        if(debug) LOG.debug("Endpoint: " +endpoint+ ", params: " + params);
        final String [] imageurls = params.get(ParamNames.IMAGE_URLS).split(",");
        final String [] text = params.get(ParamNames.TEXT).split(",");
        final String s = params.get(ParamNames.TIMEOUT);
        final long timeout = s == null || s.isEmpty() ? 0 : Long.parseLong(s);
//        verify(service, times(1)).flag(imageCaptor.capture(), textCaptor.capture(), timeoutCaptor.capture());
        verify(service, times(1)).flag(contentCaptor.capture(), timeoutCaptor.capture());
        assertMethodVarArgs(service, "requestFlags", 0, imageurls, imageCaptor);
        assertMethodVarArgs(service, "requestFlags", 1, text, textCaptor);
        assertMethodArg(service, "requestFlags", 2, timeout, timeoutCaptor);
    }
    
    private void verifyResBuilder(String endpoint, boolean err) {
        final ArgumentCaptor<String> msgCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<String> nameCaptor = ArgumentCaptor.forClass(String.class);
        final ArgumentCaptor<Object> valCaptor = ArgumentCaptor.forClass(Object.class);
        final ArgumentCaptor<Boolean> errCaptor = ArgumentCaptor.forClass(boolean.class);

        verify(resBuilder, times(1)).buildResponse(
                msgCaptor.capture(), nameCaptor.capture(), 
                valCaptor.capture(), errCaptor.capture());
        
//        assertMethodArg(resBuilder, "buildResponse", 0, [We can't know this value], msgCaptor);
        assertMethodArg(resBuilder, "buildResponse", 1, endpoint, nameCaptor);
//        assertMethodArg(resBuilder, "buildResponse", 2, [We can't know this value], valCaptor);
        assertMethodArg(resBuilder, "buildResponse", 3, err, errCaptor);
    }
    
    private <T> void assertMethodArg(
            Object instance, String methodName, int argumentIndex, 
            T stubArg, ArgumentCaptor<T> captor){
        final T captured = captor.getValue();
        
        if(debug) LOG.debug("Stub argument: " + stubArg + "\n     Captured: " + captured);
        
        assertThat(
                String.format("%s.%s(args[%d]) != %s", 
                        instance.getClass().getSimpleName(), methodName, argumentIndex, String.valueOf(captured)), 
                captured, is(stubArg));
    }
    
    private <T> void assertMethodVarArgs(
            Object instance, String methodName, int argumentIndex, 
            T [] stubArgArr, ArgumentCaptor captor){
        final List stubArgList = Arrays.asList(stubArgArr);
        final List captured = captor.getAllValues();
        
        if(debug) LOG.debug("Stub arguments: " + stubArgList + "\n      Captured: " + captured);
        
        assertThat(
                String.format("%s.%s(args[%d]) != %s", 
                        instance.getClass().getSimpleName(), methodName, argumentIndex, String.valueOf(captured)), 
                captured, is(stubArgList));
    }
}
