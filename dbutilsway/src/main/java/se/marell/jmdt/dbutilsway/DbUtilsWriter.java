/*
 * Created by Daniel Marell 12-11-11 10:53 PM
 */
package se.marell.jmdt.dbutilsway;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.log4j.Logger;
import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLine;
import se.marell.dcommons.progress.ProgressTracker;
import se.marell.jmdt.commons.AbstractJpaHierarchyWriter;
import se.marell.jpatools.EntityManagerConnection;
import se.marell.jpatools.JpaUtils;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DbUtilsWriter extends AbstractJpaHierarchyWriter {
    private final static Logger logger = Logger.getLogger(DbUtilsWriter.class);

    private QueryRunner run = new QueryRunner();

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
        logger.debug("Committed");
        stopMeasureTime();
    }

    private void internalWrite(EntityManager em, Connection conn, List<Customer> customers) throws SQLException {
        ResultSetHandler<Object[]> rsIdHandler = new ResultSetHandler<Object[]>() {
            public Object[] handle(ResultSet rs) throws SQLException {
                if (!rs.next()) {
                    return null;
                }
                return new Integer[]{rs.getInt(1)};
            }
        };

        startIntervalLogging(5000, "Writing");

        for (Customer c : customers) {
            intervalLogging();
            run.update(conn, "insert into Customer(customer_no) values(?)", c.getCustomerNo());
            int customerId = (Integer) run.query(
                    conn, "select id from Customer where customer_no=?",
                    rsIdHandler, c.getCustomerNo())[0];
            for (CustomerOrder order : c.getCustomerOrders()) {
                run.update(conn, "insert into CustomerOrder( order_no, customer_id ) values( ?, ? )",
                        order.getOrderNo(), customerId);
                int customerOrderId = (Integer) run.query(
                        conn, "select id from CustomerOrder where order_no=?",
                        rsIdHandler, order.getOrderNo())[0];

                for (OrderLine line : order.getOrderLines()) {
                    int productId = findProduct(em, line.getProduct().getProductNo()).getId();
                    run.update(conn,
                            "insert into OrderLine( product_id, num_items, customer_order_id ) values( ?, ?, ? )",
                            productId, line.getNumItems(), customerOrderId);
                }
            }
        }
    }
}
