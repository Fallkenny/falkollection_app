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

import java.util.ArrayList;

import falk.falkollection.R;
import falk.falkollection.adapter.CollectionsAdapter;
import falk.falkollection.database.DBSQLiteHelper;
import falk.falkollection.interfaces.IClickListener;
import falk.falkollection.model.FCollection;

public class CollectionsActivity extends AppCompatActivity implements IClickListener {

    private DBSQLiteHelper dbHelper;
    private RecyclerView recyclerView;
    CollectionsAdapter collectionsAdapter;
    ArrayList<FCollection> collections;

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
            Intent intent = new Intent(getBaseContext(), CollectionEdit.class);

            intent.putExtra("ID", collections.get(pos).getId());
            intent.putExtra("readOnly", false);
            startActivity(intent);

        }

    };


    public void updateActionBarCount()
    {
        setTitle("Coleções:" + collections.size());
    }

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

                            FCollection contact = collections.get(pos);
                            collections.remove(pos);
                            collectionsAdapter.notifyItemRemoved(pos);
                            collectionsAdapter.notifyItemRangeChanged (pos, collections.size());
                            dbHelper.deleteCollection(contact);
                            collectionsAdapter = new CollectionsAdapter(collections,CollectionsActivity.this);
                            recyclerView.setAdapter(collectionsAdapter);
                            updateActionBarCount();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            recyclerView.setAdapter(new CollectionsAdapter(collections,CollectionsActivity.this));
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(CollectionsActivity.this);
            builder.setMessage("Tem certeza que quer deletar a Coleção? Os itens irão para seção AVULSOS").setPositiveButton("Sim", dialogClickListener)
                    .setNegativeButton("Não", dialogClickListener).show();
        }

        // More code here

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collections);

        dbHelper = new DBSQLiteHelper(this);
        collections = dbHelper.getAllCollections();

        recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        collectionsAdapter = new CollectionsAdapter(collections, this);
        recyclerView.setAdapter(collectionsAdapter);


        new ItemTouchHelper(callbackRight).attachToRecyclerView(recyclerView);
        new ItemTouchHelper(callbackLeft).attachToRecyclerView(recyclerView);

        collectionsAdapter.notifyDataSetChanged();

    }

    @Override
    protected void onStart() {
        super.onStart();

        collections = dbHelper.getAllCollections();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        CollectionsAdapter adapter = new CollectionsAdapter(collections, this);
        recyclerView.setAdapter(adapter);
        updateActionBarCount();
    }

    public void addCollection_BtnClick(View view){
        Intent intent = new Intent(getBaseContext(), CollectionEdit.class);
        intent.putExtra("readOnly", false);
        startActivity(intent);
    }

    @Override
    public void onClick_IClickListener(int position) {

        Intent intent = new Intent(this, CollectionItemsActivity.class);
        intent.putExtra("CollectionID", collections.get(position).getId());
        intent.putExtra("readOnly", true);
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              startActivity(intent);
                          }
                      });
    }

}