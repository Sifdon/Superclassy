package clairecw.example.admin.superclassy;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ChooseCategory extends ActionBarActivity implements View.OnClickListener {

    TextView red, orange, green, blue, purple;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_category);

        red = (TextView)findViewById(R.id.red);
        red.setOnClickListener(this);
        orange = (TextView)findViewById(R.id.orange);
        orange.setOnClickListener(this);
        green = (TextView)findViewById(R.id.green);
        green.setOnClickListener(this);
        blue = (TextView)findViewById(R.id.blue);
        blue.setOnClickListener(this);
        purple = (TextView)findViewById(R.id.purple);
        purple.setOnClickListener(this);
    }

    public void onClick(View v) {
        Intent returnIntent = new Intent();
        switch (v.getId()) {
            case R.id.red:
                //noinspection ResourceType
                returnIntent.putExtra("result", getResources().getString(R.color.red));
                break;
            case R.id.orange:
                //noinspection ResourceType
                returnIntent.putExtra("result", getResources().getString(R.color.lightOrange));
                break;
            case R.id.green:
                //noinspection ResourceType
                returnIntent.putExtra("result", getResources().getString(R.color.green));
                break;
            case R.id.blue:
                //noinspection ResourceType
                returnIntent.putExtra("result", getResources().getString(R.color.skyBlue));
                break;
            case R.id.purple:
                //noinspection ResourceType
                returnIntent.putExtra("result", getResources().getString(R.color.purple));
                break;
        }
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
