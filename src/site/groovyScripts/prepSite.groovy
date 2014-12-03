/*
 * #%L
 * Leo Parent Project
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
println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
println("Updating site for build version ${buildVersion}");
String downloadPageText = new File("src/site/markdown/download.md").text;
def regex = /(?m)<dependency>\s*<groupId>gov.va.vinci.leo<\/groupId>\s*<artifactId>leo(-base|-client|-core|-service)<\/artifactId>\s*<version>[^\s]*<\/version>\s*<\/dependency>/
String newMavenDep= '''
   <dependency>
       <groupId>gov.va.vinci</groupId>
       <artifactId>leo</artifactId>
       <version>''';
newMavenDep += buildVersion + "</version>\n   </dependency>";
newFile = downloadPageText.replaceAll(regex, newMavenDep);
new File("src/site/markdown/download.md").delete();
new File("src/site/markdown/download.md") << newFile;
println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
