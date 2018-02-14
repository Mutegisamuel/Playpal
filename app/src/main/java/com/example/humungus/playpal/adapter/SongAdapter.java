package com.example.humungus.playpal.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.humungus.playpal.R;
import com.example.humungus.playpal.model.SongInfo;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by humungus on 2/13/18.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongHolder> {

    ArrayList<SongInfo> songs;
    Context context;



    public SongAdapter(Context context, ArrayList<SongInfo> songs){
        this.context = context;
        this.songs = songs;
    }


    OnitemClickListener onitemClickListener;

    @Override
    public SongHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View myview = LayoutInflater.from(context).inflate(R.layout.row_song, parent,false);
        return new SongHolder(myview);
    }

    public interface OnitemClickListener{
        void onItemClick(LottieAnimationView i, View v, SongInfo obj, int position);
    }

    public SongAdapter(OnitemClickListener onitemClickListener){
        this.onitemClickListener = onitemClickListener;
    }

    @Override
    public void onBindViewHolder(SongHolder holder, final int position) {
        final SongInfo c = songs.get(position);
        SongHolder.songName.setText(c.songName);
        SongHolder.artistName.setText(c.artistName);
        SongHolder.animationView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onitemClickListener !=null){
                    onitemClickListener.onItemClick(SongHolder.animationView,v,c,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return songs.size();
    }

    public static class SongHolder extends RecyclerView.ViewHolder {
        static CircleImageView image;
        static TextView songName;
        static TextView artistName;
        static LottieAnimationView animationView;

        public SongHolder(View itemView) {
            super(itemView);
            image = (CircleImageView) itemView.findViewById(R.id.image);
            songName = (TextView) itemView.findViewById(R.id.songname);
            artistName = (TextView) itemView.findViewById(R.id.artist);
            animationView = (LottieAnimationView) itemView.findViewById(R.id.animation_view);
        }
    }
}
