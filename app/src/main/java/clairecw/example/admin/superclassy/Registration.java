package clairecw.example.admin.superclassy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import clairecw.example.admin.superclassy.R;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.HashMap;
import java.util.Map;


public class Registration extends ActionBarActivity implements View.OnClickListener, View.OnFocusChangeListener {

    Button reg;
    EditText username, password, email, firstName, lastName;
    CheckBox checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        Firebase.setAndroidContext(this);

        reg = (Button)(findViewById(R.id.button));
        username = (EditText)(findViewById(R.id.username));
        password = (EditText)(findViewById(R.id.password));
        email = (EditText)(findViewById(R.id.email));
        username.setOnFocusChangeListener(this);
        password.setOnFocusChangeListener(this);
        email.setOnFocusChangeListener(this);
        checkbox = (CheckBox)(findViewById(R.id.checkBox));
        firstName = (EditText)findViewById(R.id.firstName);
        lastName = (EditText)findViewById(R.id.lastName);

        TextView text = (TextView)findViewById(R.id.textView3);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");

        text.setTypeface(custom_font);

        reg.setOnClickListener(this);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClick(View v) {
        createUser();
        finish();
    }

    public void createUser() {
        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
        myFirebaseRef.createUser(email.getText().toString(), password.getText().toString(), new Firebase.ValueResultHandler<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> result) {
                myFirebaseRef.authWithPassword(email.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("username", username.getText().toString());
                        map.put("email", email.getText().toString());
                        map.put("provider", password.getText().toString());
                        map.put("recruiter", checkbox.isChecked());
                        map.put("firstName", firstName.getText().toString());
                        map.put("lastName", lastName.getText().toString());

                        myFirebaseRef.child("users").child(authData.getUid()).setValue(map);
                        Intent intent = new Intent(Registration.this, AccountEdit.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        Toast.makeText(Registration.this, "Error logging in. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onError(FirebaseError firebaseError) {
                Toast.makeText(Registration.this, "Error creating user. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (!username.hasFocus() && !password.hasFocus() && !email.hasFocus()) hideKeyboard(v);
        }
    }


}
