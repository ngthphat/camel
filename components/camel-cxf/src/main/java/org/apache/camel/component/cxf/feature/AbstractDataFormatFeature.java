/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.camel.component.cxf.feature;

import java.util.List;
import java.util.logging.Logger;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.Interceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.phase.PhaseInterceptor;

/**
 * The abstract class for the data format feature
 */
public abstract class AbstractDataFormatFeature extends AbstractFeature {


    protected abstract Logger getLogger();

    protected void resetServiceInvokerInterceptor(Server server) {
        List<Interceptor> serviceInterceptor = server.getEndpoint().getService().getInInterceptors();
        removeInterceptorWhichIsInThePhases(serviceInterceptor, new String[]{Phase.INVOKE});
        serviceInterceptor.add(new MessageInvokerInterceptor());
    }

    @SuppressWarnings("unchecked")
    protected void removeInterceptorWhichIsInThePhases(List<Interceptor> interceptors, String[] phaseNames) {
        for (Interceptor i : interceptors) {
            if (i instanceof PhaseInterceptor) {
                PhaseInterceptor p = (PhaseInterceptor)i;
                for (String phaseName : phaseNames) {
                    if (p.getPhase().equals(phaseName)) {
                        getLogger().info("removing the interceptor " + p);
                        interceptors.remove(p);
                        break;
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected void removeInterceptorWhichIsOutThePhases(List<Interceptor> interceptors, String[] phaseNames) {
        for (Interceptor i : interceptors) {
            boolean outside = false;
            if (i instanceof PhaseInterceptor) {
                PhaseInterceptor p = (PhaseInterceptor)i;
                for (String phaseName : phaseNames) {
                    if (p.getPhase().equals(phaseName)) {
                        outside = true;
                        break;
                    }
                }
                if (!outside) {
                    getLogger().info("removing the interceptor " + p);
                    interceptors.remove(p);
                }
            }
        }
    }

}
