/*
 * Created by Daniel Marell 2012-11-09 14:37
 */
package se.marell.jpatools;

import org.apache.log4j.Logger;
import org.apache.openjpa.persistence.OpenJPAEntityManager;
import org.apache.openjpa.persistence.OpenJPAPersistence;
import org.hibernate.Session;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.jdbc.connections.spi.ConnectionProvider;

import javax.persistence.EntityManager;
import java.sql.*;
import java.util.Properties;

/**
 * JPA/JDBC related utility methods.
 */
public final class JpaUtils {
    private final static Logger logger = Logger.getLogger(JpaUtils.class);

    private JpaUtils() {
    }

    public static class DbUtilException extends RuntimeException {
        public DbUtilException(String message) {
            super(message);
        }

        public DbUtilException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * This is a horrible method caused by the problem that there is no portable way to get the jdbc connection
     * from an EntityManager.
     *
     * @param em The entity manager
     * @return A jdbc connection wrapped in an EntityManagerConnection object
     */
    public static EntityManagerConnection getConnectionFromEntityManager(EntityManager em) {
        Connection conn;

        conn = getOpenJpaConnection(em);
        if (conn != null) {
            return new EntityManagerConnection(conn);
        }

        conn = getHibernateConnection(em);
        if (conn != null) {
            return new EntityManagerConnection(conn);
        }

        conn = getEclipseConnection(em);
        if (conn != null) {
            return new EntityManagerConnection(conn) {
                @Override
                public void close() {
                    // When using a Connection from EclipseLink, do not close conn
                }
            };
        }

        logger.error("Failed to get jdbc connection");
        throw new DbUtilException("Failed to get jdbc connection");
    }

    public static Connection getOpenJpaConnection(EntityManager em) {
        try {
            OpenJPAEntityManager kem = OpenJPAPersistence.cast(em);
            kem.beginStore();
            return (Connection) kem.getConnection();
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static Connection getHibernateConnection(EntityManager em) {
        try {
            Session session = (Session) em.getDelegate();
            SessionFactoryImplementor sfi = (SessionFactoryImplementor) session.getSessionFactory();
            ConnectionProvider cp = sfi.getConnectionProvider();
            try {
                return cp.getConnection();
            } catch (SQLException e1) {
                return null;
            }
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static Connection getEclipseConnection(EntityManager em) {
        try {
            return em.unwrap(Connection.class);
        } catch (ClassCastException e) {
            return null;
        }
    }

    public static Connection getConnection() {
        Properties connectionProps = new Properties();
        connectionProps.put("user", "root");
        connectionProps.put("password", "iphone");
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/custorders", connectionProps);
        } catch (SQLException e) {
            logger.error("Cannot connect DB");
            return null;
        }
    }

    /**
     * Close prepared statements.
     *
     * @param statements Statements to close
     */
    public static void properlyClose(PreparedStatement... statements) {
        for (PreparedStatement ps : statements) {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    logger.error("Failed to close prepStmt", e);
                }
            }
        }
    }

    /**
     * Close ResultSets.
     *
     * @param resultSets ResultSets to close
     */
    public static void properlyClose(ResultSet... resultSets) {
        for (ResultSet rs : resultSets) {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    logger.error("Failed to close result set", e);
                }
            }
        }
    }

    /**
     * Close connections.
     *
     * @param connections Connections to close
     */
    public static void properlyClose(Connection... connections) {
        for (Connection c : connections) {
            if (c != null) {
                try {
                    c.close();
                } catch (SQLException e) {
                    logger.error("Failed to close Connection", e);
                }
            }
        }
    }

}
