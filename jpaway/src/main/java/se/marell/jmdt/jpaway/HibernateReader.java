/*
 * Created by Daniel Marell 12-11-16 7:37 AM
 */
package se.marell.jmdt.jpaway;

import entity.Customer;
import se.marell.jmdt.commons.AbstractJpaHierarchyReader;

import javax.persistence.EntityManager;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HibernateReader extends AbstractJpaHierarchyReader {
    @Override
    public Set<Customer> read(EntityManager em, String customerNoPattern) {
        em.getTransaction().begin();
        try {
            List<Customer> customers = Customer.readSubTree2JoinFetchByCustomerNoPattern(em, customerNoPattern);
            em.getTransaction().commit();
            return new HashSet<Customer>(customers);
        } catch (RuntimeException e) {
            em.getTransaction().rollback();
            throw e;
        }
    }
}
