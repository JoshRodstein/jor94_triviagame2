package edu.pitt.cs1699.jor94_triviagame2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TermsManager";
    private static final String TABLE_TERMS = "Terms";
    private static final String KEY_TERM = "term";
    private static final String KEY_DEF = "def";

    public DatabaseHelper(Context context){
       super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        Log.d("ON_CREATE DB HELPER:", "INSIDE DB ONCREATE");
        String CREATE_TERMS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_TERMS + "(\n"
                + KEY_TERM + " TEXT PRIMARY KEY,\n"
                + KEY_DEF + " TEXT,"
                + ");";
        db.execSQL(CREATE_TERMS_TABLE);

        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = fbdb.getReference("TermsAndDefs");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child:dataSnapshot.getChildren()) {

                    Log.w("TERM: ",child.getKey());
                    Log.w("DEF: ",child.getValue().toString());
                    TermAndDef td = new TermAndDef(child.getKey(), child.getValue().toString());
                    addTerm(td);
                }
            }@Override
            public void onCancelled(DatabaseError firebaseError) {}
        });
    }

    public void onUpgrade(SQLiteDatabase db, int i, int i1){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);

        onCreate(db);
    }

    void addTerm(TermAndDef td){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TERM, td.getTerm());
        values.put(KEY_DEF, td.getDef());

        db.insert(TABLE_TERMS, null, values);
        db.close();
    }

    TermAndDef getTermAndDef(String term) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TERMS, new String[]{KEY_TERM, KEY_DEF}, KEY_TERM + "=?",
                new String[]{term}, null, null, null, null);

        if(cursor != null) {
            cursor.moveToFirst();
        }

        TermAndDef td = new TermAndDef(cursor.getString(0), cursor.getString(1));

        return td;
    }

    public List<TermAndDef> getAllTermsAndDefs(){
        List<TermAndDef> tdList = new ArrayList<>();

        String selectQuery = "SELECT *FROM " + TABLE_TERMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor.moveToFirst()){
            do {
                TermAndDef td = new TermAndDef();
                td.setTerm(cursor.getString(0));
                td.setDef(cursor.getString(1));

                tdList.add(td);

            } while(cursor.moveToNext());
        }
        return tdList;
    }

    public int getTermCount(){
        String countQuery = "SELECT *FROM " + TABLE_TERMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

        return cursor.getCount();
    }



}
