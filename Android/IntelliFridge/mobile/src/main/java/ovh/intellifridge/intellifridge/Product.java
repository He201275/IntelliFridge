package ovh.intellifridge.intellifridge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Francis O. Makokha
 * Objet produit et ses méthodes
 */

public class Product {
    String productName,productNameNS_fr,productNameNS_en,productNameNS_nl,productNSType,frigoNom;
    int productNSId;
    long productSId;
    int productQuantity;
    Date dateAjout;

    public String getFrigoNom() {
        return frigoNom;
    }

    public void setFrigoNom(String frigoNom) {
        this.frigoNom = frigoNom;
    }

    public Date getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Date dateAjout) {
        this.dateAjout = dateAjout;
    }

    public void setDateAjout(String dateAjout) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.dateAjout = dateFormat.parse(dateAjout);
    }

    public long getProductSId() {
        return productSId;
    }

    public void setProductSId(long productSId) {

        this.productSId = productSId;
    }

    int frigoId;

    public Product(){
        super();
    }

    public Product(String pName, int pQuantity){
        this.productName = pName;
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
