package com.example.purchaselist.database.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.purchaselist.database.data.PurchaseListData;
import com.example.purchaselist.model.Purchase;
import com.example.purchaselist.model.PurchaseList;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class PurchaseListDao implements Dao<PurchaseList> {
    private SQLiteDatabase db;

    @Override
    public PurchaseList get(int id) {
        @SuppressLint("Recycle") Cursor cursor = db.query(
                PurchaseListData.TABLE_NAME,
                new String[] {PurchaseListData.KEY_ID, PurchaseListData.KEY_TITLE},
                PurchaseListData.KEY_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null, null
        );
        if (cursor != null){
            cursor.moveToFirst();
            return PurchaseList.builder()
                    .id(Integer.parseInt(cursor.getString(0)))
                    .title(cursor.getString(1))
                    .purchases(getPurchases(Integer.parseInt(cursor.getString(0))))
                    .build();
        }
        return null;
    }

    @Override
    public List<PurchaseList> getAll() {
        String query = String.format("select * from %s", PurchaseListData.TABLE_NAME);
        return getAllByQuery(query);
    }

    @Override
    public PurchaseList create(PurchaseList purchaseList) {
        ContentValues values = new ContentValues();
        values.put(PurchaseListData.KEY_TITLE, purchaseList.getTitle());
        db.insert(PurchaseListData.TABLE_NAME, null, values);

        String query = String.format(
                "select * from %s " +
                        "order by %s desc " +
                        "limit 1",
                PurchaseListData.TABLE_NAME, PurchaseListData.KEY_ID);
        List<PurchaseList> latestPurchaseList = getAllByQuery(query);
        if (latestPurchaseList.size() == 1){
            Log.i("TAG", "create new l: " + latestPurchaseList.get(0));
            return latestPurchaseList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public PurchaseList update(PurchaseList purchaseList) {
        ContentValues values = new ContentValues();
        values.put(PurchaseListData.KEY_TITLE, purchaseList.getTitle());

        String whereClause = PurchaseListData.KEY_ID + "=?";
        String[] whereArgs = {String.valueOf(purchaseList.getId())};
        db.update(PurchaseListData.TABLE_NAME, values, whereClause, whereArgs);

        Log.i("TAG", "update l: " + get(purchaseList.getId()));
        return get(purchaseList.getId());
    }

    @Override
    public boolean delete(int id) {
        PurchaseList purchaseList = get(id);
        PurchaseDao dao = new PurchaseDao();
        dao.setDb(db);
        purchaseList.getPurchases().forEach(purchase -> dao.delete(purchase.getId()));

        String whereClause = PurchaseListData.KEY_ID + "=?";
        String[] whereArgs = {String.valueOf(id)};
        int rowsAffected = db.delete(PurchaseListData.TABLE_NAME, whereClause, whereArgs);

        Log.i("TAG", String.format("delete: %s rows (l), id=%s", rowsAffected, id));
        return rowsAffected > 0;
    }

    @Override
    public void setDb(SQLiteDatabase db) {
        this.db = db;
    }

    private List<Purchase> getPurchases(int purchaseListId){
        PurchaseDao purchaseDao = new PurchaseDao();
        purchaseDao.setDb(db);
        return purchaseDao.findByPurchaseList(purchaseListId);
    }

    private List<PurchaseList> getAllByQuery(String query){
        List<PurchaseList> purchaseLists = new ArrayList<>();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()){
            do {
                int id = Integer.parseInt(cursor.getString(0));
                PurchaseList purchaseList = PurchaseList.builder()
                        .id(id)
                        .title(cursor.getString(1))
                        .purchases(getPurchases(id))
                        .build();
                purchaseLists.add(purchaseList);
            } while (cursor.moveToNext());
        }
        return purchaseLists;
    }
}
