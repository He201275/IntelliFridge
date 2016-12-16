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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.ADD_PRODUCT_URL;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_FRIDGE_ID;
import static ovh.intellifridge.intellifridge.Config.KEY_FRIDGE_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_BRAND;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_IMAGEURL;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_NS_ID;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_PRESENT;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_QUANTITY;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_SCANNABLE;
import static ovh.intellifridge.intellifridge.Config.KEY_PRODUCT_S_ID;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.PRODUCT_ADD_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_FRIDGES_NAME;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_FRIDGE_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_NB_FRIDGES_PREFS;
import static ovh.intellifridge.intellifridge.Config.VOLLEY_ERROR_TAG;

/**
 * Created by franc on 10-12-16.
 */

public class ProductNSRVAdapter extends RecyclerView.Adapter<ProductNSRVAdapter.ProductNSViewHolder> {
    Product[] productNsList;
    Context context;
    Fridge[] fridges;
    private String server_status;

    public ProductNSRVAdapter(Product[] productNsList, Context context) {
        this.productNsList = productNsList;
        this.context = context;
    }

    public ProductNSRVAdapter(Product[] productNsList) {
        this.productNsList = productNsList;
    }

    public static class ProductNSViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView productName,productType;

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
    public void onBindViewHolder(final ProductNSViewHolder productNSViewHolder, int position) {
        productNSViewHolder.productName.setText(productNsList[position].getProductNameNS_fr());
        productNSViewHolder.productType.setText(productNsList[position].getProductNSType());
        productNSViewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(productNSViewHolder.cardView.getContext());
                final AlertDialog alertDialog = builder.create();
                builder.setTitle(R.string.add_product_title);
                builder.setView(R.layout.add_product_ns_dialog);
                builder.setPositiveButton(R.string.add_fridge_addBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //addProductFridge();
                    }
                });
                builder.setNegativeButton(R.string.add_fridge_cancelBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        alertDialog.dismiss();
                    }
                });
                fridges = loadFridgeList();
                Log.wtf("FRID",fridges.toString());
                /*ArrayAdapter spinnerArrayAdapter= new ArrayAdapter(context,android.R.layout.simple_spinner_item,fridges);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                Spinner spinner = (Spinner)alertDialog.findViewById(R.id.fridge_spinner_ns);
                spinner.setAdapter(spinnerArrayAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                        //fridge_selected_name = adapterView.getItemAtPosition(position).toString();
                        //getFridgeIdFromName(fridge_selected_name);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });*/


                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
                return false;
            }
        });
    }

    /*private void addProductFridge() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ADD_PRODUCT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            final JWTVerifier verifier = new JWTVerifier(JWT_KEY);
                            final Map<String, Object> claims= verifier.verify(response);
                            JSONObject server_response = new JSONObject(claims);
                            server_status = server_response.getString(SERVER_STATUS);
                        } catch (JWTVerifyException e) {
                            Log.e("JWT ERROR",e.toString());
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            Toast.makeText(context,R.string.product_add_fridge_success,Toast.LENGTH_LONG).show();
                        }
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
                String apiKey = getApiKey();
                int userId = getUserId();
                int produit_ns_id = 1;
                int isScannable = 1;
                int isPresent = 1;
                fridge_selected_id = String.valueOf(fridgeId);
                String jwt = signParamsAddProduct(apiKey,userId,fridge_selected_id,produit_ns_id,scanned_barcode,isPresent,isScannable,quantity,imageUrl,fridge_selected_name);
                Map<String,String> params = new HashMap<>();
                //Adding parameters to POST request
                params.put(JWT_POST, jwt);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(stringRequest, PRODUCT_ADD_REQUEST_TAG);
    }

    String signParamsAddProduct(int apiKey,int userId,int fridge_selected_id,int product_ns_id,int scanned_barcode,int isPresent,int isScannable,int quantity,String imageUrl,String fridge_selected_name){
        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();
        claims.put(KEY_USERID,userId);
        claims.put(KEY_API_KEY,apiKey);
        claims.put(KEY_PRODUCT_NS_ID,produit_ns_id);
        claims.put(KEY_PRODUCT_S_ID,barcode);
        claims.put(KEY_PRODUCT_NAME,productName);
        claims.put(KEY_PRODUCT_BRAND,brands);
        claims.put(KEY_FRIDGE_ID,fridge_selected_id);
        claims.put(KEY_PRODUCT_IMAGEURL,imageUrl);
        claims.put(KEY_PRODUCT_QUANTITY,quantity);
        claims.put(KEY_PRODUCT_SCANNABLE,isScannable);
        claims.put(KEY_PRODUCT_PRESENT,isPresent);
        claims.put(KEY_FRIDGE_NAME,fridge_selected_name);
        return jwt = signer.sign(claims);
    }*/

    private int getUserId() {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    private String getApiKey() {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }

    public Fridge[] loadFridgeList(){
        SharedPreferences preferences =  context.getSharedPreferences(SHARED_PREF_FRIDGES_NAME, Context.MODE_PRIVATE);
        int size = preferences.getInt(USER_NB_FRIDGES_PREFS,0);
        Fridge[] fridgeList = new Fridge[size];
        for (int i=0;i<size;i++){
            Gson gson = new Gson();
            String json = preferences.getString(USER_FRIDGE_PREFS+i,"");
            fridgeList[i] = gson.fromJson(json,Fridge.class);
        }
        return fridgeList;
    }

    @Override
    public int getItemCount() {
        return productNsList.length;
    }
}
