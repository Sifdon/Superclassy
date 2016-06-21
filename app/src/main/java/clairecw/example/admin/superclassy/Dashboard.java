package clairecw.example.admin.superclassy;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

public class Dashboard extends ActionBarActivity implements View.OnClickListener {

    Button uploadButton, profileButton, searchButton, groupsButton;
    Button explore;
    AuthData user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        uploadButton = (Button)findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(this);
        uploadButton.setBackgroundColor(Color.TRANSPARENT);

        profileButton = (Button)findViewById(R.id.profileButton);
        profileButton.setOnClickListener(this);
        profileButton.setBackgroundColor(Color.TRANSPARENT);

        searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        searchButton.setBackgroundColor(Color.TRANSPARENT);

        groupsButton = (Button)findViewById(R.id.groupsButton);
        groupsButton.setOnClickListener(this);
        groupsButton.setBackgroundColor(Color.TRANSPARENT);

        explore = (Button)findViewById(R.id.explore);
        explore.setOnClickListener(this);

        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
        user = myFirebaseRef.getAuth();

        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void onClick(View v) {

        if (v == uploadButton) {
            Intent myIntent = new Intent(this, UploadFile.class);
            startActivity(myIntent);
        }
        if (v == profileButton) {
            Intent intent = new Intent(this, AccountEdit.class);
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

        if (v == explore) {
            Intent intent = new Intent(this, Explore.class);
            startActivity(intent);
        }
    }

}
