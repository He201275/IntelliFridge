package ovh.intellifridge.intellifridge;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * @author Francis O. Makokha
 * {@link FridgeContentRVAdapter}
 * Gère les éléments de la liste de courses
 */

public class GroceryListRVAdapter extends RecyclerView.Adapter<GroceryListRVAdapter.GroceryListViewHolder> {
    GroceryListItem[] groceryListItems;

    GroceryListRVAdapter(GroceryListItem[] glItems){
        this.groceryListItems = glItems;
    }
    
    public static class GroceryListViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView productName,productQuantity;

        public GroceryListViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.grocery_list_cardview);
            productName = (TextView)itemView.findViewById(R.id.card_product_name_gl);
            productQuantity = (TextView)itemView.findViewById(R.id.card_product_quantity_gl);
        }
    }
    @Override
    public GroceryListViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.grocery_list_card,viewGroup,false);
        GroceryListViewHolder glvh = new GroceryListViewHolder(view);
        return glvh;
    }

    @Override
    public void onBindViewHolder(GroceryListViewHolder groceryListViewHolder, int position) {
        groceryListViewHolder.productName.setText(groceryListItems[position].getProductName());
        groceryListViewHolder.productQuantity.setText(Integer.toString(groceryListItems[position].getProductQuantity()));
        // TODO: 06-12-16
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return groceryListItems.length;
    }
}
