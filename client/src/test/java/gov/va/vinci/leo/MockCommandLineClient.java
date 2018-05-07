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

import org.kohsuke.args4j.CmdLineException;

import java.io.File;

/**
 * Created by thomasginter on 3/31/16.
 */
public class MockCommandLineClient extends CommandLineClient {

    /**
     * Default constructor.
     */
    public MockCommandLineClient(String[] args) {
        super(args);
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
