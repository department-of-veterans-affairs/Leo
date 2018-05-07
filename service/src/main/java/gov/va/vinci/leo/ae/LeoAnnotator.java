package gov.va.vinci.leo.ae;

/*
 * #%L
 * Leo Service
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

import gov.va.vinci.leo.descriptors.LeoDelegate;

/**
 * Interface for Leo pipeline annotator objects.
 *
 * User: Thomas Ginter
 * Date: 8/8/14
 * Time: 09:40
 */
public interface LeoAnnotator {

    /**
     * Return the Descriptor for this AnalysisEngine or RemoteAnalysisEngine.
     *
     * @return Descriptor object.
     * @throws Exception If there is an error generating the descriptor.
     */
    public LeoDelegate getDescriptor() throws Exception;
}
