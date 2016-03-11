/**
 *
 */
package gov.va.vinci.leo.cr;

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

import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.leo.tools.TextFilter;
import gov.va.vinci.leo.types.CSI;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * Get a list of the files in a directory and return them one at a time.  Optionally a
 * recurse flag can also be set which causes the reader to search recursively
 * through the entire folder structure of the parent directory.
 * <br/><br/>
 * Note: Collection readers, to be used within UIMA, need to be generated via the CollectionReader factory.
 * <br/><br/>
 * For example, a file subreader would be created via:
 * <br/><br/>
 * <pre>
 * {@code
 *
 *     CollectionReader reader = CollectionReaderFactory
 *                                  .generateFileSubReader(new File("my-input-directory/sub-dir/"), true);
 *
 *  }</pre>
 *
 * @author thomasginter
 */
public class FileCollectionReader extends BaseFileCollectionReader {

    /**
     * Default file extension string.
     */
    public static final String fileExtensionString = ".txt";

    /**
     * Default constructor used during UIMA initialization.
     */
    public FileCollectionReader() {
        //Use a SuffixFileFilter by default
        this.setFilenameFilter(new SuffixFileFilter(fileExtensionString));
    }

    /**
     * Constructor that takes an input directory path and recurse flag as inputs.
     *
     * @param inputDirectory Path to the input directory to be read.
     * @param recurse        If true then recursively search sub-directories for files to process.
     */
    public FileCollectionReader(File inputDirectory, boolean recurse) {
        super(inputDirectory, recurse);
        //Use a SuffixFileFilter by default
        this.setFilenameFilter(new SuffixFileFilter(fileExtensionString));
    }

    /**
     * Get the next file to be processed in the pipeline.
     *
     * @param aCAS CAS to be populated with the next document.
     * @throws Exception if retrieval of the next file fails
     */
    @Override
    public void getNext(CAS aCAS) throws IOException, CollectionException {
        File next = mFileCollection.get(mFileIndex++);

        String fileText = (StringUtils.isBlank(mEncoding)) ? FileUtils.file2String(next) : FileUtils.file2String(next, mEncoding);
        String docText = LeoUtils.filterText(fileText, filters);
        aCAS.setDocumentText(docText);
        JCas jcas = null;
        try {
            jcas = aCAS.getJCas();
        } catch (CASException e) {
            throw new CollectionException(e);
        }
        CSI csi = new CSI(jcas);
        csi.setBegin(0);
        csi.setEnd(docText.length());
        csi.setID(next.getName());
        csi.setLocator(next.toURI().getPath());
        csi.addToIndexes();
    }

}//FileSubReader class
