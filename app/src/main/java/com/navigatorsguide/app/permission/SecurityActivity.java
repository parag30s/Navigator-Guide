package com.navigatorsguide.app.permission;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.navigatorsguide.app.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;


public class SecurityActivity extends AppCompatActivity {
    private static final String FILE_NAME_ENC = "random_file_enc";
    private static final String FILE_NAME_DEC = "random_file_dec.db";
    Button btn_enc, btn_dec;
    ImageView imageView;
    File myDir;

    String my_key = "UVDO17cBZJ9qWBGH";
    String my_spec = "YxbcZaAktj2OAnNE";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        btn_enc = findViewById(R.id.btn_encrypt);
        btn_dec = findViewById(R.id.btn_decrypt);
        imageView = findViewById(R.id.imageView);

        myDir = new File(getExternalFilesDir(null).toString() + "/");

        btn_enc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Drawable drawable = ContextCompat.getDrawable(SecurityActivity.this, R.drawable.ic_side_menu_bg);
//                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
//                Bitmap bitmap = bitmapDrawable.getBitmap();
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
//                InputStream is = new ByteArrayInputStream(stream.toByteArray());


                InputStream is = null;
                try {
                    is = getAssets().open("database/NavGuide.db");
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //create file
                File outputFileEnc = new File(myDir, FILE_NAME_ENC);
                try {
                    Encrypter.encryptToFile(my_key, my_spec, is, new FileOutputStream(outputFileEnc));

                    Toast.makeText(SecurityActivity.this, "Encrypted", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }
            }
        });

        btn_dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File outputFileDec = new File(myDir, FILE_NAME_DEC);
                File encFile = new File(myDir, FILE_NAME_ENC);
                try {
                    Encrypter.decryptToFile(my_key, my_spec, new FileInputStream(encFile), new FileOutputStream(outputFileDec));

                    imageView.setImageURI(Uri.fromFile(outputFileDec));

//                    outputFileDec.delete();

                    Toast.makeText(SecurityActivity.this, "Decrypt", Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}