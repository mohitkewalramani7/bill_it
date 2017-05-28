package apps.mohit.billit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * This class is the ListView Adapter which populates the ListView with data from the database.
 * This method makes use of an ArrayList to store all database values and to correspondingly
 * populate the ListView
 *
 * @author Mohit Kewalramani
 * @version 2.0
 * @since 2017-05-10
 */
public class BillListAdapter extends BaseAdapter{

    // List of stored BillItems to display on the ListView
    public ArrayList<BillEntry> billList = new ArrayList();

    public BillListAdapter(){

    }

    /**
     * Returns the size of the Arraylist
     * @return int the size of the ArrayList
     */
    @Override
    public int getCount() {
        return billList.size();
    }

    /**
     * Returns the item at the indexed position at the ArrayList
     * @param position The index of the list at which we are looking at
     * @return BillEntry The entry at the given position
     */
    @Override
    public Object getItem(int position) {
        return billList.get(position);
    }

    /**
     * The item's index we are currently looking at
     * @param position The assigned position
     * @return int The position we are looking at
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * This method iterates through the items in the ArrayList and populates the view
     * ListView
     * @param position The index of the List to query
     * @param convertView the view onto which we are writing the bill's data onto
     * @param parent The parent Activity onto which we are writing the bill's data to
     * @return View the view with all the data constructed on
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            convertView = inflater.inflate(R.layout.bill_list_entry, parent, false);
        }
        BillEntry entry = billList.get(position);

        TextView amountView = (TextView) convertView.findViewById(R.id.BillAmountView);
        amountView.setText("$ " + entry.getAmount());

        TextView dateView = (TextView) convertView.findViewById(R.id.BillDateView);
        dateView.setText(entry.getDate());

        TextView titleView = (TextView) convertView.findViewById(R.id.BillTitleView);
        titleView.setText(entry.getTitle());

        return convertView;
    }
}
