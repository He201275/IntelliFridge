package ovh.intellifridge.intellifridge;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllergyFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    SwipeRefreshLayout swipeLayout;
    private View rootView;

    public AllergyFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_allergy, container, false);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_allergy);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(getResources().getColor(android.R.color.holo_red_dark));

        displayAllergyList();

        return rootView;
    }

    private void displayAllergyList() {
        String[] list= {"Gluten","Crustacés","Oeufs","Poisson","Arachides","Soja","Lactose","Noix","Céleri","Moutarde","Sésame","Sulfites","Lupin","Mollusque"};
        ListView listView = (ListView) rootView.findViewById(R.id.allergy_list);
        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_list_item_checked,
                list
        );
        listView.setAdapter(listViewAdapter);
    }

    @Override
    public void onRefresh() {
        displayAllergyList();
        swipeLayout.setRefreshing(false);
    }
}
