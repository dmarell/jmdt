/*
 * Created by Daniel Marell 12-11-09 8:32 AM
 */
package se.marell.jmdt.presentation;

import entity.Product;

import javax.persistence.*;

@Entity
@Table(name = "EAGERORDERLINE")
public class EagerOrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "num_items", nullable = false)
    private int numItems;

    @ManyToOne
    @JoinColumn(name = "customer_order_id")
    private EagerCustomerOrder customerOrder;

    public EagerOrderLine() {
    }

    public EagerOrderLine(int numItems) {
        this.numItems = numItems;
    }

    public EagerOrderLine(Integer id, int numItems) {
        this.id = id;
        this.numItems = numItems;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    public EagerCustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(EagerCustomerOrder order) {
        this.customerOrder = order;
    }

    @Override
    public String toString() {
        return "EagerOrderLine{" +
                "id=" + id +
                ", numItems=" + numItems +
                '}';
    }
}
