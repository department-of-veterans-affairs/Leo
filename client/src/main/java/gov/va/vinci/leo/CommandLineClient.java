package gov.va.vinci.leo;

/*
 * #%L
 * Leo Examples
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

import gov.va.vinci.leo.cr.BaseLeoCollectionReader;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * An example UIMA AS Client that takes command line arguments for its configuration.
 */
public class CommandLineClient {

    /**
     * Groovy config file for the client parameters such as the brokerURL.
     */
    //new File("config/ClientConfig.groovy")
    @Option(name="-clientConfigFile",usage="The groovy config file that defines the client properties. (only ONE allowed).", required=true)
    File[] clientConfigFile;

    /**
     * Groovy config file for a CollectionReader.
     */
    //new File("config/readers/FileCollectionReaderExample.groovyample.groovy")
    @Option(name="-readerConfigFile",usage="The groovy config file that defines the reader (only ONE readerConfigFile allowed).", required=true)
    File[] readerConfigFile;

    /**
     * Groovy config files for one or more listeners.
     */
    // new File("config/listeners/SimpleXmiListenerener.groovy"),
    // new File("config/listeners/SimpleCsvListenerExample.groovye.groovy")
    @Option(name="-listenerConfigFile",usage="The groovy config file that defines the reader (one required, but can specify more than one).", required=true)
    File[] listenerConfigFileList;

    /**
     * Leo Client what will be configured and run using the config files provided.
     */
    Client myClient = new Client();

    /**
     * Default constructor.
     */
    public CommandLineClient() {

    }

    /**
     * Initialize the client using groovy config files for the client, reader, and one or more listeners.
     *
     * @param clientConfigFile client groovy config file.
     * @param readerConfig reader groovy config file.
     * @param listenerConfigFiles array of groovy config files for one or more listeners.
     */
    public CommandLineClient(File clientConfigFile, File readerConfig, File[] listenerConfigFiles) {
        readerConfigFile = new File[] { readerConfig};
        listenerConfigFileList = listenerConfigFiles;
        this.clientConfigFile = new File[] {clientConfigFile};
    }

    /**
     * Parse the groovy config files, and return the listener objects that are defined in them.
     *
     * @param configs   the groovy config files to slurp
     * @return   the list of listeners that are defined in the groovy configs.
     * @throws MalformedURLException if the configuration file url that was set is invalid.
     */
    public static List<UimaAsBaseCallbackListener> getListeners(File...configs) throws MalformedURLException {
        ConfigSlurper configSlurper = new ConfigSlurper();
        List<UimaAsBaseCallbackListener> listeners = new ArrayList<UimaAsBaseCallbackListener>();

        for (File config: configs) {
            ConfigObject configObject = configSlurper.parse(config.toURI().toURL());
            if (configObject.get("listener") != null ) {
                listeners.add((UimaAsBaseCallbackListener) configObject.get("listener"));
            }
        }
        return listeners;
    }

    /**
     * Parse the groovy config file, and return the reader object. This must be a BaseLeoCollectionReader.
     *
     * @param config   the groovy config file to slurp
     * @return   the reader defined in the groovy config.
     * @throws MalformedURLException if the configuration file url that was set is invalid.
     */
    public static BaseLeoCollectionReader getReader(File config) throws MalformedURLException {
        ConfigSlurper configSlurper = new ConfigSlurper();

        ConfigObject configObject = configSlurper.parse(config.toURI().toURL());
        if (configObject.get("reader") != null ) {
            return (BaseLeoCollectionReader) configObject.get("reader");
        }
      return null;
    }

    /**
     * Set the client properties, such as brokerURL, from a pre-loaded groovy config file.
     *
     * @param leoClient Client object to configure.
     * @return Reference to the configured Client.
     * @throws MalformedURLException if the configuration file url that was set is invalid.
     * @throws InvocationTargetException if the groovy config refers to objects not in the classpath.
     * @throws IllegalAccessException if there is a rights issue accessing settings from the groovy config.
     */
    protected Client setClientProperties(Client leoClient) throws MalformedURLException, InvocationTargetException, IllegalAccessException {
        if (clientConfigFile.length != 1) {
            return leoClient;
        }

        ConfigSlurper configSlurper = new ConfigSlurper();
        ConfigObject o = configSlurper.parse(clientConfigFile[0].toURI().toURL());

        Set<Map.Entry> entries = o.entrySet();
        for (Map.Entry e : entries) {
            System.out.println("Setting property " + e.getKey() + " on client to " + e.getValue() + ".");
            BeanUtils.setProperty(leoClient, e.getKey().toString(), e.getValue());
        }

        return leoClient;
    }

    /**
     * Actual run method that configures and runs the client.
     */
    public void runClient() {
        try {

            if (readerConfigFile.length > 1 || clientConfigFile.length > 1) {
                printUsage();
                return;
            }
            /**
             * These point to whichever readers/listeners configurations are needed in this particular client.
             * There can be many listeners, but only one reader.
             */
            List<UimaAsBaseCallbackListener> listeners = getListeners(listenerConfigFileList);
            BaseLeoCollectionReader reader = getReader(readerConfigFile[0]);


            /**
             * Configure the client.
             */
            setClientProperties(myClient);


            System.out.println("Broker URL: " + myClient.getBrokerURL() + "    Endpoint name: " + myClient.getEndpoint());

            /**
             * Process Documents
             */
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            /**
             * Add the listeners from the groovy config.
             */
            for (UimaAsBaseCallbackListener listener: listeners) {
                myClient.addUABListener(listener);
            }

            /**
             * Run the client with the collection reader.
             */
            myClient.run(reader);

            System.out.println("Client finished in: " + stopWatch.getTime() + "ms.");
            //launchViewer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Main method for java execution.
     *
     * @param args list of String arguments for the program.
     * @throws org.kohsuke.args4j.CmdLineException if there is an error parsing the command line arguments.
     */
    public static void main(String[] args) throws CmdLineException {
        CommandLineClient bean = new CommandLineClient();
        CmdLineParser parser = new CmdLineParser(bean);
        try {
            parser.parseArgument(args);
        } catch (CmdLineException e) {
            printUsage();
        }
        bean.runClient();
    }

    /**
     * Print the command line usage statement to the console and exit.
     */
    public static void printUsage() {
        CmdLineParser parser = new CmdLineParser(new CommandLineClient());
        System.out.print("Usage: java " + CommandLineClient.class.getCanonicalName());
        parser.printSingleLineUsage(System.out);
        System.out.println();
        parser.printUsage(System.out);
        System.exit(1);
    }
}
