package com.msg.server;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class IMDBOpenHelper extends SQLiteOpenHelper {

	public IMDBOpenHelper(Context context) {
		super(context, "im.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS friends (_id integer primary key not null,"
				+ "NAME varchar(50) DEFAULT NULL,"
				+ "PINYIN varchar(200) DEFAULT NULL,"
				+ "FIRSTPY varchar(15) DEFAULT NULL,"
				+ "UID varchar(100) DEFAULT NULL,"
				+ "TEL varchar(150) DEFAULT NULL,"
				+ "SEX integer DEFAULT NULL,"
				+ "BRITHDAY varchar(150) DEFAULT NULL,"
				+ "COMPANY varchar(150) DEFAULT NULL,"
				+ "BRANCH varchar(150) DEFAULT NULL,"
				+ "JOB varchar(150) DEFAULT NULL,"
				+ "GROUPNAME varchar(200) DEFAULT NULL,"
				+ "CTIME varchar(15) DEFAULT NULL,"
				+ "UPDATEDATA char(15) DEFAULT NULL,"
				+ "SHOW integer DEFAULT NULL)");
		db.execSQL("CREATE TABLE IF NOT EXISTS groups (_id integer primary key not null,"
				+ "GROUPNAME varchar(200) DEFAULT NULL,"
				+ "UID varchar(100) DEFAULT NULL,"
				+ "UPDATEDATA char(15) DEFAULT NULL)");
		db.execSQL("CREATE TABLE IF NOT EXISTS messages (_id integer primary key not null,"
				+ "fromJid varchar(64) DEFAULT NULL,"
				+ "fromName varchar(64) DEFAULT NULL,"
				+ "toJid varchar(64) DEFAULT NULL,"
				+ "msgType INTEGER DEFAULT NULL, "
				+ "msgOrient INTEGER DEFAULT NULL, "
				+ "msgOpened INTEGER DEFAULT NULL, "
				+ "imageUrl varchar(200) DEFAULT NULL,"
				+ "imagePath varchar(200) DEFAULT NULL,"
				+ "voiceUrl varchar(200) DEFAULT NULL,"
				+ "voicePath varchar(200) DEFAULT NULL,"
				+ "title TEXT DEFAULT NULL,"
				+ "msg TEXT DEFAULT NULL,"
				+ "creationData char(15) DEFAULT NULL,"
		        + "miniPic varchar(200))");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS friends");
		db.execSQL("DROP TABLE IF EXISTS messages");
		onCreate(db);
	}
}
