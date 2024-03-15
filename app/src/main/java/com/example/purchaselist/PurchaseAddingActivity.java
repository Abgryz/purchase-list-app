package com.example.purchaselist;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.purchaselist.database.handler.DatabaseHandler;
import com.example.purchaselist.model.Purchase;
import com.google.android.material.textfield.TextInputLayout;

import lombok.var;

public class PurchaseAddingActivity extends AppCompatActivity {
    private final DatabaseHandler db = new DatabaseHandler(this);
    private int purchaseListId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_purchase_adding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Intent intent = getIntent();
        purchaseListId = intent.getIntExtra("purchaseListId", -1);

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        Button savePurchaseButton = findViewById(R.id.savePurchaseButton);
        savePurchaseButton.setOnClickListener(v -> savePurchaseOnClickListener());
    }

    private void savePurchaseOnClickListener() {
        TextInputLayout purchaseInputLayout1 = findViewById(R.id.purchaseInputLayout);
        EditText countInput = findViewById(R.id.countInput);
        EditText priceInput = findViewById(R.id.priceInput);
        var editText = purchaseInputLayout1.getEditText();
        try {
            String purchaseInputLayout = editText.getText().toString().trim();
            double count = TextUtils.isEmpty(countInput.getText().toString()) ? 1 : Double.parseDouble(countInput.getText().toString());
            float price = TextUtils.isEmpty(priceInput.getText().toString()) ? 0.00f : Float.parseFloat(priceInput.getText().toString());
            if (!TextUtils.isEmpty(purchaseInputLayout)){
                Purchase purchase = Purchase.builder()
                        .product(purchaseInputLayout)
                        .count(count)
                        .price(price)
                        .purchaseList(purchaseListId)
                        .build();
                db.getPurchaseHandler().create(purchase);

                finish();
            } else {
                throw new NullPointerException();
            }
        } catch (NullPointerException e){
            Toast.makeText(this, "Введіть назву покупки", Toast.LENGTH_SHORT).show();
        }
    }
}