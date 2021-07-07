package falk.falkollection.viewholder;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import falk.falkollection.R;
import falk.falkollection.database.DBSQLiteHelper;
import falk.falkollection.interfaces.IClickListener;
import falk.falkollection.model.CollectionItem;
import falk.falkollection.model.FCollection;

public class CollectionsViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener {
    private TextView txtCollectionName;
    private ImageView imageViewCollectionCover;
    private TextView txtVolumesQT;
    private TextView txtcollectionQT;
    private TextView txtVolumesQTOwned;
    private Context context;

    IClickListener contactsClickListener;
    public CollectionsViewHolder(@NonNull View itemView, IClickListener iClickListener) {
        super(itemView);
        context= itemView.getContext();
        itemView.setOnClickListener(this);
        this.contactsClickListener = iClickListener;
        imageViewCollectionCover = itemView.findViewById(R.id.imageViewCollectionCover);
        txtCollectionName = itemView.findViewById(R.id.collectionName);
        txtcollectionQT = itemView.findViewById(R.id.collectionqt);
        txtVolumesQT = itemView.findViewById(R.id.volumes_count);
        txtVolumesQTOwned = itemView.findViewById(R.id.volumes_countOwned);
    }

    public void setCheckVisibility(boolean visible){
        ImageView check = itemView.findViewById(R.id.complete_check);
        int visibility = visible? View.VISIBLE :View.INVISIBLE;
        check.setVisibility(visibility);
    }

    public void setData(FCollection fCollection) {
                    txtCollectionName.setText(fCollection.getName());
                    int total = fCollection.getVolumesTotal();
                    if(total>0)
                        txtcollectionQT.setText("[" + total+"]");
                    else
                        txtcollectionQT.setText("[-]");

                    DBSQLiteHelper helper =  new DBSQLiteHelper(context);
                    ArrayList<CollectionItem> list =  helper.getAllCollectionItemsFromCollection(fCollection.getId());
                    long owned = list.stream().filter((item)->item.getOwned() == true).count();
                    int size = list.size();
                    txtVolumesQT.setText("Registrados: "+size);
                    txtVolumesQTOwned.setText("Obtidos: "+owned);
                    setCheckVisibility((total == owned) && total>0);
                    Bitmap bitmap = fCollection.getImage();
                    if(bitmap == null) {
                        bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.icons8_purple_book_48);
                    }
                    imageViewCollectionCover.setImageBitmap(bitmap);

    }

    public void onClick(View view){
        contactsClickListener.onClick_IClickListener(getAdapterPosition());
    }
}