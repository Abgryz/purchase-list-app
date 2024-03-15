package com.example.purchaselist.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purchaselist.R;
import com.example.purchaselist.database.handler.DatabaseHandler;
import com.example.purchaselist.model.Purchase;

import java.util.List;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {
    private Context context;
    private final List<Purchase> purchaseList;
    private final DatabaseHandler db;

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View purchaseList = LayoutInflater.from(context).inflate(R.layout.purchase, parent, false);
        return new PurchaseAdapter.PurchaseViewHolder(purchaseList);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);
        holder.bind(purchase);
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    public class PurchaseViewHolder extends RecyclerView.ViewHolder{
        private final CheckBox purchaseCheckBox;
        private final TextView count;
        private final TextView price;
        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);

            count = itemView.findViewById(R.id.count);
            price = itemView.findViewById(R.id.price);
            purchaseCheckBox = itemView.findViewById(R.id.purchaseCheckBox);
            itemView.findViewById(R.id.deletePurchaseButton).setOnClickListener(v -> deletePurchaseButtonListener());
            purchaseCheckBox.setOnCheckedChangeListener((v, isChecked) -> purchaseCheckBoxOnCheckedChangeListener(isChecked));
        }

        private void deletePurchaseButtonListener() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                db.getPurchaseHandler().delete(purchaseList.remove(position).getId());
                notifyItemRemoved(position);
            }
        }

        private void purchaseCheckBoxOnCheckedChangeListener(boolean isChecked){
            int position = getAdapterPosition();
            Purchase purchase = purchaseList.get(position);
            purchase.setBought(isChecked);
            db.getPurchaseHandler().update(purchase);
            itemView.post(() -> notifyItemChanged(position));
        }

        @SuppressLint("SetTextI18n")
        public void bind(Purchase purchase){
            purchaseCheckBox.setText(purchase.getProduct());
            purchaseCheckBox.setChecked(purchase.isBought());
            count.setText(String.valueOf(purchase.getCount()));
            price.setText(purchase.priceSum() + "$");

            ConstraintLayout purchaseLayout = itemView.findViewById(R.id.purchaseLayout);
            if(purchase.isBought()){
                purchaseLayout.setBackgroundColor(Color.parseColor("#00FE84"));
            } else {
                purchaseLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
        }
    }
}
