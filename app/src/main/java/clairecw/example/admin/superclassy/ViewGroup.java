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
    ArrayList<String> fileIds = new ArrayList<String>();
    ArrayList<String> members;
    Button join;
    ImageButton close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_group);

        Bundle extras = getIntent().getExtras();
        final String value = extras.getString("groupId");

        close = (ImageButton)findViewById(R.id.close);
        close.setOnClickListener(this);

        Firebase.setAndroidContext(this);
        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
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

                if (members.contains(user.getUid())) {
                    join.setEnabled(false);
                    join.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    join.setText("Joined");
                    //join.setBackgroundColor();
                }

                String founderId = (String)snapshot.child("groups").child(value).child("founder").getValue();
                String founderName = (String)snapshot.child("users").child(founderId).child("firstName").getValue() + " " +
                        (String)snapshot.child("users").child(founderId).child("lastName").getValue();
                if (founderName != null) founder.setText("Founder: " + founderName);
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void onClick(View v) {
        if (v == close) {
            finish();
        }
        if (v == join) {
            
        }
    }
}