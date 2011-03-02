/*
 * VitUnitSpringJunit4Runner.java
 *
 * Copyright (C) 2010 Fingerprints Software
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library.
 * If not, see <http://www.gnu.org/licenses/>.
 */
package com.fingerprintsoft.vitunit.runner;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.internal.runners.InitializationError;
import org.junit.runner.notification.RunNotifier;
import org.springframework.test.annotation.ProfileValueUtils;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author <a href=mailto:fuzail@fingerprintsoft.org>Fuzail Sarang</a>
 * 
 */
public class VitUnitSpringJUnit4Runner extends SpringJUnit4ClassRunner {

    private static final Log logger = LogFactory
	    .getLog(SpringJUnit4ClassRunner.class);

    /**
     * @param clazz
     * @throws InitializationError
     */
    public VitUnitSpringJUnit4Runner(Class<?> clazz) throws InitializationError {
	super(clazz);
    }

    public void run(final RunNotifier notifier) {
	if (!ProfileValueUtils.isTestEnabledInThisEnvironment(getTestClass()
		.getJavaClass())) {
	    notifier.fireTestIgnored(getDescription());
	    return;
	}
	
	new VitUnitSpringClassRoadie(notifier, getTestClass(),
		getDescription(), new Runnable() {
		    public void run() {
			runMethods(notifier);
		    }
		}).runProtected();
    }

}
