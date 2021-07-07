package falk.falkollection.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import falk.falkollection.R;
import falk.falkollection.interfaces.IClickListener;
import falk.falkollection.model.FCollection;
import falk.falkollection.viewholder.CollectionsViewHolder;

public class CollectionsAdapter  extends RecyclerView.Adapter<CollectionsViewHolder> {
    private final ArrayList<FCollection> collectionsList;
    private  IClickListener collectionsClickListener;


    public CollectionsAdapter(ArrayList<FCollection> FCollections, IClickListener collectionsClickListener) {
        this.collectionsList = FCollections;
        this.collectionsClickListener = collectionsClickListener;
    }

    @NonNull
    @Override
    public CollectionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_layout,parent,false);

        return new CollectionsViewHolder(view, collectionsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionsViewHolder holder, int position) {
        holder.setData(collectionsList.get(position));
    }

    @Override
    public int getItemCount() {

        return collectionsList.size();
    }
}
