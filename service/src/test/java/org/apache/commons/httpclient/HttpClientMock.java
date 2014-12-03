package org.apache.commons.httpclient;

/*
 * #%L
 * Leo
 * %%
 * Copyright (C) 2010 - 2014 Department of Veterans Affairs
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */


import java.io.ByteArrayInputStream;

public class HttpClientMock extends HttpClient {
    private int expectedResponseStatus;
    private String expectedResponseBody;
    private HttpMethod lastMethod;

    public HttpClientMock(int responseStatus, String responseBody) {
        this.expectedResponseStatus = responseStatus;
        this.expectedResponseBody = responseBody;
    }

    @Override
    public int executeMethod(HttpMethod method) {
        lastMethod = method;

        try {
            ((HttpMethodBase) method).setResponseStream(
                    new ByteArrayInputStream(expectedResponseBody.getBytes("UTF-8")));
            return expectedResponseStatus;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public HttpMethod getLastMethodFromExecuteMethod() {
        return lastMethod;
    }
}
