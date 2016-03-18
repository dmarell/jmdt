/*
 * Created by Daniel Marell 12-12-04 10:45 PM
 */
package se.marell.jmdt.jdbcway;

public class SqlRuntimeException extends RuntimeException {
    public SqlRuntimeException(Throwable cause) {
        super(cause);
    }

    public SqlRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}

