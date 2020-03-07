package com.example.gaetanocimino.speech;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.microsoft.cognitiveservices.speech.CancellationReason;
import com.microsoft.cognitiveservices.speech.ResultReason;
import com.microsoft.cognitiveservices.speech.SpeechConfig;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisCancellationDetails;
import com.microsoft.cognitiveservices.speech.SpeechSynthesisResult;
import com.microsoft.cognitiveservices.speech.SpeechSynthesizer;

import java.util.concurrent.Future;

public class Single extends Activity {

    private TextView txt;
    private TextView tit;
    private String lingua;
    private String testo;
    private SpeechSynthesisResult result;
    private SpeechSynthesizer synth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single);

        txt = (TextView) findViewById(R.id.testo);
        tit = (TextView) findViewById(R.id.titolo);

        Intent i = getIntent();
        String titolo = i.getStringExtra("titolo");
        testo = i.getStringExtra("testo");
        lingua = i.getStringExtra("lingua");

        tit.setText(titolo);
        txt.setText(testo);
    }


    public void ascolta(View v){
        new Riproduci().execute();
    }

    //Effettua la riproduzione vocale del testo
    class Riproduci extends AsyncTask<Integer,Integer,Integer>{

        @Override
        protected Integer doInBackground(Integer... integers) {
            try {
                int exitCode = 1;
                //Andiamo a creare un oggetto per effettuare la sintesi vocale del testo specificando la subscription key e la regione del servizio cognitivo
                SpeechConfig config = SpeechConfig.fromSubscription("f3d361f7b64443e1b531afd602e6b7f9", "westus");
                assert(config != null);

                //Setta la lingua da utilizzare per la sintesi vocale
                config.setSpeechSynthesisLanguage(lingua);

                synth = new SpeechSynthesizer(config);
                assert(synth != null);

                Future<SpeechSynthesisResult> task = synth.SpeakTextAsync(testo);
                assert(task != null);

                //Fa partire la riproduzione vocale del testo
                result = task.get();
                assert(result != null);

                if (result.getReason() == ResultReason.SynthesizingAudioCompleted) {
                    Log.d("PARLATO","Speech synthesized to speaker for text [" + testo + "]");
                    exitCode = 0;
                }
                else if (result.getReason() == ResultReason.Canceled) {
                    SpeechSynthesisCancellationDetails cancellation = SpeechSynthesisCancellationDetails.fromResult(result);
                    System.out.println("Annullato: Ragione=" + cancellation.getReason());

                    if (cancellation.getReason() == CancellationReason.Error) {
                        System.out.println("ErrorCode=" + cancellation.getErrorCode());
                        System.out.println("ErrorDetails=" + cancellation.getErrorDetails());
                        System.out.println("Did you update the subscription info?");
                    }
                }

                result.close();
                synth.close();
            } catch (Exception ex) {
                System.out.println("Unexpected exception: " + ex.getMessage());
                assert(false);
            }
            return null;
        }
    }

    public void ferma(View v){
        System.exit(0);
    }
}
