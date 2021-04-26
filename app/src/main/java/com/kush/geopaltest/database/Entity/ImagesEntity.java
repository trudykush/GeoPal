package com.kush.geopaltest.database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "imagesTable")
public class ImagesEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ImageIDPK")
    private int imageIDPK;

    @ColumnInfo(name = "ItemImageID")
    private String itemImageID;

    @ColumnInfo(name = "ItemImage", typeAffinity = ColumnInfo.BLOB)
    private byte[] itemImage;

    @ColumnInfo(name = "ItemImageDescription")
    private String imageDescription;

    public ImagesEntity(String itemImageID, byte[] itemImage, String imageDescription) {
        this.itemImageID = itemImageID;
        this.itemImage   = itemImage;
        this.imageDescription = imageDescription;
    }

    public int getImageIDPK() {
        return imageIDPK;
    }

    public void setImageIDPK(int imageIDPK) {
        imageIDPK = imageIDPK;
    }

    public String getItemImageID() {
        return itemImageID;
    }

    public byte[] getItemImage() {
        return itemImage;
    }

    public String getImageDescription() {
        return imageDescription;
    }
}
