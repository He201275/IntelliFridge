package ovh.intellifridge.intellifridge;

/**
 * Created by franc on 03-12-16.
 */

public class Fridge {
    private String fridgeName;
    private int nbProducts; // TODO: 03-12-16

    public Fridge(){
        super();
    }

    public Fridge(String frName){
        this.fridgeName = frName;
    }

    public String getFridgeName() {
        return fridgeName;
    }

    public void setFridgeName(String fridgeName) {
        this.fridgeName = fridgeName;
    }

    public int getNbProducts() {
        return nbProducts;
    }

    public void setNbProducts(int nbProducts) {
        this.nbProducts = nbProducts;
    }
}
