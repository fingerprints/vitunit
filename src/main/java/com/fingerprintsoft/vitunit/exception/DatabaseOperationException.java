/*
 * DatabaseOperationException.java
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
package com.fingerprintsoft.vitunit.exception;

/**
 * @author fuzail_s
 * 
 */
public class DatabaseOperationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public DatabaseOperationException() {
        super();
    }

    /**
     * @param message
     * @param cause
     */
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param message
     */
    public DatabaseOperationException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public DatabaseOperationException(Throwable cause) {
        super(cause);
    }

}
