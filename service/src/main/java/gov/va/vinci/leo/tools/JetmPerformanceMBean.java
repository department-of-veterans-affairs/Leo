package gov.va.vinci.leo.tools;

/*
 * #%L
 * Leo Service
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

import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import java.util.Date;

/**
 *
 * Management bean interface for an exportable MBean to use JMX.
 *
 * Created by ryancornia on 1/17/17.
 */
public interface JetmPerformanceMBean {

    public static String DEFAULT_BEAN_NAME="gov.va.vinci.performance:type=Jetm";

    /**
     * Return the current JETM performance metrics.
     *
     * @param reset If true, JETM metrics are reset after the gather. If false, they are not reset and the next
     *              call will be cumulative metrics.
     * @return The JETM metrics. The TabluarData includes columns: "dateOfMeasurement", "measurementPoint", "numberOfMeasurements", "average", "min", "max", "total"
     * @throws OpenDataException
     */
    public TabularData metrics(boolean reset) throws OpenDataException;

    /**
     * Resets JETM metrics immediately.
     */
    public void resetMetrics();

    /**
     * Return true if the monitor is currently collecting, or else false.
     * @return true if the monitor is currently collecting, or else false.
     */
    public boolean getCollecting();

    /**
     * True if the monitor has been started, false if it has not.
     * @return True if the monitor has been started, false if it has not.
     */
    public boolean getStarted();

    /**
     * The date the monitor was started or null if the monitor hasn't been started.
     * @return The date the monitor was started or null if the monitor hasn't been started.
     */
    public Date getStartDate();

    /**
     * Date of the last rest of the monitor, or null if no reset has occured.
     * @return Date of the last rest of the monitor, or null if no reset has occured.
     */
    public Date getLastResetDate();
}
