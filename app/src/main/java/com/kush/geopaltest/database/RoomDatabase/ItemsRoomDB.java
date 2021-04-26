package com.kush.geopaltest.database.RoomDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.kush.geopaltest.database.DAO.ImagesDAO;
import com.kush.geopaltest.database.DAO.ItemsDAO;
import com.kush.geopaltest.database.Entity.ImagesEntity;
import com.kush.geopaltest.database.Entity.ItemsEntity;

@Database(entities = {ItemsEntity.class, ImagesEntity.class}, version = 1)
public abstract class ItemsRoomDB extends RoomDatabase {

    public abstract ItemsDAO itemsDAO();
    public abstract ImagesDAO imagesDAO();

    private static volatile ItemsRoomDB todoRoomDB;

    // to make it singleton class
    public static ItemsRoomDB getTodoRoomDB(final Context context) {
        if (todoRoomDB == null) {
            synchronized (ItemsRoomDB.class) {
                if (todoRoomDB == null) {
                    todoRoomDB = Room.databaseBuilder(context.getApplicationContext(),
                            ItemsRoomDB.class, "todoRoomDB")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return todoRoomDB;
    }
}
