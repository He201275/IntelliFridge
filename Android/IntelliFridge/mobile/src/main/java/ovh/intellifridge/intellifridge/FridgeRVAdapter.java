package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.JWTVerifyException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.HashMap;
import java.util.Map;

import static ovh.intellifridge.intellifridge.Config.DATA;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_GET_LIST_URL;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_LIST_REQUEST_TAG;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_NAME_EXTRA;
import static ovh.intellifridge.intellifridge.Config.FRIDGE_REMOVE_URL;
import static ovh.intellifridge.intellifridge.Config.JWT_KEY;
import static ovh.intellifridge.intellifridge.Config.JWT_POST;
import static ovh.intellifridge.intellifridge.Config.KEY_API_KEY;
import static ovh.intellifridge.intellifridge.Config.KEY_FRIDGE_NAME;
import static ovh.intellifridge.intellifridge.Config.KEY_USERID;
import static ovh.intellifridge.intellifridge.Config.SERVER_STATUS;
import static ovh.intellifridge.intellifridge.Config.SERVER_SUCCESS;
import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_API_KEY;
import static ovh.intellifridge.intellifridge.Config.USER_ID_PREFS;


public class FridgeRVAdapter extends RecyclerView.Adapter<FridgeRVAdapter.FridgeViewHolder> {
    Fridge[] fridges;
    Context context;
    private JSONObject server_response;
    private String server_status;
    JSONArray jsonArray;

    public static class FridgeViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView fridgeName,nbItemsFridge; // TODO: 03-12-16
        ImageView overflow, imageView;

        public FridgeViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.fridge_card_view);
            imageView = (ImageView)itemView.findViewById(R.id.thumbnail);
            fridgeName = (TextView)itemView.findViewById(R.id.fridge_name_card);
            overflow = (ImageView) itemView.findViewById(R.id.overflow);
        }
    }

    FridgeRVAdapter(Fridge[] fridgeList,Context ctx){
        this.fridges = fridgeList;
        this.context = ctx;
    }

    @Override
    public FridgeViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fridge_card,viewGroup,false);
        FridgeViewHolder fvh = new FridgeViewHolder(view);
        return fvh;
    }

    @Override
    public void onBindViewHolder(final FridgeViewHolder fridgeViewHolder, final int position) {
        fridgeViewHolder.fridgeName.setText(fridges[position].getFridgeName());
        fridgeViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),R.string.open_fridge,Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(),FridgeContentActivity.class);
                intent.putExtra(FRIDGE_NAME_EXTRA,fridges[fridgeViewHolder.getAdapterPosition()].getFridgeName());
                view.getContext().startActivity(intent);
            }
        });
        fridgeViewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return Boolean.parseBoolean(null);
            }
        });

        fridgeViewHolder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                PopupMenu popup = new PopupMenu(view.getContext(), fridgeViewHolder.overflow);
                popup.getMenuInflater().inflate(R.menu.menu_fridge_more, popup.getMenu());
                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();
                        // TODO: 04-12-16
                        switch (id){
                            case R.id.action_delete:
                                deleteFridge(fridges[position].getFridgeName());
                                break;
                            case R.id.action_edit:
                                editFridge(fridges[position].getFridgeName());
                                break;
                        }
                        Toast.makeText(view.getContext(),"You Clicked : " + item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    }
                });

                popup.show();//showing popup menu
            }
        });
    }

    private void editFridge(String fridgeName) {
    }

    private void deleteFridge(final String fridgeName) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, FRIDGE_REMOVE_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        final String secret = JWT_KEY;
                        try {
                            final JWTVerifier verifier = new JWTVerifier(secret);
                            final Map<String, Object> claims= verifier.verify(response);
                            server_response = new JSONObject(claims);
                            server_status = server_response.getString(SERVER_STATUS);
                            jsonArray = server_response.getJSONArray(DATA);
                        } catch (JWTVerifyException e) {
                            // Invalid Token
                            Log.e("JWT ERROR",e.toString());
                        } catch (NoSuchAlgorithmException | IOException | SignatureException | InvalidKeyException | JSONException e) {
                            e.printStackTrace();
                        }

                        if (server_status.equals(SERVER_SUCCESS)){
                            Toast.makeText(context,R.string.remove_fridge_success,Toast.LENGTH_SHORT).show();
                            // TODO: 04-12-16 : Auto refresh
                        }else{
                            // TODO: 04-12-16
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY ERROR",error.toString());
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String apiKey = getApiKey();
                int userId = getUserId();
                Map<String,String> params = new HashMap<>();
                String jwt = signParams(fridgeName,userId,apiKey);
                //Adding parameters to POST request
                params.put(JWT_POST,jwt);
                return params;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(stringRequest, FRIDGE_LIST_REQUEST_TAG);
    }

    private String signParams(String fridgeName, int userId, String apiKey) {
        final String secret = JWT_KEY;

        final JWTSigner signer = new JWTSigner(secret);
        final HashMap<String, Object> claims = new HashMap<String, Object>();

        claims.put(KEY_USERID, String.valueOf(userId));
        claims.put(KEY_API_KEY, apiKey);
        claims.put(KEY_FRIDGE_NAME,fridgeName);
        return signer.sign(claims);
    }

    private String getApiKey(){
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME,Context.MODE_PRIVATE);
        return preferences.getString(USER_API_KEY,"");
    }

    private int getUserId() {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getInt(USER_ID_PREFS,0);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return fridges.length;
    }
}
