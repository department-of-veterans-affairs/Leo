package gov.va.vinci.leo.listener;

import org.apache.log4j.Logger;
import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.EntityProcessStatus;

/**
 * Created by thomasginter on 3/30/16.
 */
public class DoNothingListener extends BaseListener {

    Logger logger = Logger.getLogger(DoNothingListener.class);

    /**
     * Default Constructor.
     */
    public DoNothingListener() {
        super();
    }

    /**
     * @param aCas    the CAS containing the processed entity and the analysis results
     * @param aStatus the status of the processing. This object contains a record of any Exception that occurred, as well as timing information.
     * @see UimaAsBaseCallbackListener#entityProcessComplete(CAS, EntityProcessStatus)
     */
    @Override
    public void entityProcessComplete(CAS aCas, EntityProcessStatus aStatus) {
        super.entityProcessComplete(aCas, aStatus);
        String documentText = aCas.getDocumentText();
        logger.info(documentText);
    }
}
