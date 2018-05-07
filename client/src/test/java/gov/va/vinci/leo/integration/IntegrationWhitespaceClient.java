package gov.va.vinci.leo.integration;

/*
 * #%L
 * Leo Client
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

import gov.va.vinci.leo.Client;
import gov.va.vinci.leo.cr.FileCollectionReader;
import gov.va.vinci.leo.listener.SimpleXmiListener;
import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.commons.io.FileUtils;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;

import java.io.File;

/**
 * User: Thomas Ginter
 * Date: 11/5/13
 * Time: 12:48 PM
 */
public class IntegrationWhitespaceClient {
    //Output directory
    public static final File OUT_DIR = new File(FileUtils.getTempDirectory(), LeoUtils.getUUID());
    //Service Data Directories for file input

    protected static String REMOTE_SERVICE_QUEUE_NAME="WhitespaceRemoteService";

    protected static String BROKER_URL="tcp://localhost:61616";

    public void run() throws Exception {

        //Get the right data and service information for the one being executed
        File dataDir = new File("src/test/resources/a/b/c");
        String serviceQueueName = REMOTE_SERVICE_QUEUE_NAME, serviceBrokerURL = BROKER_URL;


        //Init the client and process the data
        FileCollectionReader cr = new FileCollectionReader();
        cr.setRecurseFlag(true);
        cr.setInputDirectory(dataDir);
        cr.setEncoding("utf-8");

        Client myClient = new Client(getListeners());
        myClient.setBrokerURL(serviceBrokerURL);
        myClient.setServiceName(serviceQueueName);
        myClient.setInputQueueName(serviceQueueName);
        myClient.run(cr);

        System.out.println("Finished client processing, press any key to exit.");
        System.in.read();
        System.exit(0);
    }

    protected UimaAsBaseCallbackListener[] getListeners() {
        //Setup the output directory
        if(!OUT_DIR.mkdir())
            throw new RuntimeException("Unable to create output directory: " + OUT_DIR.getAbsolutePath());
        OUT_DIR.deleteOnExit();

        UimaAsBaseCallbackListener[] list = new UimaAsBaseCallbackListener[1];
        list[0] = new SimpleXmiListener(OUT_DIR).setLaunchAnnotationViewer(true) ;
        return list;
    }

    public static void main(String[] args) {
        try {
            new IntegrationWhitespaceClient().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
