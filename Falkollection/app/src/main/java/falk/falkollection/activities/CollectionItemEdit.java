package falk.falkollection.activities;

import androidx.appcompat.app.AppCompatActivity;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;

import falk.falkollection.R;
import falk.falkollection.database.DBSQLiteHelper;
import falk.falkollection.model.CollectionItem;
import falk.falkollection.model.ISBNTitleBitmap;
import falk.falkollection.util.ISBNDataRetriever;

public class CollectionItemEdit extends AppCompatActivity {
    private final int GALLERY = 1;
    private final int SCANNER = 49374;
    private File pictureFile = null;
    private Bitmap bitmap = null;
    private int collectionID = 0;
    private CollectionItem editingCollectionItem;
    private DBSQLiteHelper dbHelper;
    private static final int CAMERA_REQUEST = 1888;

    Runnable runnableISBN = new Runnable() {
        @Override
        public void run() {
            try
            {
                loadDataFromISBN();
            }
            catch (Exception ex)
            { }
        }
    };

    Runnable progressBarVisible = new Runnable() {
        @Override
        public void run() {
            try
            {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

            }
            catch (Exception ex)
            { }
        }
    };

    Runnable progressBarInvisible = new Runnable() {
        @Override
        public void run() {
            try
            {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

            }
            catch (Exception ex)
            { }
        }
    };

    TextWatcher isbnTextWatcher =  new TextWatcher() {

        @Override
        public void afterTextChanged(Editable s) {

        }

        @Override
        public void beforeTextChanged(CharSequence s, int start,
        int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start,
        int before, int count) {
            if(s.length() == 13 || s.length() ==10)
            {
                new Thread(runnableISBN).start();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_item_edit);
        editingCollectionItem=  new CollectionItem();
        dbHelper = new DBSQLiteHelper(this);

        Intent intent = getIntent();
        final int id = intent.getIntExtra("id", 0);
        final int CollectionID = intent.getIntExtra("CollectionID", 0);
        collectionID = CollectionID;
        final boolean isReadOnly = intent.getBooleanExtra("readOnly",false);

        EditText nameField = findViewById(R.id.editTitleName);
        EditText volumesField = findViewById(R.id.editTextVolumeNo);
        CheckBox obtainedCheckBox = findViewById(R.id.checkBox);

        ImageButton scanButton = findViewById(R.id.btScan);
        ImageView imageView = findViewById(R.id.imageView_CollectionItemCover);
        FloatingActionButton saveButton = findViewById(R.id.saveButton);

        EditText ISBNfield = findViewById(R.id.editTextISBN);



        if(isReadOnly)
        {
            nameField.setEnabled(false);
            volumesField.setEnabled(false);

            obtainedCheckBox.setEnabled(false);
            saveButton.setVisibility(View.INVISIBLE);
            scanButton.setVisibility(View.INVISIBLE);
        }

        if(id!=0)
        {
            editingCollectionItem = dbHelper.getCollectionItem(id);
            nameField.setText(editingCollectionItem.getTitle());
            bitmap = editingCollectionItem.getImage();
            if(bitmap != null)
                imageView.setImageBitmap(bitmap);
            else
                imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icons8_purple_book_482));
            obtainedCheckBox.setChecked(editingCollectionItem.getOwned());
            volumesField.setText(String.valueOf(editingCollectionItem.getVolumeNo()));
            if(editingCollectionItem.getIsbn() != 0)
                ISBNfield.setText(String.valueOf(editingCollectionItem.getIsbn()));

            setTitle("Editar Item");
        }
        else
            setTitle("Novo Item");
        ISBNfield.addTextChangedListener(isbnTextWatcher);

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

    public void resetImage(View view){
        bitmap =null;
        ImageView imageView = findViewById(R.id.imageView_CollectionItemCover);
        imageView.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.icons8_purple_book_482));
    }

    public void getParentTitle(View view)
    {
        EditText nameField = findViewById(R.id.editTitleName);
        nameField.setText(dbHelper.getCollection(collectionID).getName());
        EditText vol = findViewById(R.id.editTextVolumeNo);
        vol.requestFocus();
    }

    private void loadPicture(String filepath) {
        bitmap = BitmapFactory.decodeFile(filepath);
        ImageView imageView = findViewById(R.id.imageView_CollectionItemCover);
        imageView.setImageBitmap(bitmap);
    }


    public void loadDataFromISBN()
    {
        runOnUiThread(progressBarVisible);

        EditText ISBNField = findViewById(R.id.editTextISBN);

        long ISBN = Long.parseLong(ISBNField.getText().toString());

        final ISBNTitleBitmap isbnTitleBitmap =  ISBNDataRetriever.getTitleAndImageFromISBN(ISBN);

        Runnable runnableISBNFieldUpdate = new Runnable() {
            @Override
            public void run() {
                try
                {
                    loadFieldsWithIBSNdata(isbnTitleBitmap);

                }
                catch (Exception ex)
                { }
            }
        };
        runOnUiThread(runnableISBNFieldUpdate);
        runOnUiThread(progressBarInvisible);
    }

    public void loadFieldsWithIBSNdata(ISBNTitleBitmap isbnTitleBitmap)
    {

        if(isbnTitleBitmap != null)
        {
            if(isbnTitleBitmap.getBitmap() !=null)
            {
                Bitmap newBitmap = isbnTitleBitmap.getBitmap();
                ImageView imageView = findViewById(R.id.imageView_CollectionItemCover);
                if(bitmap != null)
                {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    bitmap = newBitmap;
                                    imageView.setImageBitmap(newBitmap);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(CollectionItemEdit.this);
                    builder.setMessage("Substituir a imagem com a buscada via ISBN?").setPositiveButton("Sim", dialogClickListener)
                            .setNegativeButton("Não", dialogClickListener).show();

                }
                else
                    {
                        bitmap = newBitmap;
                        imageView.setImageBitmap(newBitmap);
                    }

            }
            if(isbnTitleBitmap.getTitle() !=null && !isbnTitleBitmap.getTitle().isEmpty())
            {
                EditText field = findViewById(R.id.editTitleName);
                if(!field.getText().toString().isEmpty()) {


                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    EditText nameField = findViewById(R.id.editTitleName);
                                    nameField.setText(isbnTitleBitmap.getTitle());
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:

                                    break;
                            }
                        }
                    };


                    AlertDialog.Builder builder = new AlertDialog.Builder(CollectionItemEdit.this);
                    builder.setMessage("Substituir pelo título a seguir?\n" + isbnTitleBitmap.getTitle()).setPositiveButton("Sim", dialogClickListener)
                            .setNegativeButton("Não", dialogClickListener).show();
                }
                else
                {
                    EditText nameField = findViewById(R.id.editTitleName);
                    nameField.setText(isbnTitleBitmap.getTitle());
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult intentResult =  IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if (resultCode == RESULT_OK && requestCode == SCANNER)
        {
            if(intentResult.getContents() != null)
            {
                String isbnStr = intentResult.getContents();
                EditText ISBNField = findViewById(R.id.editTextISBN);
                ISBNField.setText(isbnStr);
            }
        }

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
            ImageView imageView = findViewById(R.id.imageView_CollectionItemCover);
            imageView.setImageBitmap(bitmap);
        }
    }


    public void btnScanClick(View view)
    {
        IntentIntegrator intentIntegrator =  new IntentIntegrator(this);
        intentIntegrator.setPrompt("Escaneie o código de barras do Livro/Mangá");
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setOrientationLocked(true);
        intentIntegrator.setCaptureActivity(CameraScannerActivity.class);
        intentIntegrator.initiateScan();
    }

    public void btnSaveClick(View view){
        EditText nameField = findViewById(R.id.editTitleName);
        EditText volumeField = findViewById(R.id.editTextVolumeNo);
        EditText isbnField = findViewById(R.id.editTextISBN);
        CheckBox checkBox = findViewById(R.id.checkBox);
        String nameFieldContent = nameField.getText().toString();
        String volumeFieldContent = volumeField.getText().toString();
        String isbnFieldContent = isbnField.getText().toString();
        if(nameFieldContent.isEmpty())
        {
            nameField.setError(getString(R.string.fieldValidation));
            return;
        }

        editingCollectionItem.setTitle(nameFieldContent);
        if(!volumeFieldContent.isEmpty())
        {
            editingCollectionItem.setVolumeNo(Integer.parseInt(volumeFieldContent));
        }
        if(!isbnFieldContent.isEmpty())
            editingCollectionItem.setIsbn(Long.parseLong(isbnFieldContent));
        editingCollectionItem.setOwned(checkBox.isChecked());
        editingCollectionItem.setCollectionId(collectionID);
        if(bitmap != null){
            editingCollectionItem.setImage(bitmap);
        }

        if (editingCollectionItem.getId() != 0)
            dbHelper.updateCollectionItem(editingCollectionItem);
        else
            dbHelper.addCollectionItem(editingCollectionItem, collectionID);


        this.finish();

    }

}