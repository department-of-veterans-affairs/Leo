package gov.va.vinci.leo.types;

/*
 * #%L
 * Leo Core
 * %%
 * Copyright (C) 2010 - 2017 Department of Veterans Affairs
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

import org.junit.Test;
import org.mockito.Mockito;

/**
 * Created by thomasginter on 4/8/16.
 */
public class CSITypeTest {

    @Test
    public void testCSIProperties() throws Exception {
        /**
        AnalysisEngine ae = UIMAFramework.produceAnalysisEngine(
                new LeoAEDescriptor("desc.gov.va.vinci.leo.ae.RemoteExampleWhitespaceTokenizerDescriptor", true)
                    .addType(TypeLibrarian.getCSITypeSystemDescription())
                    .getAnalysisEngineDescription()
        );
        assertNotNull(ae);
         **/

        //Setup the CAS
//        JCas jcas = Mockito.mock(JCas.class);
//        assertNotNull(jcas);
        String docText = "hello world";

        //Create the CSI annotation
        CSI csi = Mockito.mock(CSI.class);

    }
}
