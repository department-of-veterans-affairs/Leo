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
import gov.va.vinci.leo.descriptors.LeoRemoteAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.regex.ae.RegexAnnotator;

public class IntegrationRemoteService {
    protected static boolean GENERATE_TYPES=false;

    protected static String SERVICE_QUEUE_NAME="RemoteServiceIntegrationQueue";

    protected static String REMOTE_SERVICE_QUEUE_NAME="WhitespaceRemoteService";

    protected static String BROKER_URL="tcp://localhost:61616";

    protected static String TEST_REGEX_OUTPUT_TYPE = "gov.va.vinci.leo.types.TR";

    public void run(boolean generateTypes) throws Exception {
        Service localService = new Service();
        localService.setBrokerURL(BROKER_URL);
        localService.setServiceName(SERVICE_QUEUE_NAME);
        localService.setInputQueueName(SERVICE_QUEUE_NAME);
        localService.deploy(createRemotePipelineConstructorOnly(generateTypes));
        //localService.deploy(createRemotePipeline(generateTypes));
        //localService.setDeploymentDescriptorFile("src/test/resources/desc/gov/va/vinci/leo/RemoteDeployment.xml");
        //localService.deploy();

        //Keep server running
        System.out.println("Deployment Descriptor: " + localService.getDeploymentDescriptorFile());
        System.out.println("Aggregate Descriptor: " + localService.getAggregateDescriptorFile());
        System.out.println("Remote delegate service running, press enter in this console to stop.");
        System.in.read();
        System.exit(0);
    }

    protected LeoAEDescriptor createRemotePipeline(boolean generateTypes) throws Exception {
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        LeoTypeSystemDescription lstd = createTypeSystem(generateTypes);
        aggregate.addDelegate(new LeoRemoteAEDescriptor()
                        .setName("RemoteWhitespaceService")
                        .addParameterSetting(LeoRemoteAEDescriptor.Param.BROKER_URL.getName(), BROKER_URL)
                        .addParameterSetting(LeoRemoteAEDescriptor.Param.ENDPOINT.getName(), REMOTE_SERVICE_QUEUE_NAME)
                        .addParameterSetting(LeoRemoteAEDescriptor.Param.TIMEOUT.getName(), "5000")
                        .addParameterSetting(LeoRemoteAEDescriptor.Param.GET_META_TIMEOUT.getName(), "5000")
                        .addParameterSetting(LeoRemoteAEDescriptor.Param.CPC_TIMEOUT.getName(), "5000")
                )
                .addDelegate(new RegexAnnotator().getLeoAEDescriptor()
                        .addParameterSetting(RegexAnnotator.Param.INPUT_TYPE.getName(), true, true, "String", new String[]{ExampleWhitespaceTokenizer.WORD_OUTPUT_TYPE_NAME})
                        .setParameterSetting(RegexAnnotator.Param.OUTPUT_TYPE.getName(), TEST_REGEX_OUTPUT_TYPE)
                        .setParameterSetting(RegexAnnotator.Param.RESOURCE.getName(), "src/test/resources/t.regex")
                        .setParameterSetting(RegexAnnotator.Param.CASE_SENSITIVE.getName(), false)
                        .setParameterSetting(RegexAnnotator.Param.WORD_BOUNDARY.getName(), false)
                        .addTypeSystemDescription(lstd)
                );
        return aggregate;
    }

    protected LeoAEDescriptor createRemotePipelineConstructorOnly(boolean generateTypes) throws Exception {
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        LeoTypeSystemDescription lstd = createTypeSystem(generateTypes);
        aggregate.addDelegate(new LeoRemoteAEDescriptor(BROKER_URL, REMOTE_SERVICE_QUEUE_NAME)
                 .setName("RemoteWhitespaceService"))
                 .addDelegate(new RegexAnnotator().getLeoAEDescriptor()
                        .addParameterSetting(RegexAnnotator.Param.INPUT_TYPE.getName(), true, true, "String", new String[]{ExampleWhitespaceTokenizer.WORD_OUTPUT_TYPE_NAME})
                        .setParameterSetting(RegexAnnotator.Param.OUTPUT_TYPE.getName(), TEST_REGEX_OUTPUT_TYPE)
                        .setParameterSetting(RegexAnnotator.Param.RESOURCE.getName(), "src/test/resources/t.regex")
                        .setParameterSetting(RegexAnnotator.Param.CASE_SENSITIVE.getName(), false)
                        .setParameterSetting(RegexAnnotator.Param.WORD_BOUNDARY.getName(), false)
                        .addTypeSystemDescription(lstd));
        return aggregate;
    }

    protected LeoTypeSystemDescription createTypeSystem(boolean generateTypes) throws Exception {
        LeoTypeSystemDescription ftsd = new LeoTypeSystemDescription();
        ftsd.addType(TEST_REGEX_OUTPUT_TYPE, "", "uima.tcas.Annotation")
            .addType(ExampleWhitespaceTokenizer.WORD_OUTPUT_TYPE_NAME, "", "uima.tcas.Annotation");
        if(generateTypes)
            ftsd.jCasGen("src/test/java", "target/classes");
        return ftsd;
    }

    public static void main(String[] args) {
        try {
            new IntegrationRemoteService().run(GENERATE_TYPES);
        } catch(Exception e1) {
            e1.printStackTrace();
        }
    }

}