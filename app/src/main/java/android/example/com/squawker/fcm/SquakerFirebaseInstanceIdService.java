package android.example.com.squawker.fcm;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class SquakerFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String LOG_TAG = SquakerFirebaseInstanceIdService.class.getSimpleName();

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        Log.d(LOG_TAG, "Firebase log is: " + FirebaseInstanceId.getInstance().getToken());
    }
}
