package com.shouman.apps.reseller.admin.services;

//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.media.RingtoneManager;
//import android.net.Uri;
//import android.os.Build;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.NotificationCompat;
//import androidx.core.content.ContextCompat;
//
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.messaging.FirebaseMessaging;
//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;
//import com.shouman.apps.hawk.R;
//import com.shouman.apps.hawk.common.Common;
//import com.shouman.apps.hawk.data.database.firebaseRepo.FirebaseAuthRepo;
//import com.shouman.apps.hawk.preferences.UserPreference;
//import com.shouman.apps.hawk.ui.main.companyUI.navDrawer.MainActivity;
//
//import java.util.Map;

//public class MyFirebaseMessagingService extends FirebaseMessagingService {
//    private static final String JSON_KEY_SALESMAN_NAME = "salesman_name";
//    private static final String JSON_KEY_CUSTOMER_NAME = "customer_name";
//    private static final String JSON_KEY_ACTIVITY_TYPE = "activity_type";
//    private static final String APP_NOTIFICATION_CHANNEL_ID = "55478";
//
//    @Override
//    public void onNewToken(@NonNull String s) {
//        super.onNewToken(s);
//        String userType = UserPreference.getInstance().getUserType(getApplicationContext());
//
//        if (userType != null && userType.equals("company_account")) {
//            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//            if (user != null) {
//                String userUID = user.getUid();
//                FirebaseAuthRepo.getInstance().addNewTokenToCompanyUser(userUID, s);
//            }
//        }
//        Log.e("newToken", s);
//    }
//
//    @Override
//    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        Log.e("FCM", "onMessageReceived: " + remoteMessage.getData().get(JSON_KEY_CUSTOMER_NAME));
//        sendNotification(remoteMessage.getData());
//
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("New_Customer");
//
//    }
//
//    private void sendNotification(Map<String, String> data) {
//        Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        // Create the pending intent to launch the activity
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        String salesmanName = data.get(JSON_KEY_SALESMAN_NAME);
//        String customerName = data.get(JSON_KEY_CUSTOMER_NAME);
//        String activityType = data.get(JSON_KEY_ACTIVITY_TYPE);
//        String title = null;
//        String body = null;
//
//        if (activityType != null) {
//
//            switch (activityType) {
//                case Common.ACTIVITY_NEW_CUSTOMER:
//                    title = getString(R.string.notify_new_customer_title);
//                    body = salesmanName + getString(R.string.notify_new_customer_body) + customerName + "\".";
//
//                    break;
//                case Common.ACTIVITY_NEW_VISIT:
//                    title = getString(R.string.notify_new_visit_title);
//                    body = salesmanName + getString(R.string.notify_new_visit_body) + customerName + "\".";
//
//                    break;
//                case Common.ACTIVITY_NEW_SALESMAN:
//                    title = getString(R.string.notify_new_salesman_title);
//                    body = salesmanName + getString(R.string.notify_new_salesman_body);
//
//                    break;
//            }
//
//        } else return;
//
//        NotificationManager notificationManager = (NotificationManager)
//                getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (notificationManager == null) return;
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel mChannel = new NotificationChannel(
//                    APP_NOTIFICATION_CHANNEL_ID,
//                    getApplicationContext().getString(R.string.main_notification_channel_name),
//                    NotificationManager.IMPORTANCE_HIGH);
//
//            notificationManager.createNotificationChannel(mChannel);
//        }
//
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, APP_NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_stat_drawing)
//                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary))
//                .setContentTitle(title)
//                .setContentText(body)
//                .setChannelId(APP_NOTIFICATION_CHANNEL_ID)
//                .setAutoCancel(true)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
//                .setDefaults(Notification.DEFAULT_ALL)
//                .setSound(defaultSoundUri)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setContentIntent(pendingIntent);
//
//        notificationManager.notify(Integer.parseInt(APP_NOTIFICATION_CHANNEL_ID) /* ID of notification */, notificationBuilder.build());
//    }
//
//}
