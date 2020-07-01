 package com.example.facedetect;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import dmax.dialog.SpotsDialog;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

//import com.camerakit.CameraKitView;
import com.example.facedetect.Helper.GraphicOverlay;
import com.example.facedetect.Helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

 public class MainActivity extends AppCompatActivity {
     ImageView imageView;
     private static int RESULT_LOAD_IMAGE = 1;
     CameraView cameraKitView;
     Button btnDetect;
     GraphicOverlay graphicOverlay;
     AlertDialog alertDialog;

     @Override
     protected void onStart() {
         super.onStart();
         cameraKitView.start();
     }
     @Override
     protected void onResume() {
         super.onResume();
         cameraKitView.start();

     }
     @Override
     protected void onPause() {
         super.onPause();
         cameraKitView.stop();

     }
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        cameraKitView=findViewById(R.id.cameraView);
        btnDetect=findViewById(R.id.btn_detect);
        graphicOverlay=findViewById(R.id.graphic_overlay);


         alertDialog=new SpotsDialog.Builder().setContext(this)
                 .setMessage("Please wait")
                 .setCancelable(false)
                 .build();
         btnDetect.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 cameraKitView.start();
                 cameraKitView.captureImage();
                 graphicOverlay.clear();
             }
         });
         cameraKitView.addCameraKitListener(new CameraKitEventListener() {
             @Override
             public void onEvent(CameraKitEvent cameraKitEvent) {

             }

             @Override
             public void onError(CameraKitError cameraKitError) {

             }

             @Override
             public void onImage(CameraKitImage cameraKitImage) {
                alertDialog.show();
                 Bitmap bitmap=cameraKitImage.getBitmap();
                 bitmap=Bitmap.createScaledBitmap(bitmap,cameraKitView.getWidth(),cameraKitView.getHeight(),false);
                 cameraKitView.stop();

                 runFaceDetect(bitmap);

             }

             @Override
             public void onVideo(CameraKitVideo cameraKitVideo) {

             }
         });






        //imageView=findViewById(R.id.imageView);
    }

     private void runFaceDetect(Bitmap bitmap) {
         FirebaseVisionImage image=FirebaseVisionImage.fromBitmap(bitmap);
         FirebaseVisionFaceDetectorOptions options=new FirebaseVisionFaceDetectorOptions.Builder()
                 .build();

         FirebaseVisionFaceDetector detector= FirebaseVision.getInstance()
                 .getVisionFaceDetector(options);

         detector.detectInImage(image)
                 .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                     @Override
                     public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                          processResult(firebaseVisionFaces);
                     }
                 }).addOnFailureListener(new OnFailureListener() {
             @Override
             public void onFailure(@NonNull Exception e) {
                 Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
             }
         });

     }

     private void processResult(List<FirebaseVisionFace> firebaseVisionFaces) {


         int count=0;
         for(FirebaseVisionFace face:firebaseVisionFaces){
             Rect bounds=face.getBoundingBox();

             RectOverlay rect=new RectOverlay(graphicOverlay,bounds);
             graphicOverlay.add(rect);
         }
         alertDialog.dismiss();
         Toast.makeText(getApplicationContext(),String.format("Detected %d faces in image",count),Toast.LENGTH_SHORT).show();

     }


//     public void snap(View view) {
//         Intent pickPhoto = new Intent(Intent.ACTION_PICK,
//                 android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//         startActivityForResult(pickPhoto , 1);
//     }
//     protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//         super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//         switch(requestCode) {
//
//             case 1:
//                 if(resultCode == RESULT_OK){
//                     Uri selectedImage = imageReturnedIntent.getData();
//                     imageView.setImageURI(selectedImage);
//                 }
//
//                 break;
//
//         }
//     }
 }
