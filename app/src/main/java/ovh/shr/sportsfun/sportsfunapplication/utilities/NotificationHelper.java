package ovh.shr.sportsfun.sportsfunapplication.utilities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.google.gson.JsonObject;

import ovh.shr.sportsfun.sportsfunapplication.R;
import ovh.shr.sportsfun.sportsfunapplication.activity.MainActivity;
import ovh.shr.sportsfun.sportsfunapplication.activity.MessageActivity;

public class NotificationHelper {

    private static final String CHANNEL_1_ID = "channel_one";

    public static void createChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "Channel 1",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.setDescription("This is Channel 1");

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }



    public static void newNotification(Context context, JsonObject jsonObject) {

        Intent newIntent = new Intent(context, MessageActivity.class);

        newIntent.putExtra("partnerName", jsonObject.get("partnerName").getAsString());
        newIntent.putExtra("partnerID", jsonObject.get("partnerID").getAsString());

        PendingIntent contentIntent = PendingIntent.getActivity(context, 1, newIntent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .setContentTitle(jsonObject.get("partnerName").getAsString())
                .setContentText(jsonObject.get("message").getAsString())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(contentIntent)
                .setAutoCancel(true);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.notify(1, mBuilder.build());
    }

    public static void newNotification2(Context context, String title, String message) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_message_black_24dp)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        manager.notify(2, mBuilder.build());
    }
}
