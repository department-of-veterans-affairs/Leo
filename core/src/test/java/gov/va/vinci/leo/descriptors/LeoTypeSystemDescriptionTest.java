/**
 * LeoTypeSystemDescriptionTest.java
 *
 * @author thomasginter
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
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.uima.resource.metadata.FeatureDescription;
import org.apache.uima.resource.metadata.TypeDescription;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit Testing the LeoTypeSystemDescription object.
 * 
 * @author thomasginter
 */
public class LeoTypeSystemDescriptionTest {
	
	@Test
	public void testTypeSystemConstructors() throws Exception {
		LeoTypeSystemDescription ftsd0 = new LeoTypeSystemDescription("desc.gov.va.vinci.leo.types.TestTypeSystem", true);
		assertNotNull(ftsd0);
		TypeDescription td0 = ftsd0.getType("gov.va.vinci.leo.types.TestType");
		assertNotNull(td0);
		FeatureDescription[] features = td0.getFeatures();
		assertTrue(features.length > 0);
		assertTrue("name".equals(features[0].getName()));
		
		String testType1 = "gov.va.vinci.leo.types.TestType1";
		LeoTypeSystemDescription ftsd1
			= new LeoTypeSystemDescription(testType1,
											"Test Type 1", 
											"uima.tcas.Annotation");
		assertNotNull(ftsd1);
		TypeDescription td1 = ftsd1.getType(testType1);
		assertNotNull(td1);
		
		LeoTypeSystemDescription ftsd2 = new LeoTypeSystemDescription(td1);
		assertNotNull(ftsd2);
		
	}//testTypeSystemConstructors method
	
	@Test
	public void jCasGenTest() throws Exception {
		String srcDirectory = "src/test/java";
		String binDirectory = "target/resources/bin";
		
		//Create the bin directory if it does not exist
		File binFile = new File(binDirectory);
		if(!binFile.exists()) {
			assertTrue(binFile.mkdirs());
		}
		
		//Remove the Source Files if they exist
		removeTestTypeFiles(srcDirectory, binDirectory);
		
		//Import the Type System
		LeoTypeSystemDescription ftsd = null;

        ftsd = new LeoTypeSystemDescription("desc.gov.va.vinci.leo.types.TestTypeSystem", true);
        assertNotNull(ftsd);
        ftsd.jCasGen(srcDirectory, binDirectory);

		assertNotNull(ftsd);
        List<String> fileNames = new ArrayList<String>();
        for (TypeDescription td :ftsd.getTypes()) {
            String name = td.getName().substring(td.getName().lastIndexOf('.') + 1, td.getName().length());
            fileNames.add(name + ".java");
            fileNames.add(name + "_Type.java");
        }//for
		List<File> javaSrcFiles = LeoUtils.listFiles(new File(srcDirectory), new NameFileFilter(fileNames), true);
		assertNotNull(javaSrcFiles);
		
		File classFile = new File(binDirectory + "/gov/va/vinci/leo/types/TestType.class");
		assertTrue(classFile.exists());
		
		//Cleanup generated files
		removeTestTypeFiles(srcDirectory, binDirectory);
	}//jCasGenTest method
	
	private void removeTestTypeFiles(String srcDirectory, String binDirectory) {
		String[] files = { srcDirectory + "/gov/va/vinci/leo/types/TestType_Type.java",
						   srcDirectory + "/gov/va/vinci/leo/types/TestType.java",
						   binDirectory + "/gov/va/vinci/leo/types/TestType.class",
						   binDirectory + "/gov/va/vinci/leo/types/TestType_Type.class",
						   binDirectory + "/gov/va/vinci/leo/types/TestType_Type$1.class"
		};
		//Delete each file in the list
		for(String file : files) {
			try {
				File f = new File(file);
				if(f.exists()) {
					f.delete();
				}//if
			} catch (Exception e) {
				System.err.println("Exception thrown removing file: " + file);
				e.printStackTrace();
			}//catch
		}//for
	}//removeTestTypeFiles method
	
}//LeoTypeSystemDescriptionTest class
