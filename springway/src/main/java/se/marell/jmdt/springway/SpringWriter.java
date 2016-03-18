/*
 * Created by Daniel Marell 12-12-08 4:04 PM
 */
package se.marell.jmdt.springway;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLine;
import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import se.marell.dcommons.progress.ProgressTracker;
import se.marell.jmdt.commons.AbstractJpaHierarchyWriter;
import se.marell.jpatools.EntityManagerConnection;
import se.marell.jpatools.JpaUtils;

import javax.persistence.EntityManager;
import java.sql.SQLException;
import java.util.List;

public class SpringWriter extends AbstractJpaHierarchyWriter {
    private final static Logger logger = Logger.getLogger(SpringWriter.class);

    private SimpleJdbcTemplate simpleJdbcTemplate;

    @Override
    public void write(EntityManager em, List<Customer> customers, ProgressTracker pt) {
        writeInit();
        startMeasureTime();
        logger.info(getClass().getSimpleName() + ": Started writing numCustomers=" + customers.size());
        em.getTransaction().begin();
        EntityManagerConnection conn = JpaUtils.getConnectionFromEntityManager(em);
        simpleJdbcTemplate = new SimpleJdbcTemplate(new ConnectionWrapperDataSource(conn.getConn()));
        try {
            internalWrite(em, customers);
        } catch (SQLException e) {
            logger.error("internalWrite failed", e);
        }
        conn.close();
        em.getTransaction().commit();
        logger.debug("Committed");
        stopMeasureTime();
    }

    private void internalWrite(EntityManager em, List<Customer> customers) throws SQLException {
        startIntervalLogging(5000, "Writing");

        for (Customer c : customers) {
            intervalLogging();
            simpleJdbcTemplate.update("insert into Customer(customer_no) values(?)", c.getCustomerNo());
            int customerId = simpleJdbcTemplate.queryForInt("select id from Customer where customer_no=?",
                    c.getCustomerNo());
            for (CustomerOrder order : c.getCustomerOrders()) {
                simpleJdbcTemplate.update("insert into CustomerOrder( order_no, customer_id ) values( ?, ? )",
                        order.getOrderNo(), customerId);

                int customerOrderId = simpleJdbcTemplate.queryForInt("select id from CustomerOrder where order_no=?",
                        order.getOrderNo());

                for (OrderLine line : order.getOrderLines()) {
                    int productId = findProduct(em, line.getProduct().getProductNo()).getId();
                    simpleJdbcTemplate.update("insert into OrderLine( product_id, num_items, customer_order_id ) values( ?, ?, ? )",
                            productId, line.getNumItems(), customerOrderId);
                }
            }
        }
    }
}
