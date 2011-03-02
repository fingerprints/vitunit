/*
 * DataSetConfiguration.java
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
package com.fingerprintsoft.vitunit.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.dbunit.dataset.datatype.IDataTypeFactory;

/**
 * DataSetConfiguration defines class-level metadata which can be used to
 * instruct client <code>DatabaseFixtureTestListener</code>s on which data sets
 * to run.
 * 
 * @author Fuzail Sarang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface DataSetConfiguration {

    public static final String EMPTY_STRING = "";

    /**
     * An array of data sets for insert operations.
     * 
     * @return The data sets to run.
     */
    String[] insertDatasets() default { EMPTY_STRING };

    /**
     * An array of data sets for refresh operations.
     * 
     * @return The data sets to run.
     */
    String[] refreshDatasets() default { EMPTY_STRING };

    /**
     * An array of data sets for update operations.
     * 
     * @return The data sets to run.
     */
    String[] updateDatasets() default { EMPTY_STRING };

    /**
     * An array of data sets for delete all operations.
     * 
     * @return The data sets to run.
     */
    String[] deleteAllDataSets() default { EMPTY_STRING };

    /**
     * The DBUnit data type factory class.
     * 
     * @return DBUnit data type factory class.
     */
    Class<? extends IDataTypeFactory> dataTypeFactoryClass() default org.dbunit.dataset.datatype.DefaultDataTypeFactory.class;

    /**
     * The String location for the dbunit.properties file.
     * 
     * @return The location of dbunit.properties.
     */
    String dbunitProperties() default  EMPTY_STRING;

    /**
     * Weather or not DBUnit should perform clean inserts into the database or
     * just plain inserts.
     * 
     * @return true or false
     */
    boolean clean() default true;

}
