package clairecw.example.admin.superclassy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by admin on 4/19/16.
 */
public class EditTags extends ActionBarActivity implements View.OnClickListener {


    AuthData user;
    ImageButton closeButton;
    ListView list;

    ArrayList<String> tags;
    String[] allTags = {"actor", "athlete", "architect", "artist", "carpenter", "chef", "chemist",
            "childcare", "coach", "dancer", "designer", "developer", "doctor", "electrician",
            "engineer", "farmer", "lawyer", "librarian", "mathemetician", "mechanic",
            "musician", "nurse", "nutritionist", "photographer", "pilot", "psychologist",
            "real estate", "reporter", "researcher", "social worker", "statistician",
            "student", "teacher", "veterinarian", "volunteer", "writer", "zookeeper"};
    Integer[] imageId = new Integer[allTags.length];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tags);

        closeButton = (ImageButton)(findViewById(R.id.close));
        closeButton.setOnClickListener(this);

        for (int i = 0; i < allTags.length; i++) {
            imageId[i] = android.R.color.transparent;
        }

        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
        user = myFirebaseRef.getAuth();
        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                tags = (ArrayList<String>) snapshot.child("users").child(user.getUid()).child("tags").getValue();
                if (tags == null) tags = new ArrayList<String>();
                else if (!tags.isEmpty()) {
                    for (String s : tags) {
                        for (int i = 0; i < allTags.length; i++) {
                            if (s.equals(allTags[i])) {
                                imageId[i] = R.drawable.check;
                            }
                        }
                    }
                }

                CustomList adapter = new
                        CustomList(EditTags.this, allTags, imageId);
                list = (ListView) findViewById(R.id.list);
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        //Toast.makeText(MainActivity.this, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();
                        if (tags != null && tags.contains(allTags[position])) {
                            tags.remove(allTags[position]);
                            ImageView imageView = (ImageView) view.findViewById(R.id.img);
                            imageView.setImageResource(android.R.color.transparent);
                        } else {
                            tags.add(allTags[position]);
                            ImageView imageView = (ImageView) view.findViewById(R.id.img);
                            imageView.setImageResource(R.drawable.check);
                        }
                    }
                });

            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });




    }

    public void onClick(View v) {
        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
        user = myFirebaseRef.getAuth();

        myFirebaseRef.child("users").child(user.getUid()).child("tags").setValue(tags);

        Intent myIntent = new Intent(EditTags.this, AccountEdit.class);
        startActivity(myIntent);
        finish();
    }
}