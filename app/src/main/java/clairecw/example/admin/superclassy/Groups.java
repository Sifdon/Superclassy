package clairecw.example.admin.superclassy;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Groups extends ActionBarActivity implements View.OnClickListener {

    Button uploadButton, homeButton, searchButton, profileButton;
    Button createButton;
    ImageButton refresh;
    ListView list;

    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> colors = new ArrayList<String>();
    ArrayList<String> groupIds = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);

        uploadButton = (Button)findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(this);
        uploadButton.setBackgroundColor(Color.TRANSPARENT);

        homeButton = (Button)findViewById(R.id.homeButton);
        homeButton.setOnClickListener(this);
        homeButton.setBackgroundColor(Color.TRANSPARENT);

        searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        searchButton.setBackgroundColor(Color.TRANSPARENT);

        profileButton = (Button)findViewById(R.id.profileButton);
        profileButton.setOnClickListener(this);
        profileButton.setBackgroundColor(Color.TRANSPARENT);

        createButton = (Button)findViewById(R.id.createButton);
        createButton.setOnClickListener(this);

        refresh = (ImageButton)findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        list = (ListView)findViewById(R.id.listView);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                    String groupId = groupIds.get(position);
                    Intent intent = new Intent(getBaseContext(), ViewGroup.class);
                    intent.putExtra("groupId", groupId);
                    startActivity(intent);

            }
        });

        Firebase.setAndroidContext(this);
        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
        final AuthData user = myFirebaseRef.getAuth();
        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                groupIds = (ArrayList<String>)snapshot.child("users").child(user.getUid()).child("groups").getValue();
                if (groupIds != null) {
                    for (String group : groupIds) {
                        System.out.println(group);
                        nameList.add((String)snapshot.child("groups").child(group).child("name").getValue());
                        switch (((Long)snapshot.child("groups").child(group).child("type").getValue()).intValue()) {
                            case 1: colors.add("#99CC00"); break;
                            case 2: colors.add("#FFBB33"); break;
                            case 3: colors.add("#E2485A"); break;
                            case 4: colors.add("#33B5E5"); break;
                            case 5: colors.add("#AA66CC"); break;
                            default: colors.add("#FFFFFF"); break;
                        }
                    }
                }


                list.setAdapter(new GroupListAdapter(Groups.this, nameList, colors));

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
        if (v == profileButton) {
            Intent intent = new Intent(this, AccountEdit.class);
            startActivity(intent);
            finish();
        }
        if (v == createButton) {
            Intent intent = new Intent(this, CreateGroup.class);
            startActivity(intent);
        }
        if (v == refresh) {
            Intent intent = new Intent(this, Groups.class);
            startActivity(intent);
            finish();
        }
    }
}
