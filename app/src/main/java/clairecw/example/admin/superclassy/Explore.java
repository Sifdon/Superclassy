package clairecw.example.admin.superclassy;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class Explore extends ActionBarActivity implements View.OnClickListener {

    ImageButton close;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore);

        close = (ImageButton)findViewById(R.id.close);
        close.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v == close) {
            finish();
        }
    }
}
