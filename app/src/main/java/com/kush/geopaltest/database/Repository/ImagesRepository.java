package com.kush.geopaltest.database.Repository;

import android.content.Context;
import android.os.AsyncTask;

import com.kush.geopaltest.database.DAO.ImagesDAO;
import com.kush.geopaltest.database.Entity.ImagesEntity;
import com.kush.geopaltest.database.Entity.ItemsEntity;
import com.kush.geopaltest.database.RoomDatabase.ItemsRoomDB;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ImagesRepository {

    private ImagesDAO imagesDAO;

    public ImagesRepository(Context context) {
        ItemsRoomDB itemsRoomDB = ItemsRoomDB.getTodoRoomDB(context);
        imagesDAO = itemsRoomDB.imagesDAO();
    }

    // Insert Images
    public void insertImagesDetails(ImagesEntity imagesEntity) {
        new InsertingImages(imagesEntity).execute();

    }

    private class InsertingImages extends AsyncTask<Void, Void, Void> {
        ImagesEntity imagesEntity;
        public InsertingImages(ImagesEntity imagesEntity) {
            this.imagesEntity = imagesEntity;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            imagesDAO.insertImages(imagesEntity);
            return null;
        }
    }

    // get image per itemId
    public List<ImagesEntity> getImagePerItemID(String itemID) throws ExecutionException, InterruptedException {
        return new GettingImagePerItem(itemID).execute().get();
    }

    private class GettingImagePerItem  extends AsyncTask<Void, Void, List<ImagesEntity>> {

        String mItemID;
        public GettingImagePerItem(String itemID) {
            this.mItemID = itemID;
        }

        @Override
        protected List<ImagesEntity> doInBackground(Void... voids) {
            return imagesDAO.getImageForItem(mItemID);
        }
    }

    // get count of image per itemId
    public int getImageCount(String itemID) throws ExecutionException, InterruptedException {
        return new GettingImageCount(itemID).execute().get();
    }

    private class GettingImageCount  extends AsyncTask<Void, Void, Integer> {

        String mItemID;
        public GettingImageCount(String itemID) {
            this.mItemID = itemID;
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            return imagesDAO.numberOfImagesForItem(mItemID);
        }
    }

    // delete an image from list
    public void deleteAImageFromList(int itemID) {
        new DeleteImage(itemID).execute();
    }

    private class DeleteImage extends AsyncTask<Void, Void, Void> {

        int mItemID;
        public DeleteImage(int itemID) {
            this.mItemID = itemID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            imagesDAO.deleteImagePerSwipe(mItemID);
            return null;
        }
    }

    // delete an image when item card removed
    public void deleteImageWhenCardRemoved(String itemID) {
        new DeleteImageWithCard(itemID).execute();
    }

    private class DeleteImageWithCard extends AsyncTask<Void, Void, Void> {

        String mItemID;
        public DeleteImageWithCard(String itemID) {
            this.mItemID = itemID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            imagesDAO.deleteImagePerItem(mItemID);
            return null;
        }
    }

}
