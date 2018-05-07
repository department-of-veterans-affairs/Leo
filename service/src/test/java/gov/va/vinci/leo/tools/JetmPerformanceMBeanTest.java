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

import com.google.common.base.Strings;
import etm.core.configuration.BasicEtmConfigurator;
import etm.core.configuration.EtmManager;
import etm.core.monitor.EtmMonitor;
import etm.core.monitor.EtmPoint;
import org.junit.Test;

import javax.management.openmbean.CompositeData;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularData;
import java.text.DecimalFormat;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Created by ryancornia on 1/18/17.
 */
public class JetmPerformanceMBeanTest {

    @Test
    public void basicTypesTest() throws OpenDataException {
        JetmPerformance bean = new JetmPerformance();

        TabularData data = bean.metrics(false);
        assertEquals(data.getTabularType().getTypeName(), "jetmSnapshots");
        assertEquals(data.getTabularType().getRowType().getTypeName(), "snapshot");
        assertEquals(data.getTabularType().getRowType().keySet().size(), 9);
    }

    @Test
    public void metricsTest() throws OpenDataException, InterruptedException {
        JetmPerformance bean = new JetmPerformance();

        CreateMetrics createMetrics = new CreateMetrics();
        createMetrics.doCreateMetrics();

        TabularData data = bean.metrics(false);
        assertEquals(4, data.keySet().size());
        Iterator<CompositeData> iterator = (Iterator<CompositeData>) data.values().iterator();

        CompositeData row = iterator.next();
        String returnedName = (String) row.get("measurementPoint");

        assertEquals(row.values().size(), 9);

        assertNotNull(row.get("id"));
        assertEquals(1, row.get("id"));
        assertNull(row.get("parentId"));

        row = iterator.next();
        assertNotNull(row.get("id"));
        assertEquals(2, row.get("id"));
        assertEquals(1, row.get("parentId"));

        row = iterator.next();
        assertNotNull(row.get("id"));
        assertEquals(3, row.get("id"));
        assertEquals(2, row.get("parentId"));

        row = iterator.next();
        assertEquals(4, row.get("id"));
        assertNull(row.get("parentId"));

        Iterator<CompositeData> printIterator = (Iterator<CompositeData>) data.values().iterator();

        DecimalFormat decimalFormat = new  DecimalFormat("#.##");
        while (printIterator.hasNext()) {
            CompositeData d = printIterator.next();

            String parentId = d.get("parentId") == null ? "" : "" +  d.get("parentId");

        }

        assertTrue(bean.getCollecting());
        assertTrue(bean.getStarted());
        assertEquals(bean.getLastResetDate(), bean.getStartDate());

        bean.resetMetrics();;
        assertNotEquals(bean.getLastResetDate(), bean.getStartDate());

    }


    public class CreateMetrics {
        private final EtmMonitor etmMonitor = EtmManager.getEtmMonitor();

        public CreateMetrics() {
            BasicEtmConfigurator.configure(true);

            etmMonitor.start();
        }

        public void doCreateMetrics() throws InterruptedException {
            EtmPoint point = etmMonitor.createPoint("CreateMetrics:topZ");
            Thread.sleep(1000);
            point.collect();


            point = etmMonitor.createPoint("CreateMetrics:topPoint");

            for (int i=0; i< 10; i++) {
                nestedMethod(1, 2);
            }
            point.collect();

        }

        public void nestedMethod(int level, int maxLevel) throws InterruptedException {
            EtmPoint point = etmMonitor.createPoint("CreateMetrics:nestedMethod" + level);
            try {

                Thread.sleep((long)(Math.random() * 100));
                if (level < maxLevel) {
                    nestedMethod(level+1, maxLevel);
                }
            } finally {
                point.collect();
            }
        }
    }

}
