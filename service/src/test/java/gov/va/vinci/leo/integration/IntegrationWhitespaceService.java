package gov.va.vinci.leo.integration;

/*
 * #%L
 * Leo Service
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

import gov.va.vinci.leo.Service;
import gov.va.vinci.leo.ae.ExampleWhitespaceTokenizer;
import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.types.TypeLibrarian;

/**
 * User: Thomas Ginter
 * Date: 11/4/13
 * Time: 3:46 PM
 */
public class IntegrationWhitespaceService {
    protected static boolean GENERATE_TYPES=false;

    protected static boolean REGISTER_WITH_JAM=false;

    protected static String REMOTE_SERVICE_QUEUE_NAME="WhitespaceRemoteService";

    protected static String BROKER_URL="tcp://localhost:61616";

    public void run(boolean generateTypes, boolean registerWithJam) throws Exception {
        //Deploy Remote Service
        Service remoteService = new Service();
        remoteService.setServiceName(REMOTE_SERVICE_QUEUE_NAME);
        remoteService.setEndpoint(REMOTE_SERVICE_QUEUE_NAME);
        remoteService.setBrokerURL(BROKER_URL);
        remoteService.deploy(createExampleWhitespacePipeline(generateTypes));

        System.out.println("WhitespaceRemoteService Deployment: " + remoteService.getDeploymentDescriptorFile());
        System.out.println("WhitespaceRemoteService Aggregate: " + remoteService.getAggregateDescriptorFile());
        System.out.println("WhitespaceRemoteService running, press enter in this console to continue.");
        System.in.read();
        System.exit(0);
    }

    public static LeoAEDescriptor createExampleWhitespacePipeline(boolean generateTypes) throws Exception {
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        aggregate.addDelegate(((LeoAEDescriptor) new ExampleWhitespaceTokenizer().getDescriptor())
                .setTypeSystemDescription(createTypeSystem(generateTypes))
                .setParameterSetting(ExampleWhitespaceTokenizer.Param.TOKEN_OUTPUT_TYPE.getName(), ExampleWhitespaceTokenizer.TOKEN_OUTPUT_TYPE_NAME)
                .setParameterSetting(ExampleWhitespaceTokenizer.Param.TOKEN_OUTPUT_TYPE_FEATURE.getName(), ExampleWhitespaceTokenizer.TOKEN_OUTPUT_TYPE_FEATURE_NAME)
                .setParameterSetting(ExampleWhitespaceTokenizer.Param.WORD_OUTPUT_TYPE.getName(), ExampleWhitespaceTokenizer.WORD_OUTPUT_TYPE_NAME));
        return aggregate;
    }

    public static LeoTypeSystemDescription createTypeSystem(boolean generateTypes) throws Exception {
        LeoTypeSystemDescription ftsd = new ExampleWhitespaceTokenizer().getLeoTypeSystemDescription();
        ftsd.addTypeSystemDescription(new LeoTypeSystemDescription(TypeLibrarian.getCSITypeSystemDescription()));
        if(generateTypes)
            ftsd.jCasGen("src/test/java", "target/classes");
        return ftsd;
    }

    public static void main(String[] args) {
        try {
            new IntegrationWhitespaceService().run(GENERATE_TYPES, REGISTER_WITH_JAM);
        } catch(Exception e1) {
            e1.printStackTrace();
        }
    }
}
