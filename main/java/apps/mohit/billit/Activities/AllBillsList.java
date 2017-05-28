package apps.mohit.billit.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import apps.mohit.billit.BillListAdapter;
import apps.mohit.billit.DatabaseHelper;
import apps.mohit.billit.R;

/**
 * This activity enables the user to view all bills that are included on the
 * local database. The user has the option to view all the bills that are due
 * this week, this month, and finally show all bills in the database
 *
 * @author Mohit Kewalramani
 * @version 2.0
 * @since 2017-05-10
 */
public class AllBillsList extends AppCompatActivity {

    private String billRangeChoice;           // The date range choice the user wants to see the bills from
    private DatabaseHelper databaseHelper;    // The database helper class that lets us access the database
    private TextView activityTitle;                // The title of the Activity (Positioned at the top)
    private BillListAdapter billListAdapter;       // The ListAdapter which displays the bill list for viewing
    private ListView billListView;                 // The Listview that displays the bills for viewing

    /**
     * This method is executed once this Activity is initialized
     * Initializes the global variables and sets up the ListView to ensure that
     * all the data from the local SQLite database has been populated
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_bills_list);

        billRangeChoice = getIntent().getStringExtra("viewChoice");
        databaseHelper = new DatabaseHelper(this);
        activityTitle = (TextView) findViewById(R.id.choiceTitle);
        billListAdapter = new BillListAdapter();
        billListView = (ListView) findViewById(R.id.allItemsListView);

        populateDataAndTitle();
        billListView.setAdapter(billListAdapter);
        allowEditFunctionality();
        allowDeleteFunctionality();
    }

    /**
     * This method populates the bill list adapter with the data records from
     * the local database based on the range mentioned by the user
     */
    private void populateDataAndTitle(){
        switch (billRangeChoice){
            case "allBills":
                billListAdapter.billList = databaseHelper.getAllBillsInFormat();
                activityTitle.setText("All Entered Bills");
                break;
            case "thisMonth":
                billListAdapter.billList = databaseHelper.getThisMonthBillsInFormat();
                activityTitle.setText("Bills Due In A Month");
                break;
            case "thisWeek":
                billListAdapter.billList = databaseHelper.getThisWeekBillsInFormat();
                activityTitle.setText("Bills Due In A Week");
                break;
        }
    }

    /**
     * Allows the user to single click on any record, and be given the choice
     * to edit a record. This method initializes the listview to have the
     * listener set up for a single click
     */
    private void allowEditFunctionality(){
        final AlertDialog.Builder editAlertDialog = new AlertDialog.Builder(this);
        billListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Cursor cursor = databaseHelper.getReadableDatabase()
                        .rawQuery("select * from bills", null);
                cursor.moveToPosition(position);
                editAlertDialog.setTitle("Edit Bill");
                editAlertDialog.setMessage("Do you want to edit this Bill?");
                editAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Integer id = cursor.getInt(0);
                        String title = cursor.getString(1);
                        String amount = cursor.getString(2);
                        String date = cursor.getString(3);
                        String notification = cursor.getString(4);
                        openEditBill(id, title, amount, date, notification);
                    }
                });
                editAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                editAlertDialog.create();
                editAlertDialog.show();
            }
        });
    }

    /**
     * This method opens a new activity where the user can edit the bill's details
     * as entered in the database. The details are relayed using an intent
     * @param id The id of the bill in the database
     * @param title The title of the bill in the database
     * @param amount The amount as listed in the database
     * @param date The due date of the bill as listed in the database
     * @param notification The notification choice of the bill in the database
     */
    private void openEditBill(Integer id, String title, String amount, String date,
                              String notification){
        Intent intent = new Intent(this, EditBill.class);
        intent.putExtra("id", id);
        intent.putExtra("title", title);
        intent.putExtra("amount", amount);
        intent.putExtra("date", date);
        intent.putExtra("notification", notification);
        intent.putExtra("viewChoice", billRangeChoice);
        startActivity(intent);
    }

    /**
     * This method allows the user to perform a long click on any item in the database
     * in order to delete it. This method sets up a long click listener for the listview.
     * Following a long click, an alert dialog would be displayed if the user choses to
     * delete a record
     * @return boolean True if the operation completes successfully
     */
    private void allowDeleteFunctionality(){
        final AlertDialog.Builder deleteAlertDialog = new AlertDialog.Builder(this);
        billListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = databaseHelper.getReadableDatabase()
                        .rawQuery("select * from bills", null);
                cursor.moveToPosition(position);
                final Integer calculatedId = cursor.getInt(0);

                deleteAlertDialog.setTitle("Delete Bill");
                deleteAlertDialog.setMessage("Are you sure you want to delete this Bill?");

                deleteAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteEntry(calculatedId);

                        billListAdapter.billList = databaseHelper.getAllBillsInFormat();
                        billListAdapter.notifyDataSetChanged();
                    }
                });

                deleteAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                deleteAlertDialog.create();
                deleteAlertDialog.show();
                return true;
            }
        });
    }

    /**
     * This method opens a new activity for the user to add a new bill into the local
     * database. This is a piece of functionality that is embedded into this Activity,
     * so the user doesn't have to go back to the home screen to add a bill
     * @param view The view from which this method is called
     */
    public void newBill(View view){
        Intent intent = new Intent(this, AddBill.class);
        intent.putExtra("viewChoice", billRangeChoice);
        startActivity(intent);
    }

}