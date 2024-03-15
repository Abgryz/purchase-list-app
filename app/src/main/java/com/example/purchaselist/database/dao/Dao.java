package com.example.purchaselist.database.dao;

import android.database.sqlite.SQLiteDatabase;

import java.util.List;

public interface Dao<T> {
    T get(int id);
    List<T> getAll();
    T create(T t);
    T update(T t);
    void delete(int id);
    void setDb(SQLiteDatabase db);
}