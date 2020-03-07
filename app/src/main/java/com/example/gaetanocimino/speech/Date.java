package com.example.gaetanocimino.speech;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class Date extends Activity {

    private ListView listView;
    private int count;
    private SharedPreferences s;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.datelista);

        s = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        listView = (ListView)findViewById(R.id.mylistview);

        CustomAdapter customAdapter =
                new CustomAdapter(this, R.layout.date, new ArrayList<Evento>());

        listView.setAdapter(customAdapter);

        count = s.getInt("EVENTI",0);

        for(int i = 0;i<count;i++){
            String nomeEvento = s.getString("NOMEEVENTO"+i,null);
            int giorno = s.getInt("GIORNOEVENTO"+i,0);
            int mese = s.getInt("MESEEVENTO"+i,0);
            int anno = s.getInt("ANNOEVENTO"+i,0);
            Evento e = new Evento(nomeEvento,giorno,mese,anno);
            customAdapter.add(e);
        }
    }
}
