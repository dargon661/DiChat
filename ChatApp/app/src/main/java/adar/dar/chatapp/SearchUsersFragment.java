package adar.dar.chatapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import adar.dar.chatapp.Model.FireBaseUtil;
import adar.dar.chatapp.Model.User;
import adar.dar.chatapp.RecyclerView.GrAdapter;


public class SearchUsersFragment extends Fragment implements View.OnClickListener {

    EditText searchUsers;
    ImageButton search;
    RecyclerView recyclerView;
    List<User> userList=new ArrayList<>();
    Button sendRequest;

    View view;

    public SearchUsersFragment() {

    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =inflater.inflate(R.layout.fragment_search_users, container, false);
        userList.clear();
        initViews();
        return view;
    }
    private void initViews() {
        searchUsers=view.findViewById(R.id.SUAeditTextSearch);
        search=view.findViewById(R.id.SUAsearch);
        recyclerView=view.findViewById(R.id.SUAusers);
        sendRequest=view.findViewById(R.id.IULsend);

        searchUsers.requestFocus();

        search.setOnClickListener(this);
        UsersList();
        searchUsers.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {

                String searchTerm = searchUsers.getText().toString();
                if(!searchTerm.isEmpty() && searchTerm.length()>2){

                    FilteredRV(searchTerm);
                    return true;
                }
                searchUsers.setError("Invalid Username");
                return true;
            }
            return false;
        });
        searchUsers.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FilteredRV(s.toString()); // Call filtering method while typing
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        String searchTerm = searchUsers.getText().toString();
        if(searchTerm.isEmpty() || searchTerm.length()<3){
            searchUsers.setError("Invalid Username");
            return;
        }
        FilteredRV(searchTerm);
    }
    public void FilteredRV(String search)
    {
        String searchLower = search.toLowerCase();

        // Filter the list using Java Streams (Java 8+)
        List<User> filteredList = userList.stream()
                .filter(user -> user.getUsername().toLowerCase().contains(searchLower))
                .collect(Collectors.toList());

        // Update the RecyclerView with the filtered list
        GrAdapter grAdapter = new GrAdapter(filteredList, view.getContext());
        recyclerView.setAdapter(grAdapter);
    }
    public void UsersList()
    {
        FireBaseUtil.getAllUsers().thenAccept(users -> {
            // Process the user list
            userList.addAll(users);
            ShowUsers();
        }).exceptionally(e -> {
            e.printStackTrace();
            return null;
        });
    }
    public void ShowUsers()
    {
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);
        GrAdapter grAdapter = new GrAdapter(userList, view.getContext());
        recyclerView.setAdapter(grAdapter);
    }


}