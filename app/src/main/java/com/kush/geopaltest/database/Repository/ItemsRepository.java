package com.kush.geopaltest.database.Repository;

import android.content.Context;
import android.os.AsyncTask;

import com.kush.geopaltest.database.DAO.ItemsDAO;
import com.kush.geopaltest.database.Entity.ImagesEntity;
import com.kush.geopaltest.database.Entity.ItemsEntity;
import com.kush.geopaltest.database.RoomDatabase.ItemsRoomDB;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ItemsRepository {

    private ItemsDAO itemsDAO;

    public ItemsRepository(Context context) {
        ItemsRoomDB itemsRoomDB = ItemsRoomDB.getTodoRoomDB(context);
        itemsDAO = itemsRoomDB.itemsDAO();
    }

    // Insert items
    public void insertItemsDetails(ItemsEntity itemEntity) {
        new InsertingItems(itemEntity).execute();

    }

    private class InsertingItems extends AsyncTask<Void, Void, Void> {
        ItemsEntity itemEntity;
        public InsertingItems(ItemsEntity itemEntity) {
            this.itemEntity = itemEntity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            itemsDAO.insertItemsDetails(itemEntity);
            return null;
        }
    }

    // update items
    public void updateItemsDetails(String itemID, String title, String description, String dueDate,
                                   String status, String currentTime) {
        new UpdatingItems(itemID, title, description, dueDate, status, currentTime).execute();
    }

    private class UpdatingItems extends AsyncTask<Void, Void, Void> {

        String itemID, title, description;
        String dueDate, status, currentTime;
        public UpdatingItems(String itemID, String title, String description,
                             String dueDate, String status, String currentTime) {
            this.itemID = itemID;
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
            this.status = status;
            this.currentTime = currentTime;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            itemsDAO.updateTodoList(itemID, title, description, dueDate, status, currentTime);
            return null;
        }
    }

    // get all items
    public List<ItemsEntity> getAllItemsDetails() throws ExecutionException, InterruptedException {
        return new GettingAllDetails().execute().get();
    }

    private class GettingAllDetails  extends AsyncTask<Void, Void, List<ItemsEntity>> {

        @Override
        protected List<ItemsEntity> doInBackground(Void... voids) {
            return itemsDAO.getAllTodoList();
        }
    }

    // get item per id
    public List<ItemsEntity> getItemsDetailsPerID(String itemID) throws ExecutionException, InterruptedException {
        return new GettingItemDetails(itemID).execute().get();
    }

    private class GettingItemDetails  extends AsyncTask<Void, Void, List<ItemsEntity>> {

        String mItemID;
        public GettingItemDetails(String itemID) {
            this.mItemID = itemID;
        }

        @Override
        protected List<ItemsEntity> doInBackground(Void... voids) {
            return itemsDAO.getItemListPerID(mItemID);
        }
    }

    // get count of all items
    public int getCountOfAllItems() throws ExecutionException, InterruptedException {
        return new GettingCount().execute().get();
    }

    private class GettingCount extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            return itemsDAO.countNumberOFTodoItems();
        }
    }

    // check whether item already exist
    public int checkingIfItemAlreadyExist(String itemID) throws ExecutionException, InterruptedException {
        return new CheckingItemExist(itemID).execute().get();
    }

    private class CheckingItemExist extends AsyncTask<Void, Void, Integer> {

        String itemID;
        public CheckingItemExist(String itemID) {
            this.itemID = itemID;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return itemsDAO.whetherIDAlreadyExist(itemID);
        }
    }

    // delete an item from list
    public void deleteAItemFromList(String itemID) {
        new DeleteItem(itemID).execute();
    }

    private class DeleteItem extends AsyncTask<Void, Void, Void> {

        String mItemID;
        public DeleteItem(String itemID) {
            this.mItemID = itemID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            itemsDAO.deleteATodoItem(mItemID);
            return null;
        }
    }
}
