package gov.va.vinci.leo;

/*
 * #%L
 * Leo Client
 * %%
 * Copyright (C) 2010 - 2017 Department of Veterans Affairs
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

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by thomasginter on 3/31/16.
 */
public class CommandLineClientTest {

    String rootDirectory = "";

    @Before
    public void setTestString() throws IOException {
        String path = new File(".").getCanonicalPath();
        if (!path.endsWith("client")) {
            rootDirectory = "client/";
        }
    }

    @Test
    public void testRunClient() throws Exception {
        //Setup the config files and initialize the client using settings constructor
        File clientConfigFile = new File(rootDirectory + "src/test/resources/conf/ClientConfig.groovy");
        File readerConfigFile = new File(rootDirectory + "src/test/resources/conf/readers/RandomStringCollectionReader.groovy");
        File listenerConfigFile = new File(rootDirectory + "src/test/resources/conf/listeners/DoNothingListenerConfig.groovy");
        MockCommandLineClient mockCLClient = new MockCommandLineClient(
                clientConfigFile,
                readerConfigFile,
                new File[] { listenerConfigFile }
        );
        //Make sure this client was initialized
        assertNotNull(mockCLClient);
        //Check the file settings
        assertEquals(clientConfigFile.getAbsolutePath(), mockCLClient.clientConfigFile[0].getAbsolutePath());
        assertEquals(readerConfigFile.getAbsolutePath(), mockCLClient.readerConfigFile[0].getAbsolutePath());
        assertEquals(listenerConfigFile.getAbsolutePath(), mockCLClient.listenerConfigFileList[0].getAbsolutePath());
        //Run the client
        mockCLClient.runClient();

        //Empty Constructor
        mockCLClient = new MockCommandLineClient(new String[] {});
        assertNotNull(mockCLClient);
        mockCLClient.clientConfigFile = new File[] {clientConfigFile};
        mockCLClient.readerConfigFile = new File[] {readerConfigFile};
        mockCLClient.listenerConfigFileList = new File[] { listenerConfigFile };
        mockCLClient.runClient();
    }

    @Test
    public void testSetClientProperties() throws IllegalAccessException, InvocationTargetException, MalformedURLException, NoSuchMethodException {
        CommandLineClient commandLineClient = new CommandLineClient(new String[] {"-clientConfigFile", "src/test/resources/conf/ClientConfig.groovy", "-readerConfigFile", "src/test/resources/ReaderConfig.groovy", "-listenerConfigFile", "src/test/resources/ListenerConfig.groovy"});
        Client leoClient = new Client();
        commandLineClient.setClientProperties(leoClient);
        assertEquals("TestEndpoint", leoClient.getEndpoint());


    }
}
