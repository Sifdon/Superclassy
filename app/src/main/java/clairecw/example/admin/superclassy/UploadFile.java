package clairecw.example.admin.superclassy;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import java.io.File;

import clairecw.example.admin.helpers.DocumentHelper;
import clairecw.example.admin.helpers.ImageResponse;
import clairecw.example.admin.helpers.IntentHelper;
import clairecw.example.admin.helpers.Upload;
import clairecw.example.admin.helpers.UploadService;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UploadFile extends ActionBarActivity implements View.OnClickListener, View.OnFocusChangeListener {

    ImageButton uploadButton, close;
    Button save;
    EditText descBox;
    Button homeButton, searchButton, groupsButton, profileButton;

    private Upload upload; // Upload object containing image and meta data
    private File chosenFile; //chosen file from intent

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_file);

        homeButton = (Button)findViewById(R.id.homeButton);
        homeButton.setOnClickListener(this);
        homeButton.setBackgroundColor(Color.TRANSPARENT);

        searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(this);
        searchButton.setBackgroundColor(Color.TRANSPARENT);

        groupsButton = (Button)findViewById(R.id.groupsButton);
        groupsButton.setOnClickListener(this);
        groupsButton.setBackgroundColor(Color.TRANSPARENT);

        profileButton = (Button)findViewById(R.id.profileButton);
        profileButton.setOnClickListener(this);
        profileButton.setBackgroundColor(Color.TRANSPARENT);

        uploadButton = (ImageButton)findViewById(R.id.imageButton);
        uploadButton.setOnClickListener(this);

        save = (Button)findViewById(R.id.saveButton);
        save.setOnClickListener(this);

        close = (ImageButton)(findViewById(R.id.close));
        close.setOnClickListener(this);

        descBox = (EditText)findViewById(R.id.editText);
        descBox.setOnFocusChangeListener(this);

        Firebase.setAndroidContext(this);
        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
        AuthData user = myFirebaseRef.getAuth();
        if (user == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void onClick(View v) {
        if (v == close) {
            finish();
        }
        if (v == uploadButton) {
            onChooseImage();
        }
        if (v == save) {
            if (chosenFile == null) return;
            createUpload(chosenFile);
            new UploadService(this).Execute(upload, new UiCallback(), 0);
            Intent myIntent = new Intent(this, AccountEdit.class);
            startActivity(myIntent);
            finish();
        }
        if (v == homeButton) {
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
            finish();
        }
        if (v == searchButton) {
            Intent myIntent = new Intent(this, Search.class);
            startActivity(myIntent);
            finish();
        }
        if (v == groupsButton) {
            Intent intent = new Intent(this, Groups.class);
            startActivity(intent);
            finish();
        }
        if (v == profileButton) {
            Intent intent = new Intent(this, AccountEdit.class);
            startActivity(intent);
            finish();
        }
    }

    private void createUpload(File image) {
        upload = new Upload();

        upload.image = image;
        upload.title = "";
        upload.description = descBox.getText().toString();
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
                .resize(200, 170)
                .centerCrop()
                .into(uploadButton);

    }

    public void onChooseImage() {
        IntentHelper.chooseFileIntent(this);
    }

    private class UiCallback implements Callback<ImageResponse> {

        @Override
        public void success(ImageResponse imageResponse, Response response) {

        }

        @Override
        public void failure(RetrofitError error) {
            if (error == null) {
                Toast.makeText(UploadFile.this, "No internet connection", Toast.LENGTH_SHORT).show();
            }
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
