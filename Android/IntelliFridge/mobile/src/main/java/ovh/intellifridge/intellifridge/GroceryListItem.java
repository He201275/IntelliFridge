package ovh.intellifridge.intellifridge;

/**
 * Created by franc on 06-12-16.
 */

public class GroceryListItem extends Product {
    String listNote;
    public GroceryListItem(){
        super();
    }

    public GroceryListItem(String pName, int pSId,int pNSId, int pQuantity, String lNote){
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

    @Override
    public int getProductSId() {
        return super.getProductSId();
    }

    @Override
    public void setProductSId(int productSId) {
        super.setProductSId(productSId);
    }

    public void setListNote(String listNote) {
        this.listNote = listNote;
    }
}
