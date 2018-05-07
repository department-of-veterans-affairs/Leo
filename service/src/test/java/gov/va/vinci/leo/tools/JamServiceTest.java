package gov.va.vinci.leo.tools;

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

import com.sun.tools.attach.AgentInitializationException;
import com.sun.tools.attach.AgentLoadException;
import com.sun.tools.attach.AttachNotSupportedException;
import org.apache.commons.httpclient.HttpClientMock;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;

import static org.junit.Assert.*;

public class JamServiceTest {

    final String CREATE_JMX_SERVER_XML_RESULTS = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\t<jmxServer>\t\t<queueName>myQueue</queueName>\t\t<brokerUrl>broker://url</brokerUrl>\t\t<host>localhost</host>\t\t<port>100</port></jmxServer>";

    @Test
    public void constructorTest() {
        JamService jamService = new JamService("http://localhost/jam");
        assertTrue(jamService.getJamServerBaseUrl().equals("http://localhost/jam/"));
        jamService = new JamService("http://localhost/jam/");
        assertTrue(jamService.getJamServerBaseUrl().equals("http://localhost/jam/"));

        assertEquals(jamService.createJmxServerXml("myQueue", "broker://url", "localhost", 100), CREATE_JMX_SERVER_XML_RESULTS);
    }

    @Test
    public void doHttpCallTest() throws IOException {
        HttpClientMock mockHttpClient = new HttpClientMock(200, "test");
        JamService jamService = new JamService("http://localhost/jam");
        jamService.setClient(mockHttpClient);
        String result = jamService.doHttpCall(new GetMethod(jamService.getJamServerBaseUrl() + "webservice/removeServiceQueue/" + URLEncoder.encode("mydata")));
        assertTrue(result.equals("test"));
    }

    @Test(expected=Exception.class)
    public void doHttpCallExceptionTest() throws IOException {
        HttpClientMock mockHttpClient = new HttpClientMock(100, "test");
        JamService jamService = new JamService("http://localhost/jam");
        jamService.setClient(mockHttpClient);
        String result = jamService.doHttpCall(new GetMethod(jamService.getJamServerBaseUrl() + "webservice/removeServiceQueue/" + URLEncoder.encode("mydata")));

        assertFalse(true);
    }

    @Test
    public void removeServiceQueueTest() throws IOException {
        HttpClientMock mockHttpClient = new HttpClientMock(200, "test");
        JamService jamService = new JamService("http://localhost/jam");
        jamService.setClient(mockHttpClient);
        jamService.removeServiceQueue("MyQueue");
        assertTrue(mockHttpClient.getLastMethodFromExecuteMethod().getURI().toString().equals("http://localhost/jam/webservice/removeServiceQueue/MyQueue"));
    }

    @Test
    public void enableServiceQueueTest() throws IOException {
        HttpClientMock mockHttpClient = new HttpClientMock(200, "test");
        JamService jamService = new JamService("http://localhost/jam");
        jamService.setClient(mockHttpClient);
        jamService.enableServiceQueue("MyQueue");
        assertTrue(mockHttpClient.getLastMethodFromExecuteMethod().getURI().toString().equals("http://localhost/jam/webservice/enableServiceQueue/MyQueue"));
    }

    @Test
    public void disableServiceQueueTest() throws IOException {
        HttpClientMock mockHttpClient = new HttpClientMock(200, "test");
        JamService jamService = new JamService("http://localhost/jam");
        jamService.setClient(mockHttpClient);
        jamService.disableServiceQueue("MyQueue");
        assertTrue(mockHttpClient.getLastMethodFromExecuteMethod().getURI().toString().equals("http://localhost/jam/webservice/disableServiceQueue/MyQueue"));
    }

    @Test
    public void addServiceToQueueTest() throws IOException {
        HttpClientMock mockHttpClient = new HttpClientMock(200, "test");
        JamService jamService = new JamService("http://localhost/jam");
        jamService.setClient(mockHttpClient);
        jamService.addServerToServiceQueue("myQueueName", "tcp://broker", "localhost", 1000);
        assertTrue(mockHttpClient.getLastMethodFromExecuteMethod().getURI().toString().equals("http://localhost/jam/webservice/addServerToServiceQueue"));
    }

    @Test
    public void getRegisterServiceRequestBodyTest() {
        JamService jamService = new JamService("http://localhost/jam");
        assertTrue(jamService.getServiceQueueExistRequestBody("myQueueName", "tcp://broker").equals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><serviceQueue><queueName>myQueueName</queueName><brokerUrl>tcp://broker</brokerUrl></serviceQueue>"));
        assertTrue(jamService.getRegisterServiceRequestBody("myQueueName", "tcp://broker", 100, true, true).equals("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \t<serviceQueue>\t\t<queueName>myQueueName</queueName>\t\t<brokerUrl>tcp://broker</brokerUrl>\t\t<queryIntervalInSeconds>100</queryIntervalInSeconds>\t\t<resetStatisticsAfterQuery>true</resetStatisticsAfterQuery>\t\t<profileEnabled>true</profileEnabled></serviceQueue>"));
    }

    @Test
    public void registerServiceQueueTest() throws IOException {
        HttpClientMock mockHttpClient = new HttpClientMock(200, "test");
        JamService jamService = new JamService("http://localhost/jam");
        jamService.setClient(mockHttpClient);
        jamService.registerServiceQueue("myQueueName", "tcp://broker", 100, false, false);
        assertTrue(mockHttpClient.getLastMethodFromExecuteMethod().getURI().toString().equals("http://localhost/jam/webservice/registerServiceQueue"));
    }


    @Test
    public void doesServiceQueueExistTest() throws IOException {
        HttpClientMock mockHttpClient = new HttpClientMock(200, "test");
        JamService jamService = new JamService("http://localhost/jam");
        jamService.setClient(mockHttpClient);
        assertFalse(jamService.doesServiceQueueExist("myQueueName", "tcp://broker"));
        assertTrue(mockHttpClient.getLastMethodFromExecuteMethod().getURI().toString().equals("http://localhost/jam/webservice/doesServiceQueueExist"));

    }

    @Test
    public void doesServiceQueueExistTrueTest() throws IOException {
        HttpClientMock mockHttpClient = new HttpClientMock(200, "TRUE");
        JamService jamService = new JamService("http://localhost/jam");
        jamService.setClient(mockHttpClient);
        assertTrue(jamService.doesServiceQueueExist("myQueueName", "tcp://broker"));
        assertTrue(mockHttpClient.getLastMethodFromExecuteMethod().getURI().toString().equals("http://localhost/jam/webservice/doesServiceQueueExist"));

    }

    @Test
    public void removeServerFromServiceQueueTest() throws IOException {
        HttpClientMock mockHttpClient = new HttpClientMock(200, "TRUE");
        JamService jamService = new JamService("http://localhost/jam");
        jamService.setClient(mockHttpClient);
        jamService.removeServerFromServiceQueue("myQueueName", "broker://", "localhost", 1000);
        assertTrue(mockHttpClient.getLastMethodFromExecuteMethod().getURI().toString().equals("http://localhost/jam/webservice/removeServerFromServiceQueue"));

    }

    @Test
    public void loadJmxTest() throws IOException, AttachNotSupportedException, AgentLoadException, AgentInitializationException {

        JamService jamService = new JamService("http://localhost/jam");
        int port = jamService.loadJMXAgent(-1);

        assert(port != -1);

    }



}