package com.example.weather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class WeatherOpenHelper extends SQLiteOpenHelper {
	
	public final String CREATE_PROVINCE = "create table Privince("
			+ "id integer primary key autocreament,"
			+ "province_name text,"
			+ "province_code text)";
	public final String CREATE_CITY 	= "create table City("
			+ "id integer primary key autocreament"
			+ "city_name text,"
			+ "city_code text,"
			+ "priovnce_id integer)";
	
	public final String CREATE_COUNTRY	= "create table Country("
			+ "id integer primary key autocreament"
			+ "country_name text,"
			+ "country_code text,"
			+ "city_id integer)";

	

	public WeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_PROVINCE);
		db.execSQL(CREATE_CITY);
		db.execSQL(CREATE_COUNTRY);
		

	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
