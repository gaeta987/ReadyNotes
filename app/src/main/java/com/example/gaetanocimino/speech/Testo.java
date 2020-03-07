package com.example.gaetanocimino.speech;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.google.gson.*;
import javax.net.ssl.HttpsURLConnection;

public class Testo extends Activity {

    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.testo);

        listView = (ListView)findViewById(R.id.mylistview);

        Intent i = getIntent();
        ArrayList<String> testi = i.getStringArrayListExtra("Testi");
        ArrayList<String> titoli = i.getStringArrayListExtra("Titoli");
        ArrayList<String> lingue = i.getStringArrayListExtra("Lingue");

        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, R.layout.list_element, R.id.textViewList,  titoli);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  str  = listView.getItemAtPosition(position).toString();

                Intent in = new Intent();
                in.setClass(getApplicationContext(), Single.class);
                in.putExtra("testo",testi.get(position));
                in.putExtra("titolo",titoli.get(position));
                in.putExtra("lingua",lingue.get(position));
                startActivity(in);
            }
        });
    }
}
