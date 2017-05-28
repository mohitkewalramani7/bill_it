package apps.mohit.billit.Activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import apps.mohit.billit.BillEntry;
import apps.mohit.billit.DatabaseHelper;
import apps.mohit.billit.R;

/**
 * This Activity is the launcher activity
 * It is the initial activity that allows the user to arrive at their home screen and make the
 * selection regarding whether to add a new bill, or to view the bills current in their database
 *
 * @author Mohit Kewalramani
 * @version 2.0
 * @since 2017-05-10
 */
public class MainActivity extends AppCompatActivity {

    // Helper class to access the database
    private DatabaseHelper databaseHelper = new DatabaseHelper(this);

    /**
     * This method is initialized when the Activity starts up. Today's date is shown on the
     * screen for the User, the number of bills in the database is also posted to the home
     * screen for the User's reference, and any notifications to be delivered are done so
     * @param savedInstanceState The saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateTodaysDate();
        populateSummarySentence();
        relayNotifications();
    }

    /**
     * This is the helper method that populates today's date
     */
    private void populateTodaysDate(){
        TextView dateView = (TextView) findViewById(R.id.dateView);
        String currentDate = DateFormat.getDateInstance().format(new Date());
        dateView.setText(String.format("Today's Date: %s", currentDate));
    }

    /**
     * This is the helper method that populates the summary sentence which relays the total
     * number of bills currently in the database
     */
    private void populateSummarySentence(){
        TextView countMessage = (TextView)findViewById(R.id.countData);
        SQLiteDatabase readableDatabase = databaseHelper.getReadableDatabase();
        Cursor cursor = readableDatabase.rawQuery("select count(*) from bills", null);
        cursor.moveToFirst();
        String billCount = cursor.getString(0);
        countMessage.setText(String.format("You Have %s Bills On Your List", billCount));
    }

    /**
     * This is a helper method that relays any notifications to the user based on the
     * notification choice that was set once the bill was created/updated. It cycles
     * through all bills and relays the notifications for the relevant ones
     */
    private void relayNotifications() {
        for (BillEntry entry : databaseHelper.getAllBillsInFormat()) {
            String notificationPreference = entry.getNotification();
            switch (notificationPreference) {
                case "No Notifications":
                    break;
                case "3 Days Before Due":
                    dateComparison(todayDateAddedForComparison(3), entry.getDate(),
                            entry.getTitle(), entry.getAmount());
                    break;
                case "5 Days Before Due":
                    dateComparison(todayDateAddedForComparison(5), entry.getDate(),
                            entry.getTitle(), entry.getAmount());
                    break;
                case "1 Week Before Due":
                    dateComparison(todayDateAddedForComparison(7), entry.getDate(),
                            entry.getTitle(), entry.getAmount());
                    break;
                case "2 Weeks Before Due":
                    dateComparison(todayDateAddedForComparison(14), entry.getDate(),
                            entry.getTitle(), entry.getAmount());
                    break;
            }
        }
    }

    /**
     * This method compares today's date amended with the due date currently set for the bill
     * in the database. If they are equal, a notification is relayed
     * @param todayDateAmended Today's date forwarded by the number of days indicated in the
     *                         notification choice selected on the bill
     * @param dateInTable The due date in the table
     * @param title The title of the bill as listed in the database
     * @param amount The bill amount due as listed in the database
     */
    private void dateComparison(String todayDateAmended, String dateInTable,
                                String title, String amount){
        if (todayDateAmended.equals(dateInTable)){
            createNotification(title, amount, dateInTable);
        }
    }

    /**
     * This method is the helper method that adds today's date by the number of days
     * that the user requested to see a notification for. (Prior to the bill due date).
     * If today's amended date equals the due date in the database, a notification is relayed
     * to the user
     * @param choice The number of days prior a notification was requested
     * @return str The string value of today's date amended
     */
    private String todayDateAddedForComparison(int choice){
        Integer current_year, current_month, current_day;
        String year_string, month_string, day_string;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, choice);
        current_year = calendar.get(Calendar.YEAR);
        current_month = calendar.get(Calendar.MONTH) + 1;
        current_day = calendar.get(Calendar.DAY_OF_MONTH);

        year_string = String.valueOf(current_year);
        if (current_month < 10){
            month_string = String.format("0%s", current_month);
        }
        else{
            month_string = String.valueOf(current_month);
        }
        if (current_day < 10){
            day_string = String.format("0%s", current_day);
        }
        else{
            day_string = String.valueOf(current_day);
        }
        return String.format("%s-%s-%s", year_string, month_string, day_string);
    }

    /**
     * This helper method creates the notification and pushes it to the user
     * @param title The title of the bill
     * @param amount The amount of the bill
     * @param date The due date of the bill
     */
    private void createNotification(String title, String amount, String date){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.app_logo);
        mBuilder.setContentTitle("You Have A Bill Due");
        mBuilder.setContentText(String.format("Amount : $%s for %s by %s", amount, title, date));
        mBuilder.setAutoCancel(true);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }

    /**
     * Opens the activity to allow the user to add a new bill to the local database
     * @param view The view from which this method is called
     */
    public void addNewBill(View view){
        Intent intent = new Intent(this, AddBill.class);
        startActivity(intent);
    }

    /**
     * This method calls a helper method to allow the user to view all bills due this week
     * @param view The view from which the method is called
     */
    public void viewBillsThisWeekList(View view){

        viewBillsList("thisWeek");
    }

    /**
     * This method calls a helper to allow the user to view all bills due this month
     * @param view The view from which the method is called
     */
    public void viewThisMonthBillList(View view){
        viewBillsList("thisMonth");
    }

    /**
     * This method calls a helper to allow the user to view all bills in the database
     * @param view The view from which the method is called
     */
    public void viewAllBillsList(View view){
        viewBillsList("allBills");
    }

    /**
     * This method is a helper method which initializes an Activity to allow users to
     * see a ListView populated with bills based on their indicated range
     * @param dateRange the time frame to query bills from the database from
     */
    private void viewBillsList(String dateRange){
        Intent intent = new Intent(this, AllBillsList.class);
        intent.putExtra("viewChoice", dateRange);
        startActivity(intent);
    }

}