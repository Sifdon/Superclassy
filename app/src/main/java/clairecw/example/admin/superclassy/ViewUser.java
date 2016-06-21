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

public class ViewUser extends ActionBarActivity implements View.OnClickListener {

    AuthData user;
    TextView username, descBox, tagLabel;
    ArrayList<String> fileIds = new ArrayList<String>();
    ImageView profile;
    Button contact;
    ImageButton close;
    String email;

    GridView portfolio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        Bundle extras = getIntent().getExtras();
        final String value = extras.getString("userId");

        Firebase.setAndroidContext(this);
        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
        user = myFirebaseRef.getAuth();
        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        profile = (ImageView)findViewById(R.id.profilePic);

        close = (ImageButton)findViewById(R.id.close);
        close.setOnClickListener(this);

        username = (TextView)findViewById(R.id.username);
        descBox = (TextView)findViewById(R.id.desc);

        tagLabel = (TextView)findViewById(R.id.tagLabel);

        contact = (Button)findViewById(R.id.contact);
        contact.setOnClickListener(this);

        portfolio = (GridView)findViewById(R.id.gridView);

        final GridView listView = (GridView) findViewById(R.id.gridView);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                String fileId = fileIds.get(position);
                Intent intent = new Intent(getBaseContext(), ViewWork.class);
                String[] toPass = {fileId, value};
                intent.putExtra("fileId", toPass);
                startActivity(intent);
            }
        });

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String firstName = (String)snapshot.child("users").child(value).child("firstName").getValue();
                String lastName = (String)snapshot.child("users").child(value).child("lastName").getValue();
                ViewUser.this.username.setText(firstName + " " + lastName);
                Object desc = snapshot.child("users").child(value).child("description").getValue();
                if (desc != null) descBox.setText(desc.toString());
                ArrayList<String> tags = (ArrayList<String>)snapshot.child("users").child(value).child("tags").getValue();
                if (tags != null) {
                    String tagString = "";
                    for (int i = 0; i < tags.size(); i++) {
                        tagString += tags.get(i);
                        if (i != tags.size() - 1) tagString += ", ";
                    }
                    tagLabel.setText(tagString);
                }

                email = (String)snapshot.child("users").child(value).child("email").getValue();

                Object ppic = snapshot.child("users").child(value).child("profPic").getValue();
                if (ppic != null) {
                    new ImageDownloaderTask(profile, ViewUser.this).execute((String)ppic);
                }

                boolean isRecruiter = (boolean)snapshot.child("users").child(user.getUid()).child("recruiter").getValue();
                if (!isRecruiter) {
                    contact.setEnabled(false);
                }

                ArrayList<ListItem> listData = new ArrayList<ListItem>();
                HashMap<String, Object> files = (HashMap<String, Object>)snapshot.child("users").child(value).child("files").getValue();
                if (files != null) {
                    int count = 0;
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
                        ListItem newsData = new ListItem();
                        newsData.setUrl(images[n]);
                        listData.add(newsData);
                    }
                }

                listView.setAdapter(new ImageAdapter(ViewUser.this, listData));

            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void onClick(View v) {
        if (v == close) {
            finish();
        }
        if (v == contact) {
            Intent myIntent = new Intent(this, Contact.class);
            myIntent.putExtra("email", email);
            startActivity(myIntent);
        }
    }
}
