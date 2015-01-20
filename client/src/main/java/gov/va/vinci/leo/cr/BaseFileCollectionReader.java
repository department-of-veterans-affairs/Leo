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

import com.google.gson.Gson;
import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Get a list of the files in a directory and return them one at a time.  Optionally a
 * recurse flag can also be set which causes the reader to search recursively
 * through the entire folder structure of the parent directory.
 *
 * @author thomasginter
 */
public abstract class BaseFileCollectionReader extends BaseLeoCollectionReader {
    /**
     * Input Directory File object to be searched for available files.
     */
    protected File mInDir = null;

    /**
     * Recurse flag we will search recursively in sub-directories if true.
     * Defaults to false.
     */
    protected boolean mRecurse = false;

    /**
     * Index of the next file to be processed.
     */
    protected int mFileIndex = 0;

    /**
     * Array of File objects being processed.
     */
    protected ArrayList<File> mFileCollection = new ArrayList<File>();

    /**
     * Encoding type for the files being read in. Defaults to system default.
     */
    protected String mEncoding = null;

    /**
     * Filters out the files found by filename extension.
     */
    protected FilenameFilter filenameFilter = null;

    /**
     * Name of the input Directory Parameter.
     */
    public final static String inputDirectoryParam = "inputDirectory";
    /**
     * Name of the recurse flag Parameter.
     */
    public final static String recurseParam = "find_recurse";
    /**
     * Name of the encoding parameter.
     */
    public final static String encodingParam = "encoding";
    /**
     * One or more file name extensions for filtering input files.
     */
    public final static String filterParam = "fileExtensionsFilter";

    /**
     * Logger for class.
     */
    protected Logger LOG = Logger.getLogger(this.getClass());

    /**
     * Default constructor used during UIMA initialization.
     */
    public BaseFileCollectionReader() {

    }

    /**
     * Constructor that sets the input directory to be searched and the recurse flag that
     * controls whether or not the reader will descend in to subdirectories.
     *
     * @param inputDirectory Input directory to be searched
     * @param recurse        Recurse flag will descend into subdirectories if true, defaults to false.
     */
    public BaseFileCollectionReader(File inputDirectory, boolean recurse) {
        this(inputDirectory, recurse, null);
    }//Constructor with inputDirectory and recurse flag input params

    /**
     * Initialize with the input directory to be searched, recurse flag for parsing into subdirectories,
     * and file name filter to be used.
     *
     * @param inputDirectory Input directory to be searched
     * @param recurse        Recurse flag will descend into subdirectories if true, defaults to false
     * @param filter         FileName extention filter to use, if null defaults to <code>.txt</code>
     */
    public BaseFileCollectionReader(File inputDirectory, boolean recurse, SuffixFileFilter filter) {
        this(inputDirectory, null, recurse, filter);
    }

    /**
     * Initialize with the input directory to be searched, recurse flag for parsing into subdirectories,
     * and file name filter to be used.
     *
     * @param inputDirectory Input directory to be searched
     * @param encoding       Encoding format to use when reading in the data
     * @param recurse        Recurse flag will descend into subdirectories if true, defaults to false
     * @param filter         FileName extention filter to use, if null defaults to <code>.txt</code>
     */
    public BaseFileCollectionReader(File inputDirectory, String encoding,  boolean recurse, SuffixFileFilter filter) {
        if (inputDirectory == null || !inputDirectory.isDirectory()) {
            throw new IllegalArgumentException("Input Directory must not be null, and must point to an existing directory. ('" + inputDirectory + "')");
        }

        filenameFilter = filter;
        setInputDirectory(inputDirectory);
        setRecurseFlag(recurse);
        mFileIndex = 0;
        this.mEncoding = encoding;
    }

    /**
     * This method is called during initialization, and does nothing by default. Subclasses should override it to perform one-time startup logic.
     */
    @Override
    public void initialize() throws ResourceInitializationException {
        super.initialize();
        mInDir = new File((String)getConfigParameterValue(inputDirectoryParam));

        if (getConfigParameterValue(recurseParam) != null) {
            Boolean tmpValue = (Boolean) getConfigParameterValue(recurseParam);
            if (tmpValue) {
                mRecurse = true;
            }//if tmpValue == TRUE
        }//if recurseParam

        if (getConfigParameterValue(encodingParam) != null) {
            mEncoding = (String) getConfigParameterValue(encodingParam);
        } else {
            mEncoding = Charset.defaultCharset().displayName();
        }

        if (getConfigParameterValue(filterParam) != null) {
            filenameFilter = new Gson().fromJson((String) getConfigParameterValue(filterParam), SuffixFileFilter.class);
        }

        //Initialize the collection
        if (mInDir != null) {
            findFiles(mInDir);
        }//if mInDir != null

        mFileIndex = 0;
    }//initialize method

    /**
     * Find the list of files that meet the requirements.
     *
     * @param f the file to search. This should be a directory.
     */
    protected void findFiles(File f) {
        if (f == null || !f.exists()) return;

        File[] files;
        if (filenameFilter != null) {
            files =f.listFiles(filenameFilter);
            File[] directories = f.listFiles(new FileFilter() {
                public boolean accept(File f) {
                    return f.isDirectory();
                }
            });
            files = ArrayUtils.addAll(files, directories);
        } else {
            files = f.listFiles();
        }

        for (File file : files) {
            if (!file.isDirectory()) {
                mFileCollection.add(file);
            } else if (mRecurse) {
                findFiles(file);
            }//else if mRecurse
        }//for
    }//findFiles method

    /**
     * Set the inputDirectory for this FileSubReader object.
     *
     * @param inputDirectory the input directory to load files from.
     */
    public void setInputDirectory(File inputDirectory) {
        mInDir = inputDirectory;
    }//setInputDirectory method

    /**
     * Set the recurse flag for this property.  The recurse flag controls whether or not this
     * reader will recurse through subdirectories to find files or simply search the parent
     * directory alone.
     *
     * @param recurse  if true, sub directories are also searched, otherwise just the specified directory is
     *                 used for input.
     */
    public void setRecurseFlag(boolean recurse) {
        mRecurse = recurse;
    }//setRecurseFlag method

    /**
     * Return the number of documents in the set.
     *
     * @return the size of the collection.
     *
     */
    public int getCollectionSize() {
        return mFileCollection.size();
    }//getCollectionSize method

    /**
     * @return the index of the current document in the collection.
     */
    public int getCurrentIndex() {
        return mFileIndex;
    }//getCurrentIndex

    /**
     * @return true if there is another document in the collection, false if not.
     * @throws java.io.IOException if there is an error reading the data.
     * @throws org.apache.uima.collection.CollectionException if retrieval of the next file fails
     */
    public boolean hasNext() throws IOException, CollectionException {
        return (mFileIndex < mFileCollection.size());
    }//hasNext method

    /**
     * Get the next file to be processed in the pipeline.
     * @param aCAS the cas to populate with the next document.
     * @throws java.io.IOException if there is an error reading the data.
     * @throws org.apache.uima.collection.CollectionException if retrieval of the next file fails
     */
    public abstract void getNext(CAS aCAS) throws IOException, CollectionException;


    /**
     *
     * @return an array of Progress objects. Each object may have different units (for example number of entities or bytes).
     */
    @Override
    public Progress[] getProgress() {
        return new Progress[]{new ProgressImpl(getCurrentIndex(),
                getCollectionSize(),
                Progress.ENTITIES)};
    }//getProgress method

    /**
     * Return the encoding format that this CollectionReader will use for the source data.
     *
     * @return encoding format String.
     */
    public String getEncoding() {
        return mEncoding;
    }

    /**
     * Set the file encoding from the encoding string provided.  Determines the kind of encoding to use when reading in
     * source data.
     *
     * @param encoding encoding format to use.
     */
    public void setEncoding(String encoding) {
        this.mEncoding = encoding;
    }

    /**
     * Create a map of parameter names and values from the parameters in the static inner Param class as well as class
     * field variables.  Then return a CollectionReader which has been initialized by the framework using the
     * parameter settings provided.
     *
     * @return CollectionReader object.
     * @throws ResourceInitializationException if there is an error initializing the CollectionReader.
     */
    public CollectionReader produceCollectionReader() throws ResourceInitializationException {
        Map<String, Object> parameterValues = new HashMap<String, Object>();
        parameterValues.put(Param.ENCODING.getName(), mEncoding);
        if (filenameFilter != null) {
            parameterValues.put(Param.FILE_EXTENSIONS_FILTER.getName(), new Gson().toJson(filenameFilter));
        }
        parameterValues.put(Param.FIND_RECURSE.getName(), mRecurse);
        parameterValues.put(Param.INPUT_DIRECTORY.getName(), mInDir.getAbsolutePath());
        return produceCollectionReader(LeoUtils.getStaticConfigurationParameters(Param.class), parameterValues);
    }

    /**
     * Static inner class for holding parameter information.
     */
    public static class Param extends BaseLeoCollectionReader.Param {
        /**
         * Input directory to read from.
         */
        public static ConfigurationParameter INPUT_DIRECTORY =
                new ConfigurationParameterImpl("inputDirectory", "The directory to use for reading in files",
                        ConfigurationParameter.TYPE_STRING, true, false, new String[] {});
        /**
         * Determine if inputDirectory is searched resursively or not.
         */
        public static ConfigurationParameter FIND_RECURSE =
                new ConfigurationParameterImpl("find_recurse", "If true, the directory is recursively searched.",
                        ConfigurationParameter.TYPE_BOOLEAN, false, false, new String[] {});

        /**
         * The document encoding. (ie UTF-8)
         */
        public static ConfigurationParameter ENCODING =
                new ConfigurationParameterImpl("encoding", "The text encoding used in the file.",
                        ConfigurationParameter.TYPE_STRING, false, false, new String[] {});

        /**
         * A file extension filter to only return documents matching a certain file extension.
         */
        public static ConfigurationParameter FILE_EXTENSIONS_FILTER =
            new ConfigurationParameterImpl("fileExtensionsFilter", "A file extension filter to only return documents matching a certain file extension.",
                    ConfigurationParameter.TYPE_STRING, false, false, new String[] {});
    }
}//FileSubReader class
