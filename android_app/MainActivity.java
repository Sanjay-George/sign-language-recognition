package com.example.vishwashrisairam.opencvcamera2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, View.OnClickListener , View.OnTouchListener {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("MyLibs");
    }

    //JavaCameraView javaCameraView;
    Button btnRecordCtrl,btnSignCtrl;
    Mat mrgba,imgGray,imgCanny,mrgbaT,mrgbaF;
    boolean recording=false;
    private CaptureView mOpenCvCameraView;
    private List<Camera.Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;
    public String result;
    private String fileName=Environment.getExternalStorageDirectory().getPath() +
            "/sample_picture.jpg";
    private int index=0,counter=0;


    BaseLoaderCallback mLoaderCallBack=new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch(status){
                case BaseLoaderCallback.SUCCESS:{
//                    javaCameraView.enableView();
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(MainActivity.this);
                    break;
                }
                default:{
                    super.onManagerConnected(status);
                    break;
                }
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.surface_view);

        /*
        javaCameraView=(JavaCameraView)findViewById(R.id.java_camera_view);
        javaCameraView.setVisibility(SurfaceView.VISIBLE);
        javaCameraView.setCvCameraViewListener(this);
        */
        mOpenCvCameraView=(CaptureView)findViewById(R.id.surface_view);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);

        // Example of a call to a native method

        btnRecordCtrl=(Button)findViewById(R.id.recorder_control);
        btnRecordCtrl.setText("Start");
        btnRecordCtrl.setOnClickListener(this);

        btnSignCtrl=(Button)findViewById(R.id.sign_control);
        btnSignCtrl.setText("Sign");
        btnSignCtrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                index=0;
            }
        });

    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */




    @Override
    protected void onPause() {
        super.onPause();
        /*if(javaCameraView!=null)
            javaCameraView.disableView();*/
        if(mOpenCvCameraView!=null)
            mOpenCvCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(OpenCVLoader.initDebug()) {
            Log.d("MainActivity", "OpenCV Loaded sucessfully");
            mLoaderCallBack.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }else {
            Log.d("MainActivity", "OpenCV not Loaded");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9,getApplicationContext(),mLoaderCallBack);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if(javaCameraView!=null)
//            javaCameraView.disableView();
        if(mOpenCvCameraView!=null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mrgba=new Mat(height,width, CvType.CV_8UC4);
        mrgbaT=new Mat(height,width, CvType.CV_8UC4);
        mrgbaF=new Mat(height,width, CvType.CV_8UC4);
        imgGray=new Mat(height,width, CvType.CV_8UC1);
        imgCanny=new Mat(height,width, CvType.CV_8UC1);
    }

    @Override
    public void onCameraViewStopped() {
        mrgba.release();
        mrgbaT.release();
        mrgbaF.release();
        imgGray.release();
        imgCanny.release();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mrgba=inputFrame.rgba();
//        Imgproc.cvtColor(mrgba,imgGray,Imgproc.COLOR_RGB2YCrCb);

       // NativeClass.convertGray(mrgba.getNativeObjAddr(),imgGray.getNativeObjAddr());

//        Imgproc.Canny(imgGray,imgCanny,50,150);
//        return mrgba;

        return mrgba;
//        return imgCanny;
    }

    @Override
    public void onClick(View view) {

        final Handler h=new Handler();
        final Runnable runnable=new Runnable() {
            @Override
            public void run() {
                fileName = Environment.getExternalStorageDirectory().getPath() +
                        "/sample_picture.jpg";
                mOpenCvCameraView.takePicture(fileName);
                Log.d("Runnable","File saved");
                Send(index++,counter++);

                if(recording)
                    h.postDelayed(this,500);

            }

        };
        if(!recording){
            recording=true;
            btnRecordCtrl.setText("Stop");
            index=0;
            counter=0;
            h.postDelayed(runnable,0);

        }else{
            recording=false;
            btnRecordCtrl.setText("Start");
            h.removeCallbacks(runnable);

            index=-1;
            Toast.makeText(this,"Please wait for response...", Toast.LENGTH_SHORT).show();

//            Intent i=new Intent(this,Result.class);
//            i.putExtra("message",result);
//            startActivity(i);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        List<String> effects = mOpenCvCameraView.getEffectList();

        if (effects == null) {
            Log.e("MainActivity", "Color effects are not supported by device!");
            return true;
        }

        mColorEffectsMenu = menu.addSubMenu("Color Effect");
        mEffectMenuItems = new MenuItem[effects.size()];

        int idx = 0;
        ListIterator<String> effectItr = effects.listIterator();
        while(effectItr.hasNext()) {
            String element = effectItr.next();
            mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, element);
            idx++;
        }

        mResolutionMenu = menu.addSubMenu("Resolution");
        mResolutionList = mOpenCvCameraView.getResolutionList();
        mResolutionMenuItems = new MenuItem[mResolutionList.size()];

        ListIterator<Camera.Size> resolutionItr = mResolutionList.listIterator();
        idx = 0;
        while(resolutionItr.hasNext()) {
            Camera.Size element = resolutionItr.next();
            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
            idx++;
        }

        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Log.i("MainActivity", "called onOptionsItemSelected; selected item: " + item);
        if (item.getGroupId() == 1)
        {
            mOpenCvCameraView.setEffect((String) item.getTitle());
            Toast.makeText(this, mOpenCvCameraView.getEffect(), Toast.LENGTH_SHORT).show();
        }
        else if (item.getGroupId() == 2)
        {
            int id = item.getItemId();
            Camera.Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution();
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
        }

        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i("MainActivity","onTouch event");
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        //String currentDateandTime = sdf.format(new Date());
        fileName = Environment.getExternalStorageDirectory().getPath() +
                "/sample_picture.jpg";
        mOpenCvCameraView.takePicture(fileName);
        Toast.makeText(this, fileName + " saved", Toast.LENGTH_SHORT).show();

        Send(index,counter);


        Intent i=new Intent(this,Result.class);
        i.putExtra("message",result);
        startActivity(i);

        return false;
    }



    private void Send(final int index,final int counter) {
        Log.d("MainActivity","Inside Send function");
        final Mat img = Highgui.imread(fileName);

        final int ctr=index;

        // NativeClass.convertGray(img.getNativeObjAddr(),imgGray.getNativeObjAddr());

        Thread t=new Thread(new Runnable() {
            public String serverMsg;

            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost("http://172.37.1.20:5000/upload");

                Log.d("MainActivity","Send Image to server");

                String responseString;
                try {
                    AndroidMultipartEntity entity = new AndroidMultipartEntity(
                            new AndroidMultipartEntity.ProgressListener() {

                                @Override
                                public void transferred(long num) {
                                    //publishProgress((int) ((num / (float) totalSize) * 100));
                                }
                            });

                    File sourceFile = new File(fileName);

                    // Adding file data to http body
                    entity.addPart("file", new FileBody(sourceFile));

                    // Extra parameters if you want to pass to server
                    entity.addPart("website",
                            new StringBody("www.androidhive.info"));
                    entity.addPart("email", new StringBody("abc@gmail.com"));

                    entity.addPart("index",new StringBody(String.valueOf(ctr)));
                    entity.addPart("counter",new StringBody(String.valueOf(counter)));

                    long totalSize = entity.getContentLength();
                    httppost.setEntity(entity);

                    httppost.setHeader("Accept","application/json");
                    httppost.setHeader("Content","application/json");


                    // Making server call
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity r_entity = response.getEntity();

                    int statusCode = response.getStatusLine().getStatusCode();
                    if (statusCode == 200) {
                        // Server response
                        responseString = EntityUtils.toString(r_entity);
                    } else {
                        responseString = "Error occurred! Http Status Code: "
                                + statusCode;
                    }

                } catch (ClientProtocolException e) {
                    responseString = e.toString();
                } catch (IOException e) {
                    responseString = e.toString();
                }
                try {
                    JSONObject respjson=new JSONObject(responseString);
                    result=respjson.getString("message");
                } catch (JSONException e) {
                    e.printStackTrace();
                }



                Log.d("Response",responseString);
                System.out.println(responseString);

                if(ctr==-1) {
                    Intent i = new Intent(MainActivity.this, Result.class);
                    i.putExtra("message", result);
                    startActivity(i);
                }
                /*
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(responseString).setTitle("Response from Servers")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // do nothing
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                */

            }
        });
        t.start();

    }
}


