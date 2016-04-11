package gov.va.vinci.leo;

import org.kohsuke.args4j.CmdLineException;

import java.io.File;

/**
 * Created by thomasginter on 3/31/16.
 */
public class MockCommandLineClient extends CommandLineClient {

    /**
     * Default constructor.
     */
    public MockCommandLineClient() {
        super();
        myClient = new MockClient();
    }

    /**
     * Initialize the client using groovy config files for the client, reader, and one or more listeners.
     *
     * @param clientConfigFile    client groovy config file.
     * @param readerConfig        reader groovy config file.
     * @param listenerConfigFiles array of groovy config files for one or more listeners.
     */
    public MockCommandLineClient(File clientConfigFile, File readerConfig, File[] listenerConfigFiles) {
        super(clientConfigFile, readerConfig, listenerConfigFiles);
        myClient = new MockClient();
    }

    public static void main(String[] args) throws CmdLineException {
        CommandLineClient.main(args);
    }

    public static void printUsage() {
        CommandLineClient.printUsage();
    }
}
