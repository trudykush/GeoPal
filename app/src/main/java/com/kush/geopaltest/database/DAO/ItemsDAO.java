package com.kush.geopaltest.database.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kush.geopaltest.database.Entity.ItemsEntity;

import java.util.List;

@Dao
public interface ItemsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItemsDetails(ItemsEntity todoItemEntity);

    @Query("SELECT * FROM itemDetailsTable")
    List<ItemsEntity> getAllTodoList();

    @Query("SELECT * FROM itemDetailsTable WHERE ItemID = :itemID")
    List<ItemsEntity> getItemListPerID(String itemID);

    @Query("UPDATE itemDetailsTable SET ItemTitle = :title," +
            " ItemDescription = :description, ItemDueDate = :dueDate," +
            " ItemStatus = :status, CurrentTime =:currentTime" +
            " WHERE ItemID = :itemID")
    void updateTodoList(String itemID, String title, String description, String dueDate,
                        String status, String currentTime);

    @Query("SELECT COUNT(ItemID) FROM itemDetailsTable")
    int countNumberOFTodoItems();

    @Query("SELECT COUNT() FROM itemDetailsTable WHERE ItemID = :itemID")
    int whetherIDAlreadyExist(String itemID);

    @Query("DELETE FROM itemDetailsTable WHERE ItemID = :itemID")
    void deleteATodoItem(String itemID);
}
