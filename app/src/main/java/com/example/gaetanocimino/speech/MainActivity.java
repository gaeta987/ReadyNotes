package com.example.gaetanocimino.speech;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.microsoft.cognitiveservices.speech.CancellationDetails;
import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;

import java.io.*;

import com.microsoft.cognitiveservices.speech.translation.SpeechTranslationConfig;
import com.microsoft.cognitiveservices.speech.translation.TranslationRecognitionResult;
import com.microsoft.cognitiveservices.speech.translation.TranslationRecognizer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static android.Manifest.permission.*;

public class MainActivity extends AppCompatActivity {

    private static String speechSubscriptionKey = "";
    private static String serviceRegion = "westus";
    private int count;
    private ArrayAdapter<String> adapter;
    private ArrayAdapter<String> adapter1;
    private String toLanguage = "it";
    private String toLang = "it-IT";
    private String fromLanguag = "it-IT";
    private String[] lingue;
    private String[] lingue1;
    private boolean bool = true;
    private TextView txt;
    private String testo = "";
    private ArrayList<String> testi;
    private ArrayList<String> nomiTesti;
    private ArrayList<String> lingueTesti;
    private TextView textedit;
    private EditText edit;
    private Button salvaedit;
    private Button annullaedit;
    private Button visualizza;
    private Button speech;
    private Button stop;
    private SharedPreferences prefs;
    private ImageView image;
    private Spinner dropdown;
    private Spinner dropdown1;
    private String nomeTesto;
    private TranslationRecognizer recognizer;
    private ArrayList<Integer> giorni;
    private ArrayList<Integer> mesi;
    private ArrayList<Integer> anni;
    private ArrayList<String> nomi;
    private int eventi;
    private boolean evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        evento = false;

        nomi = new ArrayList<String>();
        giorni = new ArrayList<Integer>();
        mesi = new ArrayList<Integer>();
        anni = new ArrayList<Integer>();

        txt = (TextView) this.findViewById(R.id.sts);

        txt.setMovementMethod(new ScrollingMovementMethod());

        image = (ImageView) findViewById(R.id.image);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        visualizza = (Button) this.findViewById(R.id.visualizza);
        speech = (Button) this.findViewById(R.id.button);
        stop = (Button) this.findViewById(R.id.stop);

        textedit = (TextView) this.findViewById(R.id.textedit);
        textedit.setVisibility(View.INVISIBLE);
        edit = (EditText) this.findViewById(R.id.edit);
        edit.setVisibility(View.INVISIBLE);
        salvaedit = (Button) this.findViewById(R.id.salvaedit);
        salvaedit.setVisibility(View.INVISIBLE);
        annullaedit = (Button) this.findViewById(R.id.annullaedit);
        annullaedit.setVisibility(View.INVISIBLE);

        testi = new ArrayList<String>();
        nomiTesti = new ArrayList<String>();
        lingueTesti = new ArrayList<String>();

        count = prefs.getInt("COUNT",0);
        eventi = prefs.getInt("EVENTI",0);

        for(int i = 0;i<eventi;i++) {
            String nomeEvento = prefs.getString("NOMEEVENTO"+i, null);
            nomi.add(nomeEvento);
            int giorno = prefs.getInt("GIORNOEVENTO"+i, 0);
            giorni.add(giorno);
            int mese = prefs.getInt("MESEEVENTO"+i, 0);
            mesi.add(mese);
            int anno = prefs.getInt("ANNOEVENTO"+i, 0);
            anni.add(anno);
        }

        int requestCode = 5;
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, INTERNET}, requestCode);

        dropdown = findViewById(R.id.spinner1);
        dropdown1 = findViewById(R.id.spinner2);
        //crea una lista di elementi per lo spinner.
        String[] items = new String[]{"Italiano","Inglese","Catalano","Danese","Tedesco","Spagnolo","Finlandese",
        "Francese","Olandese","Polacco","Portoghese","Russo","Svedese","Turco"};
        lingue = new String[]{"it","en","ca","da","de","es","fi","fr","nl","pl","pt","ru","sv","tr"};
        lingue1 = new String[]{"it-IT","en-US","ca-CA","da-DA","de-DE","es-ES","fi-FI","fr-FR","nl-NL","pl-PL","pt-PT","ru-RU","sv-SV","tr-TR"};
        //crea un adapter per descrivere come gli elementi sono mostrati
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                toLanguage = lingue[i];
                toLang = lingue1[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dropdown1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fromLanguag = lingue1[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        dropdown.setAdapter(adapter);
        dropdown1.setAdapter(adapter1);
    }

    public void onSpeechButtonClicked(View v) throws IOException {
        int pos = controllo();
        if(pos != -1){
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            evento = true;
                            edit.setText(nomi.get(pos));
                            break;
                        case DialogInterface.BUTTON_NEGATIVE:
                            evento = false;
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Vuoi salvare il testo per l'evento " + nomi.get(pos) + "?")
                    .setPositiveButton("Si", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
        image.setImageDrawable(getResources().getDrawable(R.drawable.blue));
        bool = true;
        testo = "";
        txt.setVisibility(View.VISIBLE);
        new LoadTask().execute(toLanguage);
    }

    //Controlla se esiste qualche evento salvato nel giorno attuale
    private int controllo(){
        GregorianCalendar g = new GregorianCalendar();
        int giorno = g.get(GregorianCalendar.DAY_OF_MONTH);
        int mese = g.get(GregorianCalendar.MONTH)+1;
        int anno = g.get(GregorianCalendar.YEAR);
        for(int i = 0;i<eventi;i++){
            if((giorno == giorni.get(i)) && (mese == mesi.get(i)) && (anno == anni.get(i))){
               return i;
            }
        }

        return -1;
    }

    //Ferma la registrazione ed effettua il controllo ortografico
    public void onStop(View v){
        new Finish().execute();
        image.setImageDrawable(getResources().getDrawable(R.drawable.red));
        new Ortografia().execute();
        txt.setText("");
        txt.setVisibility(View.INVISIBLE);
        Toast.makeText(getApplicationContext(),"Controllo ortografico in corso",Toast.LENGTH_SHORT).show();
    }

    //Ferma la registrazione
    class Finish extends AsyncTask<Integer,Integer,Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            try {
                recognizer.stopContinuousRecognitionAsync().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //Effettua il controllo ortografico sul testo risultante dalla registrazione
    class Ortografia extends AsyncTask<String,String,String>{

        @Override
        protected String doInBackground(String... strings) {
            try {
                return ProvaOrtografia.check(testo);
            } catch (Exception e) {
                Log.d("ORTOGRAFIA",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            testo = s;
            showDialog(null);
            Toast.makeText(getApplicationContext(),"Controllo ortografico completato",Toast.LENGTH_SHORT).show();
        }
    }

    public void showDialog(View v) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        if(evento){
                            salvataggioModificato();
                        } else {
                            salvataggio();
                        }
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Vuoi salvare il testo?")
                .setPositiveButton("Si", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

        return;
    }

    public void salvataggio(){
        edit.setVisibility(View.VISIBLE);
        textedit.setVisibility(View.VISIBLE);
        salvaedit.setVisibility(View.VISIBLE);
        annullaedit.setVisibility(View.VISIBLE);

        txt.setVisibility(View.INVISIBLE);
        visualizza.setEnabled(false);
        speech.setEnabled(false);
        stop.setEnabled(false);
    }

    public void salvataggioModificato(){
        txt.setVisibility(View.INVISIBLE);
        visualizza.setEnabled(false);
        speech.setEnabled(false);
        stop.setEnabled(false);
        evento = false;
        salva(null);
    }

    public void svuota(View v){
        testo = "";
        txt.setText(testo);
    }

    public void annulla(View v){
        edit.setVisibility(View.INVISIBLE);
        textedit.setVisibility(View.INVISIBLE);
        salvaedit.setVisibility(View.INVISIBLE);
        annullaedit.setVisibility(View.INVISIBLE);
        txt.setVisibility(View.VISIBLE);
        visualizza.setEnabled(true);
        speech.setEnabled(true);
        stop.setEnabled(true);
        testo = "";
    }

    public void salva(View v){
        nomeTesto = edit.getText().toString();
        edit.setText("");
        if(nomeTesto.equals("")){
            Toast.makeText(getApplicationContext(), "Inserisci un nome!" , Toast.LENGTH_LONG)
                    .show();

            return;
        }

        nomiTesti.add(nomeTesto);
        testi.add(testo);
        lingueTesti.add(toLang);


        new Database().execute();

        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(Integer.toString(count),testo);
        editor.putString(Integer.toString(count)+"a",nomeTesto);
        editor.putString(Integer.toString(count)+"b",toLang);

        count++;

        editor.putInt("COUNT",count);
        editor.commit();

        edit.setVisibility(View.INVISIBLE);
        textedit.setVisibility(View.INVISIBLE);
        salvaedit.setVisibility(View.INVISIBLE);
        annullaedit.setVisibility(View.INVISIBLE);
        visualizza.setEnabled(true);
        speech.setEnabled(true);
        stop.setEnabled(true);
        testo = "";
        txt.setText("");

        Toast.makeText(getApplicationContext(),"Testo salvato correttamente!",Toast.LENGTH_SHORT).show();
    }

    //Effettua il salvataggio del testo nel database SQL
    class Database extends AsyncTask<Integer,Integer,Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            Connection connection = null;
            try {
                connection = DriverManagerConnectionPool.getConnection();
                PreparedStatement s = connection.prepareStatement("INSERT INTO [dbo].[Testo]([testo],[titolo])" +
                        " VALUES (?,?)");

                s.setString(1, testo);
                s.setString(2, nomeTesto);

                s.executeUpdate();

                connection.commit();
            } catch (Exception e) {
                Log.d("DATABASEE",e.getMessage());
            }

            return null;
        }
    }

    public void gotoTesti(View v){
        Intent in = new Intent();
        in.setClass(getApplicationContext(), Testo.class);
        testi = new ArrayList<String>();
        nomiTesti = new ArrayList<String>();
        lingueTesti = new ArrayList<String>();
        Log.d("PROVAA",count+"");
        for(int i = 0;i<count;i++){
            String testo = prefs.getString(Integer.toString(i),"");
            String titolo = prefs.getString(Integer.toString(i)+"a","");
            String lingua = prefs.getString(Integer.toString(i)+"b","");
            testi.add(testo);
            nomiTesti.add(titolo);
            lingueTesti.add(lingua);
        }
        in.putExtra("Testi",testi);
        in.putExtra("Titoli",nomiTesti);
        in.putExtra("Lingue",lingueTesti);
        startActivity(in);
    }

    //Effettua il riconoscimento e la traduzione del testo
    class LoadTask extends AsyncTask<String,String,String> {

        @Override
        protected void onProgressUpdate(String... values) {
            txt.setText("Traduzione in '" + dropdown.getSelectedItem().toString() + "': " + values[0]);
            Log.d("TESTOO","boh");
        }

        @Override
        protected String doInBackground(String... strings) {
            int exitCode = 1;

            //Andiamo a creare un oggetto per effettuare la traduzione del testo specificando la subscription key e la regione del servizio cognitivo
            SpeechTranslationConfig config = SpeechTranslationConfig.fromSubscription("", "westus");
            assert (config != null);

            Log.d("TESTO","ciao1");

            String fromLanguage = fromLanguag;

            //Andiamo a settare la lingua di input e la lingua di output
            config.setSpeechRecognitionLanguage(fromLanguage);
            config.addTargetLanguage(toLanguage);

            config.setVoiceName("de-DE-Hedda");

            //Crea un translation recognizer che usa il microfono di default del dispositivo per registrare il testo
            recognizer = new TranslationRecognizer(config);
            {
                recognizer.recognized.addEventListener((s, e) -> {
                    if (e.getResult().getReason() == ResultReason.TranslatedSpeech) {
                        Map<String, String> map = e.getResult().getTranslations();
                        Log.d("TESTO",map.get(toLanguage));
                        testo += map.get(toLanguage);
                        if (map.get(toLanguage).equals("")) {
                            return;
                        }
                        publishProgress(testo);
                    }
                    if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                        Log.d("PROVA","Testo riconosciuto: Text=" + e.getResult().getText());
                        Log.d("PROVA"," Errore nella traduzione del testo");
                    } else if (e.getResult().getReason() == ResultReason.NoMatch) {
                        Log.d("PROVA","Testo non riconosciuto");
                    }
                });

                recognizer.canceled.addEventListener((s, e) -> {
                    Log.d("PROVA","Annullato: Ragione=" + e.getReason());
                    if (e.getReason() == CancellationReason.Error) {
                        Log.d("PROVA","ErrorCode=" + e.getErrorCode());
                        Log.d("PROVA","ErrorDetails=" + e.getErrorDetails());
                    }
                });

                recognizer.sessionStarted.addEventListener((s, e) -> {
                    Log.d("PROVA","Session started event.");
                });

                recognizer.sessionStopped.addEventListener((s, e) -> {
                    Log.d("PROVA","Session stopped event.");
                });

                //Inizia la registrazione
                try {
                    recognizer.startContinuousRecognitionAsync().get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }
    }

}
