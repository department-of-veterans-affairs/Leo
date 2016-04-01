/**
 * SampleService.java
 *
 * @author thomasginter
 */
package gov.va.vinci.leo;

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

import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import gov.va.vinci.leo.types.TypeLibrarian;
import gov.va.vinci.leo.whitespace.ae.WhitespaceTokenizer;
import gov.va.vinci.leo.whitespace.types.Token;
import gov.va.vinci.leo.whitespace.types.WordToken;

/**
 * Test code that creates services that can be used for SystemIntegration Testing.
 * Not to be used as jUnit tests.
 *
 * @author thomasginter
 */
public class SampleService {

    /**
     * Provide a simple service definition for testing.
     *
     * @return Aggregate LeoAEDescriptor object
     * @throws Exception
     */
    public static LeoAEDescriptor simpleServiceDefinition() throws Exception {
        return simpleServiceDefinition(null);
    }

    public static LeoAEDescriptor simpleServiceDefinition(LeoTypeSystemDescription typeSystemDescriptionToAdd) throws Exception {
        LeoTypeSystemDescription typeSystem = getTypeSystemDescription(typeSystemDescriptionToAdd);
        LeoAEDescriptor ae = new WhitespaceTokenizer().getLeoAEDescriptor()
                .setParameterSetting(WhitespaceTokenizer.Param.TOKEN_OUTPUT_TYPE.getName(), Token.class.getCanonicalName())
                .setParameterSetting(WhitespaceTokenizer.Param.WORD_OUTPUT_TYPE.getName(), WordToken.class.getCanonicalName())
                .setParameterSetting(WhitespaceTokenizer.Param.TOKEN_OUTPUT_TYPE_FEATURE.getName(), "TokenType")
                .setTypeSystemDescription(typeSystem);
        ae.addType(TypeLibrarian.getCSITypeSystemDescription());
        ae.setIsAsync(true);
        return ae;
    }

    public static LeoTypeSystemDescription getTypeSystemDescription(LeoTypeSystemDescription typeSystemDescriptionToAdd) {
        if(typeSystemDescriptionToAdd == null)
            return getTypeSystemDescription();
        return getTypeSystemDescription().addTypeSystemDescription(typeSystemDescriptionToAdd);
    }

    public static LeoTypeSystemDescription getTypeSystemDescription() {
        return new WhitespaceTokenizer().getLeoTypeSystemDescription().addType(TypeLibrarian.getCSITypeSystemDescription());
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Service s = new Service();
            s.deploy(SampleService.simpleServiceDefinition());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//main method

}//SampleService class
