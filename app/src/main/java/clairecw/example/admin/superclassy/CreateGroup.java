package clairecw.example.admin.superclassy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CreateGroup extends ActionBarActivity implements View.OnClickListener, View.OnFocusChangeListener {

    ImageButton close;
    View color;
    Button create;
    EditText description, name;
    AuthData user;
    TextView founder;
    int type;
    final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);

        close = (ImageButton)(findViewById(R.id.close));
        close.setOnClickListener(this);

        color = (View)findViewById(R.id.color);
        color.setOnClickListener(this);

        founder = (TextView)findViewById(R.id.textView4);

        create = (Button)findViewById(R.id.create);
        create.setOnClickListener(this);

        name = (EditText)findViewById(R.id.nameText);
        name.setOnFocusChangeListener(this);
        description = (EditText)findViewById(R.id.editText2);
        description.setOnFocusChangeListener(this);

        Firebase.setAndroidContext(this);
        user = myFirebaseRef.getAuth();
        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }

        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (user == null) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                }

                Object uName = snapshot.child("users").child(user.getUid()).child("username").getValue();
                if (uName != null) CreateGroup.this.founder.setText("Founder: " + uName.toString());
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    public void onClick(View v) {
        if (v == close) {
            finish();
        }
        if (v == color) {
            Intent i = new Intent(this, ChooseCategory.class);
            startActivityForResult(i, 1);
        }
        if (v == create) {
            Firebase newRef = myFirebaseRef.child("groups").push();

            ArrayList<String> members = new ArrayList<String>();
            members.add(user.getUid());

            Map<String, Object> map = new HashMap<String, Object>();
            map.put("name", name.getText().toString());
            map.put("description", description.getText().toString());
            map.put("founder", user.getUid());
            map.put("members", members);
            map.put("type", type);

            newRef.setValue(map);

            ArrayList<String> groups = new ArrayList<String>();
            groups.add(newRef.getKey());
            Firebase userRef = myFirebaseRef.child("users").child(user.getUid()).child("groups");
            userRef.setValue(groups);

            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                String result = data.getStringExtra("result");
                switch (result) {
                    case "#ff99cc00": type = 1; break;
                    case "#ffffbb33": type = 2; break;
                    case "#ffe2485a": type = 3; break;
                    case "#ff33b5e5": type = 4; break;
                    case "#ffaa66cc": type = 5; break;

                }
                color.setBackgroundColor(Color.parseColor(result));
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (name.hasFocus() == false && description.hasFocus() == false) hideKeyboard(v);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
