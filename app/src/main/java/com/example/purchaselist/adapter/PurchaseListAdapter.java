package com.example.purchaselist.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purchaselist.PurchaseListActivity;
import com.example.purchaselist.R;
import com.example.purchaselist.database.handler.DatabaseHandler;
import com.example.purchaselist.model.PurchaseList;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PurchaseListAdapter extends RecyclerView.Adapter<PurchaseListAdapter.PurchaseListViewHolder> {
    private Context context;
    private final List<PurchaseList> purchaseLists;
    private final DatabaseHandler db;

    @NonNull
    @Override
    public PurchaseListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View purchaseLists = LayoutInflater.from(context).inflate(R.layout.purchase_list, parent, false);
        return new PurchaseListViewHolder(purchaseLists);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseListViewHolder holder, int position) {
        PurchaseList purchaseList = purchaseLists.get(position);
        holder.bind(purchaseList);
    }

    @Override
    public int getItemCount() {
        return purchaseLists.size();
    }

    public class PurchaseListViewHolder extends RecyclerView.ViewHolder{
        private final TextView purchaseListTitle;
        private final ConstraintLayout purchaseListLayout;
        private final TextView priceSum;
        public PurchaseListViewHolder(@NonNull View itemView) {
            super(itemView);

            purchaseListTitle = itemView.findViewById(R.id.purchaseListTitle);
            purchaseListLayout = itemView.findViewById(R.id.purchaseListLayout);
            priceSum = itemView.findViewById(R.id.priceSum);
            itemView.findViewById(R.id.deletePurchaseListButton).setOnClickListener(v -> deletePurchaseListButtonListener());
            itemView.findViewById(R.id.purchaseListView).setOnClickListener(v -> purchaseListViewListener());
        }

        private void purchaseListViewListener() {
            Log.i("TAG", "purchaseListViewListener: loading new intent");
            Intent intent = new Intent(context, PurchaseListActivity.class);
            intent.putExtra("currentList", purchaseLists.get(getAdapterPosition()));
            context.startActivity(intent);
        }

        private void deletePurchaseListButtonListener() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                db.getPurchaseListHandler().delete(purchaseLists.remove(position).getId());
                notifyItemRemoved(position);
            }
        }

        @SuppressLint("SetTextI18n")
        public void bind(PurchaseList purchaseList){
            purchaseListTitle.setText(purchaseList.getTitle());
            priceSum.setText(purchaseList.priceSum() + "$");
            if (purchaseList.isBought()){
                purchaseListLayout.setBackgroundColor(Color.parseColor("#00FE84"));
            }
        }
    }
}
