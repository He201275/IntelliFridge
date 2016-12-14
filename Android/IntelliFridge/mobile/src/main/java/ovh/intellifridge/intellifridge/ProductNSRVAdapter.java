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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import static ovh.intellifridge.intellifridge.Config.SHARED_PREF_FRIDGES_NAME;
import static ovh.intellifridge.intellifridge.Config.USER_FRIDGE_PREFS;
import static ovh.intellifridge.intellifridge.Config.USER_NB_FRIDGES_PREFS;

/**
 * Created by franc on 10-12-16.
 */

public class ProductNSRVAdapter extends RecyclerView.Adapter<ProductNSRVAdapter.ProductNSViewHolder> {
    Product[] productNsList;
    Context context;
    Fridge[] fridges;

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
                AlertDialog.Builder builder = new AlertDialog.Builder(productNSViewHolder.cardView.getContext());
                builder.setTitle(R.string.add_product_title);
                builder.setView(R.layout.add_product_ns_dialog);
                builder.setPositiveButton(R.string.add_fridge_addBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setNegativeButton(R.string.add_fridge_cancelBtn, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                final AlertDialog alertDialog = builder.create();
                fridges = loadFridgeList();
                Log.wtf("FRID",fridges.toString());
                ArrayAdapter spinnerArrayAdapter= new ArrayAdapter(context,android.R.layout.simple_spinner_item,fridges);
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
                });


                alertDialog.show();
                alertDialog.setCanceledOnTouchOutside(false);
                return false;
            }
        });
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
