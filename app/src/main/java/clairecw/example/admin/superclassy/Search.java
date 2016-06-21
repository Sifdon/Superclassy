package clairecw.example.admin.superclassy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Search extends ActionBarActivity implements View.OnFocusChangeListener, View.OnClickListener {

    Button uploadButton, homeButton, groupsButton, profileButton;
    Button search;
    ListView userList;
    EditText searchBar;
    AuthData user;
    ToggleButton toggle;
    final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
    ArrayList<String> displayData = new ArrayList<String>();
    ArrayList<String> userIds = new ArrayList<String>(), usernames = new ArrayList<String>(), userData = new ArrayList<String>();
    ArrayList<String> groupIds = new ArrayList<String>(), groupNames = new ArrayList<String>(), groupData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        uploadButton = (Button)findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(this);
        uploadButton.setBackgroundColor(Color.TRANSPARENT);

        homeButton = (Button)findViewById(R.id.homeButton);
        homeButton.setOnClickListener(this);
        homeButton.setBackgroundColor(Color.TRANSPARENT);

        profileButton = (Button)findViewById(R.id.profileButton);
        profileButton.setOnClickListener(this);
        profileButton.setBackgroundColor(Color.TRANSPARENT);

        groupsButton = (Button)findViewById(R.id.groupsButton);
        groupsButton.setOnClickListener(this);
        groupsButton.setBackgroundColor(Color.TRANSPARENT);

        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(this);

        userList = (ListView)findViewById(R.id.listView);
        searchBar = (EditText)findViewById(R.id.searchBar);
        searchBar.setOnFocusChangeListener(this);

        toggle = (ToggleButton)findViewById(R.id.toggleButton);
        toggle.setOnClickListener(this);

        searchBar.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    if (!event.isShiftPressed()) performSearch(searchBar.getText().toString());
                }

                return true;
            }

        });

        Firebase.setAndroidContext(this);
        user = myFirebaseRef.getAuth();

        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                if (!toggle.isChecked()) {
                    String userId = userIds.get(usernames.indexOf(displayData.get(position)));
                    Intent intent = new Intent(getBaseContext(), ViewUser.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
                if (toggle.isChecked()) {
                    String groupId = groupIds.get(groupNames.indexOf(displayData.get(position)));
                    Intent intent = new Intent(getBaseContext(), ViewGroup.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                }
            }
        });

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                HashMap<String, Object> files = (HashMap<String, Object>) snapshot.child("users").getValue();
                for (Map.Entry<String, Object> entry : files.entrySet()) {
                    userIds.add(entry.getKey());
                    HashMap<String, Object> user = (HashMap<String, Object>) entry.getValue();
                    usernames.add((String) user.get("username"));
                    String userDatum = (String) user.get("username");
                    if (user.get("tags") != null) {
                        for (String s : (ArrayList<String>) user.get("tags")) {
                            userDatum += " " + s;
                        }
                    }
                    userData.add(userDatum);
                }
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    private void performSearch(String query) {

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideKeyboard(v);
        }
    }

    public void onClick(View v) {
        if (v == uploadButton) {
            Intent myIntent = new Intent(this, UploadFile.class);
            startActivity(myIntent);
            finish();
        }
        if (v == homeButton) {
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
            finish();
        }
        if (v == profileButton) {
            Intent myIntent = new Intent(this, AccountEdit.class);
            startActivity(myIntent);
            finish();
        }
        if (v == groupsButton) {
            Intent intent = new Intent(this, Groups.class);
            startActivity(intent);
            finish();
        }
        if (v == search) {
            displayData.clear();
            if (!toggle.isChecked()) searchUsers();
            if (toggle.isChecked()) searchGroups();
        }
        if (v == toggle) {
            if (groupData.isEmpty()) {
                myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        HashMap<String, Object> files = (HashMap<String, Object>)snapshot.child("groups").getValue();
                        for (Map.Entry<String, Object> entry : files.entrySet()) {
                            groupIds.add(entry.getKey());
                            HashMap<String, Object> user = (HashMap<String, Object>)entry.getValue();
                            groupNames.add((String)user.get("name"));
                            String userDatum = (String)user.get("name") + " " + (String)user.get("description") + " ";
                            switch (((Long)user.get("type")).intValue()) {
                                case 1: userDatum += "project"; break;
                                case 2: userDatum += "sports"; break;
                                case 3: userDatum += "career"; break;
                                case 4: userDatum += "miscellaneous interest"; break;
                                case 5: userDatum += "academic subject"; break;
                            }
                            groupData.add(userDatum);
                        }
                    }

                    public void onCancelled(FirebaseError firebaseError) {
                    }
                });
            }
        }
    }

    public void searchUsers() {
        for (int i = 0; i < userData.size(); i++) {
            if (userData.get(i).toLowerCase().contains(searchBar.getText().toString().toLowerCase())) {
                if (!displayData.contains(usernames.get(i))) displayData.add(usernames.get(i));
            }
        }
        userList.setAdapter(new ArrayAdapter<String>(Search.this, R.layout.simplerow, displayData));
    }

    public void searchGroups() {
        for (int i = 0; i < groupData.size(); i++) {
            if (groupData.get(i).toLowerCase().contains(searchBar.getText().toString().toLowerCase())) {
                if (!displayData.contains(groupNames.get(i))) displayData.add(groupNames.get(i));
            }
        }
        userList.setAdapter(new ArrayAdapter<String>(Search.this, R.layout.simplerow, displayData));
    }
}
