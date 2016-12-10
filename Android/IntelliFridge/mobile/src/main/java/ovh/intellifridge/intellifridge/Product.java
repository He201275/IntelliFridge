package ovh.intellifridge.intellifridge;

/**
 * @author Francis O. Makokha
 * Objet produit et ses méthodes
 */

public class Product {
    String productName,productNameNS_fr,productNameNS_en,productNameNS_nl,productNSType;
    int productSId,productNSId;
    int productQuantity;
    int frigoId;

    public Product(){
        super();
    }

    public Product(String pName, int pId, int pQuantity){
        this.productName = pName;
        this.productSId = pId;


        this.productQuantity = pQuantity;
    }

    public String getProductNameNS_fr() {
        return productNameNS_fr;
    }

    public void setProductNameNS_fr(String productNameNS_fr) {
        this.productNameNS_fr = productNameNS_fr;
    }

    public String getProductNameNS_en() {
        return productNameNS_en;
    }

    public void setProductNameNS_en(String productNameNS_en) {
        this.productNameNS_en = productNameNS_en;
    }

    public String getProductNameNS_nl() {
        return productNameNS_nl;
    }

    public void setProductNameNS_nl(String productNameNS_nl) {
        this.productNameNS_nl = productNameNS_nl;
    }

    public String getProductNSType() {
        return productNSType;
    }

    public void setProductNSType(String productNSType) {
        this.productNSType = productNSType;
    }

    public int getProductNSId() {
        return productNSId;
    }

    public void setProductNSId(int productNSId) {
        this.productNSId = productNSId;
    }

    public void setFrigoId(int frigoId) {
        this.frigoId = frigoId;
    }

    public int getFrigoId() {
        return frigoId;
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
