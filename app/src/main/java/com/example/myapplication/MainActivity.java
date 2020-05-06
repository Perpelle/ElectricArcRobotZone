package com.example.myapplication;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.EditText;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;

import Proxemic.Dilmo;
import Proxemic.Distance;
import Proxemic.ProxZone;

import static com.google.android.gms.vision.face.FaceDetector.FAST_MODE;


public class MainActivity extends AppCompatActivity {
    CameraSource cameraSource;
    SurfaceView cameraView;
    final int RequestCameraPermissionId = 1001;
    SparseArray<Face> faces;
    MediaPlayer alarme;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cameraView = (SurfaceView) findViewById(R.id.cameraView);
        FaceDetector detector = new FaceDetector.Builder(this)
                .setTrackingEnabled(true)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FAST_MODE)
                .build();
        if (!detector.isOperational()) {
            Log.w("MainActivity", "FaceDetector dependencies not available");
        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), detector)
                    .setFacing(CameraSource.CAMERA_FACING_FRONT)
                    .setRequestedFps(60.0f)
                    .setRequestedPreviewSize(1920, 1080)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {

                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionId);
                            return;
                        }
                        cameraSource.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });

            detector.setProcessor(new Detector.Processor<Face>() {
                @Override
                public void release() {

                }

                public void changerVolume(String Zone){


                }

                @Override
                public void receiveDetections(Detector.Detections<Face> detections) {

                    //Log.i("Visage", "Coordonnées du visage : " + faces.valueAt(i).getPosition().x + ";" + faces.valueAt(i).getPosition().y);
                    //Log.i("Visage", "Taille du visage : " + faces.valueAt(i).getWidth() + "x" + faces.valueAt(i).getHeight());
                    //Log.i("Visage", "id : " +  faces.valueAt(i).getId());
                    //Log.i("Visage", "face distance : " +  d.getDistance()+faces.valueAt(i).getId());
                    //Log.i("Visage", "Nombre de visage(s) détecté(s) : " + faces.size());
                    faces = detections.getDetectedItems();
                    ProxZone p = new ProxZone(0.5D, 1.0D, 1.5D, 2.0D);
                    Dilmo dilmo = new Dilmo(p);
                    alarme = MediaPlayer.create(getApplicationContext(), R.raw.alarme1);
                    alarme.setLooping(true);
                    alarme.seekTo(0);
                    String infoPro = "Infos Arc Electric professionnel à défnir";
                    String infoPar = "Infos Arc Electric partenaire à définir";
                    String infoEtu = "Infos Arc Electric etudiant à définir";

                    if (faces.size() != 0) {
                        for (int i = 0; i < faces.size(); i++) {
                            if (faces.size()>1) {
                                Distance d= new Distance();
                                d.setfaceHeight( faces.valueAt(i).getHeight());

                                Distance d2= new Distance();
                                d2.setfaceHeight( faces.valueAt(i+1).getHeight());

                                Distance dFinal = new Distance();

                                if (d2.getDistance() < d.getDistance()){
                                    dFinal = d2;
                                }else if (d.getDistance() <= d2.getDistance()){
                                    dFinal = d;
                                }
                                dilmo.setProxemicDistance(dFinal.getDistance());
                                String a = dilmo.getProxemicZone();

                                Log.i("ManyFaces", "Zone : "+ a + "/");

                                if (MonitoringActivity.spinnerValue.equals("Partenaire")){
                                    if (a.equals("intimiZone")) {
                                        // alarm with a higher volume
                                        alarme.setVolume(3f,3f);
                                        alarme.start();
                                        logToDisplay("Attention DANGER !");

                                    } else if (a.equals("personalZone")) {
                                        // Alarm on the phone
                                        alarme.setVolume(0.5f,0.5f);
                                        alarme.start();
                                        logToDisplay("Veuillez vous éloigner du robot à arc électrique");

                                    } else if (a.equals("socialZone")) {
                                        // Display information
                                        alarme.pause();
                                        Log.d("DIST", infoPar);
                                        logToDisplay("informations Partenaires \n" + infoPar);

                                    } else if (a.equals("publicZone")) {
                                        // Display information
                                        alarme.pause();
                                        Log.d("DIST", infoPar);
                                        logToDisplay("Vous vous rapprochez du robot à arc électrique");
                                    }

                                }else if (MonitoringActivity.spinnerValue.equals("Professionnel")){
                                    if (a.equals("intimiZone")) {
                                        // alarm with a higher volume
                                        alarme.setVolume(3f,3f);
                                        alarme.start();
                                        logToDisplay("Attention DANGER !");

                                    } else if (a.equals("personalZone")) {
                                        // Alarm on the phone
                                        alarme.setVolume(0.5f,0.5f);
                                        alarme.start();
                                        logToDisplay("Veuillez vous éloigner du robot à arc électrique");

                                    } else if (a.equals("socialZone")) {
                                        // Display information
                                        alarme.pause();
                                        Log.d("DIST", infoPro);
                                        logToDisplay("informations Pros \n" + infoPro);

                                    } else if (a.equals("publicZone")) {
                                        // Display information
                                        alarme.pause();
                                        Log.d("DIST", infoPro);
                                        logToDisplay("Vous vous rapprochez du robot à arc électrique");

                                    }
                                }else if (MonitoringActivity.spinnerValue.equals("Etudiant")){
                                    if (a.equals("intimiZone")) {
                                        // alarm with a higher volume
                                        alarme.setVolume(3f,3f);
                                        alarme.start();
                                        logToDisplay("Attention DANGER !");

                                    } else if (a.equals("personalZone")) {
                                        //Alarm on the phone
                                        alarme.setVolume(0.5f,0.5f);
                                        alarme.start();
                                        logToDisplay("Veuillez vous éloigner du robot à arc électrique");

                                    } else if (a.equals("socialZone")) {
                                        // Display information
                                        alarme.pause(); }
                                    Log.d("DIST", infoEtu);
                                    logToDisplay("informations Etudiants \n" + infoEtu);

                                } else if (a.equals("publicZone")) {
                                    // Display onformation
                                    alarme.pause();
                                    Log.d("DIST", infoEtu);
                                    logToDisplay("Vous vous rapprochez du robot à arc électrique");
                                }
                            }

                            else{
                                Distance d= new Distance();
                                d.setfaceHeight(faces.valueAt(i).getHeight());
                                dilmo.setProxemicDistance(d.getDistance());
                                String a = dilmo.getProxemicZone();

                                Log.i("OneFace", "Zone : "+ a + "/");

                                if (MonitoringActivity.spinnerValue.equals("Partenaire")){
                                    if (a.equals("intimiZone")) {
                                        //alarm with a higher volume
                                        alarme.setVolume(3f,3f);
                                        alarme.start();
                                        logToDisplay("Attention DANGER !");

                                    } else if (a.equals("personalZone")) {
                                        //Alarm on the phone
                                        alarme.setVolume(0.5f,0.5f);
                                        alarme.start();
                                        logToDisplay("Veuillez vous éloigner du robot à arc électrique");

                                    } else if (a.equals("socialZone")) {
                                        // Display information
                                        alarme.pause();
                                        Log.d("DIST", infoPar);
                                        logToDisplay("informations Partenaires \n" + infoPar);

                                    } else if (a.equals("publicZone")) {
                                        // Display information
                                        alarme.pause();
                                        Log.d("DIST", infoPar);
                                        logToDisplay("Vous vous rapprochez du robot à arc électrique");
                                    }

                                }else if (MonitoringActivity.spinnerValue.equals("Professionnel")){
                                    if (a.equals("intimiZone")) {
                                        // alarm with a higher volume
                                        alarme.setVolume(3f,3f);
                                        alarme.start();
                                        logToDisplay("Attention DANGER !");

                                    } else if (a.equals("personalZone")) {
                                        //Alarm on the phone
                                        alarme.setVolume(0.5f,0.5f);
                                        alarme.start();
                                        logToDisplay("Veuillez vous éloigner du robot à arc électrique");

                                    } else if (a.equals("socialZone")) {
                                        // Display information
                                        alarme.pause();
                                        Log.d("DIST", infoPro);
                                        logToDisplay("informations Pros \n" + infoPro);

                                    } else if (a.equals("publicZone")) {
                                        // Display information
                                        alarme.pause();
                                        Log.d("DIST", infoPro);
                                        logToDisplay("Vous vous rapprochez du robot à arc électrique");

                                    }
                                }else if (MonitoringActivity.spinnerValue.equals("Etudiant")){
                                    if (a.equals("intimiZone")) {
                                        // alarm with a higher volume
                                        alarme.setVolume(3f,3f);
                                        alarme.start();
                                        logToDisplay("Attention DANGER !");

                                    } else if (a.equals("personalZone")) {
                                        //Alarm on the phone
                                        alarme.setVolume(0.5f,0.5f);
                                        alarme.start();
                                        logToDisplay("Veuillez vous éloigner du robot à arc électrique");


                                    } else if (a.equals("socialZone")) {
                                        // Display information
                                        alarme.pause();
                                        Log.d("DIST", infoEtu);
                                        logToDisplay("information Etudiants \n" + infoEtu);

                                    } else if (a.equals("publicZone")) {
                                        // Display information
                                        alarme.pause();
                                        Log.d("DIST", infoEtu);
                                        logToDisplay("Vous vous rapprochez du robot à arc électrique");
                                    }
                                }
                            }

                        }
                    }

                }
            });

        }
    }
    private void logToDisplay(final String line) {
        runOnUiThread(new Runnable() {
            public void run() {
                EditText editText = (EditText)MainActivity.this.findViewById(R.id.rangingText);
                editText.append(line+"\n");
            }
        });
    }
}
