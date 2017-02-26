package clairecw.example.admin.superclassy;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.ToggleButton;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search extends ActionBarActivity implements View.OnFocusChangeListener, View.OnClickListener {

    Button search;
    ListView userList;
    EditText searchBar;
    AuthData user;
    SimpleAdapter adapterL;
    Spinner memberSpinner;
    String value;
    final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
    ArrayList<String> displayData = new ArrayList<String>();
    ArrayList<String> userIds = new ArrayList<String>(), usernames = new ArrayList<String>(), userData = new ArrayList<String>();
    ArrayList<String> groupIds = new ArrayList<String>(), groupNames = new ArrayList<String>(), groupData = new ArrayList<String>();
    ArrayList<String> pageIds = new ArrayList<String>(), pageNames = new ArrayList<String>(), pageData = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        memberSpinner = (Spinner) findViewById(R.id.toggleButton);
        setSpinner();

        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent myIntent;
                        switch (item.getItemId()) {
                            case R.id.action_home:
                                myIntent = new Intent(getBaseContext(), Dashboard.class);
                                startActivity(myIntent);
                                finish();
                                break;
                            case R.id.action_search:
                                break;
                            case R.id.action_upload:
                                myIntent = new Intent(getBaseContext(), UploadFile.class);
                                startActivity(myIntent);
                                finish();
                                break;
                            case R.id.action_groups:
                                myIntent = new Intent(getBaseContext(), Groups.class);
                                startActivity(myIntent);
                                finish();
                                break;
                            case R.id.action_account:
                                myIntent = new Intent(getBaseContext(), AccountEdit.class);
                                startActivity(myIntent);
                                finish();
                                break;

                        }
                        return true;
                    }
                });
        View view = bottomNavigationView.findViewById(R.id.action_search);
        view.performClick();

        search = (Button)findViewById(R.id.search);
        search.setOnClickListener(this);

        userList = (ListView)findViewById(R.id.listView);
        searchBar = (EditText)findViewById(R.id.searchBar);
        searchBar.setOnFocusChangeListener(this);

        searchBar.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                        keyCode == EditorInfo.IME_ACTION_DONE ||
                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                    //if (!event.isShiftPressed()) performSearch(searchBar.getText().toString());
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
                if (memberSpinner.getSelectedItem().toString() == "Search users") {
                    String userId = userIds.get(usernames.indexOf(displayData.get(position)));
                    Intent intent = new Intent(getBaseContext(), ViewUser.class);
                    intent.putExtra("userId", userId);
                    startActivity(intent);
                }
                else if (memberSpinner.getSelectedItem().toString() == "Search groups") {
                    String groupId = groupIds.get(groupNames.indexOf(displayData.get(position)));
                    Intent intent = new Intent(getBaseContext(), ViewGroup.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);
                }
                else if (memberSpinner.getSelectedItem().toString() == "Search pages") {
                    String pageId = pageIds.get(pageNames.indexOf(displayData.get(position)));
                    Intent intent = new Intent(getBaseContext(), ViewPage.class);
                    intent.putExtra("pageId", pageId);
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
                    usernames.add((String)user.get("firstName") + " " + (String)user.get("lastName"));
                    String userDatum = (String) user.get("username");
                    userDatum += (String)user.get("firstName") + " " + (String)user.get("lastName");
                    if (user.get("tags") != null) {
                        for (String s : (ArrayList<String>) user.get("tags")) {
                            userDatum += " " + s;
                        }
                    }
                    userData.add(userDatum);
                }

                files = (HashMap<String, Object>)snapshot.child("groups").getValue();
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

                files = (HashMap<String, Object>)snapshot.child("pages").getValue();
                for (Map.Entry<String, Object> entry : files.entrySet()) {
                    pageIds.add(entry.getKey());
                    HashMap<String, Object> user = (HashMap<String, Object>)entry.getValue();
                    pageNames.add((String)user.get("name"));
                    String userDatum = (String)user.get("name") + " " + (String)user.get("description") + " ";
                    switch (((Long)user.get("type")).intValue()) {
                        case 1: userDatum += "organization"; break;
                        case 2: userDatum += "company"; break;
                        case 3: userDatum += "school"; break;
                    }
                    pageData.add(userDatum);
                }

                Bundle extras = getIntent().getExtras();
                if (extras != null) {
                    value = extras.getString("type");
                    if (value != null) {
                        searchBar.setText(value);
                        searchGroups(value);
                        memberSpinner.setSelection(1);
                    }
                    else if (extras.getString("tag") != null) {
                        value = extras.getString("tag");
                        searchBar.setText(value);
                        searchUsers(value);
                        memberSpinner.setSelection(0);
                    }
                    else if (extras.getString("page") != null) {
                        value = extras.getString("page");
                        searchBar.setText(value);
                        searchPages(value);
                        memberSpinner.setSelection(2);
                    }
                }
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });



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
        if (v == search) {
            displayData.clear();
            if (memberSpinner.getSelectedItem().toString() == "Search users") searchUsers(searchBar.getText().toString());
            if (memberSpinner.getSelectedItem().toString() == "Search groups") searchGroups(searchBar.getText().toString());
            if (memberSpinner.getSelectedItem().toString() == "Search pages") searchPages(searchBar.getText().toString());
        }
    }

    public void searchUsers(String s) {
        for (int i = 0; i < userData.size(); i++) {
            if (userData.get(i).toLowerCase().contains(s.toLowerCase())) {
                if (!displayData.contains(usernames.get(i))) displayData.add(usernames.get(i));
            }
        }
        userList.setAdapter(new ArrayAdapter<String>(Search.this, R.layout.simplerow, displayData));
    }

    public void searchGroups(String s) {
        for (int i = 0; i < groupData.size(); i++) {
            if (groupData.get(i).toLowerCase().contains(s.toLowerCase())) {
                if (!displayData.contains(groupNames.get(i))) displayData.add(groupNames.get(i));
            }
        }
        userList.setAdapter(new ArrayAdapter<String>(Search.this, R.layout.simplerow, displayData));
    }

    public void searchPages(String s) {
        for (int i = 0; i < pageData.size(); i++) {
            if (pageData.get(i).toLowerCase().contains(s.toLowerCase())) {
                if (!displayData.contains(pageNames.get(i))) displayData.add(pageNames.get(i));
            }
        }
        userList.setAdapter(new ArrayAdapter<String>(Search.this, R.layout.simplerow, displayData));
    }

    private void setSpinner() {
        ArrayList<String> options = new ArrayList<String>();
        options.add("Search users");
        options.add("Search groups");
        options.add("Search pages");
        ArrayAdapter<String> adapterS = new ArrayAdapter<String>(Search.this, android.R.layout.simple_spinner_item, options);
        adapterS.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        memberSpinner.setAdapter(adapterS);
    }
}
