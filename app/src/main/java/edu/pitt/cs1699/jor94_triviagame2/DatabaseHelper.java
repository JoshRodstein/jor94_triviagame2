package edu.pitt.cs1699.jor94_triviagame2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TermsManager";
    private static final String TABLE_TERMS = "Terms";
    private static final String KEY_TERM = "term";
    private static final String KEY_DEF = "def";
    private static final String ON_CREATE_TAG = "ON_CREATE: ";
    private FirebaseAuth mAuth;



    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        Log.w("ON_CREATE DB HELPER:", "INSIDE DB ONCREATE");

        String CREATE_TERMS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_TERMS + "(\n"
                + KEY_TERM + " TEXT PRIMARY KEY NOT NULL,\n"
                + KEY_DEF + " TEXT NOT NULL"
                + ");";

        db.execSQL(CREATE_TERMS_TABLE);
        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference DBTermsRef = fbdb.getReference("TermsAndDefs");

        DBTermsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child:dataSnapshot.getChildren()) {
                    Log.w(ON_CREATE_TAG,child.getKey());
                    Log.w(ON_CREATE_TAG,child.getValue().toString());
                    TermAndDef td = new TermAndDef(child.getKey(), child.getValue().toString());
                    try {
                        addTerm(td);
                    }catch (Exception e){
                        Log.w(" TERM EXISTS: ", "Not Added");
                    }
                }
            }@Override
            public void onCancelled(DatabaseError firebaseError) {}
        });

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);
        Log.w("ON_UPGRADE: ", "System.Call");
        onCreate(db);
    }
    // Overloaded for manual calles without int args
    public void onUpgrade(SQLiteDatabase db){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TERMS);

        onCreate(db);
    }

    void addTermToFB(TermAndDef td) {
        SQLiteDatabase db = this.getWritableDatabase();
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = fbdb.getReference("TermsAndDefs");


        dbRef.child(td.getTerm()).setValue(td.getDef());
    }

    void addTerm(TermAndDef td){
        SQLiteDatabase db = this.getWritableDatabase();

        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = fbdb.getReference("TermsAndDefs");

        dbRef.child(td.getTerm()).setValue(td.getDef());

        ContentValues values = new ContentValues();
        values.put(KEY_TERM, td.getTerm());
        values.put(KEY_DEF, td.getDef());
        try {
            db.insertOrThrow(TABLE_TERMS, null, values);
        } catch (SQLiteConstraintException sqle){}
        db.close();
    }

    void addScore(FirebaseUser id,  Scores s){
        SQLiteDatabase db = this.getWritableDatabase();
        FirebaseDatabase fbdb = FirebaseDatabase.getInstance();
        DatabaseReference dbRef = fbdb.getReference("Scores");

        dbRef.child(id.getUid()).child(s.getTimestamp()).setValue(s.getScore());
    }

    TermAndDef getTermAndDef(String term) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_TERMS, new String[]{KEY_TERM, KEY_DEF}, KEY_TERM + "=?",
                new String[]{term}, null, null, null, null);

        if(cursor != null) {
            cursor.moveToFirst();
        }

        TermAndDef td = new TermAndDef(cursor.getString(0), cursor.getString(1));
        db.close();
        return td;
    }

    public List<TermAndDef> getAllTermsAndDefs(){
        SQLiteDatabase db = this.getWritableDatabase();
        List<TermAndDef> tdList = new ArrayList<>();

        String selectQuery = "SELECT *FROM " + TABLE_TERMS;


        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.w("getAllTermsAndDefs", "In terms and defs");
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

    public SQLiteDatabase getDB(){
        return this.getWritableDatabase();
    }



}