package com.example.humungus.playpal;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.math.BigDecimal;

/**
 * Created by humungus on 2/14/18.
 */

class MediaCursorAdapter extends SimpleCursorAdapter {
    public MediaCursorAdapter(Context context, int layout, Cursor c) {
        super(context, layout, c,new String[]{MediaStore.MediaColumns.DISPLAY_NAME, MediaStore.MediaColumns.TITLE, MediaStore.Audio.AudioColumns.DURATION},
                new int[]{R.id.displayname, R.id.title, R.id.duration});
    }

    @Override

    public void bindView (View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.title);
        TextView name = (TextView)view.findViewById(R.id.displayname);
        TextView duration = (TextView)view.findViewById(R.id.duration);

        name.setText(cursor.getString(
                cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME)
        ));
        title.setText(cursor.getString(
                cursor.getColumnIndex(MediaStore.MediaColumns.TITLE)
        ));
        long durationInMs = Long.parseLong(cursor.getString(
                cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)
        ));
        double durationInMin = ((double) durationInMs/1000.0) /6.0;

        durationInMin = new BigDecimal(Double.toString(durationInMin)).setScale(2, BigDecimal.ROUND_UP).doubleValue();

        duration.setText("" + durationInMin);

        view.setTag(cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA)));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_item,parent,false);

        bindView(v, context, cursor);

        return v;
    }
}
