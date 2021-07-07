package falk.falkollection.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import falk.falkollection.model.CollectionItem;
import falk.falkollection.model.FCollection;

public class DBSQLiteHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "testdb5";

    private static final String COLLECTIONITEM_TABLENAME = "collectionitem";
    private static final String COLLECTIONITEM_ID = "collectionitem_id";
    private static final String TITLE = "title";
    private static final String VOLUME_NO = "vol_no";
    private static final String COLLECTIONITEM_IMAGE = "collectionitem_image";
    private static final String ISBN = "isbn";
    private static final String COLLECTION_ID_ITEM = "collection_id_item";
    private static final String OWNED = "owned";
    private static final String[] COLLECTIONITEM_COLUMNS = {COLLECTIONITEM_ID, TITLE, VOLUME_NO , COLLECTIONITEM_IMAGE, ISBN, COLLECTIONITEM_ID, OWNED};



    private static final String COLLECTION_TABLENAME = "collection";
    private static final String COLLECTION_ID = "collection_id";
    private static final String NAME = "name";
    private static final String VOLUMESTOTAL = "volumestotal";
    private static final String VOLUMES = "volumes";
    private static final String COLLECTIONIMAGE = "collectionimage";
    private static final String[] COLLECTION_COLUMNS = {COLLECTION_ID, NAME, VOLUMESTOTAL , VOLUMES, COLLECTIONIMAGE};



    public DBSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //{COLLECTION_ID, NAME, VOLUMESTOTAL , VOLUMES, COLLECTIONIMAGE};
        String CREATE_TABLE_COLLECTION = "CREATE TABLE "+COLLECTION_TABLENAME+" ("+
                COLLECTION_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                NAME+" TEXT,"+
                VOLUMESTOTAL+" INTEGER,"+
                VOLUMES+" TEXT," +
                COLLECTIONIMAGE +" BLOB)";

        //{COLLECTIONITEM_ID, TITLE, VOLUME_NO , COLLECTIONITEM_IMAGE, ISBN, OWNED};
        String CREATE_TABLE_COLLECTIONITEM = "CREATE TABLE "+COLLECTIONITEM_TABLENAME+" ("+
                COLLECTIONITEM_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
                TITLE+" TEXT,"+
                VOLUME_NO+" INTEGER,"+
                COLLECTIONITEM_IMAGE+" BLOB," +
                ISBN+" LONG," +
                COLLECTION_ID_ITEM +" INTEGER," +
                OWNED +" BOOLEAN)";

        /*String CREATE_TABLE_COLLECTIONLIST = "CREATE TABLE "+COLLECTIONLIST_TABLENAME+" ("+
                COLLECTION_LIST_ID+" INTEGER,"+
                COLLECTIONITEM_LIST_ID+" INTEGER,"+
                "FOREIGN KEY ("+COLLECTION_LIST_ID+") REFERENCES "+COLLECTION_TABLENAME+"("+COLLECTION_ID+"),"+
                "FOREIGN KEY ("+COLLECTIONITEM_LIST_ID+") REFERENCES "+COLLECTIONITEM_TABLENAME+"("+COLLECTIONITEM_ID+"),"+
                " PRIMARY KEY("+COLLECTION_LIST_ID+","+COLLECTIONITEM_LIST_ID+") )";
*/

        db.execSQL(CREATE_TABLE_COLLECTION);
        db.execSQL(CREATE_TABLE_COLLECTIONITEM);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ COLLECTION_TABLENAME);
        db.execSQL("DROP TABLE IF EXISTS "+ COLLECTION_ID);
        this.onCreate(db);
    }

    private Bitmap createBitmapFromBlob(byte[] image){
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    private byte[] createByteArrayFromBitmap(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        if(bitmap == null)
            return  new byte[]{};
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    ////////////COLLECTIONS//////////////


    //{COLLECTION_ID, NAME, VOLUMESTOTAL , VOLUMES, COLLECTIONIMAGE};
    private FCollection cursorToCollection(Cursor cursor) {
        FCollection collection = new FCollection();
        collection.setId(Integer.parseInt(cursor.getString(0)));
        collection.setName(cursor.getString(1));
        collection.setVolumesTotal(cursor.getInt(2));
        collection.setVolumes(getCollectionItemListIDs(cursor.getString(3)));
        collection.setImage(createBitmapFromBlob(cursor.getBlob(4)));

        return collection;
    }

    private List<Integer> getCollectionItemListIDs(String string) {
        String[] strings = string.split(",");
        List<Integer> ids = new ArrayList<Integer>();
        for(int i=0;i<strings.length;i++)
        {
            if(strings[i] == "")
                continue;
            ids.add(Integer.parseInt( strings[i]));
        }
        return  ids;
    }

    public int updateCollection(FCollection fCollection) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, fCollection.getName());
        values.put(VOLUMESTOTAL, fCollection.getVolumesTotal());
        values.put(VOLUMES, convertListToIDString(fCollection.getVolumes()));
        values.put(COLLECTIONIMAGE, createByteArrayFromBitmap(fCollection.getImage()) );
        int i = db.update(COLLECTION_TABLENAME,
                values,
                COLLECTION_ID+" = ?",
                new String[] { String.valueOf(fCollection.getId()) });
        db.close();
        return i;
    }


    //{COLLECTION_ID, NAME, VOLUMESTOTAL , VOLUMES, COLLECTIONIMAGE};
    public void addCollection(FCollection fCollection) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(NAME, fCollection.getName());
        values.put(VOLUMESTOTAL, fCollection.getVolumesTotal());
        values.put(VOLUMES, convertListToIDString(fCollection.getVolumes()));
        values.put(COLLECTIONIMAGE, createByteArrayFromBitmap(fCollection.getImage()));
        db.insert(COLLECTION_TABLENAME, null, values);
        db.close();
    }

    private String convertListToIDString(List<Integer> volumes) {
        String str = "";
        if(volumes == null)
            return  str;
        int size = volumes.size();
        for(int i=0;i<size;i++)
            str+= volumes.get(i)+",";
        if(size>0)
        str+= volumes.get(size-1);
        return str;
    }

    public FCollection getCollection(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(COLLECTION_TABLENAME,
                COLLECTION_COLUMNS,
                COLLECTION_ID+"  = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
        if (cursor == null) {
            return null;
        } else {
            cursor.moveToFirst();
            FCollection fCollection = cursorToCollection(cursor);
            return fCollection;
        }
    }

    public ArrayList<FCollection> getAllCollections() {
        ArrayList<FCollection> collections = new ArrayList<FCollection>();
        String query = "SELECT * FROM " + COLLECTION_TABLENAME + " ORDER BY " + NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                FCollection fCollection = cursorToCollection(cursor);
                collections.add(fCollection);
            } while (cursor.moveToNext());
        }
        return collections;
    }

    public int deleteCollection(FCollection fCollection) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(COLLECTION_TABLENAME,
                COLLECTION_ID+" = ?",
                new String[] { String.valueOf(fCollection.getId()) });
        db.close();
        return i;
    }

    ////////////COLLECTIONITEMS//////////////


    //{COLLECTIONITEM_ID, TITLE, VOLUME_NO , COLLECTIONITEM_IMAGE, ISBN, OWNED};
    private CollectionItem cursorToCollectionItem(Cursor cursor) {
        CollectionItem collectionItem = new CollectionItem();
        collectionItem.setId(Integer.parseInt(cursor.getString(0)));
        collectionItem.setTitle(cursor.getString(1));
        collectionItem.setVolumeNo(cursor.getInt(2));
        collectionItem.setImage(createBitmapFromBlob(cursor.getBlob(3)));
        collectionItem.setIsbn(cursor.getLong(4));
        collectionItem.setCollectionId(cursor.getInt(5));
        collectionItem.setOwned(cursor.getInt(6) > 0);

        return collectionItem;
    }

    public int updateCollectionItem(CollectionItem collectionItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, collectionItem.getTitle());
        values.put(VOLUME_NO, collectionItem.getVolumeNo());
        values.put(COLLECTIONITEM_IMAGE, createByteArrayFromBitmap(collectionItem.getImage()) );
        values.put(ISBN, collectionItem.getIsbn() );
        values.put(COLLECTION_ID_ITEM, collectionItem.getCollectionID() );
        values.put(OWNED, collectionItem.getOwned() );
        int i = db.update(COLLECTIONITEM_TABLENAME,
                values,
                COLLECTIONITEM_ID+" = ?",
                new String[] { String.valueOf(collectionItem.getId()) });
        db.close();
        return i;
    }


    public void addCollectionItem(CollectionItem collectionItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, collectionItem.getTitle());
        values.put(VOLUME_NO, collectionItem.getVolumeNo());
        values.put(COLLECTIONITEM_IMAGE, createByteArrayFromBitmap(collectionItem.getImage()) );
        values.put(ISBN, collectionItem.getIsbn() );
        values.put(COLLECTION_ID_ITEM, collectionItem.getCollectionID() );
        values.put(OWNED, collectionItem.getOwned() );
        db.insert(COLLECTIONITEM_TABLENAME, null, values);
        db.close();
    }

    public CollectionItem getCollectionItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(COLLECTIONITEM_TABLENAME,
                COLLECTIONITEM_COLUMNS,
                COLLECTIONITEM_ID+"  = ?",
                new String[] { String.valueOf(id) },
                null,
                null,
                null,
                null);
        if (cursor == null) {
            return null;
        } else {
            cursor.moveToFirst();
            CollectionItem collectionItem = cursorToCollectionItem(cursor);
            return collectionItem;
        }
    }

    public ArrayList<CollectionItem> getAllCollectionItems() {
        ArrayList<CollectionItem> collections = new ArrayList<CollectionItem>();
        String query = "SELECT * FROM " + COLLECTIONITEM_TABLENAME + " ORDER BY " + TITLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                CollectionItem collectionItem = cursorToCollectionItem(cursor);
                collections.add(collectionItem);
            } while (cursor.moveToNext());
        }
        return collections;
    }


    public int deleteCollectionItem(CollectionItem collectionItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.delete(COLLECTIONITEM_TABLENAME,
                COLLECTIONITEM_ID+" = ?",
                new String[] { String.valueOf(collectionItem.getId()) });
        db.close();
        return i;
    }


//////////////////itens dentro de coleções////////////////
    public ArrayList<CollectionItem> getAllCollectionItemsFromCollection(int collectionID) {
        ArrayList<CollectionItem> collections = new ArrayList<CollectionItem>();
        String query = "SELECT * FROM " + COLLECTIONITEM_TABLENAME + " WHERE " +COLLECTION_ID_ITEM+"="+collectionID + " ORDER BY " + VOLUME_NO;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                CollectionItem collectionItem = cursorToCollectionItem(cursor);
                collections.add(collectionItem);
            } while (cursor.moveToNext());
        }
        return collections;
    }

    public void addCollectionItem(CollectionItem collectionItem, int collectionID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TITLE, collectionItem.getTitle());
        values.put(VOLUME_NO, collectionItem.getVolumeNo());
        values.put(COLLECTIONITEM_IMAGE, createByteArrayFromBitmap(collectionItem.getImage()) );
        values.put(ISBN, collectionItem.getIsbn() );
        values.put(COLLECTION_ID_ITEM, collectionItem.getCollectionID() );
        values.put(OWNED, collectionItem.getOwned() );
        db.insert(COLLECTIONITEM_TABLENAME, null, values);
        db.close();
    }


}
