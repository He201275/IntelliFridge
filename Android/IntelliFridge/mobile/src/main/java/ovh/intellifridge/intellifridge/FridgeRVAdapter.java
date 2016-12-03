package ovh.intellifridge.intellifridge;

import android.content.Context;
import android.content.Intent;
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

import static ovh.intellifridge.intellifridge.Config.FRIDGE_NAME_EXTRA;


public class FridgeRVAdapter extends RecyclerView.Adapter<FridgeRVAdapter.FridgeViewHolder> {
    Fridge[] fridges;
    Context context;

    public static class FridgeViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView fridgeName,nbItemsFridge; // TODO: 03-12-16
        ImageView overflow;

        public FridgeViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.fridge_card_view);
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
    public void onBindViewHolder(final FridgeViewHolder fridgeViewHolder, int position) {
        fridgeViewHolder.fridgeName.setText(fridges[position].getFridgeName());
        fridgeViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Opening fridge...",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(view.getContext(),FridgeContentActivity.class);
                intent.putExtra(FRIDGE_NAME_EXTRA,fridges[fridgeViewHolder.getAdapterPosition()].getFridgeName());
                Log.wtf("FRIDGE",fridgeViewHolder.fridgeName.toString());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return fridges.length;
    }

    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(context, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_fridge_more, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_edit:
                    // TODO: 03-12-16
                    return true;
                case R.id.action_delete:
                    // TODO: 03-12-16
                    return true;
                default:
            }
            return false;
        }
    }
}
