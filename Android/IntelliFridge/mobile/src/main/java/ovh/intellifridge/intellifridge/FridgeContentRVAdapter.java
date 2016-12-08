package ovh.intellifridge.intellifridge;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.ADD_PRODUCT_URL;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_ADD_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.VOLLEY_ERROR_TAG;

/**
 * Created by franc on 03-12-16.
 */

public class FridgeContentRVAdapter extends RecyclerView.Adapter<FridgeContentRVAdapter.ProductViewHolder>{
    Product[] fridgeContent;
    private JSONObject server_response;
    int productId, fridgeId;

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
    public void onBindViewHolder(final ProductViewHolder productViewHolderHolder, int position) {
        productId = fridgeContent[position].getProductSId();

        productViewHolderHolder.product_name.setText(fridgeContent[position].getProductName());
        productViewHolderHolder.product_quantity.setText(Integer.toString(fridgeContent[position].getProductQuantity()));
        productViewHolderHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(productViewHolderHolder.cardView.getContext());
                builder.setTitle(R.string.fridge_content_dialog_title);
                builder.setMessage(productViewHolderHolder.product_name.getText());
                builder.setPositiveButton(R.string.add_fridgeContent_addBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 07-12-16  
                    }
                });
                builder.setNegativeButton(R.string.add_fridgeContent_removeBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeOneProduct();
                    }
                });
                builder.show();
                return false;
            }
        });
    }

    private void removeOneProduct() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.wtf("RES",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(VOLLEY_ERROR_TAG,error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                /*String apiKey = getApiKey();
                int userId = getUserId();
                String jwt = signParamsAddProductS(apiKey,userId,barcode,productName,brands,fridge_selected,imageUrl,quantity);*/
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                //params.put(JWT_POST, jwt);
                return params;
            }
        };
        //MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest, PRODUCT_ADD_REQUEST_TAG);
    }

    public void remove(int position) {
        //fridgeContent.remove(position);
        notifyItemRemoved(position);
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
