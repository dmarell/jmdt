/*
 * Created by Daniel Marell 12-11-11 9:22 PM
 */
package se.marell.jmdt.jdbcway;

import org.apache.log4j.Logger;
import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLine;
import se.marell.dcommons.progress.ProgressTracker;
import se.marell.jmdt.commons.AbstractJpaHierarchyWriter;
import se.marell.jpatools.EntityManagerConnection;
import se.marell.jpatools.JpaUtils;

import javax.persistence.EntityManager;
import java.sql.*;
import java.util.List;

public class JdbcWriter extends AbstractJpaHierarchyWriter {
    private final static Logger logger = Logger.getLogger(JdbcWriter.class);

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
        String sqlCustomerInsert = "insert into Customer (customer_no) values(?)";
        PreparedStatement psCustomerInsert = conn.prepareStatement(sqlCustomerInsert,
                Statement.RETURN_GENERATED_KEYS);

        String sqlCustomerOrderInsert = "insert into CustomerOrder( order_no, customer_id ) values( ?, ? )";
        PreparedStatement psCustomerOrderInsert = conn.prepareStatement(sqlCustomerOrderInsert,
                Statement.RETURN_GENERATED_KEYS);

        String sqlOrderLineInsert = "insert into OrderLine( product_id, num_items, customer_order_id ) values( ?, ?, ? )";
        PreparedStatement psOrderLineInsert = conn.prepareStatement(sqlOrderLineInsert);

        startIntervalLogging(5000, "Writing");

        try {
            for (Customer c : customers) {
                intervalLogging();
                int customerId;
                {
                    ResultSet rs = null;
                    try {
                        psCustomerInsert.setString(1, c.getCustomerNo());
                        psCustomerInsert.executeUpdate();
                        ResultSet rs2 = null;
                        try {
                            rs2 = psCustomerInsert.getGeneratedKeys();
                            rs2.next();
                            customerId = rs2.getInt(1);
                        } finally {
                            JpaUtils.properlyClose(rs2);
                        }
                    } finally {
                        JpaUtils.properlyClose(rs);
                    }
                }

                for (CustomerOrder order : c.getCustomerOrders()) {
                    int customerOrderId;
                    {
                        ResultSet rs = null;
                        try {
                            psCustomerOrderInsert.setString(1, order.getOrderNo());
                            psCustomerOrderInsert.setInt(2, customerId);
                            psCustomerOrderInsert.executeUpdate();
                            ResultSet rs2 = null;
                            try {
                                rs2 = psCustomerOrderInsert.getGeneratedKeys();
                                rs2.next();
                                customerOrderId = rs2.getInt(1);
                            } finally {
                                JpaUtils.properlyClose(rs2);
                            }
                        } finally {
                            JpaUtils.properlyClose(rs);
                        }
                    }

                    for (OrderLine line : order.getOrderLines()) {
                        int productId = findProduct(em, line.getProduct().getProductNo()).getId();
                        psOrderLineInsert.setInt(1, productId);
                        psOrderLineInsert.setInt(2, line.getNumItems());
                        psOrderLineInsert.setInt(3, customerOrderId);
                        psOrderLineInsert.executeUpdate();
                    }
                }
            }
        } finally {
            JpaUtils.properlyClose(psCustomerInsert, psCustomerOrderInsert, psOrderLineInsert);
        }
    }

}
