package com.capstone.dolphindive.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.capstone.dolphindive.diveshoplistdetail;
import com.capstone.dolphindive.model.diveshopdata;
import com.capstone.dolphindive.R;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class placesAdapter extends RecyclerView.Adapter<placesAdapter.RecentsViewHolder>{
    private ArrayList<diveshopdata> recentsDataList;
    Context context;
    private String[] userfilter;

    public placesAdapter(Context context, int i, ArrayList<diveshopdata> recentsDataList, String[] userfilter) {
        this.context = context;
        this.recentsDataList = recentsDataList;
        this.userfilter = userfilter;
    }

    @NonNull
    @Override
    public RecentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recents_row_item, parent, false);
        RecentsViewHolder evh = new RecentsViewHolder(view);
        // here we create a recyclerview row item layout file
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull RecentsViewHolder holder, int position) {
        holder.countryName.setText(recentsDataList.get(position).getCountryName());
        holder.placeName.setText(recentsDataList.get(position).getPlaceName());
        holder.price.setText(recentsDataList.get(position).getPrice());
        holder.placeImage.setImageResource(recentsDataList.get(position).getImageUrl());
        holder.rate.setText(recentsDataList.get(position).getRate());
        holder.popular.setText(recentsDataList.get(position).getPopular());
        holder.itemView.setOnClickListener((view)-> {
                Intent i=new Intent(context, diveshoplistdetail.class);
                String[] passinglist = {recentsDataList.get(position).getPlaceName(),recentsDataList.get(position).getCountryName(),recentsDataList.get(position).getPrice(),recentsDataList.get(position).getRate(),recentsDataList.get(position).getPopular(),recentsDataList.get(position).getImageUrl().toString(),recentsDataList.get(position).getCellphone(),recentsDataList.get(position).getEmail(),recentsDataList.get(position).getAbout(),recentsDataList.get(position).getSizeavail().toString(),recentsDataList.get(position).getRoomavail().toString(),recentsDataList.get(position).getAddress(),recentsDataList.get(position).getPolicy()};
                i.putExtra("Key", passinglist);
                i.putExtra("userfilter", userfilter);
                context.startActivity(i);
        });

    }



    @Override
    public int getItemCount() {

        return recentsDataList.size();
    }


    public static final class RecentsViewHolder extends RecyclerView.ViewHolder{

        ImageView placeImage;
        TextView placeName, countryName, price,rate,popular;

        public RecentsViewHolder(@NonNull View itemView) {
            super(itemView);

            placeImage = itemView.findViewById(R.id.place_image);
            placeName = itemView.findViewById(R.id.diveshop_name);
            countryName = itemView.findViewById(R.id.country_name);
            price = itemView.findViewById(R.id.price);
            popular = itemView.findViewById(R.id.popular);
            rate = itemView.findViewById(R.id.rate);

        }
    }


}

