package lye.barcodegrader;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.opencsv.CSVReader;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static final String EXTRA_MESSAGE =  "lye.barcodegrader.EXTRA_MESSAGE";
    static final String EXTRA_MESSAGE_2 =  "lye.barcodegrader.EXTRA_MESSAGE_2"; //TODO Probar si funciona con el mismo mensaje en ambos intents

    private static final int PICKFILE_RESULT_CODE = 1;
    static final int MANUAL_MODE_CODE = 2;

    private String path = "(sin archivo)";
    private TextView archivoCargado;
    private TextView nAlumnos;
    private TextView notaMax;

    private Button manualModeButton;
    private Button autoModeButton;

    //private CSVReader csvFile; //Declarado dentro de OnActivityResult (¿No va a hacer falta fuera, se trabaja con la array?)
    private ArrayList<String[]> csvArray = new ArrayList<String[]>();

    IntentIntegrator autoModeIntegrator;
    private ArrayList<String> autoModeArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        archivoCargado = (TextView) findViewById(R.id.archivoCargado);
        nAlumnos = (TextView) findViewById(R.id.nAlum);
        notaMax = (TextView) findViewById(R.id.notaMax);

        manualModeButton = (Button) findViewById(R.id.manualModeButton);
        autoModeButton = (Button) findViewById(R.id.autoModeButton);

        manualModeButton.setEnabled(false);
        autoModeButton.setEnabled(false);

        autoModeIntegrator = new IntentIntegrator(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /*
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Fix no activity available
        CSVReader csvFile = null;

        //COmentado para que el swtich sea evaluado al hacer cancelar
        //if (data == null)
            //return;
        switch (requestCode) {
            case PICKFILE_RESULT_CODE:
                if (resultCode == RESULT_OK) {
                    //Recogiendo ruta del fichero que cargaremos
                    path = data.getData().getPath();

                    //Comprobando que el fichero sea un .csv
                    //TODO ¿Comprobar realmente el fichero y no solo la extensión?
                    if(path.substring(path.length()-4, path.length()).equals(".csv")) {
                        try {
                            //TODO Comprobar estructura del documento

                            //Abriendo fichero
                            csvFile = new CSVReader(new FileReader(path));
                            String[] nextLine;

                            //Vaciando array de datos (solo llegamos aquí si el fichero que abrimos es correcto)
                            csvArray.clear();

                            try {
                                //Recorriendo fichero y cargando la matriz con los datos
                                while ((nextLine = csvFile.readNext()) != null) {
                                    // nextLine[] is an array of values from the line
                                    csvArray.add(nextLine);
                                    //System.out.println(csvArray.get(0)[0]); //Para mostrar una cadena en concreto: (0) para la linea, [0] para el elemento

                                }
                                //System.out.println(csvArray.get(1)[0] + " - " + csvArray.get(1)[1] + " - " + csvArray.get(1)[2] + " - " + csvArray.get(1)[3] + " - " + csvArray.get(1)[4] + " - " + csvArray.get(1)[5] + " - " + csvArray.get(1)[6] + " - " + csvArray.get(1)[7] + " - ");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        //Mostrando los datos del fichero en el TextView si to.do ha ido bien
                        archivoCargado.setText(path);                           //Ruta del fichero
                        nAlumnos.setText(String.valueOf(csvArray.size() - 1));  //Número de alumnos (-1 para quitar el encabezado)
                        notaMax.setText(csvArray.get(1)[5]);                    //Nota máxima

                        manualModeButton.setEnabled(true);
                        autoModeButton.setEnabled(true);
                    }
                    else {
                        //Avisando si el fichero no es un .csv
                        Toast.makeText(getApplicationContext(), "El fichero seleccionado no es un fichero .csv", Toast.LENGTH_LONG).show();

                        //Restaurando la variable path por si se ha cargado un segundo fichero erróneo
                        path = archivoCargado.getText().toString();
                    }

                }
                break;

            case MANUAL_MODE_CODE:

                //Actualizamos nuestro csvArray con el que se devuelve ya modificado
                //System.out.println("ESTAMOS EN MANUAL_MODE_CODE");
                csvArray.clear();//¿sobra esto?
                csvArray = (ArrayList<String[]>) data.getSerializableExtra(EXTRA_MESSAGE_2);

                break;

            case IntentIntegrator.REQUEST_CODE:
                //http://stackoverflow.com/questions/15892461/how-to-trigger-bulk-mode-scan-in-zxing
                if (resultCode == RESULT_OK) {
                    IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    String codigo = intentResult.getContents(); //Pescando el código escaneado en esta vuelta

                    if(!autoModeArray.contains(codigo)) { //Evitando duplicados
                        autoModeArray.add(codigo);
                    }


                    autoModeIntegrator.initiateScan(); //Relanzar escaner

                } else if (resultCode == RESULT_CANCELED) {
                    //System.out.println("BACK");
                    String codigo;
                    int j;
                    int pos;

                    for (int i = 1; i < csvArray.size(); i++) { //Desde i = 1 para saltarnos la fila con los encabezados de columna
                        //System.out.println(csvArray.get(i)[2]);

                        //==MODO PRIMERO
                        /*
                        j = 0;
                        while(j < autoModeArray.size()){
                            //System.out.println("-->" + autoModeArray.get(j));

                            if(csvArray.get(i)[2].equals(autoModeArray.get(j) + "@uco.es")) {
                                //System.out.println("COINCIDE");
                                csvArray.get(i)[4] = csvArray.get(1)[5];
                                break;
                            }
                            j++;
                        }
                        */
                        //======

                        //==MODO SEGUNDO
                        codigo = csvArray.get(i)[2].substring(0,csvArray.get(i)[2].length() - 7);
                        if((pos=autoModeArray.indexOf(codigo)) != -1){
                            //System.out.println(autoModeArray.get(pos) + "  COINCIDE");
                            //Modificar su línea en csvArray
                            csvArray.get(i)[4] = csvArray.get(1)[5];
                            //Sacarlo de autoModeArray
                            autoModeArray.remove(pos);
                        }

                        //======
                    }
                    if(autoModeArray.size() != 0)
                    {
                        //MOSTRAR MENSAJE DE QUE HAY ALUMNOS QUE NO ESTÄN EN EL CSV
                        String alumnosNoCSV = "";

                        for(int i = 0; i < autoModeArray.size(); i++){
                            alumnosNoCSV = alumnosNoCSV + autoModeArray.get(i) + " ";
                        }

                        new AlertDialog.Builder(this)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Hay alumnos no registrados")
                                .setMessage("Los siguientes alumnos escaneados no se encuentran en el csv: " + alumnosNoCSV)
                                .setNegativeButton("Ok", null)
                                .show();
                    }

                    autoModeArray.clear();

                }

                break;


        }
        //System.out.println(RESULT_OK + " - " + RESULT_CANCELED);
        //System.out.println(requestCode + " - " + IntentIntegrator.REQUEST_CODE);
    }

    public void loadCsv(View v) {

        Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
        fileintent.setType("text/plain");
        try {
            startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            //Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
        }

    }

    public void saveCsv(View v){
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm");

        //TODO ¿Conseguir que si abres un archivo ya corregido, no ponga otra coletilla -graded al final de la anterior?
        if (!path.equals("(sin archivo)")) {
            //Si hay un archivo cargado
            String outPath = path.substring(0, path.length()-4) +"-graded_" + format.format(date) + ".csv";
            Writer outputFile = null;

            try {
                outputFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outPath), "UTF-8"));
                int nfil = csvArray.size(); //NullPointerException aquí tras haber hecho Modo Manual
                int ncol = csvArray.get(0).length;

                for (int i = 0; i < nfil; i++){
                    for(int j = 0; j < ncol; j++){
                        outputFile.write("\"" + csvArray.get(i)[j] + "\"");
                        if (j != ncol - 1)
                            outputFile.write(",");
                    }
                    outputFile.write("\r\n");
                }

                outputFile.close();

                Toast.makeText(getApplicationContext(), "Fichero csv creado en " + outPath, Toast.LENGTH_LONG).show();

            }catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            //Si no se ha cargado ningún archivo aún
            Toast.makeText(getApplicationContext(), "No se ha cargado ningún archivo", Toast.LENGTH_LONG).show();
        }
        //System.out.println(outPath);

    }

    public void startManualMode(View v)
    {
        //Comrpobamos si se ha cargado algún archivo
        if(!path.equals("(sin archivo)")) {
            //http://stackoverflow.com/questions/10407159/how-to-manage-startactivityforresult-on-android
            Intent int1 = new Intent(this, ManualModeActivity.class);

            int1.putExtra(EXTRA_MESSAGE_2, csvArray);

            startActivityForResult(int1, MANUAL_MODE_CODE);

        } else {
            Toast.makeText(getApplicationContext(), "No se ha cargado ningún archivo", Toast.LENGTH_LONG).show();
        }

    }

    public void startAutoMode(View v) {
        //Comrpobamos si se ha cargado algún archivo
        if(!path.equals("(sin archivo)")) {
            autoModeIntegrator.initiateScan();
        } else {
            Toast.makeText(getApplicationContext(), "No se ha cargado ningún archivo", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed(){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirmar cierre")
                .setMessage("¿Seguro que quieres salir de la aplicación? Los cambios no guardados se perderán.")
                .setPositiveButton("Sí", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("No", null)
                .show();
    }


}
