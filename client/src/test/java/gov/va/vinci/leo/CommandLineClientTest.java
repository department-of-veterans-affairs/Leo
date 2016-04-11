package gov.va.vinci.leo;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

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
        mockCLClient = new MockCommandLineClient();
        assertNotNull(mockCLClient);
        mockCLClient.clientConfigFile = new File[] {clientConfigFile};
        mockCLClient.readerConfigFile = new File[] {readerConfigFile};
        mockCLClient.listenerConfigFileList = new File[] { listenerConfigFile };
        mockCLClient.runClient();
    }
}
