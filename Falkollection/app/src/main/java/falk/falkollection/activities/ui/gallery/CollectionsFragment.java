package falk.falkollection.activities.ui.gallery;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import falk.falkollection.R;
import falk.falkollection.activities.CollectionEdit;
import falk.falkollection.activities.CollectionItemsActivity;
import falk.falkollection.adapter.CollectionsAdapter;
import falk.falkollection.database.DBSQLiteHelper;
import falk.falkollection.interfaces.IClickListener;
import falk.falkollection.model.FCollection;


public class CollectionsFragment extends Fragment implements IClickListener {

    private CollectionsViewModel collectionsViewModel;
    private View viewRoot;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        collectionsViewModel =
                new ViewModelProvider(this).get(CollectionsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_collections, container, false);
        viewRoot =root;

        FloatingActionButton fab = viewRoot.findViewById(R.id.addContact);
        fab.setOnClickListener(addCollection_BtnClick);

        dbHelper = new DBSQLiteHelper(getContext());
        collections = dbHelper.getAllCollections();

        recyclerView = viewRoot.findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        collectionsAdapter = new CollectionsAdapter(collections, this);
        recyclerView.setAdapter(collectionsAdapter);


        new ItemTouchHelper(callbackRight).attachToRecyclerView(recyclerView);
        new ItemTouchHelper(callbackLeft).attachToRecyclerView(recyclerView);

        collectionsAdapter.notifyDataSetChanged();


        return root;
    }


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

            Drawable icon = ContextCompat.getDrawable(getContext(),R.drawable.edit_icon);
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
            Intent intent = new Intent(getContext(), CollectionEdit.class);

            intent.putExtra("ID", collections.get(pos).getId());
            intent.putExtra("readOnly", false);
            startActivity(intent);

        }

    };


    //public void updateActionBarCount()
    //{
    //    setTitle("Coleções:" + collections.size());
    //}

    ItemTouchHelper.Callback callbackRight = new ItemTouchHelper.SimpleCallback(0,
            ItemTouchHelper.RIGHT) {
        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            c.clipRect(0f, viewHolder.itemView.getTop(),
                    dX, viewHolder.itemView.getBottom());
            Drawable icon = ContextCompat.getDrawable(getContext(),R.drawable.delete_icon);
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
                            collectionsAdapter = new CollectionsAdapter(collections, CollectionsFragment.this);
                            recyclerView.setAdapter(collectionsAdapter);
                            //updateActionBarCount();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            recyclerView.setAdapter(new CollectionsAdapter(collections,CollectionsFragment.this));
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Tem certeza que quer deletar a Coleção? Os itens nela serão perdidos.").setPositiveButton("Sim", dialogClickListener)
                    .setNegativeButton("Não", dialogClickListener).show();
        }

        // More code here

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_collections);



    }

    @Override
    public void onStart() {
        super.onStart();

        collections = dbHelper.getAllCollections();
        RecyclerView recyclerView = viewRoot.findViewById(R.id.recyclerview);
        CollectionsAdapter adapter = new CollectionsAdapter(collections, this);
        recyclerView.setAdapter(adapter);
        //updateActionBarCount();
    }

    public View.OnClickListener addCollection_BtnClick= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(getContext(), CollectionEdit.class);
            intent.putExtra("readOnly", false);
            startActivity(intent);
        }
    };

    @Override
    public void onClick_IClickListener(int position) {

        Intent intent = new Intent(getContext(), CollectionItemsActivity.class);
        intent.putExtra("CollectionID", collections.get(position).getId());
        intent.putExtra("readOnly", true);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        });
    }



}