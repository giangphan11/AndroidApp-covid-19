package phanbagiang.com.covid19;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import phanbagiang.com.model.TheGioi;

public class MainActivity extends AppCompatActivity {
    TextView txtTGNhiem, txtTGBinhPhuc, txtTGChet;
    TextView txtVNNhiem, txtVNBinhPhuc, txtVNTuVong;
    TextView txtTime, txtDate;

    ImageView imgHinhChinh, imgBtnTG;

    ProgressDialog progressDialog;
    TimerTask timerTask = null;
    Timer timer = null;

    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
    DecimalFormat df=new DecimalFormat("#,###");


    String tenFileSharePreferences="giangPhan";
    // Ảnh lưu
    private Uri mImageUri;

    //
    private NotificationManager mNotificationManager;
    private static boolean firstRun = true;
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            ConnectivityManager manager = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (manager.getActiveNetworkInfo() == null) {
                //Không có internet==> ẩn Button đăng nhập
                imgHinhChinh.setImageBitmap(getBitmapFromAsset("images/oka2.png"));
                Toast.makeText(context, "Không có kết nối mạng!", Toast.LENGTH_LONG).show();


            } else {

               File f = new File(
                        "data/data/phanbagiang.com.covid19/shared_prefs/giangPhan.xml");
                if (f.exists()) {
                    Log.d("TAG", "Đã tồn tại file ");
                    SharedPreferences preferences = getSharedPreferences(tenFileSharePreferences,MODE_PRIVATE);
                    String mImageUri = preferences.getString("image", null);
                    imgHinhChinh.setImageURI(Uri.parse(mImageUri));
                }
                if(!f.exists())
                {
                    Log.d("TAG", "chưa tồn tại file");
                    imgHinhChinh.setImageBitmap(getBitmapFromAsset("images/main1.png"));
                }
                //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                //String mImageUri = preferences.getString("image", null);
                //imgHinhChinh.setImageURI(Uri.parse(mImageUri));
                TheGioiTask task=new TheGioiTask();
                task.execute();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(firstRun)
        {
            addThongBao2();
        }
        firstRun = false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addControls();
        addEvents();
    }

    private void addThongBao2() {
        AssetManager assetManager = this.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open("images/icon_large.png");
            bitmap = BitmapFactory.decodeStream(istr);
        } catch (Exception ex) {
            Log.e("LOI", ex.toString());
        }


        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
        Intent ii = new Intent(this.getApplicationContext(), ThongTinChinhPhuActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle();
        bigText.bigText("Mỗi người dân là một chiến sỹ trên mặt trận phòng, chống dịch.");
        bigText.setBigContentTitle("Hãy ở nhà");
        //bigText.setSummaryText("Text in detail");

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.drawable.icon_small);
        mBuilder.setLargeIcon(bitmap);
        mBuilder.setContentTitle("Hãy ở nhà");
        mBuilder.setContentText("Mỗi người dân là một chiến sỹ trên mặt trận phòng, chống dịch.");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setStyle(bigText);

        mNotificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

// === Removed some obsoletes
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }

        mNotificationManager.notify(0, mBuilder.build());
    }
    private void addControls() {
        imgBtnTG = findViewById(R.id.imgBtnTG);

        imgHinhChinh = findViewById(R.id.txtHinhChinh);
        txtTime = findViewById(R.id.txtTime);
        txtDate = findViewById(R.id.txtDate);

        txtTGNhiem = findViewById(R.id.txtTGNhiem);
        txtTGBinhPhuc = findViewById(R.id.txtTGBinhPhuc);
        txtTGChet = findViewById(R.id.txtTGChet);

        txtVNNhiem = findViewById(R.id.txtVNNhiem);
        txtVNBinhPhuc = findViewById(R.id.txtVNBinhPhuc);
        txtVNTuVong = findViewById(R.id.txtVNChet);

        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        Calendar calendar = Calendar.getInstance();
                        txtTime.setText(sdf.format(calendar.getTime()));
                        txtDate.setText(sdfDate.format(calendar.getTime()));
                    }
                });
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);



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
    private void addEvents() {
        imgBtnTG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyXemDanhSachTheGioi();
            }
        });

    }

    private void xuLyXemDanhSachTheGioi() {
        Intent intent = new Intent(MainActivity.this, DanhSachTheGioiActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dangKyTuDongLangNgheWifi();


    }


    private void dangKyTuDongLangNgheWifi() {
        //ta phải tạo bộ lọc để Broad Receiver lắng nghe theo bộ lọc này
        //vì Mobile có rất nhiều thứ để lắng nghe (phần mềm chỉ nên cho lắng nghe
        //những cái cần thiết)==> Tiết kiệm PIN
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(wifiReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        huyTyDongDangKyWifi();

    }

    private void huyTyDongDangKyWifi() {
        if (wifiReceiver != null)
            unregisterReceiver(wifiReceiver);
    }

    class TheGioiTask extends AsyncTask<Void, Void, ArrayList<TheGioi>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected void onPostExecute(ArrayList<TheGioi> theGiois) {
            super.onPostExecute(theGiois);
            progressDialog.dismiss();
            for(int i=0; i<theGiois.size(); i++){
                TheGioi theGioi=theGiois.get(i);
                if(theGioi.getCountry().equals("vn")){
                    double nhiem= Double.parseDouble(theGioi.getCases());
                    double bp= Double.parseDouble(theGioi.getRecovered());
                    double tv= Double.parseDouble(theGioi.getDeaths());
                    txtVNNhiem.setText(df.format(nhiem)+"");
                    txtVNBinhPhuc.setText(df.format(bp)+"");
                    txtVNTuVong.setText(df.format(tv)+"");
                }
                if(theGioi.getCountry().equals("tg")){
                    double nhiem= Double.parseDouble(theGioi.getCases());
                    double bp= Double.parseDouble(theGioi.getRecovered());
                    double tv= Double.parseDouble(theGioi.getDeaths());
                    txtTGNhiem.setText(df.format(nhiem)+"");
                    txtTGBinhPhuc.setText(df.format(bp)+"");
                    txtTGChet.setText(df.format(tv)+"");
                }
            }
        }
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<TheGioi> doInBackground(Void... voids) {
            ArrayList<TheGioi> dsTheGioi = new ArrayList<>();
            try {
                URL url = new URL("https://code.junookyo.xyz/api/ncov-moh/data.json?fbclid=IwAR3kiDWpG88_1XQw_XqS7skxz_pmHLKW8ssJOSrPhOlqDt9Maz2DS_nvaWs");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

                InputStreamReader inputStreamReader = new InputStreamReader(connection.getInputStream(), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                JSONObject jsonObject = new JSONObject(stringBuilder.toString());
                JSONObject object=jsonObject.getJSONObject("data");
                JSONObject object1=object.getJSONObject("global");


                String cases = object1.getString("cases");
                String death = object1.getString("deaths");
                String recovered = object1.getString("recovered");
                TheGioi theGioi= new TheGioi("tg",cases,death,recovered);
                dsTheGioi.add(theGioi);

                JSONObject object2=object.getJSONObject("vietnam");
                String cases2 = object2.getString("cases");
                String death2 = object2.getString("deaths");
                String recovered2 = object2.getString("recovered");
                TheGioi theGioi2= new TheGioi("vn",cases2,death2,recovered2);
                dsTheGioi.add(theGioi2);
            } catch (Exception ex) {
                Log.e("LOI", ex.getMessage());
                Log.e("LOI2", ex.toString());
            }
            return dsTheGioi;
        }
    }

    ///////////// THOAT ///////////////////
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Nhấn back thêm 1 lần nữa để thoát", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 3000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = this.getMenuInflater();
        menuInflater.inflate(R.menu.item_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuAbout) {
            Intent intent = new Intent(MainActivity.this, ThongTinActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.mnuExit) {
            finish();
        }
        if (item.getItemId() == R.id.mnuThongTin) {
            Intent intent = new Intent(MainActivity.this, ThongTinChinhPhuActivity.class);
            startActivity(intent);
        }
        if (item.getItemId() == R.id.mnuThayAnhNen) {
            Intent intent;
            if (Build.VERSION.SDK_INT < 19) {
                intent = new Intent(Intent.ACTION_GET_CONTENT);
            } else {
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
            }
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "Select Picture"),
                    113);
        }
        if(item.getItemId()==R.id.mnuDanhSach){
            Intent intent=new Intent(MainActivity.this,HuongDanActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 113) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a image.
                // The Intent's data Uri identifies which item was selected.
                if (data != null) {

                    // This is the key line item, URI specifies the name of the data
                    mImageUri = data.getData();

                    // Removes Uri Permission so that when you restart the device, it will be allowed to reload.
                    this.grantUriPermission(this.getPackageName(), mImageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    final int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION;
                    this.getContentResolver().takePersistableUriPermission(mImageUri, takeFlags);

                    // Saves image URI as string to Default Shared Preferences
                    SharedPreferences preferences = getSharedPreferences(tenFileSharePreferences,MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("image", String.valueOf(mImageUri));
                    editor.commit();

                    // Sets the ImageView with the Image URI
                    imgHinhChinh.setImageURI(mImageUri);
                    imgHinhChinh.invalidate();
                }
            }
        }
    }


}
