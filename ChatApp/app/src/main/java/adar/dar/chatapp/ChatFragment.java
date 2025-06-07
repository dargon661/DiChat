package adar.dar.chatapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;

import adar.dar.chatapp.Model.ChatRoom;
import adar.dar.chatapp.Model.FireBaseUtil;
import adar.dar.chatapp.Model.Message;
import adar.dar.chatapp.Model.Request;
import adar.dar.chatapp.Model.User;
import adar.dar.chatapp.RecyclerView.GrAdapter;
import adar.dar.chatapp.RecyclerView.GrAdapterRecentChats;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView;
    RequestFragment requestFragment;
    List<ChatRoom> chatRoomsList;
    EditText searchBar;
    ImageButton Requets;
    TextView titleText;
    ChatFragment chatFragment;
    View view;
    GrAdapterRecentChats adapter;

    @Override
    public void onResume() {
        super.onResume();
        UsersList();  // reload chat rooms when fragment is resumed
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_chat, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.FCrecentChats);
        chatRoomsList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(layoutManager);


        // Load initial chat data


        // Initialize views and logic
        InitView();

        return view;
    }

    // Initializes UI elements and sets up listeners
    private void InitView() {
        adapter = new GrAdapterRecentChats(chatRoomsList, view.getContext());
        recyclerView.setAdapter(adapter);

        Requets = view.findViewById(R.id.FCInvites);
        searchBar = view.findViewById(R.id.searchBar);
        titleText = view.findViewById(R.id.tv_veri);
        chatFragment = new ChatFragment();
        requestFragment = new RequestFragment();

        ImageButton searchButton = view.findViewById(R.id.AMsearch);
        searchButton.setOnClickListener(v -> {
            // Toggle search bar visibility
            if (searchBar.getVisibility() == View.GONE) {
                titleText.setVisibility(View.GONE);
                searchBar.setVisibility(View.VISIBLE);
                searchBar.requestFocus();

                // Show keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(view.getContext().INPUT_METHOD_SERVICE);
                imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT);
            } else {
                searchBar.setVisibility(View.GONE);
                titleText.setVisibility(View.VISIBLE);

                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(view.getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchBar.getWindowToken(), 0);
                searchBar.setVisibility(View.GONE);
                titleText.setVisibility(View.VISIBLE);
                searchBar.setText(""); // <- This clears the search text
                FilteredRV(""); // <- This restores full list
            }
        });

        // Trigger search when user presses search or done key
        searchBar.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE) {

                String searchTerm = searchBar.getText().toString();

                FilteredRV(searchTerm);


                return true;
            }
            return false;
        });

        // Search as user types

        String text="";
        // Check if there are pending requests and show notification dot
        CheckRequests();

        // Navigate to request screen when button is clicked
        Requets.setOnClickListener(v -> {
            getParentFragmentManager().beginTransaction().replace(R.id.mainFrameLayout, requestFragment).commit();
        });
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!s.toString().isEmpty())
                {
                    FilteredRV(s.toString());
                }
                else
                {
                    adapter.updateList(chatRoomsList);
                }

            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // Checks for friend/chat requests and shows/hides a red dot
    private void CheckRequests() {
        FireBaseUtil.GetAllRequests()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        boolean hasRequests = false;
                        for (DocumentSnapshot doc : task.getResult().getDocuments()) {
                            Request request = doc.toObject(Request.class);
                            if (Objects.equals(request.getRecieverId(), FireBaseUtil.currentUserId())) {
                                hasRequests = true;
                                break; // Exit early if one request is found
                            }
                        }

                        View redDot = view.findViewById(R.id.notification_dot);
                        if (redDot != null) {
                            redDot.setVisibility(hasRequests ? View.VISIBLE : View.GONE);
                        }
                    } else {
                        Log.e("CheckRequests", "Error checking requests", task.getException());
                    }
                });
    }

    // Filters the RecyclerView based on a search term
    private void FilteredRV(String searchTerm) {
        List<ChatRoom> filteredList = new ArrayList<>();
        List<ChatRoom> tempList = new ArrayList<>(chatRoomsList);

        if (searchTerm.trim().isEmpty()) {
            return;
        }

        final int[] loadedCount = {0};

        for (ChatRoom room : tempList) {
            FireBaseUtil.getOtherUserFromChatroom(room.getUserIds()).get().addOnSuccessListener(task -> {
                loadedCount[0]++;
                if (task.exists()) {
                    User otherUser = task.toObject(User.class);
                    if (otherUser != null && otherUser.getUsername() != null &&
                            otherUser.getUsername().toLowerCase().contains(searchTerm.toLowerCase())) {
                        filteredList.add(room);
                    }
                }

                // Update adapter once all lookups are complete
                if (loadedCount[0] == tempList.size()) {
                    adapter.updateList(filteredList);
                }
            });
        }
    }



    // Loads all chat rooms and filters them by current user ID
    public void UsersList() {
        FireBaseUtil.GetAllChatRooms()
                .orderBy("lastMsgTime", Query.Direction.DESCENDING)
                .addSnapshotListener((querySnapshot2, error) -> {
                    if (error != null) {
                        error.printStackTrace();
                        return;
                    }

                    if (querySnapshot2 != null) {
                        chatRoomsList.clear();
                        for (DocumentSnapshot document : querySnapshot2.getDocuments()) {
                            ChatRoom chatRoom = document.toObject(ChatRoom.class);
                            if (chatRoom.getRoomId().contains(FireBaseUtil.currentUserId())) {
                                chatRoomsList.add(chatRoom);
                            }
                        }
                        ShowUsers(); // Refresh RecyclerView
                    }
                });
    }

    // Notify adapter to redraw data
    public void ShowUsers() {
        recyclerView.getAdapter().notifyDataSetChanged();
    }

}
