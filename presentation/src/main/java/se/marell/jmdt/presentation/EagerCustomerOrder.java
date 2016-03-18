/*
 * Created by Daniel Marell 12-11-09 8:32 AM
 */
package se.marell.jmdt.presentation;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "EAGERCUSTOMERORDER")
public class EagerCustomerOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "order_no", nullable = false, length = 12, unique = true)
    private String orderNo;

    @OneToMany(mappedBy = "customerOrder")
    private Set<EagerOrderLine> orderLines = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private EagerCustomer customer;

    public EagerCustomerOrder() {
    }

    public EagerCustomerOrder(String orderNo) {
        this.orderNo = orderNo;
    }

    public EagerCustomerOrder(Integer id, String orderNo) {
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

    public Set<EagerOrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(Set<EagerOrderLine> orderLines) {
        this.orderLines = orderLines;
    }

    public EagerCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(EagerCustomer customer) {
        this.customer = customer;
    }

    /**
     * Add an aggregate entity OrderLine.
     *
     * @param orderLine Entity to add
     */
    public void addOrderLine(EagerOrderLine orderLine) {
        orderLines.add(orderLine);
        orderLine.setCustomerOrder(this);
    }

    @Override
    public String toString() {
        return "EagerCustomerOrder{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", orderLines=" + orderLines +
                '}';
    }
}

