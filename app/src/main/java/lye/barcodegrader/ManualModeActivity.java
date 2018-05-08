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

    private TextView nombreAlumno;
    private TextView codigoAlumno;
    private TextView notaActual;

    //TextView fechaEdicion;

    private int filaAlumno = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_mode);

        nombreAlumno = (TextView) findViewById(R.id.nombreAlumno);
        codigoAlumno = (TextView) findViewById(R.id.codigoAlumno);
        notaActual = (TextView) findViewById(R.id.notaAlumno);
        TextView notaMax = (TextView) findViewById(R.id.notaMaxima);
        //fechaEdicion = (TextView) findViewById(R.id.fechaEdicion);

        //Recibiendo intent
        csvArray2 = (ArrayList<String[]>) getIntent().getSerializableExtra(MainActivity.EXTRA_MESSAGE_2);

        // XXX Aqui habria que hacerlo generico para buscar las columnas:
        //   Email (2), Calificacion (6) y Calificacion máxima (7)
        notaMax.setText(csvArray2.get(1)[7]);

        ArrayList<Button> listaBotones = new ArrayList<Button>();
        listaBotones.add((Button) findViewById(R.id.nota0));
        listaBotones.add((Button) findViewById(R.id.nota1));
        listaBotones.add((Button) findViewById(R.id.nota2));
        listaBotones.add((Button) findViewById(R.id.nota3));
        listaBotones.add((Button) findViewById(R.id.nota4));
        listaBotones.add((Button) findViewById(R.id.nota5));
        listaBotones.add((Button) findViewById(R.id.nota6));
        listaBotones.add((Button) findViewById(R.id.nota7));
        listaBotones.add((Button) findViewById(R.id.nota8));
        listaBotones.add((Button) findViewById(R.id.nota9));
        listaBotones.add((Button) findViewById(R.id.nota10));

        //Asignado la visibilidad y listener
        for(int i = 0; i < 11; i++){
            listaBotones.get(i).setOnClickListener(mGlobal_OnClickListener);

            if(Integer.parseInt(listaBotones.get(i).getText().toString()) <=
                    Double.parseDouble(notaMax.getText().toString().replaceAll(",","."))){ //FIXME falla al no identificar 10,00 como posible número. ¿En el listener sí funciona?
                listaBotones.get(i).setEnabled(true);
            }
            else
            {
                listaBotones.get(i).setEnabled(false);
            }

        }

        findViewById(R.id.notaBorrar).setOnClickListener(mGlobal_OnClickListener);


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

                if (filaAlumno == 0) {
                    Toast.makeText(getApplicationContext(), "Alumno no encontrado en el fichero cargado", Toast.LENGTH_LONG).show();
                }
                else {
                    // Aqui habria que hacerlo generico para buscar las columnas:
                    //   Email (2), Calificacion (6) y Calificacion máxima (7)
                    nombreAlumno.setText(csvArray2.get(filaAlumno)[1]);
                    codigoAlumno.setText(csvArray2.get(filaAlumno)[2].replace("@uco.es",""));
                    if (csvArray2.get(filaAlumno)[6].equals("")) {
                        notaActual.setText("-");
                    }
                    else {
                        notaActual.setText(csvArray2.get(filaAlumno)[6]);
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
            String notaPantalla;
            String notaCSV;

            if(filaAlumno != 0){
                // Aqui habria que hacerlo generico para buscar las columnas:
                //   Email (2), Calificacion (6) y Calificacion máxima (7)
                if (boton.getText().equals("Borrar")) {
                    notaPantalla = "-";
                    notaCSV = "";
                    notaActual.setText(notaPantalla);
                    csvArray2.get(filaAlumno)[6] = notaCSV;
                } else {
                    notaPantalla = boton.getText().toString();
                    notaCSV = notaPantalla;
                    notaActual.setText(notaPantalla);
                    csvArray2.get(filaAlumno)[6] = notaCSV;
                }

            } else {
                Toast.makeText(getApplicationContext(), "No se ha cargado ningún alumno", Toast.LENGTH_LONG).show();
            }

        }
    };
}
