package com.kush.geopaltest.ui;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.kush.geopaltest.R;
import com.kush.geopaltest.adapters.ImagesRVAdapter;
import com.kush.geopaltest.database.Entity.ImagesEntity;
import com.kush.geopaltest.database.Entity.ItemsEntity;
import com.kush.geopaltest.database.Repository.ImagesRepository;
import com.kush.geopaltest.database.Repository.ItemsRepository;
import com.kush.geopaltest.enums.Status;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class AddTodoItem extends AppCompatActivity {

    EditText title, description, status;
    TextView itemID, dueDate;
    Button addImage;
    private InputMethodManager mInputMethodManager;
    private Calendar mCalendar;
    private String mSavedPhotoPath;
    private String mImagePath;
    private RecyclerView mImagesRV;
    private List<ImagesEntity> mAllImages;
    private ImagesRepository mImagesRepository;
    private ImagesRVAdapter mImagesRVAdapter;

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private static final int CAMERA_REQUEST_CODE = 110;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo_item);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mImagesRepository = new ImagesRepository(AddTodoItem.this);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        bindingAllViewComponents();

        Intent getIntent = getIntent();
        String itemIDFromIntent = getIntent.getStringExtra("ItemID");
        if (itemIDFromIntent != null) {
            populateAllFields(itemIDFromIntent);
            settingUpImageRecyclerAdapter();
        } else {
            Random generator = new Random();
            String mUniqueItemID = String.valueOf(generator.nextInt(96) + 32);
            itemID.setText(mUniqueItemID);
        }
        addImage.setOnClickListener(view -> takePhotoFromCameraOrGallery());
    }

    private void populateAllFields(String itemIDFromIntent) {
        ItemsRepository mItemsRepository = new ItemsRepository(AddTodoItem.this);
        try {
            List<ItemsEntity> mAllItems = mItemsRepository.getItemsDetailsPerID(itemIDFromIntent);
            for (ItemsEntity item : mAllItems) {
                itemID.setText(item.getItemID());
                title.setText(item.getItemTitle());
                description.setText(item.getItemDescription());
                dueDate.setText(item.getItemDueDate());
                status.setText(item.getItemStatus());
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Camera permission granted.", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Camera permission denied.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void takePhotoFromCameraOrGallery() {
        if (ContextCompat.checkSelfPermission(AddTodoItem.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(AddTodoItem.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(AddTodoItem.this,
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CAMERA_REQUEST_CODE);

        }
        openCameraOrGallery();
    }

    private void openCameraOrGallery() {
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(AddTodoItem.this);
        pictureDialog.setTitle(R.string.select_action);
        pictureDialog.setIcon(R.drawable.camera);
        String[] pictureDialogItems = {
                getString(R.string.capture_new_image),
                getString(R.string.image_from_gallery)
        };
        pictureDialog.setItems(pictureDialogItems, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    try {
                        takePhotoFromCamera();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 1:
                    choosePhotoFromGallery();
                    break;
            }
        });
        pictureDialog.show();
    }

    private void bindingAllViewComponents() {
        itemID      = findViewById(R.id.item_ID);
        title       = findViewById(R.id.add_item_title);
        description = findViewById(R.id.add_item_description);
        dueDate     = findViewById(R.id.add_item_due_date);
        status      = findViewById(R.id.add_item_status);

        addImage    = findViewById(R.id.add_item_add_images);
        mImagesRV = findViewById(R.id.add_item_images_RV);
    }

    public void addTitle(View view) {
        settingKeyboard(title, mInputMethodManager);
    }

    public void addDescription(View view) {
    }

    public void addDueDate(View view) {
        mCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, year, monthOfYear, dayOfMonth) -> {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, monthOfYear);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
        };

        new DatePickerDialog(AddTodoItem.this, dateSetListener, mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateLabel() {
        String myFormat = getString(R.string.dateTimeFormat);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        dueDate.setText(sdf.format(mCalendar.getTime()));
    }

    public void addStatus(View view) {
        status.setInputType(InputType.TYPE_NULL);
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AddTodoItem.this);
        alertBuilder.setIcon(R.drawable.status_icon);
        alertBuilder.setTitle(R.string.select_status);

        List<Status> statusList = Arrays.asList(Status.values());
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(AddTodoItem.this, android.R.layout.simple_list_item_single_choice);
        for (Status list: statusList) {
            arrayAdapter.add(list.toString());
        }

        alertBuilder.setAdapter(arrayAdapter, (dialogInterface, i) -> {
            String selectedItem = arrayAdapter.getItem(i);
            status.setText(selectedItem);
        });

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();
    }

    private static void settingKeyboard(EditText editText, InputMethodManager mInputMethodManager) {
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        editText.requestFocus();
        if(mInputMethodManager != null) {
            mInputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void submitBtnClicked(View view) {
        ItemsRepository itemsRepository = new ItemsRepository(AddTodoItem.this);
        Calendar calendar = Calendar.getInstance();

        String myFormat = getString(R.string.dateTimeFormat);
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        String currentTime = sdf.format(calendar.getTime());

        if (validateFields()) {
            try {
                int itemAlreadyExist = itemsRepository
                        .checkingIfItemAlreadyExist(itemID.getText().toString());
                if (itemAlreadyExist > 0) {
                    itemsRepository.updateItemsDetails(itemID.getText().toString(),
                            title.getText().toString(), description.getText().toString(),
                            dueDate.getText().toString(), status.getText().toString(), currentTime);
                } else {
                    itemsRepository.insertItemsDetails(new ItemsEntity(itemID.getText().toString(),
                            title.getText().toString(), description.getText().toString(),
                            dueDate.getText().toString(), status.getText().toString(), currentTime));
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            startActivity(new Intent(AddTodoItem.this, MainActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    final Uri imageURI;
                    if (mImagePath != null) {
                        imageURI = Uri.parse(mImagePath);
                    } else {
                        imageURI = Uri.parse(mSavedPhotoPath);
                    }
                    new File(imageURI.getPath());
                    try {
                        Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                imageURI);
                        photo        = resize(photo, 1024, 786);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        photo.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] imageInBytes = stream.toByteArray();

                        alertDialogToGetImageDescriptionAndSaveLocally(imageInBytes);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case PICK_IMAGE_REQUEST:
                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        try {
                            Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(),
                                    selectedImage);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            int newSize = (int) (photo.getHeight() * (512.0 / photo.getWidth()));
                            Bitmap scaledImage = Bitmap.createScaledBitmap(photo, 512, newSize, true);
                            scaledImage.compress(Bitmap.CompressFormat.JPEG, 100, stream);

                            final byte[] imageInBytes = stream.toByteArray();

                            alertDialogToGetImageDescriptionAndSaveLocally(imageInBytes);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }

    private void alertDialogToGetImageDescriptionAndSaveLocally(byte[] imageInBytes) {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddTodoItem.this);
        builder.setTitle("Please describe the image");
        builder.setIcon(android.R.drawable.ic_menu_info_details);
        builder.setCancelable(false);
        EditText editText = new EditText(AddTodoItem.this);
        builder.setView(editText);
        editText.setHint(R.string.optional_desciption);

        builder.setPositiveButton(R.string.okay, (dialogInterface, i) -> {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }

            String imageDescription = editText.getText().toString();
            String timeStamp = (String) android.text.format.DateFormat
                    .format(getString(R.string.dateTimeFormat), new java.util.Date());

            if (imageDescription.isEmpty()) {
                imageDescription = getString(R.string.timestamp) + timeStamp;
            } else {
                imageDescription = getString(R.string.timestamp) + timeStamp + "__" +
                        getString(R.string.notes) + imageDescription;
            }

            mImagesRepository.insertImagesDetails(new ImagesEntity(itemID.getText().toString(),
                    imageInBytes, imageDescription));

            settingUpImageRecyclerAdapter();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.show();
    }

    private void settingUpImageRecyclerAdapter() {
        try {
            mAllImages = mImagesRepository.getImagePerItemID(itemID.getText().toString());
            Collections.reverse(mAllImages);

            mImagesRV.setLayoutManager(new LinearLayoutManager(AddTodoItem.this));
            mImagesRVAdapter = new ImagesRVAdapter(AddTodoItem.this, mAllImages);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallBack);
            itemTouchHelper.attachToRecyclerView(mImagesRV);

            mImagesRV.setAdapter(mImagesRVAdapter);

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
            int position = viewHolder.getAdapterPosition();
            int imageID = mAllImages.get(position).getImageIDPK();

            View parentLayout = findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar.make(parentLayout, R.string.image_deleted,
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", view -> {
                mImagesRV.scrollToPosition(position);
                mImagesRVAdapter.notifyDataSetChanged();
            });
            snackbar.addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    if (event == Snackbar.Callback.DISMISS_EVENT_TIMEOUT) {
                        mAllImages.remove(position);
                        mImagesRepository.deleteAImageFromList(imageID);
                        mImagesRVAdapter.notifyDataSetChanged();
                    }
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    };

    private Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width           = image.getWidth();
            int height          = image.getHeight();
            float ratioBitmap   = (float) width / (float) height;
            float ratioMax      = (float) maxWidth / (float) maxHeight;

            int finalWidth      = maxWidth;
            int finalHeight     = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth      = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight     = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        }
        return image;
    }

    private void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, PICK_IMAGE_REQUEST);
    }

    private void takePhotoFromCamera() throws IOException {
        File photoFile = createImageFile();
        Uri mImageURI = FileProvider.getUriForFile(AddTodoItem.this,
                getApplicationContext().getPackageName() + ".provider", photoFile);
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageURI);
        openCameraIntent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        startActivityForResult(openCameraIntent, REQUEST_TAKE_PHOTO);
    }

    private File createImageFile() throws IOException {
        String timeStamp     = new SimpleDateFormat("yyyyMMdd HHmmss", Locale.ENGLISH)
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File dir             = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image           = File.createTempFile(
                imageFileName,
                ".jpg",
                dir
        );
        mImagePath           = "file:" + image.getAbsolutePath();
        return image;
    }

    private boolean validateFields() {
        boolean isTitleEmpty        = title.getText().toString().equals("");
        boolean isDescriptionEmpty  = description.getText().toString().equals("");
        boolean isDueDateEmpty      = dueDate.getText().toString().equals("");
        boolean isStatusEmpty       = status.getText().toString().equals("");

        if (isTitleEmpty) {
            title.setError("Can't be Empty");
        }
        if (isDescriptionEmpty) {
            description.setError("Can't be Empty");
        }
        if (isDueDateEmpty) {
            dueDate.setError("Can't be Empty");
        }
        if (isStatusEmpty) {
            status.setError("Can't be Empty");
        }

        return !isTitleEmpty && !isDescriptionEmpty && !isDueDateEmpty && !isStatusEmpty;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /*
    * To close the keyboard when user click outside edit-text boxes
    * */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        boolean handleReturn = super.dispatchTouchEvent(ev);

        View view = getCurrentFocus();

        int x = (int) ev.getX();
        int y = (int) ev.getY();

        if(view instanceof EditText){
            View innerView = getCurrentFocus();

            if (ev.getAction() == MotionEvent.ACTION_UP && !getLocationOnScreen((EditText) innerView).contains(x, y)) {

                InputMethodManager input = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);
                if (input != null) {
                    input.hideSoftInputFromWindow(getWindow().getCurrentFocus()
                            .getWindowToken(), 0);
                }
            }
        }
        return handleReturn;
    }

    protected Rect getLocationOnScreen(EditText mEditText) {
        Rect mRect = new Rect();
        int[] location = new int[2];

        mEditText.getLocationOnScreen(location);

        mRect.left   = location[0];
        mRect.top    = location[1];
        mRect.right  = location[0] + mEditText.getWidth();
        mRect.bottom = location[1] + mEditText.getHeight();

        return mRect;
    }

}