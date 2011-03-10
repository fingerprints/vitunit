/*
 * DBOperation.java
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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.XmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import com.fingerprintsoft.vitunit.exception.DatabaseOperationException;

/**
 * @author fuzail_s
 * 
 */
public class DBOperation {

    private String location;
    private DatabaseOperation databaseOperation;

    public void execute(IDatabaseConnection connection) {
        try {
            databaseOperation.execute(connection, getDataSet());
        } catch (DatabaseUnitException e) {
            throw new DatabaseOperationException(e);
        } catch (SQLException e) {
            throw new DatabaseOperationException(e);
        }
    }

    private IDataSet getDataSet() {
        try {
            return new XmlDataSet(new FileInputStream(getLocation()));
        } catch (DataSetException e) {
            throw new DatabaseOperationException(e);
        } catch (FileNotFoundException e) {
            throw new DatabaseOperationException(e);
        }
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public DatabaseOperation getDatabaseOperation() {
        return databaseOperation;
    }

    public void setDatabaseOperation(DatabaseOperation databaseOperation) {
        this.databaseOperation = databaseOperation;
    }

}
