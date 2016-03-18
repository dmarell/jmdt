/*
 * Created by Daniel Marell 12-11-09 8:32 AM
 */
package entity;

import javax.persistence.*;

@Entity
@Table(name = "ORDERLINE")
public class OrderLine {
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
    private CustomerOrder customerOrder;

    public OrderLine() {
    }

    public OrderLine(Product product, int numItems) {
        this.product = product;
        this.numItems = numItems;
    }

    public OrderLine(Integer id, Product product, int numItems) {
        this.id = id;
        this.product = product;
        this.numItems = numItems;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getNumItems() {
        return numItems;
    }

    public void setNumItems(int numItems) {
        this.numItems = numItems;
    }

    public CustomerOrder getCustomerOrder() {
        return customerOrder;
    }

    public void setCustomerOrder(CustomerOrder order) {
        this.customerOrder = order;
    }

    @Override
    public String toString() {
        return "OrderLine{" +
                "id=" + id +
                ", product=" + product +
                ", numItems=" + numItems +
                '}';
    }
}
