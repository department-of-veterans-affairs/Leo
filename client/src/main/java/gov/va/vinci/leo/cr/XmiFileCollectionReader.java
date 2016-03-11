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

import gov.va.vinci.leo.tools.TextFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.impl.XmiCasDeserializer;
import org.apache.uima.collection.CollectionException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


/**
 * Get a list of the XMI files in a directory and populate the CAS when getNext(Cas) is
 * called on the reader. Optionally a
 * recurse flag can also be set which causes the reader to search recursively
 * through the entire folder structure of the parent directory.
 * <br/><br/>
 * Note: Collection readers, to be used within UIMA, need to be generated via the CollectionReader factory.
 * <br/><br/>
 * For example:
 * <br/><br/>
 * <pre>
 * {@code
 *
 *     CollectionReader reader = CollectionReaderFactory
 *                                  .generateXmiFileSubReader(new File("my-input-directory/xmi/"), true);
 *
 *  }</pre>
 *
 * XMI Sub reader
 *
 * @author vhaislcornir
 */
public class XmiFileCollectionReader extends BaseFileCollectionReader {

    /**
     * The default file name extension to filter on.
     */
    public static final String fileExtenstionString = ".xmi";
    /**
     * Logger for this class.
     */
    private static final Logger LOG = Logger.getLogger(XmiFileCollectionReader.class);

    /**
     * Default constructor used during UIMA initialization.
     */
    public XmiFileCollectionReader() {
        //Use a SuffixFileFilter by default
        this.setFilenameFilter(new SuffixFileFilter(fileExtenstionString));
    }

    /**
     * Constructor that sets the inputDirectory and recurse flag.
     *
     * @param inputDirectory Path to the directory that will be searched for files to process
     * @param recurse        If true then recurse into subdirectories.
     */
    public XmiFileCollectionReader(File inputDirectory, boolean recurse)  {
        super(inputDirectory, recurse);
        //Use a SuffixFileFilter by default
        this.setFilenameFilter(new SuffixFileFilter(fileExtenstionString));
    }

    /**
     * De-serialize the next XMI file in the collection.
     *
     * @see gov.va.vinci.leo.cr.BaseFileCollectionReader#getNext(org.apache.uima.cas.CAS)
     *
     * @param aCAS  the CAS to populate with the next element of the collection.
     * @throws java.io.IOException if there is an error reading from the files.
     * @throws org.apache.uima.collection.CollectionException if there is an error deserializing the XMI.
     */
    public void getNext(CAS aCAS) throws java.io.IOException, CollectionException {
        File next = mFileCollection.get(mFileIndex++);
        InputStream file = null;

        try {
            file = new FileInputStream(next);
            XmiCasDeserializer.deserialize(file, aCAS);
        } catch (SAXException e) {
            throw new CollectionException(e);
        } finally {
            try {
                file.close();
            } catch (Exception e) {
                LOG.warn("Error closing file: " + e);
            }
        }
    }

}