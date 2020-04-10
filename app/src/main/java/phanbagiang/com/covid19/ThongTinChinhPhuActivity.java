package phanbagiang.com.covid19;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class ThongTinChinhPhuActivity extends AppCompatActivity {
    ImageView imgHinh1,imgHinh2,imgHinh3, imgHinh4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_chinh_phu);
        addControls();
        addEvents();
    }

    private void addControls() {
        imgHinh1=findViewById(R.id.imgHinh1);
        imgHinh2=findViewById(R.id.imgHinh2);
        imgHinh3=findViewById(R.id.imgHinh3);
        imgHinh4=findViewById(R.id.imgHinh4);
    }

    private void addEvents() {
        imgHinh1.setImageBitmap(getBitmapFromAsset("images/anha.png"));
        imgHinh2.setImageBitmap(getBitmapFromAsset("images/v2.jpg"));
        imgHinh3.setImageBitmap(getBitmapFromAsset("images/v11.jpg"));
        imgHinh4.setImageBitmap(getBitmapFromAsset("images/v1.jpg"));
    }
    private Bitmap getBitmapFromAsset(String strName)
    {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(istr);
        return bitmap;
    }

}
