package com.kush.geopaltest.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kush.geopaltest.R;
import com.kush.geopaltest.database.Entity.ImagesEntity;

import java.util.List;

public class ImagesRVAdapter extends RecyclerView.Adapter<ImagesRVAdapter.MyViewHolder> {

    Context mContext;
    List<ImagesEntity> mAllImages;
    public ImagesRVAdapter(Context context, List<ImagesEntity> allImages) {
        this.mContext = context;
        this.mAllImages = allImages;
    }

    @NonNull
    @Override
    public ImagesRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.list_image_attachment, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagesRVAdapter.MyViewHolder holder, int position) {
        byte[] imageToDisplay = mAllImages.get(position).getItemImage();
        holder.mImageView.setImageBitmap(convertToBitmap(imageToDisplay));
        holder.mImageDescription.setText(mAllImages.get(position).getImageDescription());
    }

    private Bitmap convertToBitmap(byte[] b){
        return BitmapFactory.decodeByteArray(b, 0, b.length);
    }

    @Override
    public int getItemCount() {
        if (mAllImages != null && mAllImages.size() != 0) {
            return mAllImages.size();
        }
        return mAllImages != null ? mAllImages.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mImageView;
        TextView mImageDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mImageView        = itemView.findViewById(R.id.imageView);
            mImageDescription = itemView.findViewById(R.id.imageDescription);
        }
    }
}

