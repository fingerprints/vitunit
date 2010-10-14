/*
 * AbstractDatabaseFixtureTestListener.java
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
package com.fingerprintsoft.vitunit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import com.fingerprintsoft.vitunit.annotation.DataSetConfiguration;
import com.fingerprintsoft.vitunit.annotation.DataSourceConfiguration;

/**
 * @author Fuzail Sarang <fuzail@fingerprintsoft.org>
 * 
 */
public class BaseDatabaseFixtureTestListener extends
        AbstractTestExecutionListener {

    private static final Log logger = LogFactory
            .getLog(BaseDatabaseFixtureTestListener.class);

    private static Class<?> testClass;

    @Override
    public void prepareTestInstance(TestContext testContext) throws Exception {
        if (testClass != testContext.getTestClass()) {
            executeDatabaseOperations(testContext);
            testClass = testContext.getTestClass();
        }
    }

    public void executeDatabaseOperations(TestContext testContext) {

        String[] datasets = null;
        DataSource dataSource = getDefaultDatasource(testContext);
        DBOperationManager opsManager = new DBOperationManager();
        opsManager.setDataSource(dataSource);

        datasets = getDeleteAllDatasets(testContext);
        buildDatabaseOperations(opsManager, datasets,
                DatabaseOperation.DELETE_ALL);

        datasets = getInsertDatasets(testContext);
        if (getClean(testContext)) {
            buildDatabaseOperations(opsManager, datasets,
                    DatabaseOperation.CLEAN_INSERT);
        } else {
            buildDatabaseOperations(opsManager, datasets,
                    DatabaseOperation.INSERT);
        }

        datasets = getRefreshDatasets(testContext);
        buildDatabaseOperations(opsManager, datasets, DatabaseOperation.REFRESH);

        datasets = getUpdateDatasets(testContext);
        buildDatabaseOperations(opsManager, datasets, DatabaseOperation.UPDATE);

        opsManager.execute();

        updateSequences(dataSource, getSequences(testContext));

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

    protected String[] getSequences(TestContext testContext) {
        String[] sequences = null;
        StringBuffer sb = new StringBuffer();
        String s = new String();
        Class<?> testClass = testContext.getTestClass();
        if (testClass == null
                || !testClass
                        .isAnnotationPresent(DataSourceConfiguration.class)) {
            return null;
        }
        DataSourceConfiguration annotation = testClass
                .getAnnotation(DataSourceConfiguration.class);
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

    protected String[] getDeleteAllDatasets(TestContext testContext) {

        Class<?> testClass = testContext.getTestClass();
        if (testClass == null
                || !testClass.isAnnotationPresent(DataSetConfiguration.class)) {
            return null;
        }
        DataSetConfiguration annotation = testClass
                .getAnnotation(DataSetConfiguration.class);
        String[] datasets = annotation.deleteAllDataSets();
        return datasets;

    }

    protected String[] getInsertDatasets(TestContext testContext) {

        Class<?> testClass = testContext.getTestClass();
        if (testClass == null
                || !testClass.isAnnotationPresent(DataSetConfiguration.class)) {
            return null;
        }
        DataSetConfiguration annotation = testClass
                .getAnnotation(DataSetConfiguration.class);
        String[] datasets = annotation.insertDatasets();
        return datasets;

    }

    protected String[] getRefreshDatasets(TestContext testContext) {

        Class<?> testClass = testContext.getTestClass();
        if (testClass == null
                || !testClass.isAnnotationPresent(DataSetConfiguration.class)) {
            return null;
        }
        DataSetConfiguration annotation = testClass
                .getAnnotation(DataSetConfiguration.class);
        String[] datasets = annotation.refreshDatasets();
        return datasets;

    }

    protected String[] getUpdateDatasets(TestContext testContext) {

        Class<?> testClass = testContext.getTestClass();
        if (testClass == null
                || !testClass.isAnnotationPresent(DataSetConfiguration.class)) {
            return null;
        }
        DataSetConfiguration annotation = testClass
                .getAnnotation(DataSetConfiguration.class);
        String[] datasets = annotation.updateDatasets();
        return datasets;

    }

    protected DataSource getDefaultDatasource(TestContext testContext) {
        Class<?> testClass = testContext.getTestClass();
        if (testClass == null
                || !testClass
                        .isAnnotationPresent(DataSourceConfiguration.class)) {
            return null;
        }
        DataSourceConfiguration annotation = testClass
                .getAnnotation(DataSourceConfiguration.class);
        String dataSourceBean = annotation.defaultDataSource();
        DataSource dataSource = (DataSource) testContext
                .getApplicationContext().getBean(dataSourceBean);
        return dataSource;
    }

    protected boolean getClean(TestContext testContext) {

        Class<?> testClass = testContext.getTestClass();
        if (testClass == null
                || !testClass.isAnnotationPresent(DataSetConfiguration.class)) {
            return true;
        }
        DataSetConfiguration annotation = testClass
                .getAnnotation(DataSetConfiguration.class);
        boolean clean = annotation.clean();
        return clean;

    }
}
