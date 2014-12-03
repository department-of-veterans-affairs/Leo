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
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;

public class ExampleAnnotator extends LeoBaseAnnotator {
    @LeoAnnotatorParameter(description="My Param")
    protected String myParam = null;
    @LeoAnnotatorParameter(description="My Required Param",mandatory=true)
    protected String myParamRequired = null;

    public ExampleAnnotator() {
        /** Default constructor for UIMA initialization **/
    }

    public ExampleAnnotator(String myParam, String myParamRequired) {
        this.myParam = myParam;
        this.myParamRequired = myParamRequired;
    }

    @Override
    public void process(JCas arg0) throws AnalysisEngineProcessException {
        // TODO Auto-generated method stub
    }

    @Override
    public LeoTypeSystemDescription getLeoTypeSystemDescription() {
        return new LeoTypeSystemDescription();
    }

}
