package com.shouman.apps.reseller.admin.sync.tasks;

//import android.content.Context;
//import android.util.Log;
//
//import com.google.firebase.database.FirebaseDatabase;
//import com.shouman.apps.hawk.data.database.firebaseRepo.FirebaseSalesRepo;
//import com.shouman.apps.hawk.data.model.Customer;
//import com.shouman.apps.hawk.data.model.DailyLogEntry;
//import com.shouman.apps.hawk.data.model.Visit;
//
//import java.util.List;
//
//import io.paperdb.Book;
//import io.paperdb.Paper;
//
//public class SyncTask {
//    private static final String TAG = "UploadLocalDataTask";
//
//    synchronized public static void syncData(Context context) {
//        FirebaseDatabase.getInstance().goOffline();
//        FirebaseDatabase.getInstance().goOnline();
//        uploadLocalLog(context);
//        uploadNewCustomers(context);
//        uploadNewVisits(context);
//    }
//
//    synchronized private static void uploadNewCustomers(Context context) {
//        Book allNewCustomersBook = Paper.book("New_Customers");
//        if (allNewCustomersBook.getAllKeys().isEmpty()) return;
//
//        FirebaseSalesRepo salesRepo = FirebaseSalesRepo.getInstance();
//        for (String key : allNewCustomersBook.getAllKeys()) {
//            Customer customer = allNewCustomersBook.read(key);
//            salesRepo.uploadCustomers(context, customer, key);
//            allNewCustomersBook.delete(key);
//            Log.e(TAG, "uploadNewCustomers: upload customer" + customer.getN());
//        }
//    }
//
//    synchronized private static void uploadNewVisits(Context context) {
//        Book allNewVisitsBook = Paper.book("visits_list");
//        if (allNewVisitsBook.getAllKeys().isEmpty()) return;
//
//        FirebaseSalesRepo salesRepo = FirebaseSalesRepo.getInstance();
//        for (String key : allNewVisitsBook.getAllKeys()) {
//            String[] customerData = key.split(", ");
//            String customerUID = customerData[0];
//            String customerName = customerData[1];
//            Visit visit = allNewVisitsBook.read(key);
//            salesRepo.uploadVisits(context, visit, customerUID, customerName);
//            allNewVisitsBook.delete(key);
//            Log.e(TAG, "uploadNewCustomers: upload visit" + customerUID);
//        }
//    }
//
//    synchronized private static void uploadLocalLog(Context context) {
//        Book allLocalLog = Paper.book("Daily_Log");
//        if (allLocalLog.getAllKeys().isEmpty()) return;
//
//        FirebaseSalesRepo salesRepo = FirebaseSalesRepo.getInstance();
//        for (String date : allLocalLog.getAllKeys()) {
//            List<DailyLogEntry> logEntries = Paper.book("Daily_Log").read(date);
//            for (DailyLogEntry dailyLogEntry : logEntries) {
//                salesRepo.uploadLocalLog(context, date, dailyLogEntry);
//            }
//            allLocalLog.delete(date);
//            Log.e(TAG, "uploadLocalLog: upload log" + date);
//        }
//    }
//}
