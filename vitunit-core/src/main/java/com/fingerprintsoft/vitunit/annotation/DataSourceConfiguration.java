/*
 * DataSourceConfiguration.java
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

/**
 * @author Fuzail Sarang <fuzail@fingerprintsoft.org>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface DataSourceConfiguration {

    /**
     * 
     */
    public static final String JDBC_PROP_FILE = "jdbc.properties";
    public static final String EMPTY_STRING = "";

    /**
     * The default <code>DataSource</code> for database fixtures.
     * 
     * @return the name of the <code>DataSource</code> bean in Spring.
     */
    String jdbcPropertiesFile() default JDBC_PROP_FILE;
    
    /**
     * The default <code>DataSource</code> for database fixtures.
     * 
     * @return the name of the <code>DataSource</code> bean in Spring.
     */
    String dataSource() default EMPTY_STRING;
    
    /**
     * An file containing a list of SQL update statements to update sequences.
     * 
     * @return The sql sequences to run.
     */
    String sqlSequnces() default EMPTY_STRING;

}