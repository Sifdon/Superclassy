package clairecw.example.admin.superclassy;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import clairecw.example.admin.helpers.DocumentHelper;
import clairecw.example.admin.helpers.ImageResponse;
import clairecw.example.admin.helpers.IntentHelper;
import clairecw.example.admin.helpers.Upload;
import clairecw.example.admin.helpers.UploadService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewPost extends ActionBarActivity implements View.OnClickListener, View.OnFocusChangeListener {

    EditText title, description;
    ImageView img;
    final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
    TextView author;
    Button post;
    AuthData user;
    ImageButton close;
    String groupId;

    private Upload upload; // Upload object containing image and meta data
    private File chosenFile; //chosen file from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        post = (Button)findViewById(R.id.button2);
        post.setOnClickListener(this);
        author = (TextView)findViewById(R.id.textView11);
        img = (ImageView)findViewById(R.id.imageView3);
        img.setOnClickListener(this);
        title = (EditText)findViewById(R.id.title);
        title.setOnFocusChangeListener(this);
        description = (EditText)findViewById(R.id.editText3);
        description.setOnFocusChangeListener(this);
        close = (ImageButton)findViewById(R.id.close);
        close.setOnClickListener(this);

        Bundle extras = getIntent().getExtras();
        groupId = extras.getString("groupId");

        Firebase.setAndroidContext(this);
        user = myFirebaseRef.getAuth();
        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void onChooseImage() {
        IntentHelper.chooseFileIntent(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri returnUri;

        if (requestCode != IntentHelper.FILE_PICK) {
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        returnUri = data.getData();
        String filePath = DocumentHelper.getPath(this, returnUri);
        //Safety check to prevent null pointer exception
        if (filePath == null || filePath.isEmpty()) return;
        chosenFile = new File(filePath);

        Picasso.with(getBaseContext())
                .load(chosenFile)
                .placeholder(R.drawable.img)
                .resize(250, 250)
                .centerCrop()
                .into(img);

    }

    public void onClick(View v) {
        if (v == close) {
            finish();
        }
        if (v == post) {
            if (chosenFile == null) {
                Firebase newRef = myFirebaseRef.child("groups").child(groupId).child("posts").push();
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("description", description.getText().toString());
                map.put("title", title.getText().toString());
                map.put("author", user.getUid());
                newRef.setValue(map);
                finish();
            }
            else {
                createUpload(chosenFile);
                new UploadService(this, groupId).Execute(upload, new UiCallback(), 1);
                finish();
            }
        }
        if (v == img) {
            onChooseImage();
        }
    }

    private void createUpload(File image) {
        upload = new Upload();

        upload.image = image;
        upload.title = title.getText().toString();
        upload.description = description.getText().toString();
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

    private class UiCallback implements Callback<ImageResponse> {

        @Override
        public void success(ImageResponse imageResponse, Response response) {

        }

        @Override
        public void failure(RetrofitError error) {
            if (error == null) {
                Toast.makeText(NewPost.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
