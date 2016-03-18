/*
 * Created by Daniel Marell 12-11-09 8:32 AM
 */
package se.marell.jmdt.presentation;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "LAZYCUSTOMERORDER")
public class LazyCustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "order_no", nullable = false, length = 12, unique = true)
    private String orderNo;

    @OneToMany(mappedBy = "customerOrder")
    private Set<LazyOrderLine> orderLines = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private LazyCustomer customer;

    public LazyCustomerOrder() {
    }

    public LazyCustomerOrder(String orderNo) {
        this.orderNo = orderNo;
    }

    public LazyCustomerOrder(Integer id, String orderNo) {
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

    public Set<LazyOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(Set<LazyOrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public LazyCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(LazyCustomer customer) {
        this.customer = customer;
    }

    /**
     * Add an aggregate entity OrderLine.
     *
     * @param orderLine Entity to add
     */
    public void addOrderLine(LazyOrderLine orderLine) {
        orderLines.add(orderLine);
        orderLine.setCustomerOrder(this);
    }

    @Override
    public String toString() {
        return "LazyCustomerOrder{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", orderLines=" + orderLines +
                '}';
    }
}

