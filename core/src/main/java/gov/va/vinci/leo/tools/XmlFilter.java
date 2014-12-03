/**
 * XmlFilter.java
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
import org.apache.uima.internal.util.XMLUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Replace non XML-1.0 characters with spaces.
 *
 * @author thomasginter
 */
public class XmlFilter implements TextFilter {

    /**
     * @see gov.va.vinci.leo.tools.TextFilter#filter(java.lang.String)
     * @param text The text to filter for non XML-1.0 characters.
     * @return the text filtered. Non XML-1.0 characters become spaces.
     */
    @Override
    public String filter(String text) {
        if (StringUtils.isBlank(text)) {
            return text;
        }//if
        return XmlFilter.toXml10(text);
    }//filter method String input

    /**
     * @see gov.va.vinci.leo.tools.TextFilter#filter(java.io.InputStream)
     * @param inputStream An input stream that is read in, filtered, and returned as a string.
     * @return the text filtered. Non XML-1.0 characters become spaces.
     * @throws java.io.IOException of the stream cannot be read.
     */
    @Override
    public String filter(InputStream inputStream) throws IOException {
        return this.filter(inputStream, "UTF-8");
    }//filter method

    /**
     * @see gov.va.vinci.leo.tools.TextFilter#filter(java.io.InputStream, java.lang.String)
     * @param inputStream An input stream that is read in, filtered, and returned as a string.
     * @param charsetName  A charset to use for the stream.
     * @return the text filtered. Non XML-1.0 characters become spaces.
     * @throws java.io.IOException of the stream cannot be read.
     */
    @Override
    public String filter(InputStream inputStream, String charsetName)
            throws IOException {
        if (inputStream == null) {
            return null;
        }
        if (StringUtils.isBlank(charsetName)) {
            throw new IllegalArgumentException("Character Set Name required");
        }//if
        //Build the string from the InputStreamReader
        InputStreamReader isr = new InputStreamReader(inputStream, charsetName);
        StringBuilder sb = new StringBuilder();
        int numRead = 0;
        char[] chars = new char[1024];
        while ((numRead = isr.read(chars, 0, 1024)) > -1) {
            sb.append(chars, 0, numRead);
        }//while
        return XmlFilter.toXml10(sb.toString());
    }//filter method

    /**
     * Return a copy of the input string with non XML-1.0 characters replaced by spaces.
     *
     * @param str Input string whose text will be filtered.
     * @return Filtered copy of the input string
     */
    public static String toXml10(final String str) {
        StringBuilder sb = new StringBuilder(str.length());
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (XMLUtils.checkForNonXmlCharacters(chars, i, 1, false) == -1) {
                sb.append(chars[i]);
            } else {
                sb.append(' ');
            }//else
        }//for
        return sb.toString();
    }//toXml10 method

}//XmlFilter class
