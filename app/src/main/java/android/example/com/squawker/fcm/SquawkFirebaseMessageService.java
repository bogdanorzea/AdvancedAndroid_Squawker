package android.example.com.squawker.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.example.com.squawker.MainActivity;
import android.example.com.squawker.R;
import android.example.com.squawker.provider.SquawkContract;
import android.example.com.squawker.provider.SquawkProvider;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class SquawkFirebaseMessageService extends FirebaseMessagingService {
    private static String LOG_TAG = SquawkFirebaseMessageService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.d(LOG_TAG, "Message data payload: " + data);

            sendNotification(data);
            insertIntoDatabase(data);
        }
    }

    private void sendNotification(Map<String, String> data) {
        String squawkMessage = data.get("message");
        String squawkMessageTruncated = squawkMessage.length() <= 30 ? squawkMessage.substring(0, 30) : squawkMessage;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_duck)
                        .setContentTitle("New message")
                        .setContentText(squawkMessageTruncated)
                        .setAutoCancel(true);

        Intent resultIntent = new Intent(this, MainActivity.class);

        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_CANCEL_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.notify(1, mBuilder.build());
    }

    private void insertIntoDatabase(final Map<String, String> data) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                String squawkMessage = data.get("message");

                ContentValues cv = new ContentValues();
                cv.put(SquawkContract.COLUMN_AUTHOR, data.get("author"));
                cv.put(SquawkContract.COLUMN_AUTHOR_KEY, data.get("authorKey"));
                cv.put(SquawkContract.COLUMN_MESSAGE, squawkMessage);
                cv.put(SquawkContract.COLUMN_DATE, data.get("date"));

                getContentResolver().insert(SquawkProvider.SquawkMessages.CONTENT_URI, cv);
                return null;
            }
        }.execute();
    }
}
