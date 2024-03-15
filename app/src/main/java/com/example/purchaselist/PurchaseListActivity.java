package com.example.purchaselist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.purchaselist.adapter.PurchaseAdapter;
import com.example.purchaselist.adapter.PurchaseListAdapter;
import com.example.purchaselist.database.handler.DatabaseHandler;
import com.example.purchaselist.model.Purchase;
import com.example.purchaselist.model.PurchaseList;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;

import lombok.var;

public class PurchaseListActivity extends AppCompatActivity {
    private final DatabaseHandler db = new DatabaseHandler(this);
    private TextInputLayout purchaseInputLayout;
    private PurchaseList currentList;
    private List<Purchase> purchases;
    private PurchaseAdapter purchaseAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("TAG", "onCreate: PurchaseListActivity loaded");

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_purchase_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        currentList = db.getPurchaseListHandler().create(
                PurchaseList.builder()
                        .title("INPUT TITLE")
                        .build());

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        setAddPurchaseButton();
    }

    protected void onStart() {
        super.onStart();
        purchases = db.findAllByPurchaseListId(currentList.getId());

        Log.i("TAG", "onStart: currentListID = " + currentList.getId());
        Log.i("TAG", "onStart: currentPurchases = " + purchases);
        setPurchaseView();
    }

    private void setPurchaseView() {
        RecyclerView purchaseView = findViewById(R.id.purchaseView);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        purchaseView.setLayoutManager(layoutManager);
        purchaseAdapter = new PurchaseAdapter(this, purchases, db);
        purchaseView.setAdapter(purchaseAdapter);
    }

    private void setAddPurchaseButton(){
        Button addPurchaseButton = findViewById(R.id.addPurchaseButton);
        purchaseInputLayout = findViewById(R.id.purchaseListInputLayout);
        addPurchaseButton.setOnClickListener(v -> {
            var editText = purchaseInputLayout.getEditText();
            try {
                String purchaseTitleText = editText.getText().toString().trim();
                if (!TextUtils.isEmpty(purchaseTitleText)) {
                    currentList.setTitle(purchaseTitleText);
                    db.getPurchaseListHandler().update(currentList);

                    Intent intent = new Intent(PurchaseListActivity.this, PurchaseAddingActivity.class);
                    intent.putExtra("purchaseListId", currentList.getId());
                    startActivity(intent);
                } else {
                    throw new NullPointerException();
                }
            } catch (NullPointerException e){
                Toast.makeText(this, "Введіть назву списку покупок", Toast.LENGTH_SHORT).show();
            }
        });
    }
}