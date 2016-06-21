package clairecw.example.admin.superclassy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class MainActivity extends ActionBarActivity implements View.OnClickListener, View.OnFocusChangeListener {

    Button reg, login;
    EditText username, password;
    TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);

        reg = (Button)findViewById(R.id.reg);
        reg.setOnClickListener(this);

        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(this);

        username = (EditText)findViewById(R.id.username);
        username.setTextColor(Color.WHITE);
        username.setOnFocusChangeListener(this);
        password = (EditText)findViewById(R.id.password);
        password.setTextColor(Color.WHITE);
        password.setOnFocusChangeListener(this);

        name = (TextView)findViewById(R.id.name);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");

        name.setTypeface(custom_font);

        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
        AuthData user = myFirebaseRef.getAuth();
        if (user != null) {
            Intent intent = new Intent(MainActivity.this, AccountEdit.class);
            startActivity(intent);
            finish();
        }

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View v) {
        if (v == reg) {
            Intent myIntent = new Intent(this, Registration.class);
            startActivity(myIntent);
            finish();
        }
        if (v == login) {
            login();
        }
    }

    public void login() {

        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");

        myFirebaseRef.authWithPassword(username.getText().toString(), password.getText().toString(), new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Intent intent = new Intent(MainActivity.this, AccountEdit.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                Toast.makeText(MainActivity.this, "Error logging in. Please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (username.hasFocus() == false && password.hasFocus() == false) hideKeyboard(v);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
