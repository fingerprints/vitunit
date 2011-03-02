/*
 * VitUnitSpringClassRoadie.java
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.internal.runners.ClassRoadie;
import org.junit.internal.runners.TestClass;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;

import com.fingerprintsoft.vitunit.exception.FailedBefore;
import com.fingerprintsoft.vitunit.runner.model.VitUnitTestClass;

/**
 * @author <a href=mailto:fuzail@fingerprintsoft.org>Fuzail Sarang</a>
 * 
 */
public class VitUnitSpringClassRoadie extends ClassRoadie {

    private VitUnitTestClass testClass;
    private TestClass fTestClass;

    /**
     * @param notifier
     * @param testClass
     * @param description
     * @param runnable
     */
    public VitUnitSpringClassRoadie(RunNotifier notifier, TestClass testClass,
	    Description description, Runnable runnable) {
	super(notifier, testClass, description, runnable);

	this.testClass = new VitUnitTestClass(testClass.getJavaClass());
	this.fTestClass = testClass;
    }

    public void runProtected() {
	try {
	    runBefores();
	    runUnprotected();
	} catch (FailedBefore e) {
	} finally {
	    runAfters();
	}
    }

    private void runBefores() throws FailedBefore {

	testClass.evaluateBeforeClass();

	try {
	    try {
		List<Method> befores = getBefores();
		for (Method before : befores)
		    before.invoke(null);
	    } catch (InvocationTargetException e) {
		throw e.getTargetException();
	    }
	} catch (org.junit.internal.AssumptionViolatedException e) {
	    throw new FailedBefore();
	} catch (Throwable e) {
	    addFailure(e);
	    throw new FailedBefore();
	}
    }

    private void runAfters() {
	testClass.evaluateAfterClass();

	List<Method> afters = getAfters();
	for (Method after : afters)
	    try {
		after.invoke(null);
	    } catch (InvocationTargetException e) {
		addFailure(e.getTargetException());
	    } catch (Throwable e) {
		addFailure(e); // Untested, but seems impossible
	    }
    }

    List<Method> getBefores() {
	return fTestClass.getAnnotatedMethods(BeforeClass.class);
    }

    List<Method> getAfters() {
	return fTestClass.getAnnotatedMethods(AfterClass.class);
    }

}
