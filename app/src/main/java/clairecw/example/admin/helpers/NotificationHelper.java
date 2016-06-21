package clairecw.example.admin.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import com.firebase.client.AuthData;
import com.firebase.client.Firebase;

/**
 * Created by AKiniyalocts on 1/15/15.
 * <p/>
 * This class is just created to help with notifications, definitely not necessary.
 */
public class NotificationHelper {
    public final static String TAG = NotificationHelper.class.getSimpleName();

    private WeakReference<Context> mContext;
    private int code;

    public NotificationHelper(Context context, int code) {
        this.code = code;
        this.mContext = new WeakReference<>(context);
    }

    public void createUploadingNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_upload);
        mBuilder.setContentTitle(mContext.get().getString(clairecw.example.admin.superclassy.R.string.notification_progress));


      //  mBuilder.setColor(mContext.get().getResources().getColor(R.color.primary));

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mContext.get().getString(clairecw.example.admin.superclassy.R.string.app_name).hashCode(), mBuilder.build());

    }

    public void createUploadedNotification(ImageResponse response, String desc) {
        final Firebase myFirebaseRef = new Firebase("https://superclassy.firebaseio.com/");
        AuthData user = myFirebaseRef.getAuth();

        if (code == 0) {
            Firebase newRef = myFirebaseRef.child("users").child(user.getUid()).child("files").push();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("url", response.data.link);
            map.put("desc", desc);
            newRef.setValue(map);
        }
        else {
            myFirebaseRef.child("users").child(user.getUid()).child("profPic").setValue(response.data.link);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_gallery);
        mBuilder.setContentTitle(mContext.get().getString(clairecw.example.admin.superclassy.R.string.notifaction_success));

        mBuilder.setContentText(response.data.link);
        //Toast.makeText(mContext.get(), response.data.link, Toast.LENGTH_LONG).show();

        //mBuilder.setColor(mContext.get().getResources().getColor(R.color.primary));


        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.data.link));
        PendingIntent intent = PendingIntent.getActivity(mContext.get(), 0, resultIntent, 0);
        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);

        Intent shareIntent = new Intent(Intent.ACTION_SEND, Uri.parse(response.data.link));
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, response.data.link);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pIntent = PendingIntent.getActivity(mContext.get(), 0, shareIntent, 0);
        mBuilder.addAction(new NotificationCompat.Action(clairecw.example.admin.superclassy.R.drawable.abc_ic_menu_share_mtrl_alpha,
                mContext.get().getString(clairecw.example.admin.superclassy.R.string.notification_share_link), pIntent));

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mContext.get().getString(clairecw.example.admin.superclassy.R.string.app_name).hashCode(), mBuilder.build());
    }

    public void createFailedUploadNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setContentTitle(mContext.get().getString(clairecw.example.admin.superclassy.R.string.notification_fail));


       // mBuilder.setColor(mContext.get().getResources().getColor(R.color.primary));

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mContext.get().getString(clairecw.example.admin.superclassy.R.string.app_name).hashCode(), mBuilder.build());
    }
}
