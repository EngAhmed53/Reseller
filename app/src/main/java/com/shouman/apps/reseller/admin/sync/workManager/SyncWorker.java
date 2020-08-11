package com.shouman.apps.reseller.admin.sync.workManager;

//import android.content.Context;
//
//import androidx.annotation.NonNull;
//import androidx.work.Worker;
//import androidx.work.WorkerParameters;
//
//import com.shouman.apps.hawk.sync.tasks.SyncTask;
//
//import io.paperdb.Paper;
//
//public class SyncWorker extends Worker {
//
//    public SyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
//        super(context, workerParams);
//    }
//
//    @NonNull
//    @Override
//    public Result doWork() {
//        Paper.init(getApplicationContext());
//        SyncTask.syncData(getApplicationContext());
//        return Result.success();
//    }
//}
