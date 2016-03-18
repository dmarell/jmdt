/*
 * Created by Daniel Marell 12-11-09 8:32 AM
 */
package entity;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CUSTOMERORDER")
public class CustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "order_no", nullable = false, length = 12, unique = true)
    private String orderNo;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, mappedBy = "customerOrder")
    private Set<OrderLine> orderLines = new HashSet<OrderLine>();

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public CustomerOrder() {
    }

    public CustomerOrder(String orderNo) {
        this.orderNo = orderNo;
    }

    public CustomerOrder(Integer id, String orderNo) {
        this.id = id;
        this.orderNo = orderNo;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Set<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(Set<OrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Add an aggregate entity OrderLine.
     *
     * @param orderLine Entity to add
     */
    public void addOrderLine(OrderLine orderLine) {
        orderLines.add(orderLine);
        orderLine.setCustomerOrder(this);
    }

    @Override
    public String toString() {
        return "CustomerOrder{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", orderLines=" + orderLines +
                '}';
    }
}

