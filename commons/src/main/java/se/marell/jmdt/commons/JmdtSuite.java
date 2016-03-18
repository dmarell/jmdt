/*
 * Created by Daniel Marell 12-12-30 9:57 PM
 */
package se.marell.jmdt.commons;

public enum JmdtSuite {
    HIERARCHY_WRITE("Hierarchy write"),
    HIERARCHY_READ("Hierarchy write"),
    NON_JPA_READ_WRITE("Non-JPA write and read");

    private String name;

    private JmdtSuite(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

