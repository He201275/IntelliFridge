package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.JWTSigner;

import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_FRIDGE_ID;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_S_ID;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_REMOVE_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.REMOVE_ONE_PRODUCT_URL;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;
import static ovh.intellifridge.intellifridge.Config.VOLLEY_ERROR_TAG;

/**
 * @author Francis O. Makokha
 * S'occupe de l'affichage individuel de chaque produit contenu dans un frigo et les intéractions de l'utilisateur sur les cartes qui affichent les produits
 */
public class FridgeContentRVAdapter extends RecyclerView.Adapter<FridgeContentRVAdapter.ProductViewHolder>{
    Product[] fridgeContent;
    int productId, fridgeId;

    FridgeContentRVAdapter(Product[] content_fridge){
        this.fridgeContent = content_fridge;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView product_name,product_quantity;

        public ProductViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.fridge_content_cardview);
            product_name = (TextView)itemView.findViewById(R.id.card_product_name);
            product_quantity = (TextView)itemView.findViewById(R.id.card_product_quantity);
        }
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fridge_content_card,viewGroup,false);
        ProductViewHolder pvh = new ProductViewHolder(view);
        return pvh;
    }

    /**
     * Affiche les différents textes où il faut sur la carte, à partir des informations d'objet {@link ovh.intellifridge.intellifridge.Product}
     * Applique aussi les différents listeners présents sur la carte
     * @param productViewHolderHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(final ProductViewHolder productViewHolderHolder, int position) {
        productId = fridgeContent[position].getProductSId();
        fridgeId = fridgeContent[position].getFrigoId();
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
                        removeOneProduct(productViewHolderHolder.cardView.getContext());
                    }
                });
                builder.show();
                return false;
            }
        });
    }

    /**
     * Permet d'enlever un élément d'un produit
     * Ex.: Si on a 4 bouteilles d'eau, on passe à 3
     * @param context
     */
    private void removeOneProduct(final Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, REMOVE_ONE_PRODUCT_URL,
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
                String apiKey = getApiKey(context);
                int userId = getUserId(context);
                String jwt = signParamsRemoveOneProduct(apiKey,userId);
                Map<String,String> params = new HashMap<>();
                params.put(JWT_POST, jwt);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(stringRequest, PRODUCT_REMOVE_REQUEST_TAG);
    }

    /**
     * Signe la requête pour {@link #removeOneProduct(Context)}
     * @param apiKey
     * @param userId
     * @return
     */
    private String signParamsRemoveOneProduct(String apiKey, int userId) {
        final JWTSigner signer = new JWTSigner(JWT_KEY);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_PRODUCT_S_ID,productId);
        claims.put(KEY_USERID,userId);
        claims.put(KEY_API_KEY,apiKey);
        claims.put(KEY_FRIDGE_ID,fridgeId);
        return signer.sign(claims);
    }

    public void remove(int position) {
        //fridgeContent.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * {@link BarcodeReaderActivity#getUserId()}
     * @param context
     * @return
     */
    private int getUserId(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    /**
     * {@link BarcodeReaderActivity#getApiKey()}
     * @param context
     * @return
     */
    private String getApiKey(Context context){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * Retourne le nombre de cartes dans un frigo
     * @return
     */
    @Override
    public int getItemCount() {
        return fridgeContent.length;
    }
}
