/*
 * Created by Daniel Marell 12-11-16 12:10 AM
 */
package se.marell.jpatools;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Results from a query in a table-like manner for SELECT queries.
 * Each row corresponds to a set of bindings which fulfil the conditions of the query.
 * Access to the results is by variable name.
 *
 * @param <T> Result type
 */
public interface QueryReplyHandler<T> {
    T handle(ResultSet rs) throws SQLException;
}
