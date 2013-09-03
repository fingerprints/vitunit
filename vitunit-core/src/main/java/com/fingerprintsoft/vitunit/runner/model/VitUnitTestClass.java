/*
 * VitUnitTestClass.java
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
package com.fingerprintsoft.vitunit.runner.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.lang.StringUtils;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fingerprintsoft.vitunit.DBOperation;
import com.fingerprintsoft.vitunit.DBOperationManager;
import com.fingerprintsoft.vitunit.annotation.DataSetConfiguration;
import com.fingerprintsoft.vitunit.annotation.DataSourceConfiguration;
import com.fingerprintsoft.vitunit.exception.SetupException;

/**
 * @author <a href=mailto:fuzail@fingerprintsoft.org>Fuzail Sarang</a>
 * 
 */
public class VitUnitTestClass {

	private static final Logger logger = LoggerFactory
			.getLogger(VitUnitTestClass.class);

	private final Class<?> actualClass;

	private final DataSource dataSource;
	private final String[] deleteAllDataSets;
	private final String[] insertDataSets;
	private final String[] refreshDataSets;
	private final String[] updateDataSets;
	private final Class<? extends IDataTypeFactory> dataTypeFactory;

	public VitUnitTestClass(Class<?> actualClass) {
		super();
		this.actualClass = actualClass;
		validateAnnotationsPresent();
		dataSource = loadDatasource();

		deleteAllDataSets = getDeleteAllDatasets();
		insertDataSets = getInsertDatasets();
		refreshDataSets = getRefreshDatasets();
		updateDataSets = getUpdateDatasets();
		dataTypeFactory = getDataTypeFactory();

	}

	private void validateAnnotationsPresent() {
		if (getActualClass() == null
				|| !getActualClass().isAnnotationPresent(
						DataSetConfiguration.class)
				|| !getActualClass().isAnnotationPresent(
						DataSourceConfiguration.class)) {
			throw new SetupException("Failed to setup.");
		}

	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public Class<?> getActualClass() {
		return actualClass;
	}

	public void evaluateBeforeClass() {

		DBOperationManager opsManager = new DBOperationManager(dataSource,
				dataTypeFactory);

		if (getClean()) {
			buildDatabaseOperations(opsManager, insertDataSets,
					DatabaseOperation.CLEAN_INSERT);
		} else {
			buildDatabaseOperations(opsManager, insertDataSets,
					DatabaseOperation.INSERT);
		}

		buildDatabaseOperations(opsManager, refreshDataSets,
				DatabaseOperation.REFRESH);

		buildDatabaseOperations(opsManager, updateDataSets,
				DatabaseOperation.UPDATE);

		opsManager.execute();

		updateSequences(dataSource, getSequences());

	}

	public void evaluateAfterClass() {
		DBOperationManager opsManager = new DBOperationManager(dataSource,
				dataTypeFactory);

		buildDatabaseOperations(opsManager, deleteAllDataSets,
				DatabaseOperation.DELETE_ALL);

		opsManager.execute();

	}

	protected void updateSequences(DataSource dataSource, String[] sql) {
		Connection connection = null;
		Statement statement = null;

		try {
			connection = dataSource.getConnection();

			if (sql != null) {
				for (String query : sql) {
					if (StringUtils.isNotBlank(query)) {
						statement = connection.createStatement();
						statement.executeUpdate(query);
					}
				}
			}
		} catch (SQLException e) {
			logger.warn("An error occured while updating the sequences", e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// Do nothing
				}
			}
		}

	}

	protected String[] getSequences() {
		String[] sequences = null;
		StringBuffer sb = new StringBuffer();
		String s = new String();

		if (getActualClass() == null
				|| !getActualClass().isAnnotationPresent(
						DataSourceConfiguration.class)) {
			return null;
		}
		DataSourceConfiguration annotation = getActualClass().getAnnotation(
				DataSourceConfiguration.class);
		String sequencesfile = annotation.sqlSequnces();
		if (StringUtils.isNotBlank(sequencesfile)) {
			try {
				InputStream inputStream = this.getClass().getResourceAsStream(
						sequencesfile);
				InputStreamReader reader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(reader);
				while ((s = bufferedReader.readLine()) != null) {
					sb.append(s);
				}
				bufferedReader.close();

				// here is our splitter ! We use ";" as a delimiter for each
				// request
				// then we are sure to have well formed statements
				sequences = sb.toString().split(";");
			} catch (IOException e) {
				logger.warn("Failed to parse sql sequences update.", e);
			}
		}
		return sequences;
	}

	protected void buildDatabaseOperations(DBOperationManager opsManager,
			String[] datasets, DatabaseOperation databaseOperation) {
		if (datasets != null) {
			for (String dataSet : datasets) {
				if (StringUtils.isNotBlank(dataSet)) {
					DBOperation operation = new DBOperation();
					operation.setDatabaseOperation(databaseOperation);
					operation.setLocation(dataSet);
					opsManager.addOperation(operation);
				}
			}
		}
	}

	protected String[] getDeleteAllDatasets() {

		DataSetConfiguration annotation = getActualClass().getAnnotation(
				DataSetConfiguration.class);
		String[] datasets = annotation.deleteAllDataSets();
		return datasets;

	}

	protected String[] getInsertDatasets() {

		DataSetConfiguration annotation = getActualClass().getAnnotation(
				DataSetConfiguration.class);
		String[] datasets = annotation.insertDatasets();
		return datasets;

	}

	protected String[] getRefreshDatasets() {

		DataSetConfiguration annotation = getActualClass().getAnnotation(
				DataSetConfiguration.class);
		String[] datasets = annotation.refreshDatasets();
		return datasets;

	}

	protected String[] getUpdateDatasets() {

		DataSetConfiguration annotation = getActualClass().getAnnotation(
				DataSetConfiguration.class);
		String[] datasets = annotation.updateDatasets();
		return datasets;

	}

	protected boolean getClean() {

		DataSetConfiguration annotation = getActualClass().getAnnotation(
				DataSetConfiguration.class);
		boolean clean = annotation.clean();
		return clean;

	}

	protected DataSource loadDatasource() {

		DataSourceConfiguration annotation = getActualClass().getAnnotation(
				DataSourceConfiguration.class);

		String jdbcPropertiesFile = annotation.jdbcPropertiesFile();
		InputStream resourceAsStream = this.getClass().getClassLoader()
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

	@SuppressWarnings("unchecked")
	protected Class<? extends IDataTypeFactory> getDataTypeFactory() {

		DataSetConfiguration annotation = getActualClass().getAnnotation(
				DataSetConfiguration.class);

		Class<? extends IDataTypeFactory> dataTypeFactoryClass = annotation
				.dataTypeFactoryClass();
		String dbunitPropertiesFile = annotation.dbunitProperties();
		if (dbunitPropertiesFile != null) {
			InputStream resourceAsStream = this.getClass().getClassLoader()
					.getResourceAsStream(dbunitPropertiesFile);
			Properties properties = new Properties();
			try {
				properties.load(resourceAsStream);
				String factoryClassName = (String) properties
						.get("dbunit.datatype.factory");
				dataTypeFactoryClass = (Class<? extends IDataTypeFactory>) this
						.getClass().getClassLoader()
						.loadClass(factoryClassName);
			} catch (IOException e) {
				throw new SetupException("Could not load properties.", e);
			} catch (ClassNotFoundException e) {
				throw new SetupException("Could not load properties.", e);
			} catch (Exception e) {
				throw new SetupException("Could not load properties.", e);
			}
		}

		return dataTypeFactoryClass;

	}

	/**
	 * @param configuration
	 */
	public void evaluateAfters(DataSetConfiguration configuration) {
		DBOperationManager opsManager = new DBOperationManager(dataSource,
				dataTypeFactory);

		buildDatabaseOperations(opsManager, configuration.deleteAllDataSets(),
				DatabaseOperation.DELETE_ALL);

		opsManager.execute();

	}

	/**
	 * @param configuration
	 */
	public void evaluateBefores(DataSetConfiguration configuration) {

		DBOperationManager opsManager = new DBOperationManager(dataSource,
				dataTypeFactory);

		if (configuration.clean()) {
			buildDatabaseOperations(opsManager, configuration.insertDatasets(),
					DatabaseOperation.CLEAN_INSERT);
		} else {
			buildDatabaseOperations(opsManager, configuration.insertDatasets(),
					DatabaseOperation.INSERT);
		}

		buildDatabaseOperations(opsManager, configuration.refreshDatasets(),
				DatabaseOperation.REFRESH);

		buildDatabaseOperations(opsManager, configuration.updateDatasets(),
				DatabaseOperation.UPDATE);

		opsManager.execute();

	}

}
