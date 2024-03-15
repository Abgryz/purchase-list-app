package com.example.purchaselist.database.handler;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.purchaselist.database.dao.Dao;
import com.example.purchaselist.database.dao.PurchaseDao;
import com.example.purchaselist.database.dao.PurchaseListDao;
import com.example.purchaselist.database.data.DbData;
import com.example.purchaselist.database.data.PurchaseData;
import com.example.purchaselist.database.data.PurchaseListData;
import com.example.purchaselist.model.Purchase;
import com.example.purchaselist.model.PurchaseList;

import java.util.List;

import lombok.Getter;

@Getter
public class DatabaseHandler extends SQLiteOpenHelper {
    private final DaoHandler<Purchase> purchaseHandler = new DaoHandler<>(new PurchaseDao(), this);
    private final DaoHandler<PurchaseList> purchaseListHandler = new DaoHandler<>(new PurchaseListDao(), this);

    public DatabaseHandler(Context context) {
        super(context, DbData.DATABASE_NAME, null, DbData.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createPurchaseListTable = String.format(
                "create table %s " +
                        "(%s integer primary key autoincrement, " +
                        "%s text not null);",
                PurchaseListData.TABLE_NAME, PurchaseListData.KEY_ID, PurchaseListData.KEY_TITLE);
        String createPurchaseTable = String.format(
                "create table %s " +
                        "(%s integer primary key, " +
                        "%s text not null, " +
                        "%s real default 1, " +
                        "%s real default 0, " +
                        "%s boolean default false, " +
                        "%s integer not null, " +
                        "foreign key (%s) references %s (%s))",
                PurchaseData.TABLE_NAME, PurchaseData.KEY_ID, PurchaseData.KEY_PRODUCT,
                PurchaseData.KEY_COUNT, PurchaseData.KEY_PRICE, PurchaseData.KEY_IS_BOUGHT, PurchaseData.KEY_PURCHASE_LIST,
                PurchaseData.KEY_PURCHASE_LIST, PurchaseListData.TABLE_NAME, PurchaseListData.KEY_ID);

        db.execSQL(createPurchaseListTable);
        db.execSQL(createPurchaseTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.format("drop table if exists %s", PurchaseData.TABLE_NAME));
        db.execSQL(String.format("drop table if exists %s", PurchaseListData.TABLE_NAME));

        onCreate(db);
    }

    public List<Purchase> findAllByPurchaseListId(int id){
        SQLiteDatabase db = getReadableDatabase();
        PurchaseDao dao = new PurchaseDao();
        dao.setDb(db);
        List<Purchase> purchaseList = dao.findByPurchaseList(id);
        db.close();
        return purchaseList;
    }
}