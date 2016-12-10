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
        TextView productName;
        ImageView overflow,productImage;

        public ProductNSViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.product_ns_card_view);
            productName = (TextView)itemView.findViewById(R.id.product_name_card);
            overflow = (ImageView)itemView.findViewById(R.id.overflow_product_ns);
            productImage = (ImageView)itemView.findViewById(R.id.thumbnail_product_ns);
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
        if (Locale.getDefault().getDisplayLanguage().equals("English")){
            productNSViewHolder.productName.setText(productNsList[position].getProductNameNS_en());
        }else if (Locale.getDefault().getDisplayLanguage().equals("Français")){
            productNSViewHolder.productName.setText(productNsList[position].getProductNameNS_fr());
        }else if (Locale.getDefault().getDisplayLanguage().equals("Nederlands")){
            productNSViewHolder.productName.setText(productNsList[position].getProductNameNS_nl());
        }
        productNSViewHolder.productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 10-12-16  
            }
        });
        productNSViewHolder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 10-12-16
            }
        });
    }

    @Override
    public int getItemCount() {
        return productNsList.length;
    }
}
