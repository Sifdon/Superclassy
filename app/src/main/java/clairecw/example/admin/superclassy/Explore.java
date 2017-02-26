package clairecw.example.admin.superclassy;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Explore extends ActionBarActivity implements View.OnClickListener {

    ImageButton close, refresh;
    final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
    ArrayList<String> userUrls = new ArrayList<String>();
    ArrayList<String> tags = new ArrayList<String>();
    TextView red, orange, green, blue, purple;
    TextView organization, company, school;
    ArrayList<TextView> people = new ArrayList<TextView>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        organization = (TextView)findViewById(R.id.organization);
        organization.setOnClickListener(this);
        company = (TextView)findViewById(R.id.company);
        company.setOnClickListener(this);
        school = (TextView)findViewById(R.id.school);
        school.setOnClickListener(this);

        close = (ImageButton)findViewById(R.id.close);
        close.setOnClickListener(this);

        refresh = (ImageButton)findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        red = (TextView)findViewById(R.id.red);
        red.setOnClickListener(this);
        orange = (TextView)findViewById(R.id.orange);
        orange.setOnClickListener(this);
        green = (TextView)findViewById(R.id.green);
        green.setOnClickListener(this);
        blue = (TextView)findViewById(R.id.blue);
        blue.setOnClickListener(this);
        purple = (TextView)findViewById(R.id.purple);
        purple.setOnClickListener(this);

        people.add((TextView)findViewById(R.id.people1));
        people.add((TextView)findViewById(R.id.people2));
        people.add((TextView)findViewById(R.id.people3));
        people.add((TextView)findViewById(R.id.people4));
        people.add((TextView)findViewById(R.id.people5));
        for (TextView tv : people) {
            tv.setOnClickListener(this);
        }

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ArrayList<String> temp = new ArrayList<String>();
                HashMap<String, Object> files = (HashMap<String, Object>) snapshot.child("users").getValue();
                ArrayList keys = new ArrayList(files.entrySet());
                Collections.shuffle(keys);
                for (Object entry : keys) {
                    HashMap<String, Object> user = (HashMap<String, Object>) ((Map.Entry<String, Object>)entry).getValue();
                    if (user.get("tags") == null) continue;
                    temp = (ArrayList<String>)user.get("tags");
                    while (!temp.isEmpty() && tags.contains(temp.get(0))) {
                        temp.remove(0);
                    }
                    if (!temp.isEmpty()) {
                        tags.add(temp.get(0));
                        if (user.get("profPic") == null) userUrls.add("http://i.imgur.com/KX0ZVUo.png");
                        else userUrls.add((String)user.get("profPic"));
                    }
                    if (tags.size() == 5) break;
                }
                for (int i = 0; i < userUrls.size(); i++) {
                    new ImageDownloaderTask(people.get(i), Explore.this).execute(userUrls.get(i));
                    people.get(i).setText(tags.get(i));
                }
            }


            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void onClick(View v) {
        if (v == refresh) {
            Intent intent = new Intent(this, Explore.class);
            startActivity(intent);
            finish();
        }
        else if (v == close) {
            finish();
        }
        else {
            Intent returnIntent = new Intent(Explore.this, Search.class);
            switch (v.getId()) {
                case R.id.red:
                    returnIntent.putExtra("type", "career");
                    break;
                case R.id.orange:
                    returnIntent.putExtra("type", "sports");
                    break;
                case R.id.green:
                    returnIntent.putExtra("type", "project");
                    break;
                case R.id.blue:
                    returnIntent.putExtra("type", "miscellaneous interest");
                    break;
                case R.id.purple:
                    returnIntent.putExtra("type", "academic subject");
                    break;
                case R.id.company:
                    returnIntent.putExtra("page", "company");
                    break;
                case R.id.organization:
                    returnIntent.putExtra("page", "organization");
                    break;
                case R.id.school:
                    returnIntent.putExtra("page", "school");
                    break;
            }
            for (int i = 0; i < people.size(); i++) {
                if (v == people.get(i)) {
                    returnIntent.putExtra("tag", tags.get(i));
                    break;
                }
            }
            startActivity(returnIntent);
        }
    }
}
