/*
 * Created by Daniel Marell 12-11-09 8:33 AM
 */
package entity;

import javax.persistence.*;

@Entity
@Table(name = "PRODUCT")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "product_no", nullable = false, length = 12, unique = true)
    private String productNo;

    @Column(name = "description", nullable = false, length = 40)
    private String description;

    public Product() {
    }

    public Product(String productNo, String description) {
        this.productNo = productNo;
        this.description = description;
    }

    public Product(int id, String productNo, String description) {
        this.id = id;
        this.productNo = productNo;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getProductNo() {
        return productNo;
    }

    public void setProductNo(String productNo) {
        this.productNo = productNo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productNo='" + productNo + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
