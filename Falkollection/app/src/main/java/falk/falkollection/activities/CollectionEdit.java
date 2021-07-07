package falk.falkollection.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

import falk.falkollection.R;
import falk.falkollection.database.DBSQLiteHelper;
import falk.falkollection.model.FCollection;

public class CollectionEdit extends AppCompatActivity {
    private final int GALLERY = 1;
    private File pictureFile = null;
    private Bitmap bitmap = null;
    private FCollection editingCollection;
    private DBSQLiteHelper dbHelper;
    private static final int CAMERA_REQUEST = 1888;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_edit);
        editingCollection=  new FCollection();
        dbHelper = new DBSQLiteHelper(this);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("ID", 0);
        final boolean isReadOnly = intent.getBooleanExtra("readOnly",false);

        EditText nameField = findViewById(R.id.editTextName);
        LinearLayout volLayout = findViewById(R.id.volumelayout);
        EditText volumesField = findViewById(R.id.editTextVolumeNo);
        CheckBox uniqueCheckbox = findViewById(R.id.uniqueVolCheckBox);
        ImageButton selectCamera = findViewById(R.id.selectCamera);
        ImageButton selectGallery = findViewById(R.id.selectGallery);
        ImageButton resetImage = findViewById(R.id.resetImage);
        ImageView imageView = findViewById(R.id.imageView_CollectionCover);
        FloatingActionButton saveButton = findViewById(R.id.saveButton);
        //FloatingActionButton viewCollectionButton = findViewById(R.id.viewCollectionButton);

        if(isReadOnly)
        {
            nameField.setEnabled(false);
            volumesField.setEnabled(false);
            selectCamera.setEnabled(false);
            selectGallery.setEnabled(false);
            resetImage.setEnabled(false);
            uniqueCheckbox.setEnabled(false);
            saveButton.setVisibility(View.INVISIBLE);
            //viewCollectionButton.setVisibility(View.VISIBLE);
        }

        if(id!=0)
        {
            editingCollection = dbHelper.getCollection(id);
            nameField.setText(editingCollection.getName());

            volumesField.setText(String.valueOf(editingCollection.getVolumesTotal()));
            uniqueCheckbox.setChecked(editingCollection.getVolumesTotal() == 1);

            if(isReadOnly)
            {
                if(editingCollection.getVolumesTotal()>1)
                uniqueCheckbox.setVisibility(View.INVISIBLE);
                else
                    volLayout.setVisibility(View.VISIBLE);
            }
            setVolumesFieldsVisibility();
            bitmap = editingCollection.getImage();

            if(bitmap != null)
            {
                imageView.setImageBitmap(bitmap);
            }
            else
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icons8_purple_book_48));

        }
    }

    public void resetImage(View view){
        bitmap =null;
        ImageView imageView = findViewById(R.id.imageView_CollectionCover);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icons8_purple_book_48));
    }


    public void pictureSelect(View view){
        Intent intent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, GALLERY);
    }



    public void pictureSelectCamera(View view){
        Intent cameraIntent = new  Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    private void loadPicture(String filepath) {
        bitmap = BitmapFactory.decodeFile(filepath);
        ImageView imageView = findViewById(R.id.imageView_CollectionCover);
        imageView.setImageBitmap(bitmap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == GALLERY) {
            Uri selectedImage = data.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null,
                    null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            pictureFile = new File(picturePath);
            loadPicture(pictureFile.getAbsolutePath());
        }

        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            Bitmap picture = (Bitmap) data.getExtras().get("data");//this is your bitmap image and now you can do whatever you want with this

            bitmap = picture;
            ImageView imageView = findViewById(R.id.imageView_CollectionCover);
            imageView.setImageBitmap(bitmap);
        }


    }

    public void setVolumesFieldsVisibility()
    {
        CheckBox uniqueCheckbox = findViewById(R.id.uniqueVolCheckBox);
        LinearLayout volLayout = findViewById(R.id.volumelayout);

        int visible = uniqueCheckbox.isChecked() ? View.INVISIBLE :View.VISIBLE;

        volLayout.setVisibility(visible);
    }


    public void uniqueVolCheck(View view)
    {
        setVolumesFieldsVisibility();
    }

    public void btnSaveClick(View view){
        EditText nameField = findViewById(R.id.editTextName);
        EditText volumeQTField = findViewById(R.id.editTextVolumeNo);
        CheckBox uniqueCheckbox = findViewById(R.id.uniqueVolCheckBox);
        String nameFieldContent = nameField.getText().toString();
        String volumeQTFieldContent = volumeQTField.getText().toString();

        if(nameFieldContent.isEmpty())
        {
            nameField.setError(getString(R.string.fieldValidation));
            return;
        }

        if(volumeQTFieldContent.isEmpty() && !uniqueCheckbox.isChecked())
        {
            volumeQTField.setError(getString(R.string.fieldValidation));
            return;
        }

        editingCollection.setName(nameFieldContent);

        int volumestotal = uniqueCheckbox.isChecked() ? 1 :Integer.parseInt(volumeQTFieldContent);

        editingCollection.setVolumesTotal(volumestotal);
        editingCollection.setImage(bitmap);

        if (editingCollection.getId() != 0)
            dbHelper.updateCollection(editingCollection);
        else
            dbHelper.addCollection(editingCollection);

        this.finish();
    }

    public void btnViewCollection(View view){
        Intent intent = new Intent(getBaseContext(), CollectionItemsActivity.class);
        intent.putExtra("CollectionID", editingCollection.getId());
        startActivity(intent);
    }



}