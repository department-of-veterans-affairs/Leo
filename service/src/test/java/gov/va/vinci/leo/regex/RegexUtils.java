package gov.va.vinci.leo.regex;

/*
 * #%L
 * Regex Annotator
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Common utils used within the Regular Expression Annotator project.
 *
 */
public class RegexUtils {

    /**
     * Creates a Pattern based on a regular expression string and options passed in.
     * @param regex the regular expression string.
     * @param case_sensitivity if true, the regular expression will be evaluated as case sensitive, if false, case insensitive.
     * @param paramFlagList An array of pattern flags from the java.util.regex.Pattern class, for instance Pattern.MULTILINE
     * @param word_flag if true, the regex string is bounded by \\b and \\b to match on word boundaries. If false, the regex
     *                  string is not modified.
     * @return
     */
    public static Pattern createPattern(String regex, boolean case_sensitivity, Integer[] paramFlagList, boolean word_flag)
    {
        //Get the pattern flags
        int patternFlags = 0;
        for (Integer paramFlag : paramFlagList) {
            patternFlags = patternFlags | paramFlag.intValue();
        }

        if(!case_sensitivity) {
            patternFlags = patternFlags | Pattern.CASE_INSENSITIVE;
        }

        if(word_flag) regex = "\\b" + regex + "\\b";

        return (patternFlags == 0)? Pattern.compile(regex) : Pattern.compile(regex, patternFlags);
    }

    /**
     * Read a file into a List, removing empty lines of lines that start with a pound(#)
     *
     * @param resourceFile the file to be read in.
     * @return A string list of the contents of the file with empty lines and lines that start with a pound (#) removed.
     * @throws IOException
     */
    public static List<String> readFileToStringList(File resourceFile) throws IOException {
        List<String> expressions= new ArrayList<String>();
        
        BufferedReader br=null;
        FileReader fr = null;
        fr = new FileReader(resourceFile);
        br = new BufferedReader(fr);
        try
        {
        String regex = "";
        while((regex =  br.readLine()) != null) {
            if (regex.length() < 1) { // Skip empty lines
                continue;
            }
            if (regex.startsWith("#")) { // Skip comments
                continue;
            }

            expressions.add(regex);
        }
        }
        finally
        {
        	if ( fr!=null ) fr.close();
        	if ( br!=null ) br.close();
        }
        return expressions;
    }
}
