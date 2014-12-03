/**
 *
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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.UIMAFramework;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.apache.uima.util.InvalidXMLException;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Commonly used functions.
 *
 * @author thomasginter
 */
public class LeoUtils {
    /**
     * Return a timestamp in the format of yyyyMMdd.HHmmss.
     *
     * @return String representation of a timestamp
     */
    public static String getTimestampDateDotTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd.HHmmss");
        return sdf.format(new Date());
    }//getTimestampDateDotTime method

    /**
     * Generate a random UUID string.
     *
     * @return String representation of a UUID
     */
    public static String getUUID() {
        UUID id = UUID.randomUUID();
        return id.toString();
    }//getUUID method

    /**
     * Computes the absolute URL for this import, using the relative location or name, whichever is specified by this import object.
     *
     * @param name  the name of this import's target.
     * @return     the url of the import.
     * @throws InvalidXMLException   if the import could not be resolved
     */
    public static URL createURL(String name) throws InvalidXMLException {
        Import imprt = new Import_impl();
        imprt.setName(name);
        return imprt.findAbsoluteUrl(UIMAFramework.newDefaultResourceManager());
    }//createURL method

    /**
     * Return a URI string locator for the File object provided.
     *
     * @param f  the file to get URI for.
     * @return   the file URI.
     */
    public static String getFileURI(File f)  {
        return f.toURI().toString();
    }//getFileURI method


    /**
     * Return a list of File Objects in a directory.  Optionally return only a list of Files
     * that match the given FileFilter. Recurse into subdirectories if indicated.  If no filter
     * is provided then return all the files in the directory.
     *
     * @param src     Source directory where the search will be performed.
     * @param filter  Optional, File filter to limit results that are returned.
     * @param recurse If true then recurse into subdirectories, only return this directory results if false
     * @return List of File objects found in this directory
     */
    public static List<File> listFiles(File src, FileFilter filter, boolean recurse) {
        List<File> files = new ArrayList<File>();
        if (src == null || !src.exists() || src.isFile()) {
            return files;
        }//if

        //Get the list of files, apply the filter if provided
        File[] fileList = (filter != null) ? src.listFiles(filter) : src.listFiles();
        for (File f : fileList) {
            if (!files.contains(f)) {
                files.add(f);
            }
        }//for

        //Recurse through the list of directories
        if (recurse) {
            FileFilter dirFilter = new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return f.isDirectory();
                }//accept method
            };
            File[] dirList = src.listFiles(dirFilter);
            for (File f : dirList) {
                files.addAll(listFiles(f, filter, recurse));
            }//for
        }//if

        return files;
    }//listFiles method

    /**
     * Determines the currently executing runtime class.
     *
     * @return The currently executing runtime class
     */
    public static Class<?> getRuntimeClass() {
        /**
         * Extend security manager to allow for getting current runtime class.
         */
        class AIC extends SecurityManager {
            /**
             * Get the current runtime class.
             * @return  the current runtime class.
             */
            public Class<?> getRuntimeClass() {
                return this.getClassContext()[2];
            }
        }
        return new AIC().getRuntimeClass();
    }//getRuntimeClass method

    /**
     * Load the config file(s).
     *
     * @param environment name of the environment to load
     * @param configFilePaths paths the config files to load, local or remote
     * @return ConfigObject with accumulated variables representing the environment
     * @throws IOException if there is a problem reading the config files
     */
    public static ConfigObject loadConfigFile(String environment, String... configFilePaths) throws IOException {
        ConfigSlurper slurper = new ConfigSlurper(environment);
        ConfigObject config = new ConfigObject();
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        for (String filePath : configFilePaths) {
            InputStream in = cl.getResourceAsStream(filePath);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy(in, out);
            String resourceAsString = out.toString();
            config.merge(slurper.parse(resourceAsString));
        }
        return config;
    }

    /**
     * Return a String that has applied the list of filters to the input string.  If
     * there are no filters to apply just returns the input string.
     *
     * @param input Text to be filtered if there are filters defined
     * @param filters List of text filters to apply to the input text
     * @return Filtered text or input text if no filters defined
     * @throws java.io.IOException if there is an error getting the bytes from the filtered text
     */
    public static String filterText(String input, List<TextFilter> filters) throws IOException {
        if (filters == null) return input;
        String filtered = (input == null) ? "" : input;
        for (TextFilter filter : filters) {
            filtered = filter.filter(new ByteArrayInputStream(filtered.getBytes("UTF-8")));
        }//for
        return filtered;
    }//filterText method

    /**
     * Create the set of ConfigurationParameter objects from the Param class of an object.
     *
     * @param c Param class
     * @return Set of ConfigurationParameter objects
     */
    public static Set<ConfigurationParameter> getStaticConfigurationParameters(Class c) {
        Set<ConfigurationParameter> list  = new HashSet<ConfigurationParameter>();
        Field[] fields = c.getFields();
        for (Field field : fields) {
            try {
                if (field.getType().equals(ConfigurationParameter.class) && Modifier.isStatic(field.getModifiers())) {
                    list.add((ConfigurationParameter) field.get(null));
                }
            }
            catch (IllegalAccessException e) {
                // Handle exception here
            }
        }
        return list;
    }

    /**
     * Convert the JSON formatted string into a Map of Strings.
     * i.e. {"jsonString":"class"},
     *      {"C":"Valid","IR":"Invalid","IM":"InvalidMeasure"}
     *
     * @param jsonString
     *      JSON formatted validation string map
     * @return
     *      HashMap of key, value string pairs. If jsonString is empty then returns an empty map.
     * @throws com.google.gson.JsonSyntaxException if the JSON string is invalid
     */
    public static Map<String, String> parseMappingString(String jsonString) throws JsonSyntaxException {
        Map<String, String> map = new HashMap<String, String>();
        if(StringUtils.isBlank(jsonString))
            return map;
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = (JsonObject) jsonParser.parse(jsonString);

        for(Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return map;
    }

}//Common class
