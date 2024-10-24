package com.example.photoapp;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
    private ArrayList<Uri> uriArrayList;

    public RecycleAdapter(ArrayList<Uri> uriArrayList) {
        this.uriArrayList = uriArrayList;
    }

    @NonNull
    @Override
    public RecycleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.activity_custom_single_img,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleAdapter.ViewHolder holder, int position) {
        holder.imageView.setImageURI(uriArrayList.get(position));

    }

    @Override
    public int getItemCount() {
        return uriArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
