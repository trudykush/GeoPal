package com.kush.geopaltest.database.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.kush.geopaltest.database.Entity.ImagesEntity;

import java.util.List;

@Dao
public interface ImagesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertImages(ImagesEntity imagesEntity);

    @Query("SELECT ImageIDPK, ItemImage, ItemImageDescription" +
            " FROM imagesTable WHERE ItemImageID = :itemID")
    List<ImagesEntity> getImageForItem(String itemID);

    @Query("SELECT COUNT(ItemImage) FROM imagesTable WHERE ItemImageID = :itemID")
    int numberOfImagesForItem(String itemID);

    @Query("DELETE FROM imagesTable WHERE ImageIDPK = :itemID")
    void deleteImagePerSwipe(int itemID);

    @Query("DELETE FROM imagesTable WHERE ItemImageID = :itemID")
    void deleteImagePerItem(String itemID);
}
