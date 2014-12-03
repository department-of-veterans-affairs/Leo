

/* First created by JCasGen Tue Dec 10 16:21:43 MST 2013 */
package gov.va.vinci.leo.types;

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

import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;
import org.apache.uima.jcas.tcas.Annotation;


/** My Tool Heart Annotation
 * Updated by JCasGen Tue Dec 10 16:21:43 MST 2013
 * XML source: /var/folders/1s/dht183xn48l3gs7t9_63bkvc0000gn/T/leoTypeDescription_1b2cb157-b48a-4b4a-a4fd-5ba609ad0cab1990194951891841295.xml
 * @generated */
public class ToolHearts extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(ToolHearts.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected ToolHearts() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public ToolHearts(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public ToolHearts(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public ToolHearts(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
}

    