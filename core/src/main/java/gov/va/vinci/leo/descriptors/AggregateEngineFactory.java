/**
 *
 */
package gov.va.vinci.leo.descriptors;

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

import gov.va.vinci.leo.tools.LeoUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.Constants;
import org.apache.uima.analysis_engine.AnalysisEngineDescription;
import org.apache.uima.analysis_engine.impl.AnalysisEngineDescription_impl;
import org.apache.uima.analysis_engine.metadata.FixedFlow;
import org.apache.uima.analysis_engine.metadata.FlowControllerDeclaration;
import org.apache.uima.analysis_engine.metadata.impl.FixedFlow_impl;
import org.apache.uima.analysis_engine.metadata.impl.FlowControllerDeclaration_impl;
import org.apache.uima.collection.CasConsumerDescription;
import org.apache.uima.flow.FlowControllerDescription;
import org.apache.uima.resource.*;
import org.apache.uima.resource.metadata.ExternalResourceBinding;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.MetaDataObject;
import org.apache.uima.resource.metadata.ResourceManagerConfiguration;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.apache.uima.resource.metadata.impl.ResourceManagerConfiguration_impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates elements related to UIMA Aggregate Analysis engines.
 *
 * @author thomasginter
 */
public class AggregateEngineFactory {

    /**
     * Create an AnalysisEngineDescriptor that represents and aggregate analysis engine from a name
     * and a list of primitive engines.
     *
     * @param name       Name of the AggregateEngine, preferred to be a unique name though not enforced
     * @param primitives List of primitive engine descriptors to be included in the aggregate
     * @param byName     Flag that indicates whether or not the list of primitives is to be imported by name or by location, true = import by name
     * @return AnalysisEngineDescriptor representing an Aggregate engine
     * @throws Exception thrown if any errors occur
     */
    public static AnalysisEngineDescription createAggregateDescription(String name, List<String> primitives, boolean byName) throws Exception {
        List<ResourceSpecifier> specs = new ArrayList<ResourceSpecifier>();
        for (String p : primitives) {
            if (byName) {
                specs.add(ResourceSpecifierFactory.createResourceSpecifier(LeoUtils.createURL(p)));
            } else {
                specs.add(ResourceSpecifierFactory.createResourceSpecifier(p));
            }//else
        }//for

        return createAggregateDescription(name, specs);
    }//createAggregateDescription method

    /**
     * Create an aggregate descriptor from a list of LeoAEDescriptor objects.
     *
     * @param specs LeoAEDescriptors representing the primitive engines of this aggregate
     * @param name  String representation of the name of the aggregate
     * @return AnalysisEngineDescription representation for the generated aggregate
     */
    public static AnalysisEngineDescription createAggregateDescription(List<LeoDelegate> specs, String name) {
        return createAggregateDescription(specs, null, name);
    }

    /**
     * Create an aggregate descriptor from a list of LeoAEDescriptor objects.
     *
     * @param specs LeoAEDescriptors representing the primitive engines of this aggregate
     * @param flowControllerDescription allows the user to specify a custom flow controller
     * @param name  String representation of the name of the aggregate
     * @return AnalysisEngineDescription representation for the generated aggregate
     */
    public static AnalysisEngineDescription createAggregateDescription(List<LeoDelegate> specs, FlowControllerDescription flowControllerDescription, String name) {
        //Create the aggregate descriptor object and set the name
        AnalysisEngineDescription aed = new AnalysisEngineDescription_impl();
        aed.getAnalysisEngineMetaData().setName(name);
        aed.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
        aed.setPrimitive(false);

        //Variables to track multipleDeployment and component names
        ArrayList<String> compNames = new ArrayList<String>();
        boolean multipleDeploy = true;

        for(LeoDelegate ld : specs) {
            //Add the delegate
            String component = ld.getName();
            Import imprt = new Import_impl();
            imprt.setLocation(ld.getDescriptorLocator());
            aed.getDelegateAnalysisEngineSpecifiersWithImports().put(component, (MetaDataObject)imprt);

            //Add component to the list
            compNames.add(component);
            //Bind external resources for AnalysisEngines and set the multiple deployment flag
            if(ld instanceof LeoAEDescriptor) {
                bindExternalResources(aed, (ResourceCreationSpecifier) ld.getResourceSpecifier());
                multipleDeploy &= ((AnalysisEngineDescription) ld.getResourceSpecifier()).getAnalysisEngineMetaData()
                        .getOperationalProperties().isMultipleDeploymentAllowed();
            }
        }

        //Set the multiple deployment property
        aed.getAnalysisEngineMetaData().getOperationalProperties().setMultipleDeploymentAllowed(multipleDeploy);

        //Set the custom flow controller if provided
        if(flowControllerDescription != null) {
            FlowControllerDeclaration flowControllerDeclaration = new FlowControllerDeclaration_impl();
            flowControllerDeclaration.setSpecifier(flowControllerDescription);
            aed.setFlowControllerDeclaration(flowControllerDeclaration);
        }
        //Set the flow controller constraints, uses fixed flow if no custom flow controller was provided
        FixedFlow fixedFlow = new FixedFlow_impl();
        fixedFlow.setFixedFlow(compNames.toArray(new String[compNames.size()]));
        aed.getAnalysisEngineMetaData().setFlowConstraints(fixedFlow);

        return aed;
    }//createAggregateDescription method

    /**
     * Create an AnalysisEngineDescription from a name and a list of ResourceCreationSpecifiers each one
     * representing a primitive Analysis engine, CasConsumer, or FlowController.
     *
     * @param name  unique name of the AggregateEngine
     * @param specs List of primitive Engine Descriptors to be included in the aggregate
     * @return AnalysisEngineDescription representing an Aggregate engine
     */
    public static AnalysisEngineDescription createAggregateDescription(String name, List<ResourceSpecifier> specs)  {
        //Create the aggregate descriptor object and set the name
        AnalysisEngineDescription aed = new AnalysisEngineDescription_impl();
        aed.getAnalysisEngineMetaData().setName(name);
        aed.setFrameworkImplementation(Constants.JAVA_FRAMEWORK_NAME);
        aed.setPrimitive(false);

        //Additional variables for operational information
        ArrayList<String> compNames = new ArrayList<String>();
        FlowControllerDescription fcd = null;
        boolean multipleDeploy = true;
        Import_impl imprt = null;

        for (ResourceSpecifier spec : specs) {
            if (spec instanceof AnalysisEngineDescription || spec instanceof CasConsumerDescription) {
                //Add as an import
                String component = ((ResourceCreationSpecifier)spec).getMetaData().getName();
                aed.getDelegateAnalysisEngineSpecifiersWithImports().put(component, spec);

                //Add component name to the list
                compNames.add(component);

                //Bind external resources
                bindExternalResources(aed, (ResourceCreationSpecifier)spec);

                //Set the multiple deployment flag
                if (spec instanceof AnalysisEngineDescription) {
                    multipleDeploy &= ((AnalysisEngineDescription) spec).getAnalysisEngineMetaData()
                            .getOperationalProperties().isMultipleDeploymentAllowed();
                } else { //Can't deploy more than one CasConsumer
                    multipleDeploy = false;
                }//else
            } else if (spec instanceof FlowControllerDescription) {
                fcd = (FlowControllerDescription) spec;
            } else if (spec instanceof CustomResourceSpecifier) {
                //Add the delegate as an import
                String component = getComponentName(((CustomResourceSpecifier) spec).getParameters());
                aed.getDelegateAnalysisEngineSpecifiersWithImports().put(component, spec);

                //Add component name to the list
                compNames.add(component);
            }
        }//for

        //Set the multiple deployment property
        aed.getAnalysisEngineMetaData().getOperationalProperties().setMultipleDeploymentAllowed(multipleDeploy);

        //Setup the flow controller
        if (fcd != null) {
            FlowControllerDeclaration flowControllerDecl = new FlowControllerDeclaration_impl();
            flowControllerDecl.setSpecifier(fcd);
            aed.setFlowControllerDeclaration(flowControllerDecl);
        }//if
        //Setup fixed flow FlowController - will only be used if custom flow controller not provided.
        FixedFlow fixedFlow = new FixedFlow_impl();
        fixedFlow.setFixedFlow(compNames.toArray(new String[compNames.size()]));
        aed.getAnalysisEngineMetaData().setFlowConstraints(fixedFlow);

        //Perform full validation on the descriptor before returning
        //Removed validation to support late serialization - TomG 01/31/2012
        //aed.doFullValidation(UIMAFramework.newDefaultResourceManager());
        return aed;
    }//createAggregateDescription method

    /**
     * Bind external resources from the primitive spec to the aggregate AnalysisEngineDescription.
     *
     * @param aed  Aggregate AnalysisEngineDescription to bind resources into
     * @param spec primitive engine spec to bind resources from
     */
    protected static void bindExternalResources(AnalysisEngineDescription aed, ResourceCreationSpecifier spec) {
        ResourceManagerConfiguration armc = aed.getResourceManagerConfiguration();
        if (spec.getResourceManagerConfiguration() == null) return;
        ExternalResourceBinding[] erbs = spec.getResourceManagerConfiguration().getExternalResourceBindings();
        ExternalResourceDescription[] erds = spec.getResourceManagerConfiguration().getExternalResources();
        for (int i = 0; i < erbs.length; i++) {
            //Make sure the RMC for the aggregate is not null
            if (armc == null) {
                armc = new ResourceManagerConfiguration_impl();
                aed.setResourceManagerConfiguration(armc);
            }//if
            armc.addExternalResource(erds[i]);
            armc.addExternalResourceBinding(erbs[i]);
        }//for
    }//bindExternalResources method

    /**
     * Pull the component name from the list of parameters.  If the name is not listed then generate one in the format:
     * leoRemoteDelegate2112{UUID}
     *
     * @param parameters Array of Parameter objects
     * @return component name string
     */
    protected static String getComponentName(Parameter[] parameters) {
        String componentName = null;
        for(Parameter p : parameters) {
            if(p.getName().equalsIgnoreCase("componentName")) {
                componentName = p.getValue();
                break;
            }
        }
        if(StringUtils.isBlank(componentName)) {
            componentName = "leoRemoteDelegate2112" + LeoUtils.getUUID();
        }
        return componentName;
    }

}//AggregateEngineFactory class
