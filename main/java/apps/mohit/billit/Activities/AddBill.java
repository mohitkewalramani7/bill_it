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
 * This Activity is where users can add bills to their personal list.
 * The activity gives the choice between choosing their bill title,
 * bill amount, date due, and the notification choice
 *
 * @author Mohit Kewalramani
 * @version 2.0
 * @since 2017-05-10
 */
public class AddBill extends AppCompatActivity {

    private String listRangeViewingChoice;   // The date range viewing choice of the billList the user made

    /**
     * This method is the method that is called once this Activity is
     * has been initialized
     * @param savedInstanceState The instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bill);
        listRangeViewingChoice = getIntent().getStringExtra("viewChoice");
    }

    /**
     * This method initializes a view for the User to select a date desired
     * for the bill due date
     * @param view The view from which the method is called
     */
    public void chooseDueDate(View view){
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        datePickerFragment.show(getFragmentManager(), "datePicker");
    }

    /**
     * The method saves the bill to the database as a final exit command
     * from this activity. Once clicked, the view would proceed to the
     * ListView for the User to see all saved bills
     * @param view The view from which the method is called
     */
    public void saveBill(View view){
        EditText billTitle = (EditText) findViewById(R.id.nameEntry);
        EditText billAmount = (EditText) findViewById(R.id.amountEntry);
        TextView choosenDate = (TextView) findViewById(R.id.choosenDate);

        RadioGroup notificationChoice = (RadioGroup) findViewById(R.id.notificationChoiceRadioGroup);
        RadioButton selectedChoice =
                (RadioButton) findViewById(notificationChoice.getCheckedRadioButtonId());

        String billTitleString = billTitle.getText().toString();
        String billAmountString = billAmount.getText().toString();
        String chosenDateString = choosenDate.getText().toString();
        String notificationChoiceString = selectedChoice.getText().toString();

        if (!allFieldsEntered(billTitleString, billAmountString, chosenDateString,
                notificationChoiceString)){
            Toast.makeText(
                    getApplicationContext(),
                    "Don't Leave Any Fields Blank",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        databaseHelper.insertBill(
                billTitleString,
                billAmountString,
                chosenDateString,
                notificationChoiceString
        );
        finish();
        launchAllBillsList();
    }

    /**
     * A helper method to check whether or not all fields have been filled in yet
     * @param billTitle The title inserted of the bill
     * @param billAmount The amount entered for the bill
     * @param chosenDate The entered date due for the bill
     * @param notificationChoice The notification choice selected for the bill
     * @return boolean True if all fields are filled in, False if not
     *
     */
    private boolean allFieldsEntered(String billTitle, String billAmount,
                                     String chosenDate, String notificationChoice){
        return !(billTitle.equals("") ||
                billAmount.equals("") ||
                chosenDate.equals("") ||
                notificationChoice.equals(""));
    }

    /**
     * A helper method to help launch the All Bills List based on the view choice
     * that the user has set
     */
    private void launchAllBillsList(){
        Intent intent = new Intent(this, AllBillsList.class);
        if (listRangeViewingChoice == null){
            listRangeViewingChoice = "allBills";
        }
        intent.putExtra("viewChoice", listRangeViewingChoice);
        startActivity(intent);
    }

    /**
     * Cancels the current activity and returns to the previous one
     * @param view The view from which the method is called
     */
    public void cancelEntry(View view){
        this.finish();
    }

    /**
     * The local class that allows the User to select the due date for the bill
     * that is being edited
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        /**
         * The method that is executed once this helper class is initialized
         * @param savedInstanceState The saved instance state
         * @return DatePickerDialog
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
         * This method is executed once the desired date is chosen and the select
         * button is clicked
         * @param view The view from which the method is called
         * @param year The year selected
         * @param monthOfYear The month selected
         * @param dayOfMonth The day selected
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
            TextView chosenDate = (TextView) getActivity().findViewById(R.id.choosenDate);
            chosenDate.setText(chosenDateString);
        }
    }

}
