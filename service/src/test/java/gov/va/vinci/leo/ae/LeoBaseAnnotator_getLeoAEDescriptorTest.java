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


import gov.va.vinci.leo.descriptors.LeoAEDescriptor;
import org.junit.Assert;
import org.junit.Test;

public class LeoBaseAnnotator_getLeoAEDescriptorTest {

    @Test
    public void testDefault() throws Exception {
        ExampleAnnotator annotator = new ExampleAnnotator("test", "testRequired");
        LeoAEDescriptor d = (LeoAEDescriptor) annotator.getDescriptor();
        Assert.assertNotNull(d);
        System.out.println(d.getName());
        assert(d.getName().startsWith("ExampleAnnotator"));
        Assert.assertEquals("test", d.getParameterValue("myParam"));
        Assert.assertEquals("testRequired", d.getParameterValue("myParamRequired"));
    }

    @Test
    public void testDefault2() throws Exception {
        Example2Annotator annotator = new Example2Annotator("MyOutputType");
        LeoAEDescriptor d = (LeoAEDescriptor) annotator.getDescriptor();
        Assert.assertNotNull(d);
        assert(d.getName().startsWith("Example2Annotator"));
        Assert.assertEquals("MyOutputType", d.getParameterValue("outputType"));
    }
}
