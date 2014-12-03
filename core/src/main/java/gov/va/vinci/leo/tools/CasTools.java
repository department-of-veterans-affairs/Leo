package gov.va.vinci.leo.tools;

/*
 * #%L
 * Leo Core
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


import gov.va.vinci.leo.model.NameValue;
import gov.va.vinci.leo.types.CSI;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CASRuntimeException;
import org.apache.uima.examples.SourceDocumentInformation;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;

import java.util.Iterator;

/**
 * Collection of handy methods for CAS information, handling.
 *

 * User: Thomas Ginter
 * Date: 11/19/13
 * Time: 2:57 PM
 */
public class CasTools {
    /**
     * Class log.
     */
    public static final Logger LOG = Logger.getLogger(CasTools.class.getName());

    /**
     * Return the document ID based on the CSI or SourceDocumentInformation objects.  Returns a NameValue pair
     * representing the document ID -> CSI object if a CSI object is found.
     *
     * @param jcas JCas object where the annotations are stored
     * @return NameValue object containing the String document ID -> CSI object, CSI value is null if no CSI is found.
     * Returns NULL if no document ID can be retrieved.
     */
    public static NameValue getReferenceID(JCas jcas) {
        Iterator<Annotation> it = null;
        try {
            if (jcas.getAnnotationIndex(CSI.type).iterator().hasNext()) {
                it = jcas.getAnnotationIndex(CSI.type).iterator();
                CSI srcDocInfo = (CSI) it.next();
                if (srcDocInfo != null) {
                    return new NameValue(srcDocInfo.getID(), srcDocInfo);
                }
                return null;
            }
        } catch (CASRuntimeException cre) { /** No CSI type defined **/ }
        try {
            if (jcas.getAnnotationIndex(SourceDocumentInformation.type)
                    .iterator().hasNext()) {
                it = jcas.getAnnotationIndex(SourceDocumentInformation.type)
                        .iterator();
                SourceDocumentInformation srcDocInfo = (SourceDocumentInformation) it
                        .next();
                return new NameValue(srcDocInfo.getUri(), null);
            }
        } catch (CASRuntimeException cre) { /** No SourceDocumentInformation type defined **/ }
        //No known document information types found
        return null;
    }// getReferenceID method


}
