package jp.tanikinaapps.chviewer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FavoriteOpenHelper extends SQLiteOpenHelper {
    //データベース情報
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "favoriteDB.db";
    private static final String TABLE_NAME = "favoritedb";
    private static final String _ID = "_id";
    private static final String COLUMN_NAME_TITLE = "articleTitle";
    private static final String COLUMN_NAME_UPDATE = "articleUpDate";
    private static final String COLUMN_NAME_BLOG = "blogName";
    private static final String COLUMN_NAME_ADDRESS = "articleAddress";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + TABLE_NAME + " (" + _ID + " INTEGER PRIMARY KEY," + COLUMN_NAME_TITLE + " TEXT," + COLUMN_NAME_ADDRESS + " TEXT," + COLUMN_NAME_UPDATE + " TEXT," + COLUMN_NAME_BLOG + " TEXT)";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;

    FavoriteOpenHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db,int oldVersion,int newVersion){
        onUpgrade(db,oldVersion,newVersion);
    }
}
