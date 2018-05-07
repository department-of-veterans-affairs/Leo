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
import gov.va.vinci.leo.descriptors.LeoConfigurationParameter;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Get a list of the files in a directory and return them one at a time.  Optionally a
 * recurse flag can also be set which causes the reader to search recursively
 * through the entire folder structure of the parent directory.
 *
 * @author thomasginter
 */
public abstract class BaseFileCollectionReader extends BaseLeoCollectionReader {

    /**
     * Path to the input directory.  This variable is used to pass the value from the reader object to the CollectionReader
     * descriptor for initialization.  The mInDir will be created from this path.
     */
    @LeoConfigurationParameter(description = "Path to the input directory", mandatory = true)
    protected String inputDirectoryPath = null;

    /**
     * Recurse flag we will search recursively in sub-directories if true.
     * Defaults to false.
     */
    @LeoConfigurationParameter(description = "If true then recurse in subdirectories, defaults to false.")
    protected boolean mRecurse = false;

    /**
     * Encoding type for the files being read in. Defaults to system default.
     */
    @LeoConfigurationParameter
    protected String mEncoding = null;

    /**
     * JSON representation of the filename filter used to pass this object to the CollectionReader descriptor for
     * initialization.
     */
    @LeoConfigurationParameter(description = "JSON representation of the filename filter.")
    protected String fileNameFilterJSON = null;

    /**
     * Class name of the file name filter this reader is using.
     */
    @LeoConfigurationParameter(description = "Cannonical name of the filename filter")
    protected String fileNameFilterName = null;

    /**
     * Input Directory File object to be searched for available files.
     */
    protected File mInDir = null;

    /**
     * Index of the next file to be processed.
     */
    protected int mFileIndex = 0;

    /**
     * Array of File objects being processed.
     */
    protected ArrayList<File> mFileCollection = new ArrayList<File>();

    /**
     * Filters out the files found by filename extension.
     */
    protected FilenameFilter filenameFilter = null;

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
        if (inputDirectory == null || !inputDirectory.isDirectory()) {
            throw new IllegalArgumentException("Input Directory must not be null, and must point to an existing directory. ('" + inputDirectory + "')");
        }
        this.setInputDirectory(inputDirectory);
        this.mRecurse = recurse;
    }//Constructor with inputDirectory and recurse flag input params

    /**
     * This method is called during initialization, and does nothing by default. Subclasses should override it to perform one-time startup logic.
     */
    @Override
    public void initialize() throws ResourceInitializationException {
        super.initialize();

        mInDir = new File(inputDirectoryPath);
        if(mInDir == null || !mInDir.exists() || !mInDir.isDirectory())
            throw new ResourceInitializationException("Input Directory does not exist or is not a directory!", null);

        if(StringUtils.isNotBlank(fileNameFilterJSON) && StringUtils.isNotBlank(fileNameFilterName)) {
            try {
                Class<?> filterClass = Class.forName(fileNameFilterName);
                filenameFilter = new Gson().fromJson(fileNameFilterJSON, (Class<? extends AbstractFileFilter>) filterClass);
            } catch (ClassNotFoundException e) {
                throw new ResourceInitializationException(e);
            }
        }

        findFiles(mInDir);
        mFileIndex = 0;
    }//initialize method

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
    public <T extends BaseFileCollectionReader> T setEncoding(String encoding) {
        this.mEncoding = encoding;
        return (T) this;
    }

    /**
     * Return the input directory this reader will search for files.
     *
     * @return File object pointing to the input directory
     */
    public File getInputDirectory() {
        return mInDir;
    }

    /**
     * Set the inputDirectory for this FileSubReader object.
     *
     * @param inputDirectory the input directory to load files from.
     */
    public <T extends BaseFileCollectionReader> T setInputDirectory(File inputDirectory) {
        this.mInDir = inputDirectory;
        this.inputDirectoryPath = mInDir.getAbsolutePath();
        return (T) this;
    }//setInputDirectory method

    /**
     * Get the filename filter that will be used to filter files found in the input directory.
     *
     * @return FilenameFilter
     */
    public FilenameFilter getFilenameFilter() {
        return filenameFilter;
    }

    /**
     * Set the FilenameFilter that this object will use to filter the files found in the input directory.
     *
     * @param filenameFilter FilenameFilter
     * @param <T extends BaseFileCollectionReader> Type of the reader instance to return
     * @return reference to this reader instance
     */
    public <T extends BaseFileCollectionReader> T setFilenameFilter(FilenameFilter filenameFilter) {
        this.filenameFilter = filenameFilter;
        if(filenameFilter != null) {
            this.fileNameFilterJSON = new Gson().toJson(filenameFilter);
            this.fileNameFilterName = filenameFilter.getClass().getCanonicalName();
        }
        return (T) this;
    }

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
     * Set the recurse flag for this property.  The recurse flag controls whether or not this
     * reader will recurse through subdirectories to find files or simply search the parent
     * directory alone.
     *
     * @param recurse  if true, sub directories are also searched, otherwise just the specified directory is
     *                 used for input.
     */
    public <T extends BaseFileCollectionReader> T setRecurseFlag(boolean recurse) {
        mRecurse = recurse;
        return (T) this;
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
        LOG.debug("Progress: " + mFileIndex + " of " + mFileCollection.size());
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

}//FileSubReader class
