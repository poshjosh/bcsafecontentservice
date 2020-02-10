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

import com.bc.safecontent.service.ContentFlaggingService;
import com.bc.safecontent.service.controllers.response.ResponseBuilder;
import com.bc.safecontent.service.controllers.response.ResponseImpl;
import java.util.Objects;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.OngoingStubbing;

/**
 * Provide default mocks for object methods.
 * @author USER
 */
public class Mocker {
    
    /**
     * {@link org.mockito.Mockito Mockito} throws an UnsupportedStubbingException, 
     * when an initialised mock is not called by one of the test methods during 
     * execution. This class avoids this strict stub checking by using the 
     * {@link org.mockito.Mockito#lenient() Mockito#lenient()} method.
     */
    private static final class LinientMocker extends Mocker{
        public LinientMocker(TestConfig testConfig) {
            super(testConfig);
        }
        @Override
        protected <T extends Object> OngoingStubbing<T> when(T methodCall) {
            return Mockito.lenient().when(methodCall);
        }
    }

    private final TestConfig testConfig;

    public Mocker(TestConfig testConfig) {
        this.testConfig = Objects.requireNonNull(testConfig);
    }
    
    public ContentFlaggingService mock(ContentFlaggingService instance, String flags) {
//        when(instance.flag(any(String[].class), any(String[].class), any(long.class))).thenReturn(flags);
        when(instance.flag(any(ContentFlaggingService.Content.class), any(long.class))).thenReturn(flags);
        return instance;
    }
    
    public ResponseBuilder mock(ResponseBuilder instance, int code) {
        when(instance.buildErrorResponse(any(Object.class))).thenCallRealMethod();
        when(instance.buildErrorResponse(any(Throwable.class))).thenCallRealMethod();
        when(instance.buildErrorResponse(any(Object.class), any(Throwable.class))).thenCallRealMethod();
        when(instance.buildResponse(any(Object.class), any(String.class), any(Object.class), any(boolean.class))).thenCallRealMethod();
        when(instance.buildSuccessResponse()).thenCallRealMethod();
        when(instance.buildResponse(any(Object.class), any(Object.class), any(boolean.class)))
                .thenAnswer((InvocationOnMock invoc) -> {
            final ResponseImpl res = new ResponseImpl();
            res.setCode(code);
            res.setMessage(invoc.getArgument(0, Object.class).toString());
            res.setData(invoc.getArgument(1, Object.class));
            res.setSuccess( ! invoc.getArgument(2, Boolean.class));
            return res;
        });
        return instance;
    }

    /**
     * {@link org.mockito.Mockito Mockito} throws an UnsupportedStubbingException, 
     * when an initialised mock is not called by one of the test methods during 
     * execution. The instance returned by this method avoids this strict stub 
     * checking by using the {@link org.mockito.Mockito#lenient() Mockito#lenient()} 
     * method.
     * @return A Mocker which does not carry out strict stub checking
     */
    public Mocker lenient() {
        return new LinientMocker(testConfig);
    }
    
    protected <T extends Object> OngoingStubbing<T> when(T methodCall) {
        return Mockito.when(methodCall);
    }
}
/**
 * 
    
    public ResponseBuilder mockBuildResponse4args(ResponseBuilder resBuilder, 
            String endpoint, Object val, int code, boolean err) {
        when(resBuilder.buildErrorResponse(any(Object.class))).thenCallRealMethod();
        when(resBuilder.buildErrorResponse(any(Throwable.class))).thenCallRealMethod();
        when(resBuilder.buildErrorResponse(any(Object.class), any(Throwable.class))).thenCallRealMethod();
//        this._mockBuildResponse4args(resBuilder, endpoint, val, code, err);
        when(resBuilder.buildResponse(CoreMatchers.any(Object.class), endpoint, val, err))
                .thenAnswer((InvocationOnMock invoc) -> {
            final ResponseImpl res = new ResponseImpl();
            res.setCode(code);
            res.setData(Collections.singletonMap(endpoint, val));
            res.setMessage(invoc.getArgument(0, Object.class).toString());
            res.setSuccess(!err);
            return res;
        });
        when(resBuilder.buildSuccessResponse()).thenCallRealMethod();
//        this._mockBuildResponse3args(resBuilder, code, err);
//        when(resBuilder.buildResponse(CoreMatchers.any(Object.class), CoreMatchers.any(Object.class), err))
//                .thenAnswer((InvocationOnMock invoc) -> {
//            final ResponseImpl res = new ResponseImpl();
//            res.setCode(code);
//            res.setMessage(invoc.getArgument(0, Object.class).toString());
//            res.setData(invoc.getArgument(1, Object.class));
//            res.setSuccess(!err);
//            return res;
//        });
        return resBuilder;
    }

    private ResponseBuilder _mockBuildResponse4args(ResponseBuilder resBuilder, 
            String endpoint, Object val, int code, boolean err) {
        when(resBuilder.buildResponse(CoreMatchers.any(Object.class), endpoint, val, err))
                .thenAnswer((InvocationOnMock invoc) -> {
            final ResponseImpl res = new ResponseImpl();
            res.setCode(code);
            res.setData(Collections.singletonMap(endpoint, val));
            res.setMessage(invoc.getArgument(0, Object.class).toString());
            res.setSuccess(!err);
            return res;
        });
        return resBuilder;
    }

    public ResponseBuilder mockBuildResponse3args(ResponseBuilder resBuilder, int code, boolean err) {
        when(resBuilder.buildErrorResponse(any(Object.class))).thenCallRealMethod();
        when(resBuilder.buildErrorResponse(any(Throwable.class))).thenCallRealMethod();
        when(resBuilder.buildErrorResponse(any(Object.class), any(Throwable.class))).thenCallRealMethod();
        when(resBuilder.buildResponse(any(Object.class), any(String.class), any(Object.class), any(boolean.class))).thenCallRealMethod();
        when(resBuilder.buildSuccessResponse()).thenCallRealMethod();
        this._mockBuildResponse3args(resBuilder, code, err);
        return resBuilder;
    }
    
    private ResponseBuilder _mockBuildResponse3args(ResponseBuilder resBuilder, int code, boolean err) {
        when(resBuilder.buildResponse(CoreMatchers.any(Object.class), CoreMatchers.any(Object.class), err))
                .thenAnswer((InvocationOnMock invoc) -> {
            final ResponseImpl res = new ResponseImpl();
            res.setCode(code);
            res.setMessage(invoc.getArgument(0, Object.class).toString());
            res.setData(invoc.getArgument(1, Object.class));
            res.setSuccess(!err);
            return res;
        });
        return resBuilder;
    }
 * 
 */