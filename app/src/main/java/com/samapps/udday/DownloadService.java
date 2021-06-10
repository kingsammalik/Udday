package com.samapps.udday;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class DownloadService extends IntentService {

    private static final String DOWNLOAD_PATH = "Download_path";
    private static final String DESTINATION_PATH = "Destination_path";
    private static final String FILENAME = "FileName";
    private static final String TAG = DownloadService.class.getName();
    private static DownloadManager downloadManager;
    private long downloadID;
    private static final MutableLiveData<Integer> downloadProgress = new MutableLiveData<>();
    Cursor cursor;

    public DownloadService() {
        super("DownloadService");
    }

    public static Intent getDownloadService(final @NonNull Context callingClassContext, final @NonNull String downloadPath, final @NonNull String destinationPath, final @NonNull String fileName) {
        return new Intent(callingClassContext, DownloadService.class)
                .putExtra(DOWNLOAD_PATH, downloadPath)
                .putExtra(DESTINATION_PATH, destinationPath)
                .putExtra(FILENAME, fileName);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.e(TAG, "onHandleIntent: " );
        String downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
        String destinationPath = intent.getStringExtra(DESTINATION_PATH);
        String fileName = intent.getStringExtra(FILENAME);
        startDownload(downloadPath, destinationPath, fileName);
    }

    private void startDownload(String downloadPath, String destinationPath, String fileName) {
        Log.e(TAG, "downloading file" );
        Uri uri = Uri.parse(downloadPath);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);  // Tell on which network you want to download file.
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  // This will show notification on top when downloading the file.
        request.setTitle("Downloading a file"); // Title for notification.
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalFilesDir(getBaseContext(),destinationPath, fileName);  // Storage directory path
        downloadManager = ((DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE));
        downloadID = downloadManager.enqueue(request); // This will start downloading
        startAppDownload();
    }
    private void startAppDownload() {


        new Thread(() -> {
            boolean isDownloading = true;
            int downloadStatus, totalBytesDownloaded, totalBytes;

            try {
                while (isDownloading) {
                    DownloadManager.Query q = new DownloadManager.Query();
                    q.setFilterById(downloadID);
                    Cursor cursor = downloadManager.query(q);
                    cursor.moveToFirst();
                    int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                    cursor.moveToFirst();
                    int bytes_downloaded = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));

                    if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                        isDownloading = false;
                    }

                    Log.e(TAG, "startAppDownload: "+bytes_downloaded );
                    int lastProgressValue = (int) ((bytes_downloaded * 100l) / bytes_total);
                    downloadProgress.postValue(lastProgressValue);

                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static LiveData<Integer> getprogress(){
        return downloadProgress;
    }

}