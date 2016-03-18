/*
 * Created by Daniel Marell 12-12-30 9:58 PM
 */
package se.marell.jmdt.commons;

import java.util.List;

public class JmdtCompetitorRunResult {
    private JmdtCompetitor competitor;
    private int execTimeMs;
    private String entityManagerName;
    private String jpaImplementationName;
    private String dbName;
    private List<String> sqlStatements;
    private int numSqlInserts;
    private int numSqlSelects;
    private String source;

    public JmdtCompetitorRunResult(JmdtCompetitor competitor, int execTimeMs, String entityManagerName,
                                   String jpaImplementationName, String dbName,
                                   List<String> sqlStatements, int numSqlInserts, int numSqlSelects, String source) {
        this.competitor = competitor;
        this.execTimeMs = execTimeMs;
        this.entityManagerName = entityManagerName;
        this.jpaImplementationName = jpaImplementationName;
        this.dbName = dbName;
        this.sqlStatements = sqlStatements;
        this.numSqlInserts = numSqlInserts;
        this.numSqlSelects = numSqlSelects;
        this.source = source;
    }

    public JmdtCompetitor getCompetitor() {
        return competitor;
    }

    public int getExecTimeMs() {
        return execTimeMs;
    }

    public String getEntityManagerName() {
        return entityManagerName;
    }

    public String getJpaImplementationName() {
        return jpaImplementationName;
    }

    public String getDbName() {
        return dbName;
    }

    public List<String> getSqlStatements() {
        return sqlStatements;
    }

    public String getSource() {
        return source;
    }

    public int getNumSqlInserts() {
        return numSqlInserts;
    }

    public int getNumSqlSelects() {
        return numSqlSelects;
    }
}
