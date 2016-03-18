/*
 * Created by Daniel Marell 12-11-16 7:34 AM
 */
package se.marell.jmdt.commons;

import entity.Customer;

import javax.persistence.EntityManager;
import java.util.Set;

public abstract class AbstractJpaHierarchyReader extends AbstractJmdtCompetitor {
    protected AbstractJpaHierarchyReader() {
        super(JmdtSuite.HIERARCHY_READ);
    }

    public abstract Set<Customer> read(EntityManager em, String customerNoPattern);
}
