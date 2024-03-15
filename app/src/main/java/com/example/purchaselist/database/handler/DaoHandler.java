package com.example.purchaselist.database.handler;

import android.database.sqlite.SQLiteDatabase;

import com.example.purchaselist.database.dao.Dao;

import java.util.List;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DaoHandler<T> {
    private final Dao<T> dao;
    private final DatabaseHandler handler;

    public T create(T t){
        SQLiteDatabase db = handler.getWritableDatabase();
        dao.setDb(db);
        T elem = dao.create(t);
        db.close();
        return elem;
    }

    public T get(int id){
        SQLiteDatabase db = handler.getReadableDatabase();
        dao.setDb(db);
        T t = dao.get(id);
        db.close();
        return t;
    }

    public List<T> getAll(){
        SQLiteDatabase db = handler.getReadableDatabase();
        dao.setDb(db);
        List<T> all = dao.getAll();
        db.close();
        return all;
    }

    public void delete(int id){
        SQLiteDatabase db = handler.getWritableDatabase();
        dao.setDb(db);
        dao.delete(id);
        db.close();
    }

    public T update(T t){
        SQLiteDatabase db = handler.getWritableDatabase();
        dao.setDb(db);
        T elem = dao.update(t);
        db.close();
        return elem;
    }
}
