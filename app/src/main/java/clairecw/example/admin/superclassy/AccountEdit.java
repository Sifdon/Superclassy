package clairecw.example.admin.superclassy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.squareup.picasso.Picasso;

public class AccountEdit extends ActionBarActivity implements View.OnClickListener, View.OnFocusChangeListener {

    Button uploadButton, homeButton, searchButton, groupsButton;
    Button saveButton, logout;
    EditText descBox;
    TextView username, tagLabel;
    AuthData user;
    ImageButton addButton, refreshButton;
    ImageView profile;
    GridView portfolio;
    ArrayList<String> fileIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_edit);

        uploadButton = (Button)findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(this);
        uploadButton.setBackgroundColor(Color.TRANSPARENT);

        homeButton = (Button)findViewById(R.id.homeButton);
        homeButton.setOnClickListener(this);
        homeButton.setBackgroundColor(Color.TRANSPARENT);

        searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        searchButton.setBackgroundColor(Color.TRANSPARENT);

        groupsButton = (Button)findViewById(R.id.groupsButton);
        groupsButton.setOnClickListener(this);
        groupsButton.setBackgroundColor(Color.TRANSPARENT);

        profile = (ImageView)findViewById(R.id.profilePic);
        profile.setOnClickListener(this);

        logout = (Button)findViewById(R.id.logout);
        logout.setOnClickListener(this);

        Firebase.setAndroidContext(this);
        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
        user = myFirebaseRef.getAuth();
        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }

        username = (TextView)findViewById(R.id.username);
        descBox = (EditText)findViewById(R.id.desc);
        descBox.setImeOptions(EditorInfo.IME_ACTION_DONE);
        descBox.setOnFocusChangeListener(this);

        addButton = (ImageButton)findViewById(R.id.addButton);
        addButton.setOnClickListener(this);

        refreshButton = (ImageButton)findViewById(R.id.refreshButton);
        refreshButton.setOnClickListener(this);

        saveButton = (Button)findViewById(R.id.save);
        saveButton.setOnClickListener(this);

        tagLabel = (TextView)findViewById(R.id.tagLabel);

        portfolio = (GridView)findViewById(R.id.gridView);

        fileIds = new ArrayList<String>();

        //ArrayList<ListItem> listData = getListData();

        final GridView listView = (GridView) findViewById(R.id.gridView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                String fileId = fileIds.get(position);
                Intent intent = new Intent(getBaseContext(), EditWork.class);
                intent.putExtra("fileId", fileId);
                startActivity(intent);
                //Toast.makeText(AccountEdit.this, "Selected :" + " " + newsData, Toast.LENGTH_LONG).show();
            }
        });

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (user == null) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }

                Object uName = snapshot.child("users").child(user.getUid()).child("username").getValue();
                if (uName != null) AccountEdit.this.username.setText("@" + uName.toString());
                Object desc = snapshot.child("users").child(user.getUid()).child("description").getValue();
                if (desc != null) descBox.setText(desc.toString());
                ArrayList<String> tags = (ArrayList<String>) snapshot.child("users").child(user.getUid()).child("tags").getValue();
                String tagString = "";
                if (tags != null) {
                    for (int i = 0; i < tags.size(); i++) {
                        tagString += tags.get(i);
                        if (i != tags.size() - 1) tagString += ", ";
                    }
                    tagLabel.setText(tagString);
                }

                Object ppic = snapshot.child("users").child(user.getUid()).child("profPic").getValue();
                if (ppic != null) {
                    new ImageDownloaderTask(profile, AccountEdit.this).execute((String)ppic);
                }


                ArrayList<ListItem> listData = new ArrayList<ListItem>();
                HashMap<String, Object> files = (HashMap<String, Object>) snapshot.child("users").child(user.getUid()).child("files").getValue();
                int count = 0;
                if (files != null) {
                    for (Map.Entry<String, Object> entry : files.entrySet()) {
                        fileIds.add(entry.getKey());
                        count++;
                    }
                    String[] images = new String[count];
                    int n = 0;
                    for (Map.Entry<String, Object> entry : files.entrySet()) {
                        HashMap<String, String> file = (HashMap<String, String>) entry.getValue();
                        fileIds.add(entry.getKey());
                        images[n] = file.get("url");
                        System.out.println(file.get("url"));
                        System.out.println(images[n]);
                        ListItem newsData = new ListItem();
                        newsData.setUrl(images[n]);
                        listData.add(newsData);
                    }
                }

                listView.setAdapter(new ImageAdapter(AccountEdit.this, listData));

            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });


    }

    public void onClick(View v) {
        if (v == uploadButton) {
            Intent myIntent = new Intent(this, UploadFile.class);
            startActivity(myIntent);
        }
        if (v == homeButton) {
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
            finish();
        }
        if (v == searchButton) {
            Intent myIntent = new Intent(this, Search.class);
            startActivity(myIntent);
        }
        if (v == groupsButton) {
            Intent intent = new Intent(this, Groups.class);
            startActivity(intent);
            finish();
        }

        if (v == addButton) {
            Intent myIntent = new Intent(this, EditTags.class);
            startActivity(myIntent);
        }
        if (v == saveButton) {
            final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
            user = myFirebaseRef.getAuth();

            myFirebaseRef.child("users").child(user.getUid()).child("description").setValue(descBox.getText().toString());

            InputMethodManager inputManager = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (v == profile) {
            Intent myIntent = new Intent(this, UploadProfilePic.class);
            startActivity(myIntent);
        }
        if (v == refreshButton) {
            Intent intent = new Intent(this, AccountEdit.class);
            startActivity(intent);
            finish();
        }
        if (v == logout) {
            // confirmation
            final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
            myFirebaseRef.unauth();
            Intent myIntent = new Intent(this, MainActivity.class);
            startActivity(myIntent);
            finish();
        }
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


 }
