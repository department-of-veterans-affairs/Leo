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


import etm.core.aggregation.ExecutionAggregate;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.renderer.MeasurementRenderer;

import javax.management.*;
import javax.management.openmbean.*;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * Management bean interface for an exportable MBean to use JMX.
 *
 * Created by ryancornia on 1/17/17.
 */
public class JetmPerformance implements JetmPerformanceMBean {
    protected String[] itemNames = {
                                        "id",
                                        "parentId",
                                        "dateOfMeasurement",
                                        "measurementPoint",
                                        "numberOfMeasurements",
                                        "average",
                                        "min",
                                        "max",
                                        "total"
                                    };

    protected String[] itemDescriptions = {
                                            "Measurement Point id",
                                            "Parent id of this measurement if there is one. Null if this measurement has no parent. This only applies to nested measureuments.",
                                            "The date of the measurement",
                                            "The measurement point name.",
                                            "The number of measurements of the point.",
                                            "Average time (ms) of measurement.",
                                            "Min time (ms) of the measurement.",
                                            "Max time (ms) of the measurement.",
                                            "Total time (ms) of all measurements. "
                                        };
    protected OpenType[] itemTypes = {
                                SimpleType.INTEGER, // id of measurement
                                SimpleType.INTEGER, // parent id or null of Point
                                SimpleType.STRING, // date of measurement
                                SimpleType.STRING, // Measurement Point
                                SimpleType.LONG, // Number of measurements
                                SimpleType.DOUBLE, // Average of measurements
                                SimpleType.DOUBLE, // Min of measurements
                                SimpleType.DOUBLE, // Max of measurements
                                SimpleType.DOUBLE, // Total of measurements
                            };

    CompositeType snapshotType = new CompositeType("snapshot", "Jetm Data", itemNames, itemDescriptions, itemTypes);
    String[] index = { "dateOfMeasurement", "id" };
    TabularType jetmTableType = new TabularType("jetmSnapshots","List of Snapshots", snapshotType, index);
    EtmMonitor monitor = null;

    public JetmPerformance() throws OpenDataException {
        this(EtmManager.getEtmMonitor());
    }

    public JetmPerformance(EtmMonitor aEtmMonitor) throws OpenDataException {
        monitor  = aEtmMonitor;
    }

    public boolean getCollecting() {
        return monitor.isCollecting();
    }

    public boolean getStarted() {
        return monitor.isStarted();
    }

    public Date getStartDate() {
        return monitor.getMetaData().getStartTime();
    }

    public Date getLastResetDate() {
        return monitor.getMetaData().getLastResetTime();
    }

    /**
     * Return the current JETM performance metrics.
     *
     * @param reset If true, JETM metrics are reset after the gather. If false, they are not reset and the next
     *              call will be cumulative metrics.
     * @return The JETM metrics. The TabluarData includes columns: "dateOfMeasurement", "measurementPoint", "numberOfMeasurements", "average", "min", "max", "total"
     * @throws OpenDataException
     */
    @Override
    public TabularData metrics(boolean reset) throws OpenDataException {
        final TabularDataSupport jetmSnapshot = new TabularDataSupport(jetmTableType);
        final Long gatherTime = new Date().getTime();
        final WrappedInteger childCounter = new WrappedInteger(0);

        monitor.render(new MeasurementRenderer() {
            @Override
            public void render(Map map) {
                for (Object value: map.values())
                {
                    List<CompositeData> rows = processRow((ExecutionAggregate) value, null, gatherTime, childCounter);
                    CompositeData[] rowsArray = rows.toArray(new CompositeData[rows.size()]);
                    jetmSnapshot.putAll(rowsArray);
                }
            }
        });

        if (reset) {
            resetMetrics();
        }

        return jetmSnapshot;
    }

    private List<CompositeData> processRow(ExecutionAggregate aggregate, Integer parentId, Long gatherTime, WrappedInteger childCounter) {
        List<CompositeData> results = new ArrayList<>();
        Integer id = childCounter.incrementAndGet();
        Object[] itemValues = {
                id,
                parentId,
                gatherTime.toString(),
                aggregate.getName(),
                aggregate.getMeasurements(),
                aggregate.getAverage(),
                aggregate.getMin(),
                aggregate.getMax(),
                aggregate.getTotal()
        };
        CompositeData result = null;
        try {
            result = new CompositeDataSupport(snapshotType, itemNames, itemValues);
        } catch (OpenDataException e) {
            throw new RuntimeException(e);
        }

        results.add(result);

        if (aggregate.hasChilds()) {
            for (Object v: aggregate.getChilds().values()) {
                results.addAll(processRow((ExecutionAggregate) v, id, gatherTime, childCounter));
            }
        }
        return results;
    }

    /**
     * Resets JETM metrics immediately.
     */
    @Override
    public void resetMetrics() {
        EtmMonitor monitor = EtmManager.getEtmMonitor();
        monitor.reset();;
    }

    public class WrappedInteger {
        int counter;
        public WrappedInteger(int value) {
            this.counter = value;
        }

        public int incrementAndGet() {
            this.counter++;
            return this.counter;
        }


    }

    /**
     * Registers JETM with the MBeanServer in the JVM.
     * @throws MalformedObjectNameException
     * @throws OpenDataException
     * @throws NotCompliantMBeanException
     * @throws InstanceAlreadyExistsException
     * @throws MBeanRegistrationException
     */
    public static void registerJetm() throws MalformedObjectNameException, OpenDataException, NotCompliantMBeanException, InstanceAlreadyExistsException, MBeanRegistrationException {
        /** Register JETM bean **/
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();

        /** Construct the ObjectName for the JETM server. **/
        ObjectName mbeanName = new ObjectName(JetmPerformanceMBean.DEFAULT_BEAN_NAME);
        JetmPerformanceMBean mbean = new JetmPerformance();
        server.registerMBean(mbean, mbeanName);
    }
}
