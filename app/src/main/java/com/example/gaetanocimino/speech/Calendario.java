package com.example.gaetanocimino.speech;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.AlarmClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Calendario extends Activity {

    private EditText edit;
    private TextView txt;
    private Button b;
    private CalendarView calendario;
    private int count;
    private SharedPreferences s;
    private int giorno;
    private int mese;
    private int anno;
    private int giornoOra;
    private int meseOra;
    private int annoOra;
    private String nome;
    private Button but;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendario);

        but = (Button) findViewById(R.id.but);

        s = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        count = s.getInt("EVENTI",0);

        edit = (EditText) findViewById(R.id.edit);
        txt = (TextView) findViewById(R.id.txt);
        b = (Button) findViewById(R.id.b);
        calendario = (CalendarView) findViewById(R.id.calendario);

        GregorianCalendar g = new GregorianCalendar();
        giorno = g.get(GregorianCalendar.DAY_OF_MONTH);
        mese = g.get(GregorianCalendar.MONTH)+1;
        anno = g.get(GregorianCalendar.YEAR);

        giornoOra = g.get(GregorianCalendar.DAY_OF_MONTH);
        meseOra = g.get(GregorianCalendar.MONTH)+1;
        annoOra = g.get(GregorianCalendar.YEAR);

        createNotificationChannel();

        Log.d("DATAA",giorno +" " + mese +" " + anno);

        calendario.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int an, int me, int gio) {
                giorno = gio;
                mese = me+1;
                anno = an;
                Log.d("DATAA",giorno +" " + mese +" " + anno);
            }
        });

        reset();
    }

    //Effettua il reset delle impostazioni
    private void reset(){
        edit.setVisibility(View.INVISIBLE);
        edit.setText("");
        txt.setVisibility(View.INVISIBLE);
        b.setVisibility(View.INVISIBLE);
        but.setVisibility(View.VISIBLE);
    }

    public void eventoCalendario(View v){
        edit.setVisibility(View.VISIBLE);
        txt.setVisibility(View.VISIBLE);
        b.setVisibility(View.VISIBLE);
        but.setVisibility(View.INVISIBLE);
    }

    public void salvataggio(View v){
        nome = edit.getText().toString();
        if(nome.equals("")){
            Toast.makeText(getApplicationContext(),"Inserisci un nome!",Toast.LENGTH_SHORT).show();
            return;
        }

        Calendar dayalarm = Calendar.getInstance();

        String myFormat = "dd/MM/yy" ; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat , Locale. getDefault ()) ;
        Date date = dayalarm.getTime() ;

        Intent notificationIntent = new Intent( this, MyNotificationPublisher. class ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION_ID , 1 ) ;
        notificationIntent.putExtra(MyNotificationPublisher. NOTIFICATION , getNotification()) ;
        PendingIntent pendingIntent = PendingIntent. getBroadcast ( this, 0 , notificationIntent , PendingIntent. FLAG_UPDATE_CURRENT ) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context. ALARM_SERVICE ) ;
        assert alarmManager != null;

        if(anno == annoOra){
            if(mese == meseOra){
                Log.d("DATAA",meseOra + " " + mese);
                dayalarm.add(Calendar.DATE, giornoOra - giorno);
                if(giornoOra - giorno == 0){
                    alarmManager.set(AlarmManager.RTC_WAKEUP , dayalarm.getTimeInMillis(), pendingIntent);
                } else {
                    //Invia una notifica dopo 10 minuti dall'avvio del dispositivo
                    alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, dayalarm.getTimeInMillis()+600000, pendingIntent);
                }
            } else {
                Log.d("DATAA",meseOra + " " + mese);
                int mesi = meseOra - mese;
                int giorniRimanenti = 30 - giornoOra;
                dayalarm.add(Calendar.DATE, mesi*30+giorniRimanenti);
                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, dayalarm.getTimeInMillis()+600000, pendingIntent);
            }
        } else {
            int anni = annoOra - anno;
            int mesi = meseOra - mese;
            int giorniRimanenti = 30 - giornoOra;
            dayalarm.add(Calendar.DATE, (anni*365)+(mesi*30)+giorniRimanenti);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, dayalarm.getTimeInMillis()+600000, pendingIntent);
        }

        SharedPreferences.Editor edit = s.edit();

        edit.putString("NOMEEVENTO"+count,nome);
        edit.putInt("GIORNOEVENTO"+count,giorno);
        edit.putInt("MESEEVENTO"+count,mese);
        edit.putInt("ANNOEVENTO"+count,anno);

        count++;
        edit.putInt("EVENTI",count);

        edit.commit();

        Toast.makeText(getApplicationContext(),"Evento salvato correttamente!",Toast.LENGTH_SHORT).show();

        reset();
    }

    private Notification getNotification () {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( "ReadyNotes" ) ;
        builder.setContentText("L'evento " + nome + " è in programma oggi!");
        builder.setSmallIcon(R.drawable.logo) ;
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID );
        return builder.build() ;
    }

    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "studentChannel";
            String description = "Bla bla";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("lemubita",name,importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
