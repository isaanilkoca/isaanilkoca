package com.isaanilkoca.javamaps.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.RoomSQLiteQuery;

import com.isaanilkoca.javamaps.databinding.RecyclerRowBinding;
import com.isaanilkoca.javamaps.model.Place;
import com.isaanilkoca.javamaps.view.MapsActivity;

import java.util.List;

public class PlaceAdapter extends RecyclerView.Adapter<PlaceAdapter.PlaceHolder> {

    List<Place> placeList;

    public PlaceAdapter(List<Place> placeList){
        this.placeList=placeList;
    }

    @NonNull
    @Override
    public PlaceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerRowBinding recyclerRowBinding=RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);


        return new PlaceHolder(recyclerRowBinding);
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceHolder holder, int position) { // baglanınca ne olacak kısmı

      holder.recyclerRowBinding.recyclerViewTextView.setText(placeList.get(position).name);

      holder.itemView.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Intent intent= new Intent(holder.itemView.getContext(), MapsActivity.class);
              intent.putExtra("info","old");// eski bir veri geliyor demek
              intent.putExtra("place",placeList.get(position));
              holder.itemView.getContext().startActivity(intent);
          }
      });

    }

    public class PlaceHolder extends  RecyclerView.ViewHolder{

        RecyclerRowBinding recyclerRowBinding;


        public PlaceHolder(RecyclerRowBinding recyclerRowBinding) {
            super(recyclerRowBinding.getRoot());
            this.recyclerRowBinding=recyclerRowBinding;
        }
    }
}
