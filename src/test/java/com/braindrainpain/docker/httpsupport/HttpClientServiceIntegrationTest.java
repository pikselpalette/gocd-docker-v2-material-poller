/*
The MIT License (MIT)

Copyright (c) 2015 Manuel Kasiske

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */
package com.braindrainpain.docker.httpsupport;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * @author Manuel Kasiske
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpClientServiceIntegrationTest extends WebMockTest {

    private HttpClient httpClient;
    private GetMethod getMethod;
    private HttpClientService httpClientService;

    @Before
    public void setUp() {
        super.setUp();
        httpClient = new HttpClient();
        getMethod = new GetMethod();
        httpClientService = new HttpClientService(httpClient, getMethod);
    }

    @Test
    public void testCheckConnectionShouldReturnStatus404() throws IOException {
        try {
            httpClientService.checkConnection("http://localhost:5000/", "foo", "foo");
            fail();
        } catch (RuntimeException e) {}
        assertEquals("404 page not found", getMethod.getResponseBodyAsString());
        assertEquals(404, getMethod.getStatusCode());
    }

    @Test
    public void testCheckConnectionWithTagsUrlShouldReturnFullyJsonResponse() throws IOException {
        httpClientService.checkConnection(DockerApiHttpHandler.DUMMY_TAGS_DOCKER_API_URL, "foo", "foo");
        assertEquals(DockerApiHttpHandler.DUMMY_JSON_SUCCESS_RESPONSE, getMethod.getResponseBodyAsString());
        assertEquals(200, getMethod.getStatusCode());
    }

    @Test
    public void testCheckConnectionWithDefaultUrlShouldReturnEmptyJson() throws IOException {
        httpClientService.checkConnection("http://localhost:5000/v2/", "foo", "foo");
        assertEquals(DockerApiHttpHandler.EMPTY_JSON_RESPONSE, getMethod.getResponseBodyAsString());
        assertEquals(200, getMethod.getStatusCode());
    }

    @Test
    public void testCheckConnectionWithTagUrlAndUnknownServiceShouldReturnErrorJson() throws IOException {
        try{
            httpClientService.checkConnection("http://localhost:5000/v2/abc/tags/list", "foo", "foo");
            fail();
        } catch (RuntimeException e) {}

        assertEquals(DockerApiHttpHandler.DUMMY_ERROR_MESSAGE, getMethod.getResponseBodyAsString());
        assertEquals(404, getMethod.getStatusCode());
    }

}
