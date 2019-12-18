package com.badrul.awla;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.ServerResponse;
import net.gotev.uploadservice.UploadInfo;
import net.gotev.uploadservice.UploadNotificationConfig;
import net.gotev.uploadservice.UploadServiceSingleBroadcastReceiver;
import net.gotev.uploadservice.UploadStatusDelegate;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

import pub.devrel.easypermissions.EasyPermissions;


public class RecordInterview extends AppCompatActivity implements View.OnClickListener,EasyPermissions.PermissionCallbacks,YouTubePlayer.OnInitializedListener, UploadStatusDelegate {

    private Button buttonChoose,buttonRecord;
    private Button buttonUpload;
    private TextView textView,textTerm;
    private TextView textViewResponse;
    private static final String TAG = "AndroidUploadService";

    private UploadServiceSingleBroadcastReceiver uploadReceiver;
    Uri uri;
    //VideoView showVideo;

    //private String pathToStoredVideo;

    private static final int SELECT_VIDEO = 3;
    private static final int RECORD_VIDEO = 10;

    private String selectedPath;

    private YouTubePlayerFragment playerFragment;
    private YouTubePlayer mPlayer;

    private String YouTubeKey = "";

    private MyPlayerStateChangeListener playerStateChangeListener;
    String userID;
    String jobID;
    String jobTitle,companyTitle;
    TextToSpeech myTTS;
    final String question = "Please tell more about yourself";


    public static final String UPLOADVIDEO_URL= "http://awla.senangpark.com/api/uploadvid.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_interview);

        uploadReceiver = new UploadServiceSingleBroadcastReceiver(this);

        buttonChoose = findViewById(R.id.buttonChoose);
        buttonUpload = findViewById(R.id.buttonUpload);
        buttonRecord = findViewById(R.id.record);
        //showVideo = findViewById(R.id.videoView);
        TextView showJobTitle = findViewById(R.id.showJobTitle);
        TextView showCompanyTitle = findViewById(R.id.showCompanyTitle);


        textView = findViewById(R.id.textView);
        textViewResponse = findViewById(R.id.textViewResponse);
        textTerm = findViewById(R.id.termServ);



        myTTS = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    // replace this Locale with whatever you want
                    Locale localeToUse = new Locale("en","UK");
                    myTTS.setLanguage(localeToUse);
                    myTTS.speak(question, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });


        SharedPreferences sharedPreferences = getSharedPreferences(Config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        userID = sharedPreferences.getString(Config.U_USER_ID, "Not Available");
        jobID = sharedPreferences.getString(Config.A_JOB_ID, "Not Available");
        jobTitle = sharedPreferences.getString(Config.A_JOB_POSITION, "Not Available");
        companyTitle = sharedPreferences.getString(Config.A_COMPANY_NAME, "Not Available");


        showJobTitle.setText(jobTitle);
        showCompanyTitle.setText(companyTitle);

        playerStateChangeListener = new MyPlayerStateChangeListener();

        playerFragment =
                (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtube_player_fragment);

        playerFragment.initialize(YouTubeKey, this);

        buttonChoose.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);


        buttonRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent videoCaptureIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if(videoCaptureIntent.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(videoCaptureIntent, RECORD_VIDEO);
                }
            }
        });


    }

    private void chooseVideo() {

        isStoragePermissionGranted();



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode== SELECT_VIDEO) {
            if (resultCode == RESULT_OK) {
                System.out.println("SELECT_VIDEO");
                Uri selectedImageUri = data.getData();
                selectedPath = getPath(selectedImageUri);
                buttonUpload.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                textTerm.setVisibility(View.VISIBLE);
                textView.setText("Video Path: "+selectedPath);
                //showVideo.setVideoURI(selectedImageUri);
                //showVideo.start();

            }
        }else if (requestCode== RECORD_VIDEO) {
            if (resultCode == RESULT_OK) {
                uri = data.getData();
                if(EasyPermissions.hasPermissions(RecordInterview.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){

                    //showVideo.setVideoURI(uri);
                   // showVideo.start();
                    selectedPath = getRealPathFromURIPath(uri, RecordInterview.this);
                    buttonUpload.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    textTerm.setVisibility(View.VISIBLE);
                    textView.setText("Video Path: "+selectedPath);
                    //Store the video to your server

                }else{
                    EasyPermissions.requestPermissions(RecordInterview.this, getString(R.string.read_file), 11, Manifest.permission.READ_EXTERNAL_STORAGE);
                }
            }
    }}

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Video.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }
    @Override
    protected void onResume() {
        super.onResume();
        uploadReceiver.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        uploadReceiver.unregister(this);
    }

    public void uploadMultipart() {
        //getting name for the image

        if (selectedPath == null) {

            Toast.makeText(this, "Please move your file to internal storage and retry", Toast.LENGTH_LONG).show();
        } else {
            //Uploading code
            try {
                String uploadId = UUID.randomUUID().toString();

                //Creating a multi part request
                new MultipartUploadRequest(this, uploadId, UPLOADVIDEO_URL)
                        .addFileToUpload(selectedPath, "video") //Adding file
                        .addParameter("name", "test")
                        .addParameter("jobID", jobID)
                        .addParameter("userID", userID)//Adding text parameter to the request
                        .setNotificationConfig(new UploadNotificationConfig())
                        .setMaxRetries(2)
                        .startUpload(); //Starting the upload

            } catch (Exception exc) {
                Toast.makeText(this, exc.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
/*
    private void uploadVideo() {
        class UploadVideo extends AsyncTask<Void, Void, String> {

            ProgressDialog uploading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                uploading = ProgressDialog.show(RecordInterview.this, "Uploading File", "Please wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                uploading.dismiss();
                textViewResponse.setText(Html.fromHtml("<b>Uploaded at <a href='" + s + "'>" + s + "</a></b>"));
                textViewResponse.setMovementMethod(LinkMovementMethod.getInstance());
            }

            @Override
            protected String doInBackground(Void... params) {
                Upload u = new Upload();
                String msg = u.uploadVideo(selectedPath);
                return msg;
            }
        }
        UploadVideo uv = new UploadVideo();
        uv.execute();
    }*/


    @Override
    public void onClick(View v) {
        if (v == buttonChoose) {
            chooseVideo();
        }
        if (v == buttonUpload) {
           uploadMultipart();
        }
    }


    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("Huz","Permission is granted");
                Intent intent = new Intent();
                intent.setType("video/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
                return true;
            } else {

                Log.v("Huz","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Huz","Permission is granted");
            Intent intent = new Intent();
            intent.setType("video/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select a Video "), SELECT_VIDEO);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, RecordInterview.this);
    }
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        if(uri != null){
            if(EasyPermissions.hasPermissions(RecordInterview.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)){

                //showVideo.setVideoURI(uri);
                //showVideo.start();
                selectedPath = getRealPathFromURIPath(uri, RecordInterview.this);
                buttonUpload.setVisibility(View.VISIBLE);
                textView.setVisibility(View.VISIBLE);
                textTerm.setVisibility(View.VISIBLE);
                textView.setText("Video Path: "+selectedPath);

            }
        }
    }
    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        Log.d("Zzz", "User has denied requested permission");
    }

    private String getRealPathFromURIPath(Uri contentURI, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            return contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            return cursor.getString(idx);
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        player.setPlayerStateChangeListener(playerStateChangeListener);

        mPlayer = player;

        //Enables automatic control of orientation
        mPlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);

        //Show full screen in landscape mode always
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);

        //System controls will appear automatically
        mPlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);

        if (!wasRestored) {
            //player.cueVideo("9rLZYyMbJic");
            mPlayer.loadVideo("Et6pvq1eXhU");
        }
        else
        {
            mPlayer.play();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        mPlayer = null;
    }

    @Override
    public void onProgress(Context context, UploadInfo uploadInfo) {

    }

    @Override
    public void onError(Context context, UploadInfo uploadInfo, ServerResponse serverResponse, Exception exception) {

        Toast.makeText(this, "Error.Please Try Again.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCompleted(Context context, UploadInfo uploadInfo, ServerResponse serverResponse) {

        Intent intent = new Intent(RecordInterview.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onCancelled(Context context, UploadInfo uploadInfo) {

        Toast.makeText(this, "Upload Cancelled", Toast.LENGTH_LONG).show();

    }

    private final class MyPlayerStateChangeListener implements YouTubePlayer.PlayerStateChangeListener {

        @Override
        public void onLoading() {
            // Called when the player is loading a video
            // At this point, it's not ready to accept commands affecting playback such as play() or pause()
        }

        @Override
        public void onLoaded(String s) {
            // Called when a video is done loading.
            // Playback methods such as play(), pause() or seekToMillis(int) may be called after this callback.
        }

        @Override
        public void onAdStarted() {
            // Called when playback of an advertisement starts.
        }

        @Override
        public void onVideoStarted() {
            // Called when playback of the video starts.
        }

        @Override
        public void onVideoEnded() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason errorReason) {
            // Called when an error occurs.
        }
    } @Override
    public void onDestroy() {
        super.onDestroy();
        if (myTTS != null) {
            myTTS.stop();
            myTTS.shutdown();
        }



    }
}
