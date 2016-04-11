package gov.va.vinci.leo.ae;

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

import gov.va.vinci.leo.descriptors.LeoTypeSystemDescription;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.junit.Assert;
import org.junit.Test;

public class LeoCachingAnnotatorTest {
	MyTestAnnotator annotator = new MyTestAnnotator();
	
	@Test
	public void testGetCacheName() {
        System.out.println(annotator.getCacheName());
		Assert.assertEquals(annotator.getCacheName(), "gov.va.vinci.leo.ae.LeoCachingAnnotatorTest.MyTestAnnotator");
	}

	@Test
	public void testGetCache() {
		Assert.assertNotNull(annotator.getCache());
	}

	@Test
	public void testCache() {
		Assert.assertNull(annotator.getResultFromCache("TestItem"));
		annotator.addResultToCache("TestItem", (long) 1);
		Assert.assertEquals(annotator.getResultFromCache("TestItem"), (long) 1);
	}
	
	public class MyTestAnnotator extends LeoCachingAnnotator {

		@Override
		public void annotate(JCas arg0) throws AnalysisEngineProcessException {
			// TODO Auto-generated method stub
			
		}

        @Override
        public LeoTypeSystemDescription getLeoTypeSystemDescription() {
            return new LeoTypeSystemDescription();  //To change body of implemented methods use File | Settings | File Templates.
        }

    }
}
