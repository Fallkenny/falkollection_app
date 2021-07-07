package falk.falkollection.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import falk.falkollection.R;
import falk.falkollection.interfaces.IClickListener;
import falk.falkollection.model.CollectionItem;
import falk.falkollection.viewholder.CollectionItemsViewHolder;

public class CollectionItemsAdapter  extends RecyclerView.Adapter<CollectionItemsViewHolder> {
    private final ArrayList<CollectionItem> collectionItemsList;
    private  IClickListener collectionItemsClickListener;


    public CollectionItemsAdapter(ArrayList<CollectionItem> CollectionItems, IClickListener collectionItemsClickListener) {
        this.collectionItemsList = CollectionItems;
        this.collectionItemsClickListener = collectionItemsClickListener;
    }

    @NonNull
    @Override
    public CollectionItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_item_layout,parent,false);

        return new CollectionItemsViewHolder(view, collectionItemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionItemsViewHolder holder, int position) {
        holder.setData(collectionItemsList.get(position));
    }

    @Override
    public int getItemCount() {

        return collectionItemsList.size();
    }
}
