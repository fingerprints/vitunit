/*
 * DBOperationManager.java
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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.datatype.IDataTypeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fingerprintsoft.vitunit.exception.DatabaseOperationException;

/**
 * @author Fuzail Sarang <fuzail@fingerprintsoft.org>
 * 
 */
public class DBOperationManager {

    private static final Logger logger = LoggerFactory.getLogger(DBOperationManager.class);
    private DataSource dataSource;
    private List<DBOperation> operations;
    private Class<? extends IDataTypeFactory> dataTypeFactoryClass;

    public DBOperationManager(DataSource dataSource,
	    Class<? extends IDataTypeFactory> dataTypeFactoryClass) {
	this.dataSource = dataSource;
	this.dataTypeFactoryClass = dataTypeFactoryClass;
	operations = new ArrayList<DBOperation>();
    }

    public void execute() {
	IDatabaseConnection databaseConnection = getDatabaseConnection();
	try {
	    for (DBOperation operation : operations) {
		operation.execute(databaseConnection);
	    }
	} finally {
	    try {
		databaseConnection.close();
	    } catch (SQLException e) {
		// Do Nothing here.
		logger.warn("Could not close the connection", e);
	    }
	}
    }

    private IDatabaseConnection getDatabaseConnection() {
	try {
	    Connection connection = dataSource.getConnection();
	    IDatabaseConnection conn;
	    conn = new DatabaseConnection(connection);
	    DatabaseConfig config = conn.getConfig();
	    config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY,
		    getDataTypeFactory());
	    return conn;
	} catch (DatabaseUnitException e) {
	    throw new DatabaseOperationException(e);
	} catch (SQLException e) {
	    throw new DatabaseOperationException(e);
	} catch (ClassNotFoundException e) {
	    throw new DatabaseOperationException(e);
	}
    }

    private IDataTypeFactory getDataTypeFactory() throws ClassNotFoundException {
	IDataTypeFactory dataTypeFactory;
	try {
	    dataTypeFactory = getDataTypeFactoryClass().newInstance();
	} catch (InstantiationException e) {
	    throw new DatabaseOperationException(e);
	} catch (IllegalAccessException e) {
	    throw new DatabaseOperationException(e);
	}
	return dataTypeFactory;
    }

    public DataSource getDataSource() {
	return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
	this.dataSource = dataSource;
    }

    public void addOperation(DBOperation operation) {
	operations.add(operation);
    }

    public Class<? extends IDataTypeFactory> getDataTypeFactoryClass() {
	return dataTypeFactoryClass;
    }

    public void setDataTypeFactoryClass(
	    Class<? extends IDataTypeFactory> dataTypeFactoryClass) {
	this.dataTypeFactoryClass = dataTypeFactoryClass;
    }

}
