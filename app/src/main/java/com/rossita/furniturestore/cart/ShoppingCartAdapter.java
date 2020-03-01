package com.rossita.furniturestore.cart;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.content.Context;
import android.widget.ImageView;
import android.view.LayoutInflater;
import com.michaelmuenzer.android.scrollablennumberpicker.ScrollableNumberPicker;
import com.rossita.furniturestore.R;
import com.rossita.furniturestore.utilities.FurnitureStoreApplication;
import com.squareup.picasso.Picasso;
import com.rossita.furniturestore.utilities.MyToast;

public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    List<ShoppingCartItem> items;
    ItemClicked activity;

    public ShoppingCartAdapter(Context context, List<ShoppingCartItem> items) {
        this.items = items;
        this.activity = (ItemClicked)context;
    }

    public interface ItemClicked{
        void onItemClicked(int index);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View space;
        ImageView ivCart, ivRemove;
        ScrollableNumberPicker numberPicker;
        TextView tvNameCart, tvPriceCart, tvCategoryCart, tvQuantityCart;

        public ViewHolder(@NonNull final View itemView) {
            super(itemView);

            space = itemView.findViewById(R.id.space);
            ivCart = itemView.findViewById(R.id.ivCart);
            ivRemove = itemView.findViewById(R.id.ivRemove);
            tvNameCart = itemView.findViewById(R.id.tvNameCart);
            tvPriceCart = itemView.findViewById(R.id.tvPriceCart);
            numberPicker = itemView.findViewById(R.id.numberPicker);
            tvCategoryCart = itemView.findViewById(R.id.tvCategoryCart);
            tvQuantityCart = itemView.findViewById(R.id.tvQuantityCart);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity.onItemClicked(items.indexOf(v.getTag()));
                }
            });

        }
    }

    @NonNull
    @Override
    public ShoppingCartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item,parent,false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ShoppingCartAdapter.ViewHolder holder, int position) {

        holder.itemView.setTag(items.get(position));
        holder.tvNameCart.setText(items.get(position).getName());
        holder.tvCategoryCart.setText(items.get(position).getCategory());
        holder.tvPriceCart.setText(items.get(position).getPrice() + " ₪");
        Picasso.get().load(items.get(position).getImg()).into(holder.ivCart);
        holder.tvQuantityCart.setText(items.get(position).getQuantity() + "");

        holder.ivRemove.setTag("unchecked");
        holder.space.setId((position+1)*10);
        holder.ivRemove.setId((position+1)*11);
        holder.numberPicker.setId((position+1)*12);
        holder.tvQuantityCart.setId((position+1)*13);


        FurnitureStoreApplication.cartItem = items.get(position);
        holder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.ivRemove.getTag().toString().equals("unchecked")){
                    holder.ivRemove.setImageResource(R.drawable.delete_checked);
                    holder.ivRemove.setTag("checked");
                    MyToast.showToast(holder.tvNameCart.getText().toString() + " will be removed");
                    holder.itemView.setAlpha(0.5f);
                }else{
                    holder.ivRemove.setImageResource(R.drawable.delete_unchecked);
                    holder.ivRemove.setTag("unchecked");
                    MyToast.showToast(holder.tvNameCart.getText().toString() + " was added back");
                    holder.itemView.setAlpha(1f);
                }
            }
        });
    }



    @Override
    public int getItemCount() {
        return items.size();
    }
}
