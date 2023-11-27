package com.andromou.taabia;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

 import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_SEND_SMS = 88;
    ImageView imageView;
    EditText editTextView;
    Dialog dDialogChoose, dialogWait, dEtoile, dDialogMore;
    AdView mAdView;
    private int anInt = 0;
    private InterstitialAd mInterstitialAd;
    public static String txt;
    public static String txtLine;
    public static String txtEtoile = "";
    public static boolean bboolEdit = false;
    private static int interstitialAd = 0;
    private static int numbShare = 0;
    private static int numboolShare = 0;
    MyClipboardManager myClipboardManager;
    Toast toastActivate;
    TextView description, tvtextView;
    Uri image_uri;
    static String currentPhotoPath;
    Dialog dialogLangChooser;
    private final int REQUEST_CODE_STOREGE_PERMESSION = 1;
    private final int REQUEST_CODE_CAPTURE_PERMESSION = 6;
    private final int REQUEST_CODE_SELECT_IMAGE = 2;
    private final int REQUEST_CODE_CAPTURE_IMAGE = 3;
    AnimationDrawable animationDrawable, animationDrawable1;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    boolean boolShare;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_main);
    //    compart();
        myClipboardManager = new MyClipboardManager();
        imageView = findViewById(R.id.imageView);
        tvtextView = findViewById(R.id.tvwebView);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAPTURE_PERMESSION);
            }
        }
        animationDrawable = (AnimationDrawable) getResources().getDrawable(R.drawable.killerflying);
        ((ImageView) findViewById(R.id.imageView)).setImageDrawable(animationDrawable);
        animationDrawable.start();

        animationDrawable1 = (AnimationDrawable) getResources().getDrawable(R.drawable.bg_textview_utile);
        ((ImageView) findViewById(R.id.imageView1)).setImageDrawable(animationDrawable1);
        animationDrawable1.start();

        addNum();
        getNum();
        if(numbShare % 10 == 0 && numboolShare != 2){
          //  Toast.makeText(this, "le nombre d'entries  " + numbShare, Toast.LENGTH_LONG).show();
            dDialogMore = new Dialog(this);
            dDialogMore.setContentView(R.layout.dialog_more);
            dDialogMore.show();
        }



    }

    public void addNum(){
        sharedPreferences = getSharedPreferences("shareNum", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("nShare",numbShare);
        editor.apply();
    }

    public void addNumbool(){
        sharedPreferences = getSharedPreferences("shareNum", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt("nboolShare",2);
        editor.apply();
    }

    public void getNum(){
        sharedPreferences = getSharedPreferences("shareNum", MODE_PRIVATE);
        numbShare = sharedPreferences.getInt("nShare", numbShare);
        numboolShare = sharedPreferences.getInt("nboolShare", 0);
        numbShare++;
    }

    public void onPickImage(View view) {
        showDialogs(view);
        // interstitialShow();
        interstitialAd++;

    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_STOREGE_PERMESSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                selectImage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE_PERMESSION && grantResults.length > 0){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
             //   createImageFile();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

     @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK){
            CropImage.activity(data.getData())
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }
        else if (requestCode == REQUEST_CODE_CAPTURE_IMAGE && resultCode == RESULT_OK){
            CropImage.activity(image_uri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);
        }

        // get crop image
         if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
             CropImage.ActivityResult result = CropImage.getActivityResult(data);
             if (resultCode == RESULT_OK){
                 Uri uri = result.getUri();
                 imageView.setImageURI(uri);
                 try {
                     BitmapDrawable bd = (BitmapDrawable)imageView.getDrawable();
                     Bitmap b = bd.getBitmap();
                     //extraire(b);
                     ml(b);
                 }catch (Exception e){
                    e.printStackTrace();
                 }
             }
         }
    }

    public void ml(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
        FirebaseVisionImage firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVision firebaseVision = FirebaseVision.getInstance();
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
        task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                txt = firebaseVisionText.getText();
                // Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
                //  tvtextView.setVisibility(View.VISIBLE);
                String[] streets;
                String delimiter = "\n";
                streets = txt.split(delimiter);
                for (String line : streets){
                    int i=0;
                    i++;
                    String regexStr = "^[0-9]$";
                    txtLine = line.replace(" ", "");
                    if(txtLine.matches("[0-9]{14}") || txtLine.matches("[0-9]{16}") ) {
                        tvtextView.setText(txtLine);
                        if (txtLine.length() == 16){
                            findViewById(R.id.btnInwi).setVisibility(View.VISIBLE);
                            findViewById(R.id.btnMaroTelecom).setVisibility(View.GONE);
                            findViewById(R.id.btnOrange).setVisibility(View.GONE);
                        } else {
                            findViewById(R.id.btnMaroTelecom).setVisibility(View.VISIBLE);
                            findViewById(R.id.btnOrange).setVisibility(View.VISIBLE);
                            findViewById(R.id.btnInwi).setVisibility(View.GONE);
                        }
                       break;
                    } else if (i ==streets.length){
                        tvtextView.setText(R.string.numbre_taabia);
                      //  findViewById(R.id.layoutIam).setVisibility(View.INVISIBLE);

                    }
                }


            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
             //   Toast.makeText(getApplicationContext(), "لا شيء هنا سيدي.!!!", Toast.LENGTH_LONG).show();
            }
        });
    }






    public void onClickShare(View view) {
        // interstitialShow();
        interstitialAd++;
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Taabi'A app on Google Play \n\n" + "https://play.google.com/store/apps/details?id="+getPackageName();
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void onClickRate(View view) {
        // interstitialShow();
        interstitialAd++;
        addNumbool();
        dDialogMore.dismiss();
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }
    // share text
    public void onClickShareText(View view) {
        // interstitialShow();
        interstitialAd++;
        String text = txtLine;
        if (bboolEdit){
            text = editTextView.getText().toString();
            bboolEdit = false;
        }
        Intent i = new Intent();
        i.setAction(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, text);
        i.setType("text/plain");
        i = Intent.createChooser(i, "Share By");
        startActivity(i);
    }

    public void onClickCopy(View view) {
        // interstitialShow();
        interstitialAd++;
        String text = txtLine;
        if (bboolEdit){
            text = editTextView.getText().toString();
            bboolEdit = false;
        }
        myClipboardManager.copyToClipboard(MainActivity.this, text);
        // Toast.makeText(MainActivity.this, R.string.copy_text_to_clipboard, Toast.LENGTH_SHORT).show();
        afficher(R.layout.dialog_toast_activated, R.id.toast_layout_root, R.id.description, R.string.c_txt_clioard);
    }

    public void onClickGallory(View view) {
        // interstitialShow();
        interstitialAd++;
        dDialogChoose.dismiss();
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_STOREGE_PERMESSION);
        }else{
            selectImage();
        }
    }

    public void onClickCamera(View view) {

        dDialogChoose.dismiss();
        try {
            createImageFile();
        } catch (ActivityNotFoundException e) {
            // display error state to the user

        }
    }

    public void  afficher(int ly, int ly_root, int desc, int stDEsc){
        LayoutInflater inflater = getLayoutInflater();
        View layoutD = inflater.inflate(ly, (ViewGroup) findViewById(ly_root));
        description = layoutD.findViewById(desc);
        description.setText(stDEsc);
        // Create and show the Toast object
        toastActivate = new Toast(getApplicationContext());
        toastActivate.setGravity(Gravity.CENTER, 0, 0);
        toastActivate.setDuration(Toast.LENGTH_LONG);
        toastActivate.setView(layoutD);
        toastActivate.show();
    }

    public void onClickTextEdit(View view) {
        // interstitialShow();
        interstitialAd++;
        bboolEdit = true;
        tvtextView.setVisibility(View.INVISIBLE);
        editTextView.setVisibility(View.VISIBLE);
        editTextView.setText(txt);
    }

    private void showDialogs(View v){
        switch (v.getId()){
            case R.id.imageView:
                dDialogChoose = new Dialog(this);
                dDialogChoose.setContentView(R.layout.dialog_pic_gallery);
                dDialogChoose.show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClickPlay(View view) {
        // // // interstitialShow();
        interstitialAd++;
        dialogLangChooser = new Dialog(this);
        dialogLangChooser.setContentView(R.layout.dialog_language_chooser);
        dialogLangChooser.show();

    }

   

    public void onArabic(View view) {
        switch (view.getId()){
            case R.id.btnEtoile1:
                txtEtoile = txtLine + "*1";
                dEtoile.dismiss();
                tvtextView.setText(txtEtoile);
                break;
             case R.id.btnEtoile2:
                txtEtoile = txtLine + "*2";
                 dEtoile.dismiss();
                 tvtextView.setText(txtEtoile);
                break;
             case R.id.btnEtoile3:
                txtEtoile = txtLine + "*3";
                tvtextView.setText(txtEtoile);
                dEtoile.dismiss();
                 break;
             case R.id.btnEtoile4:
                 txtEtoile = txtLine + "*4";
                 tvtextView.setText(txtEtoile);
                 dEtoile.dismiss();
                 break;
             case R.id.btnEtoile5:
                txtEtoile = txtLine + "*5";
                tvtextView.setText(txtEtoile);
                 dEtoile.dismiss();
                 break;
             case R.id.btnEtoile6:
                txtEtoile = txtLine + "*6";
                tvtextView.setText(txtEtoile);
                 dEtoile.dismiss();
                 break;
            case R.id.btnEtoile7:
                txtEtoile = txtLine + "*7";
                tvtextView.setText(txtEtoile);
                dEtoile.dismiss();
                break;
            case R.id.btnEtoile8:
                txtEtoile = txtLine + "*8";
                tvtextView.setText(txtEtoile);
                dEtoile.dismiss();
                break;
            case R.id.btnEtoile9:
                txtEtoile = txtLine + "*9";
                tvtextView.setText(txtEtoile);
                dEtoile.dismiss();
                break;
            case R.id.btnEtoile22:
                txtEtoile = txtLine + "*22";
                dEtoile.dismiss();
                tvtextView.setText(txtEtoile);
                break;

        }
    }

    private void createImageFile() {
        String imageFileName = "photo";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile( imageFileName,".jpg", storageDir);
            currentPhotoPath = image.getAbsolutePath();
            image_uri = FileProvider.getUriForFile(MainActivity.this,
                    "com.andromou.taabia.fileprovider", image);
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
            startActivityForResult(cameraIntent, REQUEST_CODE_CAPTURE_IMAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void senMessage(String phoneNumber, String textMessage){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, textMessage, null, null);
            } catch (Exception e){
                e.printStackTrace();
            }
        }


    }

    public void onSendMessage(View view) {
      /*  if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.SEND_SMS}, PERMISSION_SEND_SMS);
        } else {

       */
            switch (view.getId()){
                case R.id.btnMaroTelecom:
                case R.id.btnOrange:
                    if (!txtEtoile.isEmpty()){
                       composeMmsMessage(txtEtoile, "555");
    //                    senMessage(txtEtoile, "555");
                    } else if (!txtLine.isEmpty()){
                        composeMmsMessage(txtLine, "555");
  //                      senMessage(txtLine, "555");
                    } //else  afficher(R.layout.custom_toast_activated, R.id.toast_layout_root, R.id.description, R.string.txt_code);
                    break;
                case R.id.btnInwi:
                    if (!txtEtoile.isEmpty()){
                       composeMmsMessage(txtEtoile, "120");
    //                    senMessage(txtEtoile, "120");
                    } else if (!txtLine.isEmpty()){
                         composeMmsMessage(txtLine, "120");
  //                      senMessage(txtLine, "120");
                    }
                    break;
            }
      //  }
    }

    public void onAddStar(View view) {
        dEtoile = new Dialog(this);
        dEtoile.setContentView(R.layout.dialog_stars);
        dEtoile.show();
    }

    public void composeMmsMessage(String message, String attachment) {
        Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address",attachment);
        smsIntent.putExtra("sms_body",message);
        smsIntent.setFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        if (smsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(smsIntent);
        }
    }

    //    implementation 'com.google.firebase:firebase-ml-vision:20.0.0'
    /*
    private void extraire(Bitmap bitmap){
        imageView.setImageBitmap(bitmap);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();


        if (textRecognizer.isOperational()){
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray<TextBlock> items = textRecognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < items.size(); i++){
                TextBlock item = items.valueAt(i);
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }

            tvtextView.setText(stringBuilder.toString());
            editTextView.setText(stringBuilder.toString());
            txt = stringBuilder.toString();
            // Toast.makeText(getApplicationContext(), txt, Toast.LENGTH_LONG).show();
            //  tvtextView.setVisibility(View.VISIBLE);
            String[] streets;
            String delimiter = "\n";
            streets = txt.split(delimiter);
            for (String line : streets){
                String regexStr = "^[0-9]$";
                txtLine = line.replace(" ", "");
                if( txtLine.length() == 14 || txtLine.length() == 16 && txtLine.matches(regexStr) == true) {
                    tvtextView.setText(txtLine);
                    findViewById(R.id.layoutIam).setVisibility(View.VISIBLE);
                    break;
                } else {
                    tvtextView.setText(R.string.numbre_taabia);
                    findViewById(R.id.layoutIam).setVisibility(View.INVISIBLE);

                }
            }
        }
    }

     */

    public void   interstitialShow(){
        if (interstitialAd % 5 == 0){
            if (mInterstitialAd != null) {
                mInterstitialAd.show(MainActivity.this);
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }
        }
    }
}