/*
 * Created by Daniel Marell 12-12-06 9:41 PM
 */
package se.marell.jpatools;

import java.sql.Connection;

/**
 * Wraps a JDBC connection from an EntityManager. Makes it possible to differentiate the close behaviour between
 * JPA implementations.
 */
public class EntityManagerConnection {
    private Connection conn;

    public EntityManagerConnection(Connection conn) {
        this.conn = conn;
    }

    public Connection getConn() {
        return conn;
    }

    public void close() {
        JpaUtils.properlyClose(conn);
    }
}
