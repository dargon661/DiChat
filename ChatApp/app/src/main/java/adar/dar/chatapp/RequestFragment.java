package adar.dar.chatapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import adar.dar.chatapp.Model.FireBaseUtil;
import adar.dar.chatapp.Model.Request;
import adar.dar.chatapp.RecyclerView.GrAdapterRequest;


public class RequestFragment extends Fragment {


    ImageView backButton;
    ChatFragment chatFragment;
    RecyclerView recyclerView;
    List<Request> requests;
    View view;

    public RequestFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        RequstsList(); // Ensures data is reloaded on fragment re-entry
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_request, container, false);
        recyclerView=view.findViewById(R.id.ARrequests);


        requests=new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(new GrAdapterRequest(requests, view.getContext()));


        RequstsList();
        InitView();

        return view;
    }


    private void InitView() {
        backButton=view.findViewById(R.id.FRback);
        chatFragment=new ChatFragment();
        backButton.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.mainFrameLayout,chatFragment).commit();
        });



    }
    private void RequstsList() {
        FireBaseUtil.GetAllRequests()
                .orderBy("createdTimestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot2, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }
                    if (querySnapshot2 != null) {
                        requests.clear(); // Clear old data
                        for (DocumentSnapshot document : querySnapshot2.getDocuments()) {

                            Request request = document.toObject(Request.class);
                            if(Objects.equals(request.getRecieverId(), FireBaseUtil.currentUserId()))
                            {
                                requests.add(request);
                            }


                        }
                        ShowUsers(); // Refresh the adapter immediately
                    }
                });

    }
    public void ShowUsers() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }



}