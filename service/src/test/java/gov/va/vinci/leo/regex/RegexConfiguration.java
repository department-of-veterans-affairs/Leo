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

import gov.va.vinci.leo.regex.ae.RegexAnnotator;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Configuration object for the Regular Expression annotator. This allows for a unified configuration between
 * the simple resource file, and the groovy resource config file.
 * <p>
 * See documentation for an example of the groovy config file.
 */
public class RegexConfiguration {
    private String annotatorName = null;
    private boolean performanceMonitoring = false;
    private ConfigObject defaults;
    private ConfigObject globalSettings;
    private Map<String, Map<Pattern, Matcher>> configurationNamePatternsMap = new LinkedHashMap<String, Map<Pattern, Matcher>>();
    private Map<String, Map> configurationNameParametersMap = new LinkedHashMap<String, Map>();

    /**
     * Constructor that takes the groovy config file.
     *
     * @param configurationFile the groovy config file to read in.
     * @throws IOException
     */
    public RegexConfiguration(File configurationFile) throws IOException {
        ConfigSlurper configSlurper = new ConfigSlurper();
        parseConfiguration(configSlurper.parse(FileUtils.readFileToString(configurationFile)));
    }

    /**
     * Constructor for simple configuration files.
     *
     * @param name                      name of this configuration.
     * @param expressions               a list of expressions
     * @param caseSensitive             if true, the patterns will be case sensitive, if false case insensitive.
     * @param wordBoundary              if true, the regex string is bounded by \\b and \\b to match on word boundaries. If false, the regex
     *                                  string is not modified.
     * @param matchedPatternFeatureName The name of the feature to put the matched pattern in. If this is set, and the annotation has a feature
     *                                  of this name, the annotation will put the regular expression pattern that matched and created the annotation in that feature. For instance,
     *                                  if the annotation covers test, and the regex that caught test was \bt.*t\b, then the annotation feature would be set to \bt.*t\b.
     * @param groupFeatureName          if set, and the regular expression has groups, this feature on the annotation will be a string array of group
     *                                  values.
     * @param conceptFeatureName        If set, this is the feature name on the output annotation that will be populated with the value set for concept_feature_value.
     * @param conceptFeatureValue       If concept_feature_name is set, this is value that is set on the concept_feature_value feature of the output annotation.
     * @param patternFlags              An array of pattern flags from the java.util.regex.Pattern class, for instance Pattern.MULTILINE
     * @param outputType                the output type for matches.
     * @param inputTypes                the annotation types that will have their content searched.
     * @param performanceMonitoring     If true, the annotator should gather performance data for each expression. If false, no performance metrics
     *                                  are gathered.
     * @throws IOException
     */
    public RegexConfiguration(String name,
                              List<String> expressions, boolean caseSensitive, boolean wordBoundary,
                              String matchedPatternFeatureName, String groupFeatureName,
                              String conceptFeatureName, String conceptFeatureValue,
                              Integer patternFlags,
                              String outputType,
                              String[] inputTypes,
                              boolean performanceMonitoring
    ) throws IOException {

        ConfigObject config = new ConfigObject();
        config.put("name", name);

        ConfigObject configMap = new ConfigObject();

        configMap.put("expressions", expressions);
        configMap.put("case_sensitive", caseSensitive);
        configMap.put("word_boundary", wordBoundary);
        configMap.put("patternFlags", patternFlags);
        configMap.put("matchedPatternFeatureName", matchedPatternFeatureName);
        configMap.put("groupFeatureName", groupFeatureName);
        configMap.put("outputType", outputType);
        configMap.put("inputTypes", inputTypes);
        configMap.put("concept_feature_name", conceptFeatureName);
        configMap.put("concept_feature_value", conceptFeatureValue);


        ConfigObject m = new ConfigObject();
        m.put(name, configMap);

        /** Add globals section. **/
        ConfigObject globalSettingsObject = new ConfigObject();
        globalSettingsObject.put("performance_monitoring", performanceMonitoring);
        m.put("global_settings", globalSettingsObject);

        config.put("configuration", m);
        parseConfiguration(config);
    }


    /**
     * Constructor for simple configuration files.
     *
     * @param name                      name of this configuration.
     * @param expressions               a list of expressions
     * @param caseSensitive             if true, the patterns will be case sensitive, if false case insensitive.
     * @param wordBoundary              if true, the regex string is bounded by \\b and \\b to match on word boundaries. If false, the regex
     *                                  string is not modified.
     * @param matchedPatternFeatureName The name of the feature to put the matched pattern in. If this is set, and the annotation has a feature
     *                                  of this name, the annotation will put the regular expression pattern that matched and created the annotation in that feature. For instance,
     *                                  if the annotation covers test, and the regex that caught test was \bt.*t\b, then the annotation feature would be set to \bt.*t\b.
     * @param groupFeatureName          if set, and the regular expression has groups, this feature on the annotation will be a string array of group
     *                                  values.
     * @param conceptFeatureName        If set, this is the feature name on the output annotation that will be populated with the value set for concept_feature_value.
     * @param conceptFeatureValue       If concept_feature_name is set, this is value that is set on the concept_feature_value feature of the output annotation.
     * @param patternFlags              An array of pattern flags from the java.util.regex.Pattern class, for instance Pattern.MULTILINE
     * @param outputType                the output type for matches.
     * @param inputTypes                the annotation types that will have their content searched.
     * @throws IOException
     */
    public RegexConfiguration(String name,
                              List<String> expressions, boolean caseSensitive, boolean wordBoundary,
                              String matchedPatternFeatureName, String groupFeatureName,
                              String conceptFeatureName, String conceptFeatureValue,
                              Integer patternFlags,
                              String outputType,
                              String[] inputTypes
    ) throws IOException {
        this(name, expressions, caseSensitive, wordBoundary, matchedPatternFeatureName,
                groupFeatureName, conceptFeatureName, conceptFeatureValue,
                patternFlags, outputType, inputTypes, false);
    }

    /**
     * Get the list of configuration names.
     *
     * @return a list of configuration names.
     */
    public Set<String> getConfigNames() {
        return configurationNameParametersMap.keySet();
    }

    /**
     * Get the patterns for a specific configuration.
     *
     * @param configName the config name to get patterns for.
     * @return the patterns associated with this configuration.
     */
    public Map<Pattern, Matcher> getPatterns(String configName) {
        return configurationNamePatternsMap.get(configName);
    }

    /**
     * Get the configuration parameters for a specific configuration.
     *
     * @param configName the config name to get the configuration parameters for.
     * @return the map of parameters associated with this configuration.
     */
    public Map getParameters(String configName) {
        return configurationNameParametersMap.get(configName);
    }


    /**
     * If true, the annotator should gather performance data for each expression. If false, no performance metrics
     * are gathered.
     *
     * @return true if performance data is to be gathered. False if it is not.
     */
    public boolean isPerformanceMonitoring() {
        return performanceMonitoring;
    }


    /**
     * Parse the configuration and create the patterns.
     *
     * @param configObject the configuration object to parse.
     * @throws IOException
     */
    protected void parseConfiguration(ConfigObject configObject) throws IOException {
        annotatorName = (String) configObject.get("name");

        ConfigObject configuration = (ConfigObject) configObject.get("configuration");

        defaults = (ConfigObject) configuration.get("defaults");
        globalSettings = (ConfigObject) configuration.get("global_settings");
        globalSettings = globalSettings != null ? globalSettings : new ConfigObject();

        if (globalSettings.get("performance_monitoring") != null && (Boolean)globalSettings.get("performance_monitoring")) {
            performanceMonitoring = true;
        }



        if (defaults == null) {
            defaults = new ConfigObject();
        }

        if (defaults.get("inputTypes") == null) {
            defaults.put("inputTypes", new String[]{"uima.tcas.DocumentAnnotation"});
        }

        // Step 1 - Create the configuration maps and merge in any defaults. .
        for (Object configurationKey : configuration.keySet()) {
            String configurationName = (String) configurationKey;

            // Defaults are handled outside of this loop.
            if ("defaults".equals(configurationName) || "global_settings".equals(configurationName)) {
                continue;
            }

            if (configurationNameParametersMap.containsKey(configurationName)) {
                throw new IllegalArgumentException("Regex configuration file has multiple configurations of the same name: '" + configurationName + "'.");
            }
            ConfigObject regexConfigObject = (ConfigObject) configuration.get(configurationName);
            configurationNameParametersMap.put(configurationName, mergeConfigs(regexConfigObject, defaults));
        }

        // Step 2 - Parse the now merged configurations and create the pattern list.
        for (String configurationName : configurationNameParametersMap.keySet()) {
            Map parameters = configurationNameParametersMap.get(configurationName);

            List<String> stringPatternList = new ArrayList<String>();

            if (parameters.get("expressions") != null) {
                stringPatternList = (List<String>) parameters.get("expressions");
            }
            if (parameters.get("expression_file") != null) {
                String[] expressions = FileUtils.readFileToString((File) parameters.get("expression_file")).split("\n");
                stringPatternList.addAll(Arrays.asList(expressions));
            }

            Map<Pattern, Matcher> patternList = new HashMap<Pattern, Matcher>();
            for (String pattern : stringPatternList) {
                Boolean caseSensitive = RegexAnnotator.DEFAULT_CASE_SENSITIVITY;
                Boolean wordFlag = RegexAnnotator.DEFAULT_WORD_FLAG;
                Integer flags = RegexAnnotator.DEFAULT_PATTERN_FLAGS;

                if (parameters.containsKey("case_sensitive")) {
                    caseSensitive = (Boolean) parameters.get("case_sensitive");
                }
                if (parameters.containsKey("word_boundary")) {
                    wordFlag = (Boolean) parameters.get("word_boundary");
                }
                if (parameters.containsKey("patternFlags")) {
                    flags = (Integer) parameters.get("patternFlags");
                }

                // Create the appropriate pattern and add to the list.
                Integer[] flagArray =new Integer[] {flags};
                Pattern p = RegexUtils.createPattern(pattern, caseSensitive, flagArray, wordFlag);
                patternList.put(p, p.matcher(""));
            }

            configurationNamePatternsMap.put(configurationName, patternList);
        }
    }

    /**
     * Merge defaults with a configuration and return the resulting configuration map.
     *
     * @param master   the configuration
     * @param defaults defaults to merge into master.
     * @return a map that is the configuration.
     */
    protected Map mergeConfigs(ConfigObject master, ConfigObject defaults) {
        Map newMaster = master.clone();
        for (Object defaultKey : defaults.keySet()) {
            if (!master.containsKey(defaultKey)) {
                newMaster.put(defaultKey, defaults.get(defaultKey));
            }
        }
        return newMaster;
    }
}