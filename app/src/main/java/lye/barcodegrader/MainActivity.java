package lye.barcodegrader;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //Mensajes para Intents
    static final String EXTRA_MESSAGE = "lye.barcodegrader.EXTRA_MESSAGE";
    static final String EXTRA_MESSAGE_2 = "lye.barcodegrader.EXTRA_MESSAGE_2"; //TODO Probar si funciona con el mismo mensaje en ambos intents

    //Códigos para onActivityResult
    private static final int PICKFILE_RESULT_CODE = 1;
    static final int MANUAL_MODE_CODE = 2;
    private static final int WRITE_REQUEST_CODE = 43;

    //Widgets
    //private String path = "(sin archivo)";
    private Uri inputUri = null;
    private TextView archivoCargado;
    private TextView nAlumnos;
    private TextView notaMax;

    private Button manualModeButton;
    private Button autoModeButton;

    //ArrayList donde guardar el fichero csv con el que se trabaja
    private ArrayList<String[]> csvArray = new ArrayList<String[]>();

    //Intent y ArrayList para los escaneos
    IntentIntegrator autoModeIntegrator;
    private ArrayList<String> autoModeArray = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Cargando widgets
        archivoCargado = (TextView) findViewById(R.id.archivoCargado);
        nAlumnos = (TextView) findViewById(R.id.nAlum);
        notaMax = (TextView) findViewById(R.id.notaMax);

        manualModeButton = (Button) findViewById(R.id.manualModeButton);
        autoModeButton = (Button) findViewById(R.id.autoModeButton);

        //Inicializando los botones de los dos modos como ocultos (al cargar la aplicación, no hay ningún csv cargado)
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
        switch (requestCode) {
            //Al volver desde el explorador de archivos tras elegir el csv con el que trabajar
            case PICKFILE_RESULT_CODE:
                CSVReader csvFile = null;
                InputStream inputStream = null;
                BufferedReader reader = null;

                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        inputUri = data.getData();
                        //System.out.println("Uri: " + uri.toString());
                        try {
                            inputStream = getContentResolver().openInputStream(inputUri);
                            reader = new BufferedReader(new InputStreamReader(inputStream));
                            csvFile = new CSVReader(reader);

                            String[] nextLine;
                            //Vaciando array de datos previo (solo llegamos aquí si el fichero que abrimos es correcto)
                            csvArray.clear();

                            try {
                                //Recorriendo fichero y cargando la matriz con los datos
                                while ((nextLine = csvFile.readNext()) != null) {
                                    // nextLine[] is an array of values from the line
                                    csvArray.add(nextLine);
                                    //System.out.println(csvArray.get(0)[0]); // Para mostrar una cadena en concreto: (0) para la linea, [0] para el elemento
                                }
                                csvFile.close();
                                reader.close();
                                inputStream.close();

                                //Mostrando los datos del fichero en el TextView si to.do ha ido bien
                                archivoCargado.setText(Uri.decode(inputUri.toString()));                 // Ruta del fichero
                                nAlumnos.setText(String.valueOf(csvArray.size() - 1));  // Número de alumnos (-1 para quitar el encabezado)
                                notaMax.setText(csvArray.get(1)[6]);                    // Nota máxima

                                //Activando la visibilidad de los botones de modo de escaneo (Ya hay un archivo cargado y listo)
                                manualModeButton.setEnabled(true);
                                autoModeButton.setEnabled(true);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        } catch (FileNotFoundException e) {
                            Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_LONG).show();
                        }

                    }
                    //Recogiendo ruta del fichero que cargaremos
                    //path = data.getData().getPath();
                    //Comprobando que el fichero sea un .csv
                    //if(path.substring(path.length()-4, path.length()).equals(".csv")) {
                    //try {
                    //TODO ¿Comprobar estructura del documento?

                    //Abriendo fichero
                    //    csvFile = new CSVReader(new FileReader(path));
                    //} catch (FileNotFoundException e) {
                    //    e.printStackTrace();
                    //}
//                    }
//                    else {
//                        //Avisando si el fichero no es un .csv
//                        Toast.makeText(getApplicationContext(), "El fichero seleccionado no es un fichero .csv", Toast.LENGTH_LONG).show();
//
//                        //Restaurando la variable path por si se ha cargado un segundo fichero erróneo
//                        path = archivoCargado.getText().toString();
//                    }

                }
                break;

            // Al volver desde la activity del modo manual
            case MANUAL_MODE_CODE:
                //Actualizamos nuestro csvArray con el que se devuelve ya modificado
                //System.out.println("ESTAMOS EN MANUAL_MODE_CODE");
                csvArray.clear();
                csvArray = (ArrayList<String[]>) data.getSerializableExtra(EXTRA_MESSAGE_2);

                break;

            //Al volver desde CADA UNA de las llamadas al escáner del modo automático
            case IntentIntegrator.REQUEST_CODE:
                //http://stackoverflow.com/questions/15892461/how-to-trigger-bulk-mode-scan-in-zxing
                //Si se ha leido un código de barras
                if (resultCode == RESULT_OK) {
                    IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    String codigo = intentResult.getContents(); //Pescando el código escaneado en esta vuelta

                    if (!autoModeArray.contains(codigo)) { //Evitando duplicados
                        autoModeArray.add(codigo);
                    }


                    autoModeIntegrator.initiateScan(); //Relanzar escaner

                    //Si se ha pulsado "atrás" desde el escáner
                } else if (resultCode == RESULT_CANCELED) {
                    //System.out.println("BACK");
                    String codigo;
                    int j;
                    int pos;

                    //Analizando la lista de códigos escaneada en autoModeArray
                    for (int i = 1; i < csvArray.size(); i++) { //Desde i = 1 para saltarnos la fila con los encabezados de columna
                        //System.out.println(csvArray.get(i)[2]);

                        // Aqui habria que hacerlo generico para buscar las columnas:
                        //   Email (2), Calificacion (6) y Calificacion máxima (7)
                        codigo = csvArray.get(i)[2].substring(0, csvArray.get(i)[2].length() - 7);
                        if ((pos = autoModeArray.indexOf(codigo)) != -1) {
                            //System.out.println(autoModeArray.get(pos) + "  COINCIDE");
                            //Modificar su línea en csvArray
                            csvArray.get(i)[6] = csvArray.get(1)[7];
                            //Sacarlo de autoModeArray
                            autoModeArray.remove(pos);
                        }

                        //======
                    }
                    //Si queda algún elemento en autoModeArray es que éste no estaba en el csv cargado
                    if (autoModeArray.size() != 0) {
                        //MOSTRAR MENSAJE DE QUE HAY ALUMNOS QUE NO ESTÄN EN EL CSV
                        String alumnosNoCSV = "";

                        for (int i = 0; i < autoModeArray.size(); i++) {
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

            case WRITE_REQUEST_CODE:
                if (resultCode == RESULT_OK && data != null) {
                    Uri outputUri = data.getData();
                    //System.out.println("Uri: " + uri.toString());
                    try {
                        OutputStream outputStream = getContentResolver().openOutputStream(outputUri);
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                        int nfil = csvArray.size();
                        int ncol = csvArray.get(0).length;

                        try {
                            //Recorriendo csvArray y escribiendo elemento a elemento
                            for (int i = 0; i < nfil; i++) {
                                for (int j = 0; j < ncol; j++) {
                                    String valueStr = csvArray.get(i)[j];
                                    // Avoid a problem when CSVreader includes BOM in first field
                                    if (valueStr. contains(" ") ||  valueStr.contains(",")) {
                                        writer.write("\"" + valueStr + "\"");
                                    }
                                    else {
                                        writer.write(valueStr);
                                    }
                                    if (j != ncol - 1)
                                        writer.write(",");
                                }
                                writer.write("\n");
                            }

                            writer.close();
                            outputStream.close();
                            Toast.makeText(getApplicationContext(), "Fichero csv creado en " + outputUri.toString(), Toast.LENGTH_LONG).show();

                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Problem writing file", Toast.LENGTH_LONG).show();
                        }

                    } catch (FileNotFoundException e) {
                        Toast.makeText(getApplicationContext(), "File Not Found", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Si no se ha indicado el archivo bien
                    Toast.makeText(getApplicationContext(), "No se ha seleccionado ningún archivo", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    //On Click para el botón de cargar csv
    public void loadCsv(View v) {
        // Note (eliminar cuando la leais): Esto daba problemas porque
        // Use ACTION_GET_CONTENT if you want your app to simply read/import data. With this approach, the app imports a _*_copy of the data_*_, such as an image file.
        // Use ACTION_OPEN_DOCUMENT if you want your app to have long term, persistent access to documents owned by a document provider. An example would be a photo-editing app that lets users edit images stored in a document provider.
        // puede que si se tiene cuidado al leer de cierta forma funcione bien pues lo que hacemos
        // es leerlo y olvidarnos pero no se si eso interfiere con los paths o algo.
        Intent fileintent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        fileintent.addCategory(Intent.CATEGORY_OPENABLE);
        fileintent.setType("text/*"); //Error con text/plain en Android 6 y con Android 5 tb intermitente?

        try {
            startActivityForResult(fileintent, PICKFILE_RESULT_CODE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            //Log.e("tag", "No activity can handle picking a file. Showing alternatives.");
        }

    }

    //OnClick para el botón de guardar el csv
    public void saveCsv(View v) {
        //Date date = new Date();
        //SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
        //Reescribiendo nombre de archivo
        //String outPath = path.substring(0, path.length()-4) +"-graded_" + format.format(date) + ".csv";
        //TODO ¿Conseguir que si abres un archivo ya corregido, no ponga otra coletilla -graded al final de la anterior?

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);

        // Filter to only show results that can be "opened", such as
        // a file (as opposed to a list of contacts or timezones).
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Create a file with the requested MIME type.
        intent.setType("text/csv");

        // This does not work at my phone as it gets a content/Document: prefix inside the path
        //List<String> pathSegments = inputUri.getPathSegments();
        //String filename = inputUri.getLastPathSegment(); //pathSegments.get(pathSegments.size() - 1) + "-graded.csv";

        // Suggest filename
        File file = new File(inputUri.getPath());
        String filename = file.getName();
        filename = filename.substring(0, filename.length()-4) + "-graded.csv";
        intent.putExtra(Intent.EXTRA_TITLE, filename);

        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    //OnClick para el botón de modo manual
    public void startManualMode(View v)
    {
        //Comrpobamos si se ha cargado algún archivo
        if(inputUri != null) {
            //http://stackoverflow.com/questions/10407159/how-to-manage-startactivityforresult-on-android
            Intent int1 = new Intent(this, ManualModeActivity.class);

            int1.putExtra(EXTRA_MESSAGE_2, csvArray);

            startActivityForResult(int1, MANUAL_MODE_CODE);

        } else {
            Toast.makeText(getApplicationContext(), "No se ha cargado ningún archivo", Toast.LENGTH_LONG).show();
        }

    }

    //OnClick para el botón de modo automático
    public void startAutoMode(View v) {
        //Comrpobamos si se ha cargado algún archivo
        if (inputUri != null) {
            autoModeIntegrator.initiateScan();
        } else {
            Toast.makeText(getApplicationContext(), "No se ha cargado ningún archivo", Toast.LENGTH_LONG).show();
        }
    }

    //Mensaje de confirmación al cerrar la aplicación
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
