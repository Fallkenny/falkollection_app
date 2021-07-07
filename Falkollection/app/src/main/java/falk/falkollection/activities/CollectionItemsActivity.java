package falk.falkollection.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import java.util.ArrayList;

import falk.falkollection.R;
import falk.falkollection.adapter.CollectionItemsAdapter;
import falk.falkollection.database.DBSQLiteHelper;
import falk.falkollection.interfaces.IClickListener;
import falk.falkollection.model.CollectionItem;
import falk.falkollection.model.FCollection;

public class CollectionItemsActivity extends AppCompatActivity implements IClickListener {

    private DBSQLiteHelper dbHelper;
    private RecyclerView recyclerView;
    CollectionItemsAdapter collectionItemsAdapter;
    ArrayList<CollectionItem> collectionItems;
    String collectionName;
    private int collectionID = 0;

    ItemTouchHelper.Callback callbackLeft = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.LEFT) {
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            c.clipRect(viewHolder.itemView.getRight()+dX, viewHolder.itemView.getTop(),
                    viewHolder.itemView.getRight(), viewHolder.itemView.getBottom());

            Drawable icon = ContextCompat.getDrawable(getBaseContext(),R.drawable.edit_icon);
            icon.setBounds((int)(viewHolder.itemView.getRight()-200), viewHolder.itemView.getTop()+50,viewHolder.itemView.getRight() , viewHolder.itemView.getBottom()-50);
            c.drawColor(getResources().getColor(R.color.purple_500,null));
            icon.draw(c);
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int pos = viewHolder.getAdapterPosition();;
            Intent intent = new Intent(getBaseContext(), CollectionItemEdit.class);

            intent.putExtra("id", collectionItems.get(pos).getId());
            intent.putExtra("CollectionID", collectionID);
            intent.putExtra("readOnly", false);
            startActivity(intent);
        }

    };

    ItemTouchHelper.Callback callbackRight = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT) {
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            c.clipRect(0f, viewHolder.itemView.getTop(),
                    dX, viewHolder.itemView.getBottom());
            Drawable icon = ContextCompat.getDrawable(getBaseContext(),R.drawable.delete_icon);
            icon.setBounds(0, viewHolder.itemView.getTop()+50, 200, viewHolder.itemView.getBottom()-50);

            c.drawColor(getResources().getColor(R.color.purple_700,null));
            icon.draw(c);
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            int pos = viewHolder.getAdapterPosition();

                            CollectionItem collectionItem = collectionItems.get(pos);
                            collectionItems.remove(pos);
                            collectionItemsAdapter.notifyItemRemoved(pos);
                            collectionItemsAdapter.notifyItemRangeChanged (pos, collectionItems.size());
                            dbHelper.deleteCollectionItem(collectionItem);
                            collectionItemsAdapter = new CollectionItemsAdapter(collectionItems,CollectionItemsActivity.this);
                            recyclerView.setAdapter(collectionItemsAdapter);
                            updateActionBarCount();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            recyclerView.setAdapter(new CollectionItemsAdapter(collectionItems,CollectionItemsActivity.this));
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(CollectionItemsActivity.this);
            builder.setMessage("Tem certeza que quer deletar o item?").setPositiveButton("Sim", dialogClickListener)
                    .setNegativeButton("Não", dialogClickListener).show();
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection_items);

        Intent intent = getIntent();
        collectionID = intent.getIntExtra("CollectionID", 0);

        dbHelper = new DBSQLiteHelper(this);
        collectionItems = dbHelper.getAllCollectionItemsFromCollection(collectionID);
        collectionName = dbHelper.getCollection(collectionID).getName();
        updateActionBarCount();
        recyclerView = findViewById(R.id.recyclerview_collectionItems);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        collectionItemsAdapter = new CollectionItemsAdapter(collectionItems, this);
        recyclerView.setAdapter(collectionItemsAdapter);


        new ItemTouchHelper(callbackRight).attachToRecyclerView(recyclerView);
        new ItemTouchHelper(callbackLeft).attachToRecyclerView(recyclerView);

        collectionItemsAdapter.notifyDataSetChanged();

    }

    public void updateActionBarCount()
    {
        setTitle(collectionName+ " ["+collectionItems.size()+"]");
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionItems = dbHelper.getAllCollectionItemsFromCollection(collectionID);

        RecyclerView recyclerView = findViewById(R.id.recyclerview_collectionItems);
        CollectionItemsAdapter adapter = new CollectionItemsAdapter(collectionItems, this);
        recyclerView.setAdapter(adapter);
        updateActionBarCount();
    }

    public void add_BtnClick(View view){
        Intent intent = new Intent(getBaseContext(), CollectionItemEdit.class);
        intent.putExtra("CollectionID", collectionID);
        intent.putExtra("readOnly", false);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick_IClickListener(int position) {

        Intent intent = new Intent(this, CollectionItemEdit.class);
        intent.putExtra("id", collectionItems.get(position).getId());
        intent.putExtra("CollectionID", collectionID);
        intent.putExtra("readOnly", true);
        startActivity(intent);

    }

}