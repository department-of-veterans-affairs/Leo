package gov.va.vinci.leo.listener;

/*
 * #%L
 * Leo Client
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

import org.apache.uima.aae.client.UimaASProcessStatus;
import org.apache.uima.aae.monitor.statistics.AnalysisEnginePerformanceMetrics;
import org.apache.uima.cas.CAS;
import org.apache.uima.util.ProcessTrace;

import java.util.List;

public class MockUimaASProcessStatus implements UimaASProcessStatus {
    @Override
    public String getCasReferenceId() {
        return null;
    }

    @Override
    public String getParentCasReferenceId() {
        return null;
    }

    @Override
    public CAS getCAS() {
        return null;
    }

    @Override
    public List<AnalysisEnginePerformanceMetrics> getPerformanceMetricsList() {
        return null;
    }

    @Override
    public boolean isException() {
        return false;
    }

    @Override
    public String getStatusMessage() {
        return null;
    }

    @Override
    public List<Exception> getExceptions() {
        return null;
    }

    @Override
    public List<String> getFailedComponentNames() {
        return null;
    }

    @Override
    public ProcessTrace getProcessTrace() {
        return null;
    }

    @Override
    public boolean isEntitySkipped() {
        return false;
    }
}