package ovh.shr.sportsfun.sportsfunapplication.utilities;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {

    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static String getColoredSpanned(String text, String color) {
        String input = "<font color=" + color + ">" + text + "</font>";
        return input;
    }


    public static String hex(byte[] array) {
    	StringBuffer sb = new StringBuffer();
    	for (int i = 0; i < array.length; ++i) {
    		sb.append(Integer.toHexString((array[i]& 0xFF) | 0x100).substring(1,3));
    	}
    	return sb.toString();
    }

    public static String MD5Hex (String message) {
    	try {
    		MessageDigest md = MessageDigest.getInstance("MD5");
    		return hex (md.digest(message.getBytes("CP1252")));
        } catch (NoSuchAlgorithmException e) {
        } catch (UnsupportedEncodingException e) {
        }
      return null;
    }

    public static String Gravatar(String url, int size) {
        return url + "?s=" + size;
    }

    public static void ToastMessage(final Activity activity, final CharSequence message) {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(activity.getApplicationContext(), message, Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }
}
