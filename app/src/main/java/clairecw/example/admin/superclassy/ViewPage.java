package clairecw.example.admin.superclassy;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewPage extends AppCompatActivity implements View.OnClickListener {

    AuthData user;
    TextView groupName, description, category;
    final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
    String value;
    ArrayList<String> members;
    Button join;
    ImageButton close, refresh;
    ListView postList;
    boolean isMember;
    ArrayList<String> groups, nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_page);

        Bundle extras = getIntent().getExtras();
        value = extras.getString("pageId");

        close = (ImageButton) findViewById(R.id.close);
        close.setOnClickListener(this);

        refresh = (ImageButton) findViewById(R.id.refresh);
        refresh.setOnClickListener(this);

        nameList = new ArrayList<String>();

        Firebase.setAndroidContext(this);
        user = myFirebaseRef.getAuth();
        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        groupName = (TextView) findViewById(R.id.groupName);
        description = (TextView) findViewById(R.id.description);
        category = (TextView) findViewById(R.id.type);

        join = (Button) findViewById(R.id.join);
        join.setOnClickListener(this);

        postList = (ListView) findViewById(R.id.listView);

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object uName = snapshot.child("pages").child(value).child("name").getValue();
                if (uName != null) ViewPage.this.groupName.setText(uName.toString());
                Object desc = snapshot.child("pages").child(value).child("description").getValue();
                if (desc != null) description.setText(desc.toString());
                String type = "";
                switch (((Long) snapshot.child("pages").child(value).child("type").getValue()).intValue()) {
                    case 1:
                        type = "Organization";
                        break;
                    case 2:
                        type = "Company";
                        break;
                    case 3:
                        type = "School";
                        break;
                    default:
                        type = "Other";
                        break;
                }
                category.setText(type);


                nameList.add((String) snapshot.child("pages").child(value).child("name").getValue());

                ArrayList<String> titles = new ArrayList<String>();
                ArrayList<String> descriptions = new ArrayList<String>();
                ArrayList<String> urls = new ArrayList<String>();

                HashMap<String, Object> posts = (HashMap<String, Object>) snapshot.child("pages").child(value).child("posts").getValue();
                if (posts != null) {
                    for (Map.Entry<String, Object> entry : posts.entrySet()) {
                        HashMap<String, String> file = (HashMap<String, String>) entry.getValue();
                        titles.add(file.get("title"));
                        descriptions.add(file.get("description"));
                        urls.add(file.get("url"));
                    }
                }

                postList.setAdapter(new PostAdapter(ViewPage.this, titles, urls, descriptions, nameList));


            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void onClick(View v) {
        if (v == close) {
            finish();
        }
        if (v == refresh) {
            Intent intent = new Intent(this, ViewPage.class);
            intent.putExtra("pageId", value);
            startActivity(intent);
            finish();
        }
        if (v == join) {
            groups.add(value);
            Firebase userRef = myFirebaseRef.child("users").child(user.getUid()).child("pages");
            userRef.setValue(groups);

            isMember = true;
            join.setEnabled(false);
            join.setBackgroundColor(Color.parseColor("#CCCCCC"));
            join.setText("Following");

            postList.setVisibility(View.VISIBLE);
        }
    }
}