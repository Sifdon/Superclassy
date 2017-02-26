package clairecw.example.admin.superclassy;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

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

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
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
                                myIntent = new Intent(getBaseContext(), Search.class);
                                startActivity(myIntent);
                                finish();
                                break;
                            case R.id.action_upload:
                                myIntent = new Intent(getBaseContext(), UploadFile.class);
                                startActivity(myIntent);
                                finish();
                                break;
                            case R.id.action_groups:
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
        View view = bottomNavigationView.findViewById(R.id.action_groups);
        view.performClick();

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
            finish();
        }

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (user == null) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                    Toast.makeText(getBaseContext(), "Error: Please sign in again.", Toast.LENGTH_LONG).show();
                }
                groupIds = (ArrayList<String>)snapshot.child("users").child(user.getUid()).child("groups").getValue();
                if (groupIds != null) {
                    for (String group : groupIds) {
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
