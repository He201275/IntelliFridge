package ovh.intellifridge.intellifridge;

/**
 * Created by franc on 03-12-16.
 */

public class Product {
    String productName;
    int productId;
    int productQuantity;

    public Product(){
        super();
    }

    public Product(String pName, int pId, int pQuantity){
        this.productName = pName;
        this.productId = pId;
        this.productQuantity = pQuantity;
    }

    @Override
    public String toString() {
        return this.productName + " Qtt. :" +this.productQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
}
