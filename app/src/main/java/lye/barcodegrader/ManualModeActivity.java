package lye.barcodegrader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;


public class ManualModeActivity extends AppCompatActivity {

    private ArrayList<String[]> csvArray2 = new ArrayList<String[]>();

    TextView nombreAlumno;
    TextView codigoAlumno;
    TextView notaActual;
    TextView notaMax;
    //TextView fechaEdicion;

    int filaAlumno;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_mode);

        nombreAlumno = (TextView) findViewById(R.id.nombreAlumno);
        codigoAlumno = (TextView) findViewById(R.id.codigoAlumno);
        notaActual = (TextView) findViewById(R.id.notaAlumno);
        notaMax = (TextView) findViewById(R.id.notaMaxima);
        //fechaEdicion = (TextView) findViewById(R.id.fechaEdicion);

        //Recibiendo intent
        csvArray2 = (ArrayList<String[]>) getIntent().getSerializableExtra(MainActivity.EXTRA_MESSAGE_2);

        notaMax.setText(csvArray2.get(1)[5]);

        findViewById(R.id.nota0).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.nota1).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.nota2).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.nota3).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.nota4).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.nota5).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.nota6).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.nota7).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.nota8).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.nota9).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.nota10).setOnClickListener(mGlobal_OnClickListener);
        findViewById(R.id.notaBorrar).setOnClickListener(mGlobal_OnClickListener);


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
        //System.out.println("ESCANEAR");
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);

        scanIntegrator.initiateScan(); //El resultado lo pilla onActivityResult
        //System.out.println("PILLADO");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        try {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

            if (scanningResult != null) {
                //Aquí recibimos los datos
                String scanContent = scanningResult.getContents();
                //System.out.println(scanContent);
                filaAlumno = buscarAlumno(scanContent);
                //TODO Comprobar si se ha devuelto que el alumno no existe
                //TODO Recoger datos del alumno
                if (filaAlumno == 0) {
                    Toast.makeText(getApplicationContext(), "Alumno no encontrado en el fichero cargado", Toast.LENGTH_LONG).show();
                }
                else {
                    nombreAlumno.setText(csvArray2.get(filaAlumno)[1]);
                    codigoAlumno.setText(csvArray2.get(filaAlumno)[2].replace("@uco.es",""));
                    if (csvArray2.get(filaAlumno)[4].equals("")) {
                        notaActual.setText("-");
                    }
                    else {
                        notaActual.setText(csvArray2.get(filaAlumno)[4]);
                    }
                }

                //System.out.println(filaAlumno);


            } else {
                Toast.makeText(getApplicationContext(), "Fallo en la lectura del código", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public int buscarAlumno(String codigo){
        //Devuelve la fila en csvArray en la que se encuentra el alumno. 0 si no está
        int fila = 0;

        String correo = codigo + "@uco.es";

        while (fila < csvArray2.size() && !csvArray2.get(fila)[2].equals(correo)) {
            //System.out.println(fila + " - " + csvArray2.get(fila)[2] + " =? " + correo + "\n\r");
            fila++;
        }

        if (fila == csvArray2.size()){
            fila = 0;
        }

        return fila;
    }

    //Global On click listener for all views
    final View.OnClickListener mGlobal_OnClickListener = new View.OnClickListener() {
        public void onClick(final View v) {
            Button boton = (Button) findViewById(v.getId());
            //Toast.makeText(getApplicationContext(), boton.getText(), Toast.LENGTH_SHORT).show();
            String notaPantalla;
            String notaCSV;

            //TODO Comprobar si hay un alumno cargado
            if (boton.getText() == "Borrar") {
                //FIXME Al elegir borrar, aparece "Borrar" en pantalla
                notaPantalla = "-";
                notaCSV = "";
            } else {
                notaPantalla = boton.getText().toString();
                notaCSV = notaPantalla;
            }

            notaActual.setText(notaPantalla);
            csvArray2.get(filaAlumno)[4] = notaCSV;

            /*switch(v.getId()) {


                case R.id.nota0:
                    //Inform the user the button1 has been clicked
                    Toast.makeText(getApplicationContext(), "Nota0 click", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nota1:
                    //Inform the user the button2 has been clicked
                    Toast.makeText(getApplicationContext(), "Nota1 click.", Toast.LENGTH_SHORT).show();
                    break;
            }*/
            System.out.println("COMUN");
        }
    };





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
