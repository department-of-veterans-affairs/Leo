/**
 * 
 */
package gov.va.vinci.leo.resources;

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

import org.apache.uima.resource.DataResource;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.SharedResourceObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

/**
 * Reads in a stop word list based on the resource provided by the UIMA annotator object 
 * and provides an easy access function to declare if a given word is part of the stop
 * word list.
 * 
 * @author thomasginter
 */
public class StopWordFilter implements SharedResourceObject, WordFilter {
	/**
	 * Stop word list for this filter class
	 */
	private HashSet<String> mStopWordList = new HashSet<String>();
	
	/* (non-Javadoc)
	 * @see gov.va.vinci.fgen.filters.WordFilter#isExcluded(java.lang.String)
	 */
	@Override
	public boolean isExcluded(String word) {
		return this.mStopWordList.contains(word.trim().toLowerCase());
	}//isExcluded method

	/* (non-Javadoc)
	 * @see org.apache.uima.resource.SharedResourceObject#load(org.apache.uima.resource.DataResource)
	 */
	@Override
	public void load(DataResource aData) throws ResourceInitializationException {
		InputStream inStr = null;
	    try {
	      // open input stream to data
	      inStr = aData.getInputStream();
	      // read each line
	      BufferedReader reader = new BufferedReader(new InputStreamReader(inStr));
	      String line;
	      while ((line = reader.readLine()) != null) {
	    	  this.mStopWordList.add(line.trim().toLowerCase());
	      }//while
	    } catch (IOException e) {
	        throw new ResourceInitializationException(e);
	    } finally {
	        if (inStr != null) {
				try {
					inStr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }//try catch finally
	}//load method

}//StopWordFilter class
