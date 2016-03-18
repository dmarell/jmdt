/*
 * Created by Daniel Marell 12-11-15 11:33 PM
 */
package se.marell.jmdt.marellway;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLine;
import org.apache.log4j.Logger;
import se.marell.dcommons.progress.ProgressTracker;
import se.marell.jmdt.commons.AbstractJpaHierarchyWriter;
import se.marell.jpatools.EntityManagerConnection;
import se.marell.jpatools.JpaUtils;
import se.marell.jpatools.QueryReplyHandler;
import se.marell.jpatools.QueryTool;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MarellWriter extends AbstractJpaHierarchyWriter {
    private final static Logger logger = Logger.getLogger(MarellWriter.class);

    @Override
    public void write(EntityManager em, List<Customer> customers, ProgressTracker pt) {
        writeInit();
        startMeasureTime();
        logger.info(getClass().getSimpleName() + ": Started writing numCustomers=" + customers.size());
        em.getTransaction().begin();
        EntityManagerConnection conn = JpaUtils.getConnectionFromEntityManager(em);
        try {
            internalWrite(em, conn.getConn(), customers);
        } catch (SQLException e) {
            logger.error("internalWrite failed", e);
        }
        conn.close();
        em.getTransaction().commit();
        stopMeasureTime();
    }

    private void internalWrite(EntityManager em, Connection conn, List<Customer> customers) throws SQLException {
        QueryReplyHandler<Integer> rsIdHandler = new QueryReplyHandler<Integer>() {
            public Integer handle(ResultSet rs) throws SQLException {
                if (!rs.next()) {
                    return null;
                }
                return rs.getInt(1);
            }
        };

        startIntervalLogging(5000, "Writing");

        QueryTool<Integer> qt = new QueryTool<Integer>(conn, rsIdHandler);
        for (Customer c : customers) {
            intervalLogging();
            int customerId = qt.insertAndGetGeneratedKeys("insert into Customer( customer_no ) values( ? )",
                    c.getCustomerNo());
            for (CustomerOrder order : c.getCustomerOrders()) {
                int customerOrderId = qt.insertAndGetGeneratedKeys(
                        "insert into CustomerOrder( order_no, customer_id ) values( ?, ? )",
                        order.getOrderNo(), customerId);
                for (OrderLine line : order.getOrderLines()) {
                    int productId = findProduct(em, line.getProduct().getProductNo()).getId();
                    qt.update("insert into OrderLine( product_id, num_items, customer_order_id ) values( ?, ?, ? )",
                            productId, line.getNumItems(), customerOrderId);
                }
            }
        }
        qt.destroy();
    }
}
