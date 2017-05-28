package apps.mohit.billit;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * This class is the type class for a Bill. It has the associated properties as fields which
 * are then stored in the local SQLite database
 *
 * @author Mohit Kewalramani
 * @version 2.0
 * @since 2017-05-10
 */
public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "BillList.db";       // The name of the table schema in the database
    private static final String BILL_TABLE_NAME = "bills";           // The title of the bills table
    private static final String BILL_COLUMN_ID = "id";               // The title of the id column
    private static final String BILL_COLUMN_NAME = "name";           // The title of the name column
    private static final String BILL_COLUMN_AMOUNT = "amount";       // The title of the amount column
    private static final String BILL_COLUMN_DUEDATE = "date";        // The title of the date column
    private static final String BILL_NOTIFICATION_CHOICE = "notification";  // The title of the notifications column

    /**
     * The constructor of the database
     * @param context The context from which the class was initialized
     */
    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 2);
    }

    /**
     * Executes the SQL command to create a local database
     * @param db The database that executes the SQL command
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table bills " +
                        "(id integer primary key, name text, amount text, date text, notification text)"
        );
    }

    /**
     * Checks the version before adding the database
     * @param db The SQLite database
     * @param oldVersion The old version of the database
     * @param newVersion The new version available of the database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS bills");
        onCreate(db);
    }

    /**
     * Inserts a bill into the database
     * @param name Name on the bill
     * @param amount The amount on the bill
     * @param date The due date of the bill
     * @param notification The notification choice of the bill entry
     * @return boolean Returns true if the add was successful
     */
    public boolean insertBill(String name, String amount, String date, String notification){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("amount", amount);
        contentValues.put("date", String.valueOf(date));
        contentValues.put("notification", notification);
        db.insert("bills", null, contentValues);
        return true;
    }

    /**
     * Returns a cursor containing the single queried record with a given id
     * @param id The id to search the database for
     */
    public Cursor getData(int id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from bills where id="+id+"", null);
        return res;
    }

    /**
     * Returns the number of rows in the database
     * @return int The number of rows in the database
     */
    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, BILL_TABLE_NAME);
        return numRows;
    }

    /**
     * Updates the columns of the bill entry based on the given id value
     * @param id The id of the record to update
     * @param name The updated name of the bill
     * @param amount The updated amount of the bill
     * @param date The updated due date of the bill
     * @param notification The updated notification choice of the bill
     * @return boolean True if the update was successful
     */
    public boolean updateEntry(Integer id, String name, String amount, String date, String notification){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("amount", amount);
        contentValues.put("date", date);
        contentValues.put("notification", notification);
        db.update("bills", contentValues, "id = ? ", new String[] { Integer.toString(id)});
        return true;
    }

    /**
     * Deletes the entry from the database with the given id
     * @param id The id in the database to delete
     * @return int The number of rows affected in the table
     */
    public Integer deleteEntry(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("bills",
                "id = ? ",
                new String[] {Integer.toString(id)});
    }

    /**
     * Returns all bills names in the database in the form of an ArrayList
     * @returns ArrayList<String>
     */
    public ArrayList<String> getAllBills(){
        ArrayList<String> arrayList = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from bills", null);
        res.moveToFirst();

        while(res.isAfterLast() == false){
            arrayList.add(res.getString(res.getColumnIndex(BILL_COLUMN_NAME)));
            res.moveToNext();
        }
        return arrayList;
    }

    /**
     * Deletes all bills from the database
     */
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(BILL_TABLE_NAME, null, null);
    }

    /**
     * Returns todays date in string format
     * @return string Today's date in string format
     */
    private String getTodaysDate(){
        Calendar today = Calendar.getInstance();
        Integer year = today.get(Calendar.YEAR);
        Integer month = today.get(Calendar.MONTH) + 1;
        Integer day = today.get(Calendar.DAY_OF_MONTH);

        String yearString = String.valueOf(year);
        String monthString;
        String dayString;
        if (month < 10){
            monthString = "0" + String.valueOf(month);
        }
        else{
            monthString = String.valueOf(month);
        }

        if (day < 10){
            dayString = "0" + String.valueOf(day);
        }
        else{
            dayString = String.valueOf(day);
        }

        return yearString + "-" + monthString + "-" + dayString;
    }

    /**
     * Returns the date one week from now in string format
     * @return string Date one week from now
     */
    private String getOneWeeksDateFromNow(){
        Calendar today = Calendar.getInstance();
        today.add(Calendar.DAY_OF_MONTH, 7);
        Integer year = today.get(Calendar.YEAR);
        Integer month = today.get(Calendar.MONTH) + 1;
        Integer day = today.get(Calendar.DAY_OF_MONTH);

        String yearString = String.valueOf(year);
        String monthString;
        String dayString;
        if (month < 10){
            monthString = "0" + String.valueOf(month);
        }
        else{
            monthString = String.valueOf(month);
        }

        if (day < 10){
            dayString = "0" + String.valueOf(day);
        }
        else{
            dayString = String.valueOf(day);
        }
        return String.format("%s-%s-%s", yearString , monthString, dayString);
    }

    /**
     * Returns the date in string format one month from now
     * @return string The date in string format one month from now
     */
    private String getOneMonthDateFromNow(){
        Calendar today = Calendar.getInstance();
        today.add(Calendar.MONTH, 1);
        Integer year = today.get(Calendar.YEAR);
        Integer month = today.get(Calendar.MONTH) + 1;
        Integer day = today.get(Calendar.DAY_OF_MONTH);

        String yearString = String.valueOf(year);
        String monthString;
        String dayString;
        if (month < 10){
            monthString = "0" + String.valueOf(month);
        }
        else{
            monthString = String.valueOf(month);
        }

        if (day < 10){
            dayString = "0" + String.valueOf(day);
        }
        else{
            dayString = String.valueOf(day);
        }

        return String.format("%s-%s-%s", yearString , monthString, dayString);
    }

    /**
     * Returns all records in the database between the date ranges of today and one
     * week from now
     * @return ArrayList<BillEntry> All records in the database between now and one week from
     *                              now in BillEntry type
     */
    public ArrayList<BillEntry> getThisWeekBillsInFormat(){
        ArrayList<BillEntry> allEntries = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from bills where date between " + "\"" + getTodaysDate() + "\"" + " and " + "\"" + getOneWeeksDateFromNow() + "\"" + " order by date", null);
        res.moveToFirst();

        while (res.isAfterLast() == false){
            String title = res.getString(res.getColumnIndex(BILL_COLUMN_NAME));
            String amount = res.getString(res.getColumnIndex(BILL_COLUMN_AMOUNT));
            String date = res.getString(res.getColumnIndex(BILL_COLUMN_DUEDATE));
            String notification = res.getString(res.getColumnIndex(BILL_NOTIFICATION_CHOICE));

            BillEntry created = new BillEntry(title, amount, date, notification);
            allEntries.add(created);
            res.moveToNext();
        }
        return allEntries;
    }

    /**
     * Returns all records in the database between the date ranges of today and one
     * month from now
     * @return ArrayList<BillEntry> All records in the database between
     *                              now and one month from now in BillEntry type
     */
    public ArrayList<BillEntry> getThisMonthBillsInFormat(){
        ArrayList<BillEntry> allEntries = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from bills where date between " + "\"" + getTodaysDate() + "\"" + " and " + "\"" + getOneMonthDateFromNow() + "\"" + " order by date", null);
        res.moveToFirst();

        while (res.isAfterLast() == false){
            String title = res.getString(res.getColumnIndex(BILL_COLUMN_NAME));
            String amount = res.getString(res.getColumnIndex(BILL_COLUMN_AMOUNT));
            String date = res.getString(res.getColumnIndex(BILL_COLUMN_DUEDATE));
            String notification = res.getString(res.getColumnIndex(BILL_NOTIFICATION_CHOICE));

            BillEntry created = new BillEntry(title, amount, date, notification);
            allEntries.add(created);
            res.moveToNext();
        }
        return allEntries;
    }

    /**
     * Returns all records from the database in BillEntry type
     * @return ArrayList<BillEntry> All records in the database in BillEntry type
     */
    public ArrayList<BillEntry> getAllBillsInFormat(){
        ArrayList<BillEntry> allEntries = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from bills order by date", null);
        res.moveToFirst();

        while (res.isAfterLast() == false){
            String title = res.getString(res.getColumnIndex(BILL_COLUMN_NAME));
            String amount = res.getString(res.getColumnIndex(BILL_COLUMN_AMOUNT));
            String date = res.getString(res.getColumnIndex(BILL_COLUMN_DUEDATE));
            String notification = res.getString(res.getColumnIndex(BILL_NOTIFICATION_CHOICE));

            BillEntry created = new BillEntry(title, amount, date, notification);
            allEntries.add(created);
            res.moveToNext();
        }
        return allEntries;
    }

}