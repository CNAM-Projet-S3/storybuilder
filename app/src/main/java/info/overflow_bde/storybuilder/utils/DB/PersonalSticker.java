package info.overflow_bde.storybuilder.utils.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public final class PersonalSticker {
	// To prevent someone from accidentally instantiating the contract class,
	// make the constructor private.
	private PersonalSticker() {
	}

	/* Inner class that defines the table contents */
	public static class PersonalStickerEntry implements BaseColumns {
		public static final String TABLE_NAME             = "personal_sticker";
		public static final String COLUMN_NAME_IMAGE      = "image";
		public static final String COLUMN_NAME_CREATED_AT = "created_at";
	}

	private static final String SQL_CREATE_ENTRIES =
			"CREATE TABLE " + PersonalStickerEntry.TABLE_NAME + " (" +
					PersonalStickerEntry._ID + " INTEGER PRIMARY KEY," +
					PersonalStickerEntry.COLUMN_NAME_IMAGE + " TEXT," +
					PersonalStickerEntry.COLUMN_NAME_CREATED_AT + " DATETIME DEFAULT CURRENT_TIMESTAMP)";

	private static final String SQL_DELETE_ENTRIES =
			"DROP TABLE IF EXISTS " + PersonalStickerEntry.TABLE_NAME;


	public static class PersonalStickerDbHelper extends SQLiteOpenHelper {
		// If you change the database schema, you must increment the database version.
		public static final int    DATABASE_VERSION = 1;
		public static final String DATABASE_NAME    = "FeedReader.db";

		public PersonalStickerDbHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_ENTRIES);
		}

		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// This database is only a cache for online data, so its upgrade policy is
			// to simply to discard the data and start over
			db.execSQL(SQL_DELETE_ENTRIES);
			onCreate(db);
		}

		public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			onUpgrade(db, oldVersion, newVersion);
		}

	}

}
