package clairecw.example.admin.superclassy;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ChooseCategory extends ActionBarActivity implements View.OnClickListener {

    TextView red, orange, green, blue, purple;
    int alpha = 120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        red = (TextView)findViewById(R.id.red);
        red.setOnClickListener(this);
        red.getBackground().setAlpha(alpha);
        orange = (TextView)findViewById(R.id.orange);
        orange.setOnClickListener(this);
        orange.getBackground().setAlpha(alpha);
        green = (TextView)findViewById(R.id.green);
        green.setOnClickListener(this);
        green.getBackground().setAlpha(alpha);
        blue = (TextView)findViewById(R.id.blue);
        blue.setOnClickListener(this);
        blue.getBackground().setAlpha(alpha);
        purple = (TextView)findViewById(R.id.purple);
        purple.setOnClickListener(this);
        purple.getBackground().setAlpha(alpha);
    }

    public void onClick(View v) {
        Intent returnIntent = new Intent();
        switch (v.getId()) {
            case R.id.red:
                returnIntent.putExtra("result", 1);
                break;
            case R.id.orange:
                returnIntent.putExtra("result", 2);
                break;
            case R.id.green:
                returnIntent.putExtra("result", 3);
                break;
            case R.id.blue:
                returnIntent.putExtra("result", 4);
                break;
            case R.id.purple:
                returnIntent.putExtra("result", 5);
                break;
        }
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
