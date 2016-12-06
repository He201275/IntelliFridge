package ovh.intellifridge.intellifridge;

/**
 * Created by franc on 03-12-16.
 */

public class Fridge {
    private int fridgeId;
    private String fridgeName;

    public Fridge(){
        super();
    }

    public Fridge(String frName){
        this.fridgeName = frName;
    }

    @Override
    public String toString() {
        return this.fridgeName;
    }

    public String getFridgeName() {
        return fridgeName;
    }

    public int getFridgeId() {
        return fridgeId;
    }

    public void setFridgeName(String fridgeName) {
        this.fridgeName = fridgeName;
    }

    public void setFridgeId(int fridgeId) {
        this.fridgeId = fridgeId;
    }
}
