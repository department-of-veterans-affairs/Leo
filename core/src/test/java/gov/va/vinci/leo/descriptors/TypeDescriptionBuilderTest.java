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

import org.apache.uima.resource.metadata.TypeDescription;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.Assert.assertEquals;

public class TypeDescriptionBuilderTest {

	@Test
	public void simpleTest() throws Exception {
		TypeDescription t = TypeDescriptionBuilder.create().getTypeDescription();
		
		StringWriter w = new StringWriter();
		t.toXML(w);
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				 	"<typeDescription xmlns=\"http://uima.apache.org/resourceSpecifier\">" +
					"    <description/>" +
					"</typeDescription>",  w.toString().replaceAll("\r\n", "").replaceAll("\n", ""));
	}
	
	@Test
	public void simpleTest2() throws Exception, IOException {
		TypeDescription t = TypeDescriptionBuilder.create("gov.va.vinci.Ryan", "Test", "uima.tcas.Annotation").getTypeDescription();
		
		StringWriter w = new StringWriter();
		t.toXML(w);
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				 	"<typeDescription xmlns=\"http://uima.apache.org/resourceSpecifier\">" +
				 	"    <name>gov.va.vinci.Ryan</name>" +
				 	"    <description>Test</description>" +
				 	"    <supertypeName>uima.tcas.Annotation</supertypeName>" +
					"</typeDescription>",  w.toString().replaceAll("\r\n", "").replaceAll("\n", ""));
	}
	
	@Test
	public void simpleTest3() throws Exception, IOException {
		TypeDescription t = TypeDescriptionBuilder
									.create("gov.va.vinci.Ryan", "Test", "uima.tcas.Annotation")
									.addFeature("myFeature", "Cool Feature!", "String")
									.getTypeDescription();
		
		StringWriter w = new StringWriter();
		t.toXML(w);
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				 	"<typeDescription xmlns=\"http://uima.apache.org/resourceSpecifier\">" +
				 	"    <name>gov.va.vinci.Ryan</name>" +
				 	"    <description>Test</description>" +
				 	"    <supertypeName>uima.tcas.Annotation</supertypeName>" +
				 	"    <features>" +
			        "        <featureDescription>" +
			        "            <name>myFeature</name>" +
			        "            <description>Cool Feature!</description>" +
			        "            <rangeTypeName>String</rangeTypeName>" +
			        "        </featureDescription>" +
			        "    </features>" +
					"</typeDescription>", w.toString().replaceAll("\r\n", "").replaceAll("\n", ""));
	}	
	
	@Test
	public void simpleTest4() throws Exception, IOException {
		TypeDescription t = TypeDescriptionBuilder
									.create("gov.va.vinci.Ryan", "Test", "uima.tcas.Annotation")
									.addFeature("myFeature", "Cool Feature!", "String", "LString", true)
									.getTypeDescription();
		
		StringWriter w = new StringWriter();
		t.toXML(w);
		
		assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				 	"<typeDescription xmlns=\"http://uima.apache.org/resourceSpecifier\">" +
				 	"    <name>gov.va.vinci.Ryan</name>" +
				 	"    <description>Test</description>" +
				 	"    <supertypeName>uima.tcas.Annotation</supertypeName>" +
				 	"    <features>" +
			        "        <featureDescription>" +
			        "            <name>myFeature</name>" +
			        "            <description>Cool Feature!</description>" +
			        "            <rangeTypeName>String</rangeTypeName>" +
			        "            <elementType>LString</elementType>" +
			        "            <multipleReferencesAllowed>true</multipleReferencesAllowed>" +
			        "        </featureDescription>" +
			        "    </features>" +
					"</typeDescription>", w.toString().replaceAll("\r\n", "").replaceAll("\n", ""));
	}	

}
