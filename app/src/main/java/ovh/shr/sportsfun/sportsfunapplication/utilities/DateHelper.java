package ovh.shr.sportsfun.sportsfunapplication.utilities;



import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

    public static String toCustomString(Date date, String pattern) {
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

    public static String toString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(date);
    }

    public static String toString2(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy");
        return dateFormat.format(date);
    }

    public static String fromCalendar(final Calendar calendar) {
        Date date = calendar.getTime();
        String formatted = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .format(date);
        return formatted;
    }

    public static String now() {
        return fromCalendar(GregorianCalendar.getInstance());
    }

}
