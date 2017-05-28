package apps.mohit.billit.Activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import apps.mohit.billit.DatabaseHelper;
import apps.mohit.billit.R;

/**
 * This activity allows the user to edit one of the bills that are stored in the local
 * SQLite database. The database correspondingly updates the record once all changes are
 * finalized
 *
 * @author Mohit Kewalramani
 * @version 2.0
 * @since 2017-05-10
 */
public class EditBill extends AppCompatActivity {

    private String listRangeViewingChoice;       // The date range viewing choice of the billList the user made
    private Integer currentBillId;               // The Database ID of the selected list item to edit
    private TextView originalDate;               // The original due date the user set while creating the bill
    private DatabaseHelper databaseHelper;       // Helper class to access the database

    /**
     * This method is called once this Activity is initialized
     * It constructs the global variables and uploads the original bill data
     * so the User can make changes
     * @param savedInstanceState the saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_bill);

        listRangeViewingChoice = getIntent().getStringExtra("viewChoice");
        currentBillId = getIntent().getIntExtra("id", 0);
        originalDate = (TextView) findViewById(R.id.updatedChosenDate);

        String originalTitleText = getIntent().getStringExtra("title");
        String originalAmountText = getIntent().getStringExtra("amount");
        String originalDateText = getIntent().getStringExtra("date");
        String originalNotificationText = getIntent().getStringExtra("notification");

        uploadOriginalBillData(
                originalTitleText,
                originalAmountText,
                originalDateText,
                originalNotificationText
        );
    }

    /**
     * This is a helper method that uploads the details of the bill to the current
     * Activity
     * @param originalTitleText The current title of the bill in the database
     * @param originalAmountText The current amount of the bill in the database
     * @param originalDateText The due date of the bill in the database
     * @param originalNotificationText The notification choice of the bill in the database
     */
    private void uploadOriginalBillData(String originalTitleText,
                                        String originalAmountText,
                                        String originalDateText,
                                        String originalNotificationText) {
        setOriginalBillTitle(originalTitleText);
        setOriginalBillAmount(originalAmountText);
        setOriginalBillDate(originalDateText);
        setOriginalNotificationChoice(originalNotificationText);
    }

    /**
     * The helper method that sets the title of the bill on the edit screen
     * @param originalTitleText The title of the bill in the database
     */
    private void setOriginalBillTitle(String originalTitleText){
        EditText originalTitle = (EditText)findViewById(R.id.editBillTitleEntry);
        originalTitle.setText(originalTitleText);
    }

    /**
     * The helper method that sets the amount of the bill on the edit screen
     * @param originalAmountText The amount of the bill in the database
     */
    private void setOriginalBillAmount(String originalAmountText){
        EditText originalAmount = (EditText) findViewById(R.id.edit_bill_amount_entry);
        originalAmount.setText(originalAmountText);
    }

    /**
     * The helper method that sets the due date of the bill on the edit screen
     * @param originalDateText The due date of the bill in the database
     */
    private void setOriginalBillDate(String originalDateText){
        originalDate.setText(originalDateText);
    }

    /**
     * The helper method that sets the pre-selects the notification choice of the originally
     * selected notification choice for the bill that currently is stored in the database. The
     * user can view the originally selected choice, and proceed to make any requested changes
     * @param originalNotificationText The originally selected notification choice
     */
    private void setOriginalNotificationChoice(String originalNotificationText){
        switch (originalNotificationText){
            case "No Notifications":
                RadioButton zeroButton = (RadioButton)findViewById(R.id.editNoNotifications);
                zeroButton.setChecked(true);
                break;
            case "3 Days Before Due":
                RadioButton threeDaysButton =
                        (RadioButton)findViewById(R.id.editThreeDayNotifications);
                threeDaysButton.setChecked(true);
                break;
            case "5 Days Before Due":
                RadioButton fiveDaysButton =
                        (RadioButton) findViewById(R.id.editFiveDayNotifications);
                fiveDaysButton.setChecked(true);
                break;
            case "1 Week Before Due":
                RadioButton oneWeekButton = (RadioButton) findViewById(R.id.editOneWeekBefore);
                oneWeekButton.setChecked(true);
                break;
            case "2 Weeks Before Due":
                RadioButton twoWeekButton = (RadioButton)findViewById(R.id.editTwoWeekBefore);
                twoWeekButton.setChecked(true);
                break;
        }
    }

    /**
     * This method opens a view initialized through a helper class that allows the user to pick
     * out the revised due date, if the user wishes to change it
     * @param view The view from which this method is called
     */
    public void allowDateChange(View view){
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * This method cancels the current activity and returns the App to the previous activity
     * @param view The view from which this method is called
     */
    public void cancelEditActivity(View view){
        finish();
    }

    /**
     * This method saves to the database all the changes that were applied once the editing
     * is complete. The database is queried based on the given id, and and fields are all
     * updated
     * @param view The view from which this method is called
     */
    public void updateActivityAfterEdit(View view){
        databaseHelper = new DatabaseHelper(this);
        EditText billTitle = (EditText) findViewById(R.id.editBillTitleEntry);
        EditText billAmount = (EditText) findViewById(R.id.edit_bill_amount_entry);
        TextView billDate = (TextView) findViewById(R.id.updatedChosenDate);
        RadioGroup notificationChoice =
                (RadioGroup) findViewById(R.id.editNotificationChoiceRadioGroup);
        RadioButton selectedChoice =
                (RadioButton) findViewById(notificationChoice.getCheckedRadioButtonId());

        String billTitleString = billTitle.getText().toString();
        String billAmountString = billAmount.getText().toString();
        String billDateString = billDate.getText().toString();
        String notificationChoiceString = selectedChoice.getText().toString();

        if (!allFieldsFilled(billTitleString, billAmountString, billDateString,
                notificationChoiceString)){
            Toast.makeText(
                    getApplicationContext(),
                    "Don't Leave Any Fields Blank",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }
        updateBillInDatabase(currentBillId, billTitleString, billAmountString, billDateString,
                notificationChoiceString);
    }

    /**
     * This is a helper method that checks to see if any fields are left blank. If any
     * fields are left blank, a message is relayed to the user
     * @param billTitleString The entered title
     * @param billAmountString The entered amount
     * @param billDateString The entered due date
     * @param notificationChoiceString The entered notification choice
     * @return boolean returns true if all fields are filled in, false if not
     */
    private Boolean allFieldsFilled(String billTitleString, String billAmountString,
                                    String billDateString, String notificationChoiceString){
        return !(billTitleString.equals("") ||
                billAmountString.equals("") ||
                billDateString.equals("") ||
                notificationChoiceString.equals("")
        );
    }

    /**
     * This is the helper method that updates the local SQLite database provided all fields
     * are filled in. It makes use of the Database Helper class to perform the update.
     * The AllBillsList Activity is launched after the insert so the user can see all the bills
     * in the database
     * @param currentBillId The bill id to update
     * @param billTitleString The new title of the bill
     * @param billAmountString The new amount due of the bill
     * @param billDateString The new due date of the bill
     * @param notificationChoiceString The new notification choice of the bill
     */
    private void updateBillInDatabase(Integer currentBillId,
                                      String billTitleString,
                                      String billAmountString,
                                      String billDateString,
                                      String notificationChoiceString){
        databaseHelper.updateEntry(currentBillId, billTitleString, billAmountString, billDateString,
                notificationChoiceString);
        Intent intent = new Intent(this, AllBillsList.class);
        intent.putExtra("viewChoice", listRangeViewingChoice);
        finish();
        startActivity(intent);
    }

    /**
     * This is a helper class that allows the user to pick out a new due date by showing
     * the DatePicker Fragment
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        /**
         * This method is called when the picker is initialized.
         * It sets the selected date as today
         * @param savedInstanceState the saved instance state
         */
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        /**
         * This method is called when the date is selected by the user and the confirm
         * button is clicked. It relays the selected date to the Activity so it can be
         * updated in the database when confirmed
         * @param view The view from which this method is called
         * @param year The new year selected
         * @param monthOfYear The new month selected
         * @param dayOfMonth The new day selected
         */
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String month_string, day_string;
            monthOfYear = monthOfYear + 1;
            if (monthOfYear < 10){
                month_string = "0" + String.valueOf(monthOfYear);
            }
            else{
                month_string = String.valueOf(monthOfYear);
            }

            if (dayOfMonth < 10){
                day_string = "0" + String.valueOf(dayOfMonth);
            }
            else{
                day_string = String.valueOf(dayOfMonth);
            }
            String chosenDateString = String.format("%s-%s-%s", year, month_string, day_string);
            TextView chosenDate = (TextView) getActivity().findViewById(R.id.updatedChosenDate);
            chosenDate.setText(chosenDateString);
        }
    }

}