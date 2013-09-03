package com.fingerprintsoft.vitunit.spring.test.context;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestContextManager;

public class VitUnitTestContextManager extends TestContextManager {

	public VitUnitTestContextManager(Class<?> testClass,
			String defaultContextLoaderClassName) {
		super(testClass, defaultContextLoaderClassName);
	}

	public VitUnitTestContextManager(Class<?> testClass) {
		super(testClass);
	}
	
	public TestContext getSpringTestContext() {
		return super.getTestContext();
	}

}
