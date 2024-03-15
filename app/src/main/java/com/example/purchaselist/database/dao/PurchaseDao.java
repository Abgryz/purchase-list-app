package com.example.purchaselist.database.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.purchaselist.database.data.PurchaseData;
import com.example.purchaselist.model.Purchase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PurchaseDao implements Dao<Purchase> {
    private SQLiteDatabase db;
    @Override
    public Purchase get(int id) {
        @SuppressLint("Recycle") Cursor cursor = db.query(
                PurchaseData.TABLE_NAME,
                new String[] {PurchaseData.KEY_ID, PurchaseData.KEY_PRODUCT, PurchaseData.KEY_COUNT, PurchaseData.KEY_PRICE, PurchaseData.KEY_IS_BOUGHT, PurchaseData.KEY_PURCHASE_LIST},
                PurchaseData.KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null
        );
        if (cursor != null){
            cursor.moveToFirst();
            return Purchase.builder()
                    .id(Integer.parseInt(cursor.getString(0)))
                    .product(cursor.getString(1))
                    .count(Double.parseDouble(cursor.getString(2)))
                    .price(Float.parseFloat(cursor.getString(3)))
                    .isBought((Integer.parseInt(cursor.getString(4))) == 1)
                    .purchaseList(Integer.parseInt(cursor.getString(5)))
                    .build();
        }
        return null;
    }

    @Override
    public List<Purchase> getAll() {
        String query = String.format("select * from %s", PurchaseData.TABLE_NAME);
        return getAllByQuery(query);
    }

    @Override
    public Purchase create(Purchase purchase) {
        ContentValues values = new ContentValues();
        values.put(PurchaseData.KEY_PRODUCT, purchase.getProduct());
        values.put(PurchaseData.KEY_COUNT, purchase.getCount());
        values.put(PurchaseData.KEY_PRICE, purchase.getPrice());
        values.put(PurchaseData.KEY_IS_BOUGHT, purchase.isBought());
        values.put(PurchaseData.KEY_PURCHASE_LIST, purchase.getPurchaseList());
        db.insert(PurchaseData.TABLE_NAME, null, values);

        String query = String.format(
                "select * from %s " +
                "order by %s desc " +
                "limit 1",
                PurchaseData.TABLE_NAME, PurchaseData.KEY_ID);
        List<Purchase> latestPurchase = getAllByQuery(query);
        if (latestPurchase.size() == 1){
            Log.i("TAG", "create new p: " + latestPurchase.get(0));
            return latestPurchase.get(0);
        } else {
            return null;
        }
    }

    @Override
    public Purchase update(Purchase purchase) {
        ContentValues values = new ContentValues();
        values.put(PurchaseData.KEY_PRODUCT, purchase.getProduct());
        values.put(PurchaseData.KEY_COUNT, purchase.getCount());
        values.put(PurchaseData.KEY_PRICE, purchase.getPrice());
        values.put(PurchaseData.KEY_IS_BOUGHT, purchase.isBought());
        values.put(PurchaseData.KEY_PURCHASE_LIST, purchase.getPurchaseList());

        String whereClause = PurchaseData.KEY_ID + "=?";
        String[] whereArgs = {String.valueOf(purchase.getId())};
        db.update(PurchaseData.TABLE_NAME, values, whereClause, whereArgs);

        Log.i("TAG", "update p: " + get(purchase.getId()));
        return get(purchase.getId());
    }

    @Override
    public void delete(int id) {
        String whereClause = PurchaseData.KEY_ID + "=?";
        String[] whereArgs = {String.valueOf(id)};
        int rowsAffected = db.delete(PurchaseData.TABLE_NAME, whereClause, whereArgs);
        Log.i("TAG", String.format("delete: %s rows (p), id=%s", rowsAffected, id));
    }

    @Override
    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    public List<Purchase> findByPurchaseList(int id){
        String query = String.format(
                "select * from %s " +
                "where %s=%s",
                PurchaseData.TABLE_NAME, PurchaseData.KEY_PURCHASE_LIST, id);
        return getAllByQuery(query);
    }

    private List<Purchase> getAllByQuery(String query){
        List<Purchase> purchases = new ArrayList<>();
        try {
            @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
            if (cursor.moveToFirst()){
                do {
                    Purchase purchase = Purchase.builder()
                            .id(Integer.parseInt(cursor.getString(0)))
                            .product(cursor.getString(1))
                            .count(Double.parseDouble(cursor.getString(2)))
                            .price(Float.parseFloat(cursor.getString(3)))
                            .isBought((Integer.parseInt(cursor.getString(4))) == 1)
                            .purchaseList(Integer.parseInt(cursor.getString(5)))
                            .build();
                    purchases.add(purchase);
                } while (cursor.moveToNext());
            }
        } catch (NullPointerException e){
            Log.e("TAG", "getAllByQuery: " + Arrays.toString(e.getStackTrace()));
        }

        return purchases;
    }
}
