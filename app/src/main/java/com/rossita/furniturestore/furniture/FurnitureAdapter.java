package com.rossita.furniturestore.furniture;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rossita.furniturestore.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FurnitureAdapter extends RecyclerView.Adapter<FurnitureAdapter.ViewHolder> {

    List<Item> itemList;
    ItemClicked activity;

    public FurnitureAdapter(Context context, List<Item> itemList) {
        this.itemList = itemList;
        activity = (ItemClicked) context;
    }

    public interface ItemClicked{
        void onItemCLicked(int index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivFurniture;
        TextView tvCategoryFurniture, tvNameFurniture, tvPriceFurniture;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivFurniture = itemView.findViewById(R.id.ivFurniture);
            tvNameFurniture = itemView.findViewById(R.id.tvNameFurniture);
            tvPriceFurniture = itemView.findViewById(R.id.tvPriceFurniture);
            tvCategoryFurniture = itemView.findViewById(R.id.tvCategoryFurniture);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemCLicked(itemList.indexOf((Item)v.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public FurnitureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.furniture,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FurnitureAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(itemList.get(position));
        Picasso.get().load(itemList.get(position).getImg()).into(holder.ivFurniture);
        holder.tvNameFurniture.setText(itemList.get(position).getName());
        holder.tvPriceFurniture.setText("price: " + itemList.get(position).getPrice() + " ₪");
        holder.tvCategoryFurniture.setText(itemList.get(position).getCategory());

    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }
}
