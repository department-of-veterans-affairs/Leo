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

/**
 * User: Thomas Ginter
 * Date: 12/6/13
 * Time: 1:49 PM
 */
public class IntegrationHeartService {
    protected static boolean GENERATE_TYPES = true;
    //Static service broker parameters
    public static final String SERVICE_QUEUE_NAME = "IntegrationHeartService";
    public static final String BROKER_URL = "tcp://localhost:61616";

    //Type system constants
    public static final String AU_ANNOTATION_TYPE = "gov.va.vinci.leo.types.AuHearts";
    public static final String AU_NEG_ANNOTATION_TYPE = "gov.va.vinci.leo.types.AuNegHearts";
    public static final String TOOL_ANNOTATION_TYPE = "gov.va.vinci.leo.types.ToolHearts";
    /**
    public void run(boolean generateTypes) throws Exception {
        //Deploy Remote Service
        Service heartService = new Service();
        heartService.setServiceName(SERVICE_QUEUE_NAME);
        heartService.setEndpoint(SERVICE_QUEUE_NAME);
        heartService.setBrokerURL(BROKER_URL);
        heartService.setDescriptorDirectory("service/src/test/resources");

        heartService.deploy(createHeartServicePipeline(generateTypes));

        System.out.println("HeartService Deployment: " + heartService.getDeploymentDescriptorFile());
        System.out.println("HeartService Aggregate: " + heartService.getAggregateDescriptorFile());
        System.out.println("HeartService running, press enter in this console to continue.");
        System.in.read();
        System.exit(0);
    }

    public static LeoAEDescriptor createHeartServicePipeline(boolean generateTypes) throws Exception {
        LeoTypeSystemDescription ltsd = createTypeSystem(generateTypes);
        LeoAEDescriptor aggregate = new LeoAEDescriptor();
        aggregate.addDelegate(new RegexAnnotator().getLeoAEDescriptor()
                        .setParameterSetting(RegexAnnotator.Param.OUTPUT_TYPE.getName(), AU_ANNOTATION_TYPE)
                        .setParameterSetting(RegexAnnotator.Param.RESOURCE.getName(), "core/src/test/resources/auHeart.regex")
                        .setParameterSetting(RegexAnnotator.Param.CASE_SENSITIVE.getName(), false)
                        .setParameterSetting(RegexAnnotator.Param.WORD_BOUNDARY.getName(), false)
                        .addTypeSystemDescription(ltsd))
                .addDelegate(new RegexAnnotator().getLeoAEDescriptor()
                        .setParameterSetting(RegexAnnotator.Param.OUTPUT_TYPE.getName(), AU_NEG_ANNOTATION_TYPE)
                        .setParameterSetting(RegexAnnotator.Param.RESOURCE.getName(), "core/src/test/resources/auHeartNeg.regex")
                        .setParameterSetting(RegexAnnotator.Param.CASE_SENSITIVE.getName(), false)
                        .setParameterSetting(RegexAnnotator.Param.WORD_BOUNDARY.getName(), false)
                        .addTypeSystemDescription(ltsd))
                .addDelegate(new RegexAnnotator().getLeoAEDescriptor()
                        .setParameterSetting(RegexAnnotator.Param.OUTPUT_TYPE.getName(), TOOL_ANNOTATION_TYPE)
                        .setParameterSetting(RegexAnnotator.Param.RESOURCE.getName(), "core/src/test/resources/toolHeart.regex")
                        .setParameterSetting(RegexAnnotator.Param.CASE_SENSITIVE.getName(), false)
                        .setParameterSetting(RegexAnnotator.Param.WORD_BOUNDARY.getName(), false)
                        .addTypeSystemDescription(ltsd));
        return aggregate;
    }

    public static LeoTypeSystemDescription createTypeSystem(boolean generateTypes) throws Exception {
        LeoTypeSystemDescription ltsd = new LeoTypeSystemDescription();
        ltsd.addType(AU_ANNOTATION_TYPE, "My Golden Heart Annotation", "uima.tcas.Annotation")
            .addType(AU_NEG_ANNOTATION_TYPE, "My Non-matching Golden Heart Annotation", "uima.tcas.Annotation")
            .addType(TOOL_ANNOTATION_TYPE, "My Tool Heart Annotation", "uima.tcas.Annotation");
        if(generateTypes)
            ltsd.jCasGen("service/src/test/java", "service/target/classes");
        return ltsd;
    }

    public static void main(String[] args) {
        try {
            new IntegrationHeartService().run(GENERATE_TYPES);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     **/
}
