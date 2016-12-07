package ovh.intellifridge.intellifridge;

/**
 * Created by franc on 03-12-16.
 */

public class Product {
    String productName;
    int productSId,productNSId;
    int productQuantity;

    public Product(){
        super();
    }

    public Product(String pName, int pId, int pQuantity){
        this.productName = pName;
        this.productSId = pId;
        this.productQuantity = pQuantity;
    }

    public void setProductSId(int productSId) {
        this.productSId = productSId;
    }

    public int getProductSId() {
        return productSId;
    }

    public int getProductQuantity() {
        return productQuantity;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductQuantity(int productQuantity) {
        this.productQuantity = productQuantity;
    }
}
