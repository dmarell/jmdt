/*
 * Created by Daniel Marell 12-12-25 2:40 PM
 */
package se.marell.jmdt.commons;

import entity.Customer;

import java.util.List;
import java.util.Set;

public abstract class AbstractHierarchyReaderWriter extends AbstractJmdtCompetitor {

    protected AbstractHierarchyReaderWriter() {
        super(JmdtSuite.NON_JPA_READ_WRITE);
    }


    public abstract void prepareForWrite();

    public abstract void write(List<Customer> customers);

    public abstract Set<Customer> read(String customerNoPattern);
}
