package com.kush.geopaltest.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.snackbar.Snackbar;
import com.kush.geopaltest.R;
import com.kush.geopaltest.adapters.ItemsRVAdapter;
import com.kush.geopaltest.database.Entity.ItemsEntity;
import com.kush.geopaltest.database.Repository.ImagesRepository;
import com.kush.geopaltest.database.Repository.ItemsRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mItemsRV;
    private SwipeRefreshLayout mSwipeList;
    private List<ItemsEntity> mAllItems;
    private ItemsRepository mItemsRepository;
    private ItemsRVAdapter mItemsRVAdapter;
    private ImagesRepository mImagesRepository;
    private ImageView noItemFoundImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mItemsRV   = findViewById(R.id.itemsRV);
        mSwipeList = findViewById(R.id.swipeItemsToRefresh);
        noItemFoundImage = findViewById(R.id.no_item_found_image);

        mSwipeList.setOnRefreshListener(this::getItemsIfAny);
        getItemsIfAny();
    }

    private void getItemsIfAny() {
        mItemsRepository  = new ItemsRepository(MainActivity.this);
        mImagesRepository = new ImagesRepository(MainActivity.this);
        try {
            int mNumberOfItems = mItemsRepository.getCountOfAllItems();
            if (mNumberOfItems > 0) {
                noItemFoundImage.setVisibility(View.GONE);
                mItemsRV.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                mAllItems = mItemsRepository.getAllItemsDetails();
                mItemsRVAdapter = new ItemsRVAdapter(MainActivity.this, mAllItems, mImagesRepository);

                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallBack);
                itemTouchHelper.attachToRecyclerView(mItemsRV);

                mItemsRV.setAdapter(mItemsRVAdapter);
            } else {
                noItemFoundImage.setVisibility(View.VISIBLE);
            }

            if (mSwipeList.isRefreshing()) {
                mSwipeList.setRefreshing(false);
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    ItemTouchHelper.SimpleCallback simpleCallBack = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position  = viewHolder.getAdapterPosition();
            String itemID = mAllItems.get(position).getItemID();

            View parentLayout = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(parentLayout, "Item Deleted",
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", view -> {
                mItemsRV.scrollToPosition(position);
                mItemsRVAdapter.notifyDataSetChanged();
            });
            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        mAllItems.remove(position);
                        mItemsRepository.deleteAItemFromList(itemID);
                        mImagesRepository.deleteImageWhenCardRemoved(itemID);
                        mItemsRVAdapter.notifyDataSetChanged();
                    }
                    try {
                        if (!(mItemsRepository.getCountOfAllItems() > 0)) {
                            noItemFoundImage.setVisibility(View.VISIBLE);
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_main_activity_options_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_item_id) {
            startActivity(new Intent(MainActivity.this, AddTodoItem.class));
        }
        return super.onOptionsItemSelected(item);
    }
}