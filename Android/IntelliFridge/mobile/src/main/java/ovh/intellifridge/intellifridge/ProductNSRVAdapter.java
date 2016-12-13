package ovh.intellifridge.intellifridge;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * Created by franc on 10-12-16.
 */

public class ProductNSRVAdapter extends RecyclerView.Adapter<ProductNSRVAdapter.ProductNSViewHolder> {
    Product[] productNsList;

    public ProductNSRVAdapter(Product[] productNsList) {
        this.productNsList = productNsList;
    }

    public static class ProductNSViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView productName,productType;
        ImageView overflow;

        public ProductNSViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.product_ns_card_view);
            productName = (TextView)itemView.findViewById(R.id.product_ns_name);
            productType = (TextView)itemView.findViewById(R.id.product_ns_type);
        }
    }
    @Override
    public ProductNSViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product_ns_card,viewGroup,false);
        ProductNSViewHolder productNSViewHolder = new ProductNSViewHolder(view);
        return productNSViewHolder;
    }

    @Override
    public void onBindViewHolder(ProductNSViewHolder productNSViewHolder, int position) {
        productNSViewHolder.productName.setText(productNsList[position].getProductNameNS_fr());
        productNSViewHolder.productType.setText(productNsList[position].getProductNSType());
        productNSViewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return productNsList.length;
    }
}
