package clairecw.example.admin.superclassy;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Dashboard extends ActionBarActivity implements View.OnClickListener {

    Button explore;
    TextView welcome, instr;
    AuthData user;
    RelativeLayout postView;

    ListView postList;
    ArrayList<String> nameList = new ArrayList<String>();
    ArrayList<String> pageIds = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Intent myIntent;
                        switch (item.getItemId()) {
                            case R.id.action_home:
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

        explore = (Button)findViewById(R.id.explore);
        explore.setOnClickListener(this);

        welcome = (TextView)findViewById(R.id.welcome);
        instr = (TextView)findViewById(R.id.instr);

        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
        user = myFirebaseRef.getAuth();

        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        postList = (ListView)findViewById(R.id.listView);
        postView = (RelativeLayout)findViewById(R.id.postView);

        postList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                String pageId = pageIds.get(position);
                Intent intent = new Intent(getBaseContext(), ViewPage.class);
                intent.putExtra("pageId", pageId);
                startActivity(intent);

            }
        });

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<String> pages = (ArrayList<String>)snapshot.child("users").child(user.getUid()).child("pages").getValue();

                if (pages != null) {
                    instr.setVisibility(View.GONE);
                    welcome.setVisibility(View.GONE);
                    postView.setVisibility(View.VISIBLE);

                    for (String page : pages) {
                        nameList.add((String)snapshot.child("pages").child(page).child("name").getValue());

                        ArrayList<String> titles = new ArrayList<String>();
                        ArrayList<String> descriptions = new ArrayList<String>();
                        ArrayList<String> urls = new ArrayList<String>();

                        HashMap<String, Object> posts = (HashMap<String, Object>)snapshot.child("pages").child(page).child("posts").getValue();
                        if (posts != null) {
                            for (Map.Entry<String, Object> entry : posts.entrySet()) {
                                pageIds.add(page);
                                HashMap<String, String> file = (HashMap<String, String>) entry.getValue();
                                titles.add(file.get("title"));
                                descriptions.add(file.get("description"));
                                urls.add(file.get("url"));
                            }
                        }

                        postList.setAdapter(new PostAdapter(Dashboard.this, titles, urls, descriptions, nameList));
                    }
                }
            }


            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void onClick(View v) {

        if (v == explore) {
            Intent intent = new Intent(this, Explore.class);
            startActivity(intent);
        }
    }

}
