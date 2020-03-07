package com.example.gaetanocimino.speech;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void registra(View v){
        Intent i = new Intent();
        i.setClass(getApplicationContext(),MainActivity.class);
        startActivity(i);
    }

    public void calendario(View v){
        Intent i = new Intent();
        i.setClass(getApplicationContext(),Calendario.class);
        startActivity(i);
    }

    public void visualizza(View v){
        Intent i = new Intent();
        i.setClass(getApplicationContext(),Date.class);
        startActivity(i);
    }

}
