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
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.UIMAFramework;
import org.apache.uima.pear.tools.PackageBrowser;
import org.apache.uima.pear.tools.PackageInstaller;
import org.apache.uima.pear.tools.PackageInstallerException;
import org.apache.uima.resource.Parameter;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.resource.metadata.Import;
import org.apache.uima.resource.metadata.impl.Import_impl;
import org.apache.uima.util.InvalidXMLException;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
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
     * Logging object
     */
    private static final Logger log = Logger.getLogger(LeoUtils.class);

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
     * Return a timestamp in the format of yyyyMMdd_HHmmss.
     *
     * @return String representation of a timestamp
     */
    public static String getTimestampDateUnderscoreTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
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
	 * Extracts the contents of a PEAR file to the directory specified, optionally performs a verification
	 * step.  If there are no errors then return the path to the root descriptor file.
	 *
	 * @param installDir Directory where the pear file will be extracted.
	 * @param pearFile Pear file to extract
	 * @param doVerification If true then the PackageInstaller will perform an optional validation step
	 * @param cleanInstallDir Clean out the installation directory before extracting
	 * @return Path to the root component descriptor file
	 * @throws PackageInstallerException If there is an error extracting the PEAR file
	 * @throws IOException If there is an error retrieving the path toe hte root component descriptor file
	 */
	public static String extractPearFile(File installDir, File pearFile, boolean doVerification, boolean cleanInstallDir)
		throws PackageInstallerException, IOException {
		PackageBrowser instPear = PackageInstaller.installPackage(installDir, pearFile, doVerification, cleanInstallDir);
		return instPear.getComponentPearDescPath();
	}

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
            public final Class<?> getRuntimeClass() {
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
        DefaultResourceLoader loader = new DefaultResourceLoader(cl);
        for (String filePath : configFilePaths) 
        {
			Resource resource = loader.getResource(filePath);
			if(!resource.exists()) {
				log.warn("File " + filePath + " not found. Loading other configuration files...");
				continue;
			}

			//Slurp in the config and merge it with the others
			String resourceString = IOUtils.toString(resource.getInputStream());
			config.merge(slurper.parse(resourceString));
			log.info("Loaded file:  " + resource.getURI());
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
	 * Return the Parameter in the array whose name matches the name provided.
	 *
	 * @param name Name of the parameter to find
	 * @param parameters Array of parameters to search
	 * @return The matching parameter or null if the parameter is not found or the arguments are empty
	 */
	public static Parameter getParameter(String name, Parameter[] parameters) {
    	if(ArrayUtils.isEmpty(parameters) || StringUtils.isBlank(name))
    		return null;
		Optional<Parameter> param = Arrays.asList(parameters).parallelStream()
				.filter(parameter -> parameter.getName().equals(name))
				.findFirst();
		if(param.isPresent())
			return param.get();
		return null;
	}

	/**
	 * Add the parameter p to the array of parameters.
	 *
	 * @param p Parameter to add
	 * @param parameters Array of parameters to which the parameter will be added
	 * @return New Array of Parameter objects which include the parameter provided.
	 */
	public static Parameter[] addParameter(Parameter p, Parameter[] parameters) {
		List<Parameter> completeList = (ArrayUtils.isEmpty(parameters))?
				new ArrayList<>(1) : new ArrayList<>(Arrays.asList(parameters));
		completeList.add(p);
		return completeList.toArray(new Parameter[completeList.size()]);
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
    
    /**
     * returns a Header-Manipulation safe string using a "WhiteList" approach.  Note that the algorithm is designed to 
     * satisfy the HP Fortify Scanning algorithms.  
     * 
     * @param value the String value to be made Header-Manipulation safe
     * @return the Header-Manipulation safe string
     */
    public static String getHeaderManipulationSafeString(Object object)
    {
    	if (object == null) return null;
    	try {
        	String value = object.toString();
        	StringBuilder sb=new StringBuilder();
        	for ( char c: value.toCharArray())
        	{
        		switch ( c)
        		{
    	    		case '\n': { sb.append(" "); break; }
    	    		case '\r': { sb.append(" "); break; }
    	    		case ' ': { sb.append(" "); break; }
    	    		case '!': { sb.append("!"); break; }
    	    		case '"': { sb.append("\""); break; }
    	    		case '#': { sb.append("#"); break; }
    	    		case '$': { sb.append("$"); break; }
    	    		case '%': { sb.append("%"); break; }
    	    		case '&': { sb.append("&"); break; }
    	    		case '\'': { sb.append("'"); break; }
    	    		case '(': { sb.append("("); break; }
    	    		case ')': { sb.append(")"); break; }
    	    		case '*': { sb.append("*"); break; }
    	    		case '+': { sb.append("+"); break; }
    	    		case ',': { sb.append(","); break; }
    	    		case '-': { sb.append("-"); break; }
    	    		case '.': { sb.append("."); break; }
    	    		case '/': { sb.append("/"); break; }
    	    		case '0': { sb.append("0"); break; }
    	    		case '1': { sb.append("1"); break; }
    	    		case '2': { sb.append("2"); break; }
    	    		case '3': { sb.append("3"); break; }
    	    		case '4': { sb.append("4"); break; }
    	    		case '5': { sb.append("5"); break; }
    	    		case '6': { sb.append("6"); break; }
    	    		case '7': { sb.append("7"); break; }
    	    		case '8': { sb.append("8"); break; }
    	    		case '9': { sb.append("9"); break; }
    	    		case ':': { sb.append(":"); break; }
    	    		case ';': { sb.append(";"); break; }
    	    		case '<': { sb.append("<"); break; }
    	    		case '=': { sb.append("="); break; }
    	    		case '>': { sb.append(">"); break; }
    	    		case '?': { sb.append("?"); break; }
    	    		case '@': { sb.append("@"); break; }
    	    		case 'A': { sb.append("A"); break; }
    	    		case 'B': { sb.append("B"); break; }
    	    		case 'C': { sb.append("C"); break; }
    	    		case 'D': { sb.append("D"); break; }
    	    		case 'E': { sb.append("E"); break; }
    	    		case 'F': { sb.append("F"); break; }
    	    		case 'G': { sb.append("G"); break; }
    	    		case 'H': { sb.append("H"); break; }
    	    		case 'I': { sb.append("I"); break; }
    	    		case 'J': { sb.append("J"); break; }
    	    		case 'K': { sb.append("K"); break; }
    	    		case 'L': { sb.append("L"); break; }
    	    		case 'M': { sb.append("M"); break; }
    	    		case 'N': { sb.append("N"); break; }
    	    		case 'O': { sb.append("O"); break; }
    	    		case 'P': { sb.append("P"); break; }
    	    		case 'Q': { sb.append("Q"); break; }
    	    		case 'R': { sb.append("R"); break; }
    	    		case 'S': { sb.append("S"); break; }
    	    		case 'T': { sb.append("T"); break; }
    	    		case 'U': { sb.append("U"); break; }
    	    		case 'V': { sb.append("V"); break; }
    	    		case 'W': { sb.append("W"); break; }
    	    		case 'X': { sb.append("X"); break; }
    	    		case 'Y': { sb.append("Y"); break; }
    	    		case 'Z': { sb.append("Z"); break; }
    	    		case '[': { sb.append("["); break; }
    	    		case '\\': { sb.append("\\"); break; }
    	    		case ']': { sb.append("]"); break; }
    	    		case '^': { sb.append("^"); break; }
    	    		case '_': { sb.append("_"); break; }
    	    		case '`': { sb.append("`"); break; }
    	    		case 'a': { sb.append("a"); break; }
    	    		case 'b': { sb.append("b"); break; }
    	    		case 'c': { sb.append("c"); break; }
    	    		case 'd': { sb.append("d"); break; }
    	    		case 'e': { sb.append("e"); break; }
    	    		case 'f': { sb.append("f"); break; }
    	    		case 'g': { sb.append("g"); break; }
    	    		case 'h': { sb.append("h"); break; }
    	    		case 'i': { sb.append("i"); break; }
    	    		case 'j': { sb.append("j"); break; }
    	    		case 'k': { sb.append("k"); break; }
    	    		case 'l': { sb.append("l"); break; }
    	    		case 'm': { sb.append("m"); break; }
    	    		case 'n': { sb.append("n"); break; }
    	    		case 'o': { sb.append("o"); break; }
    	    		case 'p': { sb.append("p"); break; }
    	    		case 'q': { sb.append("q"); break; }
    	    		case 'r': { sb.append("r"); break; }
    	    		case 's': { sb.append("s"); break; }
    	    		case 't': { sb.append("t"); break; }
    	    		case 'u': { sb.append("u"); break; }
    	    		case 'v': { sb.append("v"); break; }
    	    		case 'w': { sb.append("w"); break; }
    	    		case 'x': { sb.append("x"); break; }
    	    		case 'y': { sb.append("y"); break; }
    	    		case 'z': { sb.append("z"); break; }
    	    		case '{': { sb.append("{"); break; }
    	    		case '|': { sb.append("|"); break; }
    	    		case '}': { sb.append("}"); break; }
    	    		case '~': { sb.append("~"); break; }
    	    		default: sb.append("");
        		}
        	}
        	return sb.toString();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }

}
