/*
 * Created by Daniel Marell 12-11-16 12:11 AM
 */
package se.marell.jpatools;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Executes SQL queries with pluggable strategies for handling ResultSets.
 *
 * @param <T> Type of the result
 */
public class QueryTool<T> {

    private static class PsMapKey {
        private String sql;
        private int resultSetType;

        private PsMapKey(String sql, int resultSetType) {
            this.sql = sql;
            this.resultSetType = resultSetType;
        }

        public String getSql() {
            return sql;
        }

        public int getResultSetType() {
            return resultSetType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            PsMapKey psMapKey = (PsMapKey) o;

            if (resultSetType != psMapKey.resultSetType) return false;
            if (!sql.equals(psMapKey.sql)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = sql.hashCode();
            result = 31 * result + resultSetType;
            return result;
        }
    }

    private Connection conn;
    private QueryReplyHandler<T> handler;
    private Map<PsMapKey, PreparedStatement> psMap = new HashMap<PsMapKey, PreparedStatement>();

    public QueryTool(Connection conn, QueryReplyHandler<T> handler) {
        this.conn = conn;
        this.handler = handler;
    }

    public T query(String sql, Object... args) throws SQLException {
        ResultSet rs = null;
        try {
            PreparedStatement ps = getPreparedStatement(sql, 0);
            int index = 1;
            for (Object a : args) {
                ps.setObject(index++, a);
            }
            rs = ps.executeQuery();
            return handler.handle(rs);
        } finally {
            JpaUtils.properlyClose(rs);
        }
    }

    public T insertAndGetGeneratedKeys(String sql, Object... args) throws SQLException {
        ResultSet rs = null;
        try {
            PreparedStatement ps = getPreparedStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            int index = 1;
            for (Object a : args) {
                ps.setObject(index++, a);
            }
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            return handler.handle(rs);
        } finally {
            JpaUtils.properlyClose(rs);
        }
    }

    public int update(String sql, Object... args) throws SQLException {
        PreparedStatement ps = getPreparedStatement(sql, 0);
        int index = 1;
        for (Object a : args) {
            ps.setObject(index++, a);
        }
        return ps.executeUpdate();
    }

    public void destroy() {
        for (PreparedStatement ps : psMap.values()) {
            JpaUtils.properlyClose(ps);
        }
    }

    private PreparedStatement getPreparedStatement(String sql, int resultSetType) throws SQLException {
        PsMapKey key = new PsMapKey(sql, resultSetType);
        PreparedStatement ps = psMap.get(key);
        if (ps == null) {
            if (key.getResultSetType() != 0) {
                ps = conn.prepareStatement(key.getSql(), key.getResultSetType());
            } else {
                ps = conn.prepareStatement(key.getSql());
            }
            psMap.put(key, ps);
        }
        return ps;
    }
}
