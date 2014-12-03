/**
 * AsciiFilter.java
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


import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Replace non ASCII-8 characters with ASCII-8 characters.
 *
 * @author thomasginter
 */
public class AsciiFilter implements TextFilter {

    /**
     * @see gov.va.vinci.leo.tools.TextFilter#filter(java.lang.String)
     * @param text the text to filter.
     * @return the text filtered.
     */
    @Override
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }//if
        return AsciiService.toASCII8(text);
    }//filter method String input

    /**
     * Create a new String with non-ascii chars replaced with ascii equivalents where applicable,
     * otherwise replaces invalid char with '_'. Assumes "UTF-8" encoding.  For other encoding                            \
     * options see {@link gov.va.vinci.leo.tools.AsciiFilter#filter(InputStream, String)}
     *
     * @param inputStream the input stream to read from.
     * @return New String with filtered Text
     * @throws java.io.IOException if there is an error reading the stream
     */
    @Override
    public String filter(InputStream inputStream) throws IOException {
        return this.filter(inputStream, "UTF-8");
    }//filter method InputStreamReader input

    /**
     * @see gov.va.vinci.leo.tools.TextFilter#filter(java.io.InputStream, java.lang.String)
     * @param inputStream the input stream to read from.
     * @param charsetName the characterset the document is in.
     * @return New String with filtered Text
     * @throws java.io.IOException if there is an error reading the stream
     */
    @Override
    public String filter(InputStream inputStream, String charsetName) throws IOException {
        if (inputStream == null)
            return null;
        if (StringUtils.isBlank(charsetName)) {
            throw new IllegalArgumentException("Character Set Name required");
        }//if

        InputStreamReader isr = new InputStreamReader(inputStream, charsetName);
        StringBuilder sb = new StringBuilder();
        int numRead = 0;
        char[] chars = new char[1024];
        while ((numRead = isr.read(chars, 0, 1024)) > -1) {
            sb.append(chars, 0, numRead);
        }//while
        return AsciiService.toASCII8(sb.toString());
    }//filter method

}//AsciiFilter class
