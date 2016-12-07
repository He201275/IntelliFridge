package ovh.intellifridge.intellifridge;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Created by franc on 06-12-16.
 */

public class FridgeContentTouchHelper extends ItemTouchHelper.SimpleCallback {
    private FridgeContentRVAdapter mFridgeContentRVAdapter;

    public FridgeContentTouchHelper(FridgeContentRVAdapter fridgeContentRVAdapter) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.mFridgeContentRVAdapter = fridgeContentRVAdapter;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        //Remove product
        mFridgeContentRVAdapter.remove(viewHolder.getAdapterPosition());
    }
}
