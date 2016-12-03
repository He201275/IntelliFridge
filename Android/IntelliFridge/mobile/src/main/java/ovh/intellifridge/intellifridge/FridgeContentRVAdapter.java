package ovh.intellifridge.intellifridge;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by franc on 03-12-16.
 */

public class FridgeContentRVAdapter extends RecyclerView.Adapter<FridgeContentRVAdapter.ProductViewHolder> {
    Product[] fridgeContent;

    FridgeContentRVAdapter(Product[] content_fridge){
        this.fridgeContent = content_fridge;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView product_name,product_quantity;
        ImageView product_image; // TODO: 03-12-16  

        public ProductViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.fridge_content_cardview);
            product_name = (TextView)itemView.findViewById(R.id.card_product_name);
            product_quantity = (TextView)itemView.findViewById(R.id.card_product_quantity);
            //product_image = (ImageView)itemView.findViewById(R.id.card_product_image);
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fridge_content_card,viewGroup,false);
        ProductViewHolder pvh = new ProductViewHolder(view);
        return pvh;
    }

    @Override
    public void onBindViewHolder(ProductViewHolder productViewHolderHolder, int position) {
        productViewHolderHolder.product_name.setText(fridgeContent[position].getProductName());
        //productViewHolderHolder.product_image.setImageDrawable(Drawable.createFromPath(fridgeContent[position].getUrlImage())); // TODO: 04-12-16
        productViewHolderHolder.product_quantity.setText(Integer.toString(fridgeContent[position].getProductQuantity()));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return fridgeContent.length;
    }
}
