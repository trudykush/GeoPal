package com.kush.geopaltest.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.kush.geopaltest.R;
import com.kush.geopaltest.database.Entity.ItemsEntity;
import com.kush.geopaltest.database.Repository.ImagesRepository;
import com.kush.geopaltest.enums.Status;
import com.kush.geopaltest.ui.AddTodoItem;
import com.kush.geopaltest.ui.MainActivity;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class ItemsRVAdapter extends RecyclerView.Adapter<ItemsRVAdapter.MyViewHolder> {

    Context mContext;
    List<ItemsEntity> mAllItems;
    ImagesRepository mImagesRepository;
    public ItemsRVAdapter(Context context, List<ItemsEntity> allItems, ImagesRepository imagesRepository) {
        this.mContext = context;
        this.mAllItems = allItems;
        this.mImagesRepository = imagesRepository;
    }

    @NonNull
    @Override
    public ItemsRVAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.to_do_list, parent, false);
        return new ItemsRVAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsRVAdapter.MyViewHolder holder, int position) {

        holder.status.setText(mAllItems.get(position).getItemStatus());
        holder.title.setText(mAllItems.get(position).getItemTitle());
        holder.dueDate.setText(mAllItems.get(position).getItemDueDate());
        holder.lastEdited.setText(String.format("Last Updated: %s", mAllItems.get(position).getCurrentTime()));

        cardViewColorCode(mAllItems.get(position).getItemStatus(), holder);

        try {
            int numberOfAttachment = mImagesRepository.getImageCount(mAllItems.get(position).getItemID());
            holder.anyAttachments.setText(numberOfAttachment + " attachments");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void cardViewColorCode(String itemStatus, MyViewHolder holder) {
        if (itemStatus.equals(Status.Open.toString())) {
            holder.cardViewBorder.setBackgroundColor(Color.GREEN);
        } else if (itemStatus.equals(Status.InProgress.toString())) {
            holder.cardViewBorder.setBackgroundColor(Color.YELLOW);
        } else if (itemStatus.equals(Status.Paused.toString())) {
            holder.cardViewBorder.setBackgroundColor(Color.BLACK);
        } else if (itemStatus.equals(Status.Completed.toString())) {
            holder.cardViewBorder.setBackgroundColor(Color.BLUE);
        } else if (itemStatus.equals(Status.Closed.toString())) {
            holder.cardViewBorder.setBackgroundColor(Color.LTGRAY);
        }
    }

    @Override
    public int getItemCount() {
        if (mAllItems != null && mAllItems.size() != 0) {
            return mAllItems.size();
        }
        return mAllItems != null ? mAllItems.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

        TextView status, title, dueDate, anyAttachments, lastEdited;
        CardView itemCV;
        View cardViewBorder;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCV = itemView.findViewById(R.id.todo_cardView);
            status = itemView.findViewById(R.id.todo_status);
            title = itemView.findViewById(R.id.todo_title);
            dueDate = itemView.findViewById(R.id.todo_dueDate);
            anyAttachments = itemView.findViewById(R.id.todo_any_attachment);
            lastEdited = itemView.findViewById(R.id.todo_last_edit);
            cardViewBorder = itemView.findViewById(R.id.cardViewColor);

            itemCV.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (view.getId() == itemCV.getId()) {
                int cardPosition = getAdapterPosition();
                Intent goToUpdateItem = new Intent(mContext, AddTodoItem.class);
                goToUpdateItem.putExtra("ItemID", mAllItems.get(cardPosition).getItemID());
                view.getContext().startActivity(goToUpdateItem);
            }
        }
    }
}
