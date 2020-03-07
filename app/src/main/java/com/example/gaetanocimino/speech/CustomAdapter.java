package com.example.gaetanocimino.speech;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Evento> {
    private int resource;
    private LayoutInflater inflater;

    public CustomAdapter(Context context, int resourceId, List<Evento> objects) {
            super(context, resourceId, objects);
            resource = resourceId;
            inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
    		if (v == null) {
    			Log.d("DEBUG","Inflating view");
    			v = inflater.inflate(R.layout.date, null);
    		}
    		
            Evento e = getItem(position);
			
            TextView nameTextView;
            TextView dateTextView;
            
            nameTextView = (TextView) v.findViewById(R.id.nomeEvento);
            
        	Log.d("DEBUG","nameTextView="+nameTextView);
        	
            dateTextView = (TextView) v.findViewById(R.id.data);

            nameTextView.setText(e.getNomeEvento());
            dateTextView.setText(e.getGiorno()+"/"+e.getMese()+"/"+e.getAnno());

            return v;
    }
}

