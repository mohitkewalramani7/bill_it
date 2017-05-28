package apps.mohit.billit;

/**
 * This class is the type class for a Bill. It has the associated properties as fields which
 * are then stored in the local SQLite database
 *
 * @author Mohit Kewalramani
 * @version 2.0
 * @since 2017-05-10
 */
public class BillEntry {

    private String title;
    private String amount;
    private String date;
    private String notification;

    public BillEntry(String title, String amount, String date, String notification){
        this.title = title;
        this.amount = amount;
        this.date = date;
        this.notification = notification;
    }

    public String getTitle(){
        return title;
    }

    public String getAmount(){
        return amount;
    }

    public String getDate(){
        return date;
    }

    public String getNotification(){
        return notification;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setAmount(String amount){
        this.amount = amount;
    }

    public void setDate(String date){
        this.date = date;
    }

    public void setNotification(String notification){
        this.notification = notification;
    }

}
