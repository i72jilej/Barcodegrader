package lye.barcodegrader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class ManualModeActivity extends AppCompatActivity {

    private ArrayList<String[]> csvArray2 = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_mode);

        //Recibiendo intent
        csvArray2 = (ArrayList<String[]>) getIntent().getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        //TODO TEST (BORRAR LUEGO)
        TextView testTV;
        testTV = (TextView) findViewById(R.id.testTextBox);
        testTV.setText(csvArray2.get(0)[0]);
        System.out.println(MainActivity.EXTRA_MESSAGE);
        System.out.println(MainActivity.EXTRA_MESSAGE_2);
        csvArray2.get(0)[0] = "MODIFICADO";
        //END TEST
    }

    //TODO TEST (BORRAR LUEGO)
    public void testReturn(View v) {
        Intent returnIntent = new Intent();

        returnIntent.putExtra(MainActivity.EXTRA_MESSAGE_2, csvArray2);
        setResult(MainActivity.MANUAL_MODE_CODE, returnIntent);
        finish();
    }
    //END TEST
}
