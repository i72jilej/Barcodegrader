package lye.barcodegrader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class ManualModeActivity extends AppCompatActivity {

    private ArrayList<String[]> csvArray2 = new ArrayList<String[]>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_mode);

        //Recibiendo intent
        csvArray2 = (ArrayList<String[]>) getIntent().getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        /*
        //TODO TEST (BORRAR LUEGO)
        TextView testTV;
        testTV = (TextView) findViewById(R.id.testTextBox);
        testTV.setText(csvArray2.get(0)[0]);
        System.out.println(MainActivity.EXTRA_MESSAGE);
        System.out.println(MainActivity.EXTRA_MESSAGE_2);
        csvArray2.get(0)[0] = "MODIFICADO";
        //END TEST
        */
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Confirmar cierre")
            .setMessage("¿Seguro que quieres volver al menú principal?")
            .setPositiveButton("Sí", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent returnIntent = new Intent();
                    // csvArray2.get(0)[0] = "MODIFICADO"; //TEST
                    //System.out.println("BACK");
                    returnIntent.putExtra(MainActivity.EXTRA_MESSAGE_2, csvArray2);
                    setResult(MainActivity.MANUAL_MODE_CODE, returnIntent);
                    finish();
                }
            }).setNegativeButton("No", null)
            .show();
    }

    public void manualScan (View v){
        System.out.println("ESCANEAR");
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);

        scanIntegrator.initiateScan(); //El resultado lo pilla onActivityResult
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //Aquí recibimos los datos
            String codigoAlumno = scanningResult.getContents();
            System.out.println(codigoAlumno);
        }
        else {
            Toast.makeText(getApplicationContext(), "Escaneo cancelado", Toast.LENGTH_LONG).show();
        }
    }

    /*
    //TODO TEST (BORRAR LUEGO)
    public void testReturn(View v) {
        Intent returnIntent = new Intent();

        returnIntent.putExtra(MainActivity.EXTRA_MESSAGE_2, csvArray2);
        setResult(MainActivity.MANUAL_MODE_CODE, returnIntent);
        finish();
    }
    //END TEST
    */
}
