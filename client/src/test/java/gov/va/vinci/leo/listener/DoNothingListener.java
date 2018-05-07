package gov.va.vinci.leo.listener;

/*
 * #%L
 * Leo Client
 * %%
 * Copyright (C) 2010 - 2017 Department of Veterans Affairs
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
