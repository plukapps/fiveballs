package com.pluk.fiveballs.persistence;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.pluk.fiveballs.model.Consts;

public class PuntajeDB extends SQLiteOpenHelper {

	public class Row extends Object {
        public long _Id;
        public int score;
        public String name;
        public long idSubmit;
    }
	
	@SuppressWarnings("rawtypes")
	public class RowComparator implements Comparator { 

		public int compare(Object o1, Object o2) { 
            Row r1 = (Row)o1;
            Row r2 = (Row)o2;
            if (r1.score > r2.score)
            	return -1;
            else 
            	if (r1.score == r2.score)
	            	return 0;
	            else
	            	return 1;
        }
	}

    private static final String DATABASE_CREATE =
        "create table PUNTAJES(_id integer primary key autoincrement, "
            + "name text not null,"
            + "score text not null,"
            + "idSubmit integer not null default 0"
            +");";
    
    private static final String DATABASE_UPGRADE =
        "alter table PUNTAJES add column "
            + "idSubmit integer not null default 0"
            +";";

    private static final String DATABASE_NAME = "FIVEMORE_SCORESDB";

    private static final String DATABASE_TABLE = "PUNTAJES";

	private static final String TAG = "PuntajeDB";

	private static final int TOP_SCORES_MAX = Consts.game.TOP_SCORES_MAX;

	private static CursorFactory fabrica;

   
    private SQLiteDatabase db;

    public PuntajeDB(Context ctx) {
    	super(ctx, DATABASE_NAME, fabrica, 2);
    	db = getWritableDatabase();
    }       
	
    public void close() {
        db.close();
    }
    
    public boolean isTopScore(int score){
    	List<Row> rows = fetchAllRows();
    	//Si hay menos valores que el MAXIMO
    	if (rows.size() < TOP_SCORES_MAX){
    		return true;
    	}
    	
    	Collections.reverse(rows);
    	Row menor = rows.iterator().next();
    	
        int scoreMin = menor.score;
        Log.i(TAG, "El mínimo score de la DB es " + scoreMin);
        if (score > scoreMin){
        	//El score es TOP, debo remover el de menor score
        	long rowId = menor._Id;
        	deleteRow(rowId);
        	return true;
        }
        else return false;
        
        
    }

    public long createRow(String name, String score) {
    	Log.i(TAG, "Insertando en db");
        ContentValues initialValues = new ContentValues();
        initialValues.put("score", score);
        initialValues.put("name", name);
        initialValues.put("idSubmit", 0);
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    public void deleteRow(long rowId) {
        db.delete(DATABASE_TABLE, "_id=" + rowId, null);
    }

    @SuppressWarnings("unchecked")
	public List<Row> fetchAllRows() {
        LinkedList<Row> ret = new LinkedList<Row>();
        try {
            Cursor c =
                db.query(DATABASE_TABLE, new String[] {
                    "_id", "name", "score", "idSubmit"}, null, null, null, null, null);
            int numRows = c.getCount();
            c.moveToFirst();
            for (int i = 0; i < numRows; ++i) {
                Row row = new Row();
                row._Id = c.getLong(0);
                row.score = Integer.valueOf(c.getString(2));
                row.name = c.getString(1);
                row.idSubmit = c.getLong(3);
                ret.add(row);
                c.moveToNext();
            }
            c.close();
        } catch (SQLException e) {
            Log.e("Exception on query", e.toString());
        }
        Collections.sort(ret, new RowComparator());
        return ret;
        
    }

    public Row fetchRow(long rowId) {
        Row row = new Row();
        Cursor c =
            db.query(DATABASE_TABLE, new String[] {
                "_id", "name" , "score"}, "_id=" + rowId, null, null,
                null, null);
        if (c.getCount() > 0) {
            c.isFirst();
            row._Id = c.getLong(0);
            row.score = Integer.valueOf(c.getString(2));
            row.name = c.getString(1);
            row.idSubmit = c.getLong(3);
            return row;
        } else {
        	row._Id = -1;
            row.score = 0;
            row.name= null;
            row.idSubmit = -1;
        }
        return row;
    }

    public void updateRow(long rowId, String name, String score) {
        ContentValues args = new ContentValues();
        args.put("name", name);
        args.put("score", score);
        db.update(DATABASE_TABLE, args, "_id=" + rowId, null);
    }
    
    public void updateSubmitId(long rowId, long submitId){
    	ContentValues args = new ContentValues();
        args.put("idSubmit", submitId);
        db.update(DATABASE_TABLE, args, "_id=" + rowId, null);
    }
    
    public Cursor GetAllRows() {
        try {
            return db.query(DATABASE_TABLE, new String[] {
                    "_id", "name", "score", "idSubmit"}, null, null, null, null, null);
        } catch (SQLException e) {
            Log.e("Exception on query", e.toString());
            return null;
        }
    }

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.i(TAG, "onCreate");
		db.execSQL(DATABASE_CREATE);		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i(TAG, "onUpgrade");
		if(!db.isReadOnly()){
			if(oldVersion == 1 && newVersion == 2)
				db.execSQL(DATABASE_UPGRADE);
		}
	}
}