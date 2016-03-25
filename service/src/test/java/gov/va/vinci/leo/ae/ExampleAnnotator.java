package gov.va.vinci.leo.ae;

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

import gov.va.vinci.leo.descriptors.LeoConfigurationParameter;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.descriptors.TypeDescriptionBuilder;
import gov.va.vinci.leo.types.ExampleType;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

import java.util.HashMap;

public class ExampleAnnotator extends LeoBaseAnnotator {
    @LeoConfigurationParameter(description="My Param")
    protected String myParam = null;
    @LeoConfigurationParameter(description="My Required Param",mandatory=true)
    protected String myParamRequired = null;

    public ExampleAnnotator() {
        /** Default constructor for UIMA initialization **/
    }

    public ExampleAnnotator(String myParam, String myParamRequired) {
        this.myParam = myParam;
        this.myParamRequired = myParamRequired;
    }

    public ExampleAnnotator(String myParam, String myParamRequired, String outputType, String inputType) {
        this.myParam = myParam;
        this.myParamRequired = myParamRequired;
        this.setOutputType(outputType);
        this.setInputTypes(inputType);
    }

    @Override
    public void annotate(JCas arg0) throws AnalysisEngineProcessException {
        String docText = arg0.getDocumentText();
        HashMap<String, Object> featureMap = new HashMap<>(2);
        featureMap.put("numberOfCASesProcessed", new Integer((int) this.getNumberOfCASesProcessed()));
        featureMap.put("numberOfFilteredCASesProcessed", new Integer((int) this.getNumberOfFilteredCASesProcessed()));
        this.addOutputAnnotation(ExampleType.class.getCanonicalName(), arg0, 0, docText.length(), featureMap);
//        ExampleType example = new ExampleType(arg0, 0, docText.length());
//        example.setNumberOfCASesProcessed((int) this.numberOfCASesProcessed);
//        example.setNumberOfFilteredCASesProcessed((int) this.numberOfFilteredCASesProcessed);
//        example.addToIndexes();
    }

    @Override
    public LeoTypeSystemDescription getLeoTypeSystemDescription() {
        return new LeoTypeSystemDescription().addType(
                TypeDescriptionBuilder.create("gov.va.vinci.leo.types.ExampleType", "", "uima.tcas.Annotation")
                        .addFeature("numberOfCASesProcessed", "", "uima.cas.Integer")
                        .addFeature("numberOfFilteredCASesProcessed", "", "uima.cas.Integer")
                        .getTypeDescription()
        );
    }

    public LeoTypeSystemDescription getTypeSystemDescription() {
        return super.getLeoTypeSystemDescription();
    }

}
