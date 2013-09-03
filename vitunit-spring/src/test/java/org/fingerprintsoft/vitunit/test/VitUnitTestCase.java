/*
 * VitUnitTestCase.java
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
package org.fingerprintsoft.vitunit.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.springframework.test.context.ContextConfiguration;

import com.fingerprintsoft.vitunit.annotation.DataSourceConfiguration;
import com.fingerprintsoft.vitunit.exception.SetupException;

/**
 * @author <a href=mailto:fuzail@fingerprintsoft.org>Fuzail Sarang</a>
 * 
 */
@ContextConfiguration(locations = "/test.xml")
public abstract class VitUnitTestCase {

    protected static DataSource loadDatasource(
	    final Class<? extends VitUnitTestCase> clazz) {

	DataSourceConfiguration annotation = clazz
		.getAnnotation(DataSourceConfiguration.class);

	String jdbcPropertiesFile = annotation.jdbcPropertiesFile();
	InputStream resourceAsStream = clazz.getClassLoader()
		.getResourceAsStream(jdbcPropertiesFile);
	Properties properties = new Properties();
	try {
	    properties.load(resourceAsStream);
	} catch (IOException e) {
	    throw new SetupException("Could not load properties.", e);
	}

	try {
	    return BasicDataSourceFactory.createDataSource(properties);
	} catch (Exception e) {
	    throw new SetupException("Could not create datasource.", e);
	}
    }
}
