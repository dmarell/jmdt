/*
 * Created by Daniel Marell 12-11-11 11:19 PM
 */
package se.marell.jmdt.jdbcway;

import entity.Customer;
import entity.CustomerOrder;
import entity.OrderLine;
import org.apache.log4j.Logger;
import se.marell.dcommons.progress.ProgressTracker;
import se.marell.jmdt.commons.AbstractJpaHierarchyWriter;
import se.marell.jpatools.EntityManagerConnection;
import se.marell.jpatools.JpaUtils;

import javax.persistence.EntityManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JdbcBatchWriter extends AbstractJpaHierarchyWriter {
    private final static Logger logger = Logger.getLogger(JdbcWriter.class);
    private static final int CHUNK_SIZE = 2;

    @Override
    public void write(EntityManager em, List<Customer> customers, ProgressTracker pt) {
        writeInit();
        startMeasureTime();
        em.getTransaction().begin();
        logger.info(getClass().getSimpleName() + ": Started writing numCustomers=" + customers.size());

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
        String sqlCustomerInsert = "insert into Customer( customer_no ) values( ? )";
        PreparedStatement psCustomerInsert = conn.prepareStatement(sqlCustomerInsert);

        String sqlCustomerOrderInsert = "insert into CustomerOrder( order_no, customer_id ) values( ?, ? )";
        PreparedStatement psCustomerOrderInsert = conn.prepareStatement(sqlCustomerOrderInsert);

        String sqlOrderLineInsert = "insert into OrderLine( product_id, num_items, customer_order_id ) values( ?, ?, ? )";
        PreparedStatement psOrderLineInsert = conn.prepareStatement(sqlOrderLineInsert);

        startIntervalLogging(5000, "Writing BaseCustomers");

        // Persist BaseCustomer objects
        String[] customerNoArray = new String[customers.size()];
        Map<String, Customer> customerNoMap = new HashMap<String, Customer>();
        int count = 0;
        for (Customer c : customers) {
            intervalLogging();
            psCustomerInsert.setString(1, c.getCustomerNo());
            customerNoArray[count++] = c.getCustomerNo();
            customerNoMap.put(c.getCustomerNo(), c);
            psCustomerInsert.addBatch();
        }
        psCustomerInsert.executeBatch();

        for (String[] custNoChunk : ArrayUtil.divideArray(customerNoArray, CHUNK_SIZE)) {

            // Read IDs of this chunk of created BaseCustomer objects
            Map<String, Integer> customerIdMap = new HashMap<String, Integer>();
            {
                String sql = createSqlCustomerIdSelect(custNoChunk);
                ResultSet rs = null;
                try {
                    rs = conn.createStatement().executeQuery(sql);
                    while (rs.next()) {
                        customerIdMap.put(rs.getString("customer_no"), rs.getInt("id"));
                    }
                } finally {
                    JpaUtils.properlyClose(rs);
                }
                // Set IDs in entity objects
                for (Customer c : customers) {
                    Integer id = customerIdMap.get(c.getCustomerNo());
                    if (id != null) {
                        c.setId(id);
                    }
                }
            }

            // Persist the CustomerOrder objects which are children of the Customer objects of this chunk
            for (String customerNo : customerIdMap.keySet()) {
                Customer c = customerNoMap.get(customerNo);
                for (CustomerOrder order : c.getCustomerOrders()) {
                    psCustomerOrderInsert.setString(1, order.getOrderNo());
                    psCustomerOrderInsert.setInt(2, c.getId());
                    psCustomerOrderInsert.addBatch();
                }
            }
            psCustomerOrderInsert.executeBatch();

            // Read IDs of the created CustomerOrder objects
            Map<String, Integer> customerOrderIdMap = new HashMap<String, Integer>();
            {
                String sql = createSqlCustomerOrderIdSelect(custNoChunk);
                ResultSet rs = null;
                try {
                    rs = conn.createStatement().executeQuery(sql);
                    while (rs.next()) {
                        customerOrderIdMap.put(rs.getString("order_no"), rs.getInt("id"));
                    }
                } finally {
                    JpaUtils.properlyClose(rs);
                }
                // Set IDs in entity objects
                for (Customer c : customers) {
                    for (CustomerOrder order : c.getCustomerOrders()) {
                        Integer id = customerOrderIdMap.get(order.getOrderNo());
                        if (id != null) {
                            order.setId(id);
                        }
                    }
                }
            }


            // Persist the OrderLine objects which are children of the CustomerOrder objects which are children of
            // Customer objects of this chunk
            for (String customerNo : customerIdMap.keySet()) {
                Customer c = customerNoMap.get(customerNo);
                for (CustomerOrder order : c.getCustomerOrders()) {
                    for (OrderLine line : order.getOrderLines()) {
                        //product_id, num_items, order_id
                        psOrderLineInsert.setInt(1, findProduct(em, line.getProduct().getProductNo()).getId());
                        psOrderLineInsert.setInt(2, line.getNumItems());
                        psOrderLineInsert.setInt(3, order.getId());
                        psOrderLineInsert.addBatch();
                    }
                }
            }
            psOrderLineInsert.executeBatch();
        }
    }

    private String createSqlCustomerIdSelect(String[] customerNos) {
        return "select id,customer_no from Customer where customer_no in (" + getSqlString(customerNos) + ")";
    }

    private String createSqlCustomerOrderIdSelect(String[] customerNos) {
        return "select o.id,o.customer_id,c.customer_no,o.order_no from CustomerOrder o, Customer c " +
                "where c.customer_no in (" +
                getSqlString(customerNos) + ")";
    }

    private String getSqlString(String[] array) {
        StringBuilder sb = new StringBuilder();
        for (String s : array) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append("'");
            sb.append(s);
            sb.append("'");
        }
        return sb.toString();
    }
}
