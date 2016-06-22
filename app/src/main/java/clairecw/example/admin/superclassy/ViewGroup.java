package clairecw.example.admin.superclassy;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class ViewGroup extends ActionBarActivity implements View.OnClickListener {

    AuthData user;
    TextView groupName, description, category, founder;
    final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
    String value;
    ArrayList<String> members;
    Button join, post;
    ImageButton close, refresh;
    ListView postList;
    boolean isMember;
    ArrayList<String> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        Bundle extras = getIntent().getExtras();
        value = extras.getString("groupId");

        close = (ImageButton)findViewById(R.id.close);
        close.setOnClickListener(this);

        Firebase.setAndroidContext(this);
        user = myFirebaseRef.getAuth();
        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        groupName = (TextView)findViewById(R.id.groupName);
        description = (TextView)findViewById(R.id.description);
        category = (TextView)findViewById(R.id.type);
        founder = (TextView)findViewById(R.id.founder);

        join = (Button) findViewById(R.id.join);
        join.setOnClickListener(this);

        post = (Button) findViewById(R.id.post);
        post.setOnClickListener(this);

        postList = (ListView)findViewById(R.id.listView);

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Object uName = snapshot.child("groups").child(value).child("name").getValue();
                if (uName != null) ViewGroup.this.groupName.setText(uName.toString());
                Object desc = snapshot.child("groups").child(value).child("description").getValue();
                if (desc != null) description.setText(desc.toString());
                String type = "";
                switch (((Long)snapshot.child("groups").child(value).child("type").getValue()).intValue()) {
                    case 1: type = "Project"; break;
                    case 2: type = "Sports"; break;
                    case 3: type = "Career"; break;
                    case 4: type = "Miscellaneous Interest"; break;
                    case 5: type = "Academic Subject"; break;
                }
                category.setText(type);
                members = (ArrayList<String>)snapshot.child("groups").child(value).child("members").getValue();

                groups = (ArrayList<String>)snapshot.child("users").child(user.getUid()).child("groups").getValue();
                if (groups == null) groups = new ArrayList<String>();

                if (members.contains(user.getUid())) {
                    isMember = true;
                    join.setEnabled(false);
                    join.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    join.setText("Joined");
                    postList.setVisibility(View.VISIBLE);
                    //join.setBackgroundColor();
                }

                String founderId = (String)snapshot.child("groups").child(value).child("founder").getValue();
                String founderName = (String)snapshot.child("users").child(founderId).child("firstName").getValue() + " " +
                        (String)snapshot.child("users").child(founderId).child("lastName").getValue();
                if (founderName != null) founder.setText("Founder: " + founderName);

                ArrayList<String> titles = new ArrayList<String>();
                ArrayList<String> descriptions = new ArrayList<String>();
                ArrayList<String> urls = new ArrayList<String>();
                ArrayList<String> authors = new ArrayList<String>();

                HashMap<String, Object> posts = (HashMap<String, Object>)snapshot.child("groups").child(value).child("posts").getValue();
                if (posts != null) {
                    for (Map.Entry<String, Object> entry : posts.entrySet()) {
                        HashMap<String, String> file = (HashMap<String, String>) entry.getValue();
                        titles.add(file.get("title"));
                        descriptions.add(file.get("description"));
                        urls.add(file.get("url"));
                        String firstName = (String)snapshot.child("users").child(file.get("author")).child("firstName").getValue();
                        String lastName = (String)snapshot.child("users").child(file.get("author")).child("lastName").getValue();
                        authors.add(firstName + " " + lastName);
                    }
                }

                //System.out.println

                postList.setAdapter(new PostAdapter(ViewGroup.this, titles, urls, descriptions, authors));
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

        }
        if (v == join) {
            members.add(user.getUid());
            myFirebaseRef.child("groups").child(value).child("members").setValue(members);

            groups.add(value);
            Firebase userRef = myFirebaseRef.child("users").child(user.getUid()).child("groups");
            userRef.setValue(groups);

            isMember = true;
            join.setEnabled(false);
            join.setBackgroundColor(Color.parseColor("#CCCCCC"));
            join.setText("Joined");

            postList.setVisibility(View.VISIBLE);
        }
        if (v == post) {
            Intent intent = new Intent(ViewGroup.this, NewPost.class);
            intent.putExtra("groupId", value);
            startActivity(intent);
        }
    }
}