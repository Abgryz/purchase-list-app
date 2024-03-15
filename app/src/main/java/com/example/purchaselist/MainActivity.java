package com.example.purchaselist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purchaselist.adapter.PurchaseListAdapter;
import com.example.purchaselist.database.handler.DatabaseHandler;
import com.example.purchaselist.model.Purchase;
import com.example.purchaselist.model.PurchaseList;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<PurchaseList> purchaseLists;
    private final DatabaseHandler db = new DatabaseHandler(this);
    private RecyclerView purchaseListView;
    private PurchaseListAdapter purchaseListAdapter;
    private Button addPurchaseListButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setAddPurchaseListButton();
    }

    @Override
    protected void onStart() {
        super.onStart();
        purchaseLists = db.getPurchaseListHandler().getAll();
        setPurchaseListView();
    }

    private void setAddPurchaseListButton() {
        addPurchaseListButton = findViewById(R.id.addPurchaseListButton);
        addPurchaseListButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PurchaseListActivity.class);
            startActivity(intent);
        });
    }

    private void setPurchaseListView(){
        purchaseListView = findViewById(R.id.purchaseListView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        purchaseListView.setLayoutManager(layoutManager);
        purchaseListAdapter = new PurchaseListAdapter(this, purchaseLists, db);
        purchaseListView.setAdapter(purchaseListAdapter);
    }
}