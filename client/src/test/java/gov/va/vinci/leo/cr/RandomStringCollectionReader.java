package gov.va.vinci.leo.cr;

import gov.va.vinci.leo.descriptors.LeoConfigurationParameter;
import gov.va.vinci.leo.tools.ConfigurationParameterImpl;
import gov.va.vinci.leo.tools.LeoUtils;
import gov.va.vinci.leo.types.CSI;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.resource.metadata.ConfigurationParameter;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * This reader populates a specified number of CAS objects with random strings.  Useful primarily as an example of the
 * implementation of the BaseLeoCollectionReader API.
 *
 * User: Thomas Ginter
 * Date: 10/27/14
 * Time: 09:15
 */
public class RandomStringCollectionReader extends BaseLeoCollectionReader {

    /**
     * Number of strings produced so far.
     */
    protected int currentString = 0;
    /**
     * Number of strings that the reader will create.
     */
    @LeoConfigurationParameter
    protected int numberOfStrings = 0;
    /**
     * Maximum length of each generated string
     */
    @LeoConfigurationParameter
    protected int maxStringLength = MAX_STRING_LENGTH;
    /**
     * Generates a random length for each string.
     */
    protected Random random = new Random(System.currentTimeMillis());
    /**
     * Maximum length of the generated string.
     */
    protected static final int MAX_STRING_LENGTH = 16;

    public RandomStringCollectionReader() {
        this.numberOfStrings = 1;
    }

    public RandomStringCollectionReader(int numberOfStrings) {
        this.numberOfStrings = numberOfStrings;
    }

    public int getNumberOfStrings() {
        return numberOfStrings;
    }

    public RandomStringCollectionReader setNumberOfStrings(int numberOfStrings) {
        this.numberOfStrings = numberOfStrings;
        return this;
    }

    public int getMaxStringLength() {
        return maxStringLength;
    }

    public RandomStringCollectionReader setMaxStringLength(int maxStringLength) {
        this.maxStringLength = maxStringLength;
        return this;
    }

    /**
     * @param aCAS the CAS to populate with the next document;
     * @throws CollectionException if there is a problem getting the next and populating the CAS.
     */
    @Override
    public void getNext(CAS aCAS) throws CollectionException, IOException {
        int length = random.nextInt(MAX_STRING_LENGTH);
        aCAS.setDocumentText(RandomStringUtils.randomAlphanumeric(length));

        //Create the CSI annotation and set the properties
        try {
            JCas jCas = aCAS.getJCas();
            CSI csi = new CSI(jCas);
            csi.setBegin(0);
            csi.setEnd(length);
            csi.setID("" + currentString);
            csi.addToIndexes();

            csi.setRowData(new StringArray(jCas, length));
            for(int i = 0; i < length; i++)
                csi.setRowData(i, "testRowData" + UUID.randomUUID().toString());

            csi.setPropertiesKeys(new StringArray(jCas, length));
            for(int i = 0; i < length; i++)
                csi.setPropertiesKeys(i, "testKeys" + UUID.randomUUID().toString());

            csi.setPropertiesValues(new StringArray(jCas, length));
            for(int i = 0; i < length; i++)
                csi.setPropertiesValues(i, "testValues" + UUID.randomUUID().toString());
        } catch (CASException e) {
            throw new CollectionException(e);
        }

        currentString++;
    }

    /**
     * @return true if and only if there are more elements available from this CollectionReader.
     * @throws IOException
     * @throws CollectionException
     */
    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return currentString < numberOfStrings;
    }

    /**
     * Gets information about the number of entities and/or amount of data that has been read from
     * this <code>CollectionReader</code>, and the total amount that remains (if that information
     * is available).
     * <p/>
     * This method returns an array of <code>Progress</code> objects so that results can be reported
     * using different units. For example, the CollectionReader could report progress in terms of the
     * number of documents that have been read and also in terms of the number of bytes that have been
     * read. In many cases, it will be sufficient to return just one <code>Progress</code> object.
     *
     * @return an array of <code>Progress</code> objects. Each object may have different units (for
     * example number of entities or bytes).
     */
    @Override
    public Progress[] getProgress() {
        return new Progress[]{ new ProgressImpl(currentString, numberOfStrings, Progress.ENTITIES) };
    }


}
