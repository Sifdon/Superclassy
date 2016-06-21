package clairecw.example.admin.superclassy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

public class Contact extends ActionBarActivity implements View.OnClickListener {

    EditText email, subject, message;
    Button send;
    ImageView background;
    TextView text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        Bundle extras = getIntent().getExtras();
        final String value = extras.getString("email");

        if (value == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
        }

        email = (EditText)findViewById(R.id.emailBox);
        subject = (EditText)findViewById(R.id.subjectBox);
        message = (EditText)findViewById(R.id.messageBox);

        email.setText(value);

        send = (Button)findViewById(R.id.send);
        send.setOnClickListener(this);

        text = (TextView)findViewById(R.id.textView3);
        Typeface custom_font = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");

        text.setTypeface(custom_font);

        background = (ImageView)findViewById(R.id.backgroundImage);
        //background.setImageBitmap(b);

        Picasso.with(this)
                .load(R.drawable.socialize)
                .into(background);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClick(View v) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setType("text/plain");
        String[] emails = {email.getText().toString()};
        emailIntent.putExtra(Intent.EXTRA_EMAIL, emails);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject.getText().toString());
        emailIntent.putExtra(Intent.EXTRA_TEXT, message.getText().toString());

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            finish();
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(Contact.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

}
