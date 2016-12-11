package ovh.intellifridge.intellifridge;

/**
 * @author Francis O. Makokha
 * Objet élément liste de course hérite de {@link Product}
 */

public class GroceryListItem extends Product {
    String listNote;// TODO: 10-12-16 : display note
    public GroceryListItem(){
        super();
    }

    public GroceryListItem(String pName, long pSId,int pNSId, int pQuantity, String lNote){
        this.productSId = pSId;
        this.productNSId = pNSId;
        this.productName = pName;
        this.productQuantity = pQuantity;
        this.listNote = lNote;
    }

    public String getListNote() {
        return listNote;
    }

    @Override
    public String getProductName() {
        return super.getProductName();
    }

    @Override
    public void setProductName(String productName) {
        super.setProductName(productName);
    }

    @Override
    public int getProductQuantity() {
        return super.getProductQuantity();
    }

    @Override
    public void setProductQuantity(int productQuantity) {
        super.setProductQuantity(productQuantity);
    }

    public void setListNote(String listNote) {
        this.listNote = listNote;
    }
}
