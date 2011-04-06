/*
 * VitUnitJUnit4Runner.java
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

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import com.fingerprintsoft.vitunit.annotation.DataSetConfiguration;
import com.fingerprintsoft.vitunit.runner.model.VitUnitTestClass;

/**
 * @author <a href=mailto:fuzail@fingerprintsoft.org>Fuzail Sarang</a>
 * 
 */
public class VitUnitJUnit4Runner extends BlockJUnit4ClassRunner {

    private final VitUnitTestClass testClass;

    public VitUnitJUnit4Runner(Class<?> klass) throws InitializationError {
	super(klass);
	testClass = new VitUnitTestClass(klass);
    }

    @Override
    protected Statement withBeforeClasses(Statement statement) {
	List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(
		BeforeClass.class);
	return new VitUnitRunBeforeClass(statement, befores, null);
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {

	List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(
		AfterClass.class);
	return new VitUnitRunAfterClass(statement, afters, null);
    }

    /**
     * @see org.junit.runners.BlockJUnit4ClassRunner#withBefores(org.junit.runners.model.FrameworkMethod,
     *      java.lang.Object, org.junit.runners.model.Statement)
     */
    @Override
    protected Statement withBefores(FrameworkMethod method, Object target,
	    Statement statement) {
	List<FrameworkMethod> befores = getTestClass().getAnnotatedMethods(
		Before.class);

	if (method.getAnnotation(DataSetConfiguration.class) != null) {
	    return new VitUnitRunBefores(statement, befores, target,
		    method.getAnnotation(DataSetConfiguration.class));
	}

	return befores.isEmpty() ? statement : new RunBefores(statement,
		befores, target);
    }

    /**
     * @see org.junit.runners.BlockJUnit4ClassRunner#withAfters(org.junit.runners.model.FrameworkMethod,
     *      java.lang.Object, org.junit.runners.model.Statement)
     */
    @Override
    protected Statement withAfters(FrameworkMethod method, Object target,
	    Statement statement) {
	List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(
		After.class);
	if (method.getAnnotation(DataSetConfiguration.class) != null) {
	    return new VitUnitRunAfters(statement, afters, target,
		    method.getAnnotation(DataSetConfiguration.class));
	}

	return afters.isEmpty() ? statement : new RunAfters(statement, afters,
		target);
    }

    class VitUnitRunAfterClass extends RunAfters {

	public VitUnitRunAfterClass(Statement next,
		List<FrameworkMethod> afters, Object target) {
	    super(next, afters, target);
	}

	@Override
	public void evaluate() throws Throwable {
	    super.evaluate();
	    testClass.evaluateAfterClass();
	}

    }

    class VitUnitRunBeforeClass extends RunBefores {

	public VitUnitRunBeforeClass(Statement next,
		List<FrameworkMethod> befores, Object target) {
	    super(next, befores, target);
	}

	@Override
	public void evaluate() throws Throwable {
	    testClass.evaluateBeforeClass();
	    super.evaluate();
	}

    }

    class VitUnitRunAfters extends RunAfters {

	private DataSetConfiguration configuration;

	public VitUnitRunAfters(Statement next, List<FrameworkMethod> afters,
		Object target, DataSetConfiguration dataSetConfiguration) {
	    super(next, afters, target);
	    configuration = dataSetConfiguration;
	    
	}

	@Override
	public void evaluate() throws Throwable {
	    super.evaluate();
	    testClass.evaluateAfters(configuration);
	}

    }

    class VitUnitRunBefores extends RunBefores {

	private DataSetConfiguration configuration;

	public VitUnitRunBefores(Statement next, List<FrameworkMethod> befores,
		Object target, DataSetConfiguration dataSetConfiguration) {
	    super(next, befores, target);
	    configuration = dataSetConfiguration;
	}

	@Override
	public void evaluate() throws Throwable {
	    testClass.evaluateBefores(configuration);
	    super.evaluate();
	}
    }

}