package lye.barcodegrader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ManualModeActivity extends AppCompatActivity {

    private ArrayList<String[]> csvArray = new ArrayList<String[]>();

    private TextView testTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_mode);

        //Recibiendo intent
        csvArray = (ArrayList<String[]>) getIntent().getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        //TODO TEST (BORRAR LUEGO)
        testTV = (TextView) findViewById(R.id.testTextBox);
        testTV.setText(csvArray.get(0)[0]);
        csvArray.get(0)[0] = "MODIFICADO";
        //END TEST
    }

    //TODO TEST (BORRAR LUEGO)
    public void testReturn(View v) {
        Intent returnIntent = new Intent();

        returnIntent.putExtra(MainActivity.EXTRA_MESSAGE_2, csvArray);
        setResult(MainActivity.MANUAL_MODE_CODE, returnIntent);
        finish();
    }
    //END TEST
}
