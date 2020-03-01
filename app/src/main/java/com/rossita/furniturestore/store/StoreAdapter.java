package com.rossita.furniturestore.store;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rossita.furniturestore.R;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.ViewHolder> {

    private List<Category> categories;
    ItemClicked activity;

    public interface ItemClicked{
        void onItemClicked(int index);
    }

    public StoreAdapter(Context context, List<Category> list){
        categories = list;
        activity = (ItemClicked) context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCategory;
        TextView tvCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategory = itemView.findViewById(R.id.ivCategory);
            tvCategory = itemView.findViewById(R.id.tvCategory);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClicked(categories.indexOf((Category)v.getTag()));
                }
            });
        }
    }

    @NonNull
    @Override
    public StoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(categories.get(position));
        holder.ivCategory.setImageResource(categories.get(position).getCategoryImg());
        holder.tvCategory.setText(categories.get(position).getCategoryName());

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }
}
