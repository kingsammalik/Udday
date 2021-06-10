package com.samapps.udday;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import java.io.File;

public class MainActivity extends AppCompatActivity implements LifecycleOwner {

    private static final String TAG = MainActivity.class.getName();
    EditText url;
    Button download;
    ProgressBar progressBar;
    private String Filename = "";
    private String Filepath = "";
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 54654;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = findViewById(R.id.url);
        download = findViewById(R.id.download);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(100);
        download.setOnClickListener(view -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            startActivityForResult(Intent.createChooser(i, "Choose directory"), 9999);
        });
        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode) {
            case 9999:
                assert data != null;
                Uri uri = data.getData();
                Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                        DocumentsContract.getTreeDocumentId(uri));
                Filepath = FileUtil.getPath(this, docUri);
                //Toast.makeText(this,"path is "+path,Toast.LENGTH_SHORT).show();
                openDialog();
                break;
        }
    }

    void openDialog(){
         EditText inputEditTextField = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("File Name")
                .setMessage("Enter File Name")
                .setView(inputEditTextField)
                .setCancelable(false)
                .setPositiveButton("OK", null)
                .setNegativeButton("Cancel", null)
                .create();
        dialog.setOnShowListener(dialogInterface -> {

            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                if (inputEditTextField.getText().toString().trim().isEmpty()){
                    Toast.makeText(this, "File name is empty",Toast.LENGTH_SHORT).show();
                }
                else {
                    Filename = inputEditTextField.getText().toString();
                    Filename = Filename + url.getText().toString().substring(url.getText().toString().lastIndexOf("."));
                    Log.e(TAG, "openDialog: file name "+Filename );
                    startService(DownloadService.getDownloadService(this, url.getText().toString(), Filepath, Filename));
                    startObserving();
                    dialog.dismiss();
                }

            });
        });
        dialog.show();
    }

    private void startObserving() {
        DownloadService.getprogress().observe(this, integer -> {
            Log.e(TAG, "startObserving: "+integer );
            progressBar.setProgress(integer);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                DirectoryHelper.createDirectory(this);
        }
    }
}