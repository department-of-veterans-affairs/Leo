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

import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.descriptors.TypeDescriptionBuilder;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

public class Example2Annotator extends LeoBaseAnnotator
{

    public Example2Annotator() {
        super();
    }

    /**
     * Constructor setting the initial values for the inputTypes and outputType parameters.
     *
     * @param outputType Annotation type to be used as output for this annotator
     * @param inputTypes One or more annotation types on which processing will occur
     */
    public Example2Annotator(String outputType, String... inputTypes) {
        super(outputType, inputTypes);
    }

    @Override
    public void process(JCas arg0) throws AnalysisEngineProcessException {
        System.out.println("In " + this.getClass().getCanonicalName());
        this.addOutputAnnotation("gov.va.vinci.leo.test.MyType", arg0, 3,3);
        this.addOutputAnnotation("gov.va.vinci.leo.test.MyType", arg0, 2,2);
        this.addOutputAnnotation("gov.va.vinci.leo.test.MyType", arg0, 1,1);
        this.addOutputAnnotation("gov.va.vinci.leo.test.MyType", arg0, 5,5);
    }

    @Override
    public LeoTypeSystemDescription getLeoTypeSystemDescription() {
        LeoTypeSystemDescription typeSystemDescription = new LeoTypeSystemDescription();

        typeSystemDescription.addType(TypeDescriptionBuilder.create("gov.va.vinci.leo.test.MyType", "My Test Type", "uima.tcas.Annotation")
                .addFeature("Pattern", "Regex Pattern that matched", "uima.cas.String")
                .getTypeDescription());
        return typeSystemDescription;
    }

}

