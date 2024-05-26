package com.example.teknolost;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    Context context;
    ArrayList<Items> itemsList;
    private ArrayList<Items> itemsListFull;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ItemAdapter(Context context, ArrayList<Items> itemsList, OnItemClickListener listener) {
        this.context = context;
        this.itemsList = itemsList;
        this.listener = listener;
        this.itemsListFull = new ArrayList<>(itemsList);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.items, parent, false);
        return new MyViewHolder(itemView, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Items item = itemsList.get(position);
        holder.title.setText(item.getDataTitle());
        holder.date.setText(item.getDataDate());
        holder.landmark.setText(item.getDataLang());
        holder.description.setText(item.getDataDesc());
        Glide.with(context).load(item.getDataImage()).into(holder.imageItem);
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, description, landmark, date;
        ImageView imageItem;

        public MyViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            title = itemView.findViewById(R.id.itemName);
            description = itemView.findViewById(R.id.itemDescription);
            date = itemView.findViewById(R.id.itemDate);
            landmark = itemView.findViewById(R.id.itemLandmark);
            imageItem = itemView.findViewById(R.id.itemImage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }




}
