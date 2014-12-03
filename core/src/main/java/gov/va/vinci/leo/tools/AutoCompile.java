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

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.util.Arrays;

/**
 * Compile Java files.  Requires tool.jar (on Windows) and classes.jar (on Mac)
 * from java lib in the classpath to use this class.
 *
 * @author Prafulla
 * @author Thomas Ginter
 */
public class AutoCompile {

    /**
     * Compile the files in the list.  Optionally provide a destination directory where the
     * compiled files should be placed.
     *
     * @param files           List of java files to be compiled
     * @param outputDirectory Optional, output directory where the compiled files will be written
     * @throws Exception If there are errors compiling the files or we are unable to get a compiler
     */
    public static void compileFiles(File[] files, String outputDirectory) throws Exception {
        //If the list is null there is nothing to do
        if (files == null) {
            return;
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new Exception("Unable to get a Compiler from the ToolProvider");
        }
        StandardJavaFileManager stdFileManager = compiler.getStandardFileManager(null, null, null);
        Iterable<String> compilationOptions
                = (StringUtils.isBlank(outputDirectory)) ? null : Arrays.asList(new String[]{"-d", outputDirectory});
        try {
            Iterable<? extends JavaFileObject> compilationUnits = stdFileManager.getJavaFileObjectsFromFiles(Arrays.asList(files));
            compiler.getTask(null, stdFileManager, null, compilationOptions, null, compilationUnits).call();
        } finally {
            stdFileManager.close();
        }//finally

    }//compileFiles method
}//end AutocCompile Class