package br.sofex.com.fotografar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.snackbar.Snackbar;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMGE_CAPTURE = 1;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int CAM_REQUEST = 1;

    Button BtnFotografar;
    ImageView image;

    private static final int READ_REQUEST_CODE = 200;
    private Uri photoURI;
    private String selectedImagePath;
    private Bitmap bitmap;
    String mCurrentPhotoPath;
    private String pictureFilePath;
    int[] imageArray;
    private FaceDetector detector;
    TextView Textview1;
    Bitmap editedBitmap;
    int currentIndex = 0;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageArray = new int[]{R.drawable.antes};
        BtnFotografar = findViewById(R.id.Btn_Photo);
        image = findViewById(R.id.ImageTeste);
        Textview1 = findViewById(R.id.Textview1);

        BtnFotografar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //check if app has permission to access the external storage.
                if (EasyPermissions.hasPermissions(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //startActivityForResult(intent,REQUEST_TAKE_PHOTO);
                    //showPath();

                    sendTakePictureIntent();

                } else {
                    //If permission is not present request for the same.
                    EasyPermissions.requestPermissions(MainActivity.this,"Este aplicativo precisa acessar a câmera para prosseguir . Por favor autorize o acesso a câmera.",READ_REQUEST_CODE,Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            }
        });

    }

    private void sendTakePictureIntent() {

        //CriarPasta();
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra( MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            //startActivityForResult(cameraIntent, REQUEST_IMGE_CAPTURE);

                File pictureFile = null;
            try {
                //pictureFile = getPictureFile();
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(this,
                        "Photo file can't be created, please try again . Error: "+ex,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "br.sofex.com.fotografar.fileprovider",
                        pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_IMGE_CAPTURE);

            }
        }
    }
    // storage/self/primary/Android/data/br.sofex.com.fotografar/files/Pictures
    /*Local Android Emulador -> Pasta/Imagens*/
    /*Local Android S8 -> Meus Arquivos/Amrmazenamento Interno/Android/data/br.com.sofex.fotografar/files/Pictures */
    private File getPictureFile() throws IOException {
        File image = null;
        String timeStamp = new SimpleDateFormat(" dd_MM_yyyy_HH:mm").format(new Date());
        //String pictureFile = "ZOFTINO_" + timeStamp;
        String pictureFile = "Imagem_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (storageDir.exists()) {
            image = File.createTempFile(pictureFile,  ".jpg", storageDir);
            pictureFilePath = image.getAbsolutePath();
        }else{
            storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File f = new File(storageDir, "");
            f.mkdirs();
            image = File.createTempFile(pictureFile,  ".jpg", storageDir);
            pictureFilePath = image.getAbsolutePath();
        }

        return image;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // now, you have permission go ahead
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, REQUEST_TAKE_PHOTO);

        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_CALL_LOG)) {
                EasyPermissions.requestPermissions(MainActivity.this, "Este aplicativo precisa acessar a câmera para prosseguir . Por favor autorize o acesso a câmera.", READ_REQUEST_CODE, Manifest.permission.READ_EXTERNAL_STORAGE);
            } else {

                // now, user has denied permission permanently!

                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Voçê negou o acesso ao aplicativo.\n" +
                        "Você precissa aprovar a(s) permissão(ôes)", Snackbar.LENGTH_LONG).setAction("Alterar", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));

                    }
                });
                snackbar.show();
            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode == CAM_REQUEST){
            if (resultCode == Activity.RESULT_OK){
                //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                //image.setImageBitmap(bitmap);
                /*
                 * The Android Camera application encodes the photo in the return Intent delivered to onActivityResult()
                 * as a small Bitmap in the extras, under the key "data".
                 */
                //dispatchTakePictureIntent();
                File imgFile = new  File(pictureFilePath);
                if(imgFile.exists())            {
                    image.setImageURI(Uri.fromFile(imgFile));
                    //FaceDetector(bitmap);
                }
            }

        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getFilesDir();
        File image = new File("/data/data/br.sofex.com.fotografar/cache/"+imageFileName);
        FileOutputStream out = new FileOutputStream(image);
        out.close();

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        //Toast.makeText(this, " Path : "+mCurrentPhotoPath, Toast.LENGTH_SHORT).show();
        Log.v("App"," Path1 "+mCurrentPhotoPath);
        return image;
    }


    public void FaceDetector(Bitmap bitmap){
        FaceDetector detector = new FaceDetector.Builder( getApplicationContext() )
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setMode(FaceDetector.FAST_MODE)
                .build();
        if(!detector.isOperational()){
            AlertDialog.Builder v = null;
            new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
            return;
        }else {
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();
            SparseArray faces = detector.detect(frame);
        }
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Toast.makeText(this, " Caminho "+photoFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoFile.getAbsolutePath());
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

            }
        }
    }
    public void CriarPasta(){

        File rootPath = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "/Files");
        if (!rootPath.exists()) {

            rootPath = getFilesDir();
            File f = new File(rootPath, "");
            f.mkdirs();
            //Toast.makeText(MainActivity.this, "Pasta criada com sucesso ! ",Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(MainActivity.this, "Erro em criar a pasta ",Toast.LENGTH_LONG).show();
        }
    }
    public void showPath(){
        //Log.v("App", " Path : "+getExternalFilesDir(Environment.DIRECTORY_PICTURES));
        //Log.v("App", " Path : "+getFilesDir());
        //Toast.makeText(this, " Path 1 :"+getExternalFilesDir(Environment.DIRECTORY_PICTURES), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, " Path 1 :"+Environment.getExternalStorageDirectory(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, " Path 2 :"+getFilesDir(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(this, " Path 2 :"+getFilesDir(), Toast.LENGTH_SHORT).show();
    }

}
