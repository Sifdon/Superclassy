package clairecw.example.admin.superclassy;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class EditWork extends ActionBarActivity implements View.OnClickListener, View.OnFocusChangeListener {

    Button save;
    ImageButton close;
    EditText descBox;
    ImageView image;
    HashMap<String, Object> file;
    final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
    final AuthData user = myFirebaseRef.getAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_work);

        Bundle extras = getIntent().getExtras();
        final String value = extras.getString("fileId");

        save = (Button)findViewById(R.id.button);
        save.setOnClickListener(this);

        close = (ImageButton)findViewById(R.id.close);
        close.setOnClickListener(this);

        descBox = (EditText)findViewById(R.id.editText);
        descBox.setOnFocusChangeListener(this);

        image = (ImageView)findViewById(R.id.imageView);

        Firebase.setAndroidContext(this);
        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }


        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                file = (HashMap<String, Object>)snapshot.child("users").child(user.getUid())
                                .child("files").child(value).getValue();
                String url = (String)file.get("url");
                descBox.setText((String)file.get("desc"));
                Picasso.with(EditWork.this)
                        .load(url)
                        .resize(image.getWidth(), image.getHeight())
                        .centerCrop()
                        .into(image);
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    public void onClick(View v) {
        if (v == save) {
            file.put("desc", descBox.getText().toString());
            myFirebaseRef.updateChildren(file);
            Intent intent = new Intent(EditWork.this, AccountEdit.class);
            startActivity(intent);
            finish();
        }
        if (v == close) {
            finish();
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideKeyboard(v);
        }
    }
}
