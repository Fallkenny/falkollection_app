package falk.falkollection.viewholder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import falk.falkollection.R;
import falk.falkollection.database.DBSQLiteHelper;
import falk.falkollection.interfaces.IClickListener;
import falk.falkollection.model.CollectionItem;

public class CollectionItemsViewHolder extends RecyclerView.ViewHolder implements
        View.OnClickListener {
    private TextView txtCollectionItemName;
    private TextView txtVolNo;
    private ImageView imageViewCollectionItemCover;
    private CheckBox checkBox;
    private Context context;
    private CollectionItem collectionItem;


    IClickListener clickListener;


    View.OnClickListener checkBoxClickListener =  new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean checked = ((CheckBox) v).isChecked();
            DBSQLiteHelper helper =  new DBSQLiteHelper(context);

            collectionItem.setOwned(checked);

            helper.updateCollectionItem(collectionItem);
        }
    };

    public CollectionItemsViewHolder(@NonNull View itemView, IClickListener iClickListener) {
        super(itemView);
        itemView.setOnClickListener(this);
        this.clickListener = iClickListener;
        context= itemView.getContext();
        imageViewCollectionItemCover = itemView.findViewById(R.id.imageViewCollectionItemCover);
        txtCollectionItemName = itemView.findViewById(R.id.collectionItemName);
        txtVolNo = itemView.findViewById(R.id.vol_no);

        checkBox = itemView.findViewById(R.id.checkObtained);

        checkBox.setOnClickListener(checkBoxClickListener);
    }

    public void setData(CollectionItem fCollectionItem) {
        collectionItem =  fCollectionItem;
        txtCollectionItemName.setText(fCollectionItem.getTitle());
        txtVolNo.setText("Vol. "+ fCollectionItem.getVolumeNo());
        checkBox.setChecked(fCollectionItem.getOwned());
        Bitmap bitmap = fCollectionItem.getImage();
        if(bitmap == null)
            bitmap = BitmapFactory.decodeResource(context.getResources(),R.drawable.icons8_purple_book_482);
        imageViewCollectionItemCover.setImageBitmap(bitmap);
    }

    public void onClick(View view){
        clickListener.onClick_IClickListener(getAdapterPosition());
    }
}