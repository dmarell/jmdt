/*
 * Created by Daniel Marell 12-11-28 7:53 AM
 */
package se.marell.jmdt.jpaway;

import entity.Customer;
import se.marell.jmdt.commons.AbstractJpaHierarchyReader;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JpaReader extends AbstractJpaHierarchyReader {
    @Override
    public Set<Customer> read(EntityManager em, String customerNoPattern) {
        em.getTransaction().begin();
        try {
            List<Customer> customers = Customer.readSubTreeByCustomerNoPattern(em, customerNoPattern);
            em.getTransaction().commit();
            return new HashSet<Customer>(customers);
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
