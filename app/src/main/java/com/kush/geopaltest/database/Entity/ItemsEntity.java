package com.kush.geopaltest.database.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "itemDetailsTable")
public class ItemsEntity {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "ID")
    private int id;

    @ColumnInfo(name = "ItemID", defaultValue = "0")
    private String itemID;

    @ColumnInfo(name = "ItemTitle")
    private String itemTitle;

    @ColumnInfo(name = "ItemDescription")
    private String itemDescription;

    @ColumnInfo(name = "ItemDueDate")
    private String itemDueDate;

    @ColumnInfo(name = "ItemStatus")
    private String itemStatus;

    @ColumnInfo(name = "CurrentTime")
    private String currentTime;

    public ItemsEntity(String itemID, String itemTitle, String itemDescription,
                       String itemDueDate, String itemStatus, String currentTime) {
        this.itemID = itemID;
        this.itemTitle = itemTitle;
        this.itemDescription = itemDescription;
        this.itemDueDate = itemDueDate;
        this.itemStatus = itemStatus;
        this.currentTime = currentTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getItemID() {
        return itemID;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemDueDate() {
        return itemDueDate;
    }

    public String getItemStatus() {
        return itemStatus;
    }

    public String getCurrentTime() {
        return currentTime;
    }
}
