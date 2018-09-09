package ovh.shr.sportsfun.sportsfunapplication.utilities;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {


    public static Date fromISO8601UTC(String str) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try {
            Date dt = dateFormat.parse(str);
            return dt;
        } catch (Exception err) {
            err.printStackTrace();
            return null;
        }
    }

    public static String toString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(date);
    }

}
