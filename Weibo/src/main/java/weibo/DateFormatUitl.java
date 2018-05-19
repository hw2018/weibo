package weibo;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author huang.wei  on 2018/5/14
 */
public class DateFormatUitl {

    public static Long getTimeStamp(String time, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(time).getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getDate(Long time, String format) {
        if (format == null || format.equals("")) {
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(time)));
    }
}
