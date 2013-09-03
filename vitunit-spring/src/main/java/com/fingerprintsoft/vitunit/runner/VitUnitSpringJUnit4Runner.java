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

import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestContextManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks;
import org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks;

import com.fingerprintsoft.vitunit.annotation.DataSetConfiguration;
import com.fingerprintsoft.vitunit.runner.model.VitUnitTestClass;
import com.fingerprintsoft.vitunit.spring.test.context.VitUnitTestContextManager;

/**
 * @author <a href=mailto:fuzail@fingerprintsoft.org>Fuzail Sarang</a>
 * 
 */
public class VitUnitSpringJUnit4Runner extends SpringJUnit4ClassRunner {

	private static final Logger LOG = LoggerFactory
			.getLogger(VitUnitSpringJUnit4Runner.class);

	private final VitUnitTestClass testClass;

	public VitUnitSpringJUnit4Runner(Class<?> klass) throws InitializationError {
		super(klass);
		testClass = new VitUnitTestClass(klass);
	}

	@Override
	protected Statement withBeforeClasses(Statement statement) {
		Statement junitBeforeClasses = super.withBeforeClasses(statement);
		return new VitUnitRunBeforeClass(junitBeforeClasses,
				getTestContextManager());
	}

	@Override
	protected Statement withAfterClasses(Statement statement) {
		Statement junitBeforeClasses = super.withBeforeClasses(statement);
		return new VitUnitRunAfterClass(junitBeforeClasses,
				getTestContextManager());
	}

	/**
	 * @see org.junit.runners.BlockJUnit4ClassRunner#withBefores(org.junit.runners.model.FrameworkMethod,
	 *      java.lang.Object, org.junit.runners.model.Statement)
	 */
	@Override
	protected Statement withBefores(FrameworkMethod method, Object target,
			Statement statement) {
		Statement befores = super.withBefores(method, target, statement);

		if (method.getAnnotation(DataSetConfiguration.class) != null) {
			return new VitUnitRunBefores(befores, target, method.getMethod(),
					method.getAnnotation(DataSetConfiguration.class));
		}

		return new RunBeforeTestMethodCallbacks(befores, target,
				method.getMethod(), getTestContextManager());
	}

	/**
	 * @see org.junit.runners.BlockJUnit4ClassRunner#withAfters(org.junit.runners.model.FrameworkMethod,
	 *      java.lang.Object, org.junit.runners.model.Statement)
	 */
	@Override
	protected Statement withAfters(FrameworkMethod method, Object target,
			Statement statement) {

		Statement afters = super.withAfters(method, target, statement);
		if (method.getAnnotation(DataSetConfiguration.class) != null) {
			return new VitUnitRunAfters(afters, target, method.getMethod(),
					method.getAnnotation(DataSetConfiguration.class));
		}

		return new RunAfterTestMethodCallbacks(afters, target,
				method.getMethod(), getTestContextManager());
	}

	class VitUnitRunAfterClass extends RunAfterTestClassCallbacks {

		public VitUnitRunAfterClass(Statement next,
				TestContextManager testContextManager) {
			super(next, testContextManager);
		}

		@Override
		public void evaluate() throws Throwable {
			super.evaluate();
			if (LOG.isDebugEnabled()) {
				ApplicationContext applicationContext = ((VitUnitTestContextManager) getTestContextManager())
						.getSpringTestContext().getApplicationContext();
				LOG.debug("The application context is : " + applicationContext);
				LOG.debug("The number of configured beans are : "
						+ applicationContext.getBeanDefinitionCount());
			}
			testClass.evaluateAfterClass();
		}

	}

	@Override
	protected TestContextManager createTestContextManager(Class<?> clazz) {
		return new VitUnitTestContextManager(clazz,
				getDefaultContextLoaderClassName(clazz));
	}

	class VitUnitRunBeforeClass extends RunBeforeTestClassCallbacks {

		public VitUnitRunBeforeClass(Statement next,
				TestContextManager testContextManager) {
			super(next, testContextManager);
		}

		@Override
		public void evaluate() throws Throwable {
			if (LOG.isDebugEnabled()) {
				ApplicationContext applicationContext = ((VitUnitTestContextManager) getTestContextManager())
						.getSpringTestContext().getApplicationContext();
				LOG.debug("The application context is : " + applicationContext);
				LOG.debug("The number of configured beans are : "
						+ applicationContext.getBeanDefinitionCount());
			}
			testClass.evaluateBeforeClass();
			super.evaluate();
		}
	}

	class VitUnitRunAfters extends RunAfterTestMethodCallbacks {

		private DataSetConfiguration configuration;

		public VitUnitRunAfters(Statement afters, Object target, Method method,
				DataSetConfiguration dataSetConfiguration) {
			super(afters, target, method, getTestContextManager());
			configuration = dataSetConfiguration;
		}

		@Override
		public void evaluate() throws Throwable {
			super.evaluate();
			if (LOG.isDebugEnabled()) {
				ApplicationContext applicationContext = ((VitUnitTestContextManager) getTestContextManager())
						.getSpringTestContext().getApplicationContext();
				LOG.debug("The application context is : " + applicationContext);
				LOG.debug("The number of configured beans are : "
						+ applicationContext.getBeanDefinitionCount());
			}
			testClass.evaluateAfters(configuration);
		}

	}

	class VitUnitRunBefores extends RunBeforeTestMethodCallbacks {

		private DataSetConfiguration configuration;

		public VitUnitRunBefores(Statement befores, Object target,
				Method method, DataSetConfiguration dataSetConfiguration) {
			super(befores, target, method, getTestContextManager());
			configuration = dataSetConfiguration;
		}

		@Override
		public void evaluate() throws Throwable {
			if (LOG.isDebugEnabled()) {
				ApplicationContext applicationContext = ((VitUnitTestContextManager) getTestContextManager())
						.getSpringTestContext().getApplicationContext();
				LOG.debug("The application context is : " + applicationContext);
				LOG.debug("The number of configured beans are : "
						+ applicationContext.getBeanDefinitionCount());
			}
			testClass.evaluateBefores(configuration);
			super.evaluate();
		}
	}

}