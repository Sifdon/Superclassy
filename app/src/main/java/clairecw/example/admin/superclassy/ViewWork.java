package clairecw.example.admin.superclassy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class ViewWork extends ActionBarActivity {

    TextView descBox;
    ImageView image;
    HashMap<String, Object> file;
    final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
    final AuthData user = myFirebaseRef.getAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_work);

        Bundle extras = getIntent().getExtras();
        final String[] value = extras.getStringArray("fileId");

        descBox = (TextView)findViewById(R.id.editText);

        image = (ImageView)findViewById(R.id.imageView);

        Firebase.setAndroidContext(this);


        myFirebaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                file = (HashMap<String, Object>)snapshot.child("users").child(value[1])
                        .child("files").child(value[0]).getValue();
                String url = (String)file.get("url");
                descBox.setText((String)file.get("desc"));
                Picasso.with(ViewWork.this).load(url).into(image);
            }

            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }
}
