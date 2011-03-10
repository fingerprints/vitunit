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

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.internal.runners.statements.RunAfters;
import org.junit.internal.runners.statements.RunBefores;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

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
	return new VitUnitRunBefores(statement, befores, null);
    }

    @Override
    protected Statement withAfterClasses(Statement statement) {

	List<FrameworkMethod> afters = getTestClass().getAnnotatedMethods(
		AfterClass.class);
	return new VitUnitRunAfters(statement, afters, null);
    }

    class VitUnitRunAfters extends RunAfters {

	public VitUnitRunAfters(Statement next, List<FrameworkMethod> afters,
		Object target) {
	    super(next, afters, target);
	}

	@Override
	public void evaluate() throws Throwable {
	    super.evaluate();
	    testClass.evaluateAfterClass();
	}

    }

    class VitUnitRunBefores extends RunBefores {

	public VitUnitRunBefores(Statement next, List<FrameworkMethod> befores,
		Object target) {
	    super(next, befores, target);
	}

	@Override
	public void evaluate() throws Throwable {
	    testClass.evaluateBeforeClass();
	    super.evaluate();
	}

    }

}