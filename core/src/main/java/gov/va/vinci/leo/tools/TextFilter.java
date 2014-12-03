/**
 * TextFilter.java
 *
 * @author thomasginter
 */
package gov.va.vinci.leo.tools;

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

import java.io.IOException;
import java.io.InputStream;

/**
 * Defines methods that provide filter functionality for text.
 *
 * @author thomasginter
 */
public interface TextFilter {

    /**
     * Creates a new String from the input String with invalid characters replaced
     * with valid characters.
     *
     * @param text Input text whose characters will be filtered
     * @return New String with filtered text
     */
    public abstract String filter(final String text);

    /**
     * Create a new String from the text found in the input stream replacing invalid characters
     * with valid characters.
     *
     *
     * @param inputStream Input Text Stream that will be filtered
     * @return New String with filtered Text
     * @throws java.io.IOException if there is an error reading the stream
     */
    public abstract String filter(InputStream inputStream) throws IOException;

    /**
     * Create a new String from the text found in the input stream replacing invalid characters
     * with valid characters.  Use the character set provided for reading in the input stream.
     *
     * @param inputStream Input Text Stream that will be filtered
     * @param charsetName Name of the character set to use when reading in the stream
     * @return New String with filtered Text
     * @throws java.io.IOException if there is an error reading the stream
     */
    public abstract String filter(final InputStream inputStream, String charsetName) throws IOException;
}//TextFilter interface
