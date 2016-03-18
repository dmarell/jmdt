/*
 * Created by Daniel Marell 12-11-09 8:30 AM
 */
package se.marell.jmdt.jpaway;

import org.apache.log4j.Logger;
import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLine;
import se.marell.dcommons.progress.ProgressTracker;
import se.marell.jmdt.commons.AbstractJpaHierarchyWriter;

import javax.persistence.EntityManager;
import java.util.List;

public class JpaWriter extends AbstractJpaHierarchyWriter {
    private final static Logger logger = Logger.getLogger(JpaWriter.class);

    @Override
    public void write(EntityManager em, List<Customer> customers, ProgressTracker pt) {
        logger.info(getClass().getSimpleName() + ": Started writing numCustomers=" + customers.size());
        writeInit();
        em.getTransaction().begin();
        startIntervalLogging(5000, "Writing");
        int count = 0;
        for (Customer c : customers) {
            intervalLogging();

            if (++count % 100 == 0) {
                em.flush();
                em.clear();
            }

            // Set product relation
            for (CustomerOrder order : c.getCustomerOrders()) {
                for (OrderLine line : order.getOrderLines()) {
                    line.setProduct(findProduct(em, line.getProduct().getProductNo()));
                }
            }

            em.persist(c);

            pt.setTotalProgress(count / (float) customers.size());
        }
        em.getTransaction().commit();
    }
}
