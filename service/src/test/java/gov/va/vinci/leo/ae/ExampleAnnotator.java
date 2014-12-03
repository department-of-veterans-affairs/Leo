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
import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.metadata.ConfigurationParameter;

import java.util.ArrayList;
import java.util.List;

public class ExampleAnnotator extends LeoBaseAnnotator {

    protected String myParam = null;

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

    public static class Param extends LeoBaseAnnotator.Param {
        public static ConfigurationParameterImpl MY_PARAM
                = new ConfigurationParameterImpl("myParam", "myParam", ConfigurationParameter.TYPE_STRING, false, false, new String[]{});
        public static ConfigurationParameter MY_PARAM_REQUIRED
                =  new ConfigurationParameterImpl("myParamRequired", "myParamRequired", ConfigurationParameter.TYPE_STRING, true, false, new String[]{});

        public static ConfigurationParameter[] values() {
            List<ConfigurationParameter> results = new ArrayList<ConfigurationParameter>();
            results.add(MY_PARAM);
            results.add(MY_PARAM_REQUIRED);
            return results.toArray(new ConfigurationParameter[results.size()]);
        }

    }

}
