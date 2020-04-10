package phanbagiang.com.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import phanbagiang.com.adapter.TheGioiAdapter;
import phanbagiang.com.model.TheGioi;

public class DanhSachTheGioiActivity extends AppCompatActivity {
    ListView lvTheGioi;
    TheGioiAdapter theGioiAdapter;
    ProgressDialog progressDialog;
    ArrayList<TheGioi>dsNguon=new ArrayList<>();

    // sao chep database
    String tenDatabase = "dbCoVid19byGiangPhan.db";
    String tenDuongDanLocalPhone = "/databases/";
    public String tenBang = "Data";
    public SQLiteDatabase database =null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_the_gioi);
        saoChepSQLiteFromAssestsToLocalPhone();
        addControls();
        hienThiDataLenList();
        thucHienNapData();
        addEvents();
    }

    private void hienThiDataLenList() {
        database=openOrCreateDatabase(tenDatabase,MODE_PRIVATE,null);
        Cursor cursor=database.query(tenBang,null,null,null,null,null,null);
        //Cursor cursor=database.rawQuery("select * from Data",null);
        dsNguon.clear();

        while (cursor.moveToNext()){
            String country= cursor.getString(0);
            String cases=cursor.getString(1);
            String recovered=cursor.getString(2);
            String deaths= cursor.getString(3);
            TheGioi tg=new TheGioi(country,cases,deaths,recovered);
            dsNguon.add(tg);
        }
        cursor.close();
        theGioiAdapter.addAll(dsNguon);
    }

    private void thucHienNapData() {
        DanhSachTheGioiTask task=new DanhSachTheGioiTask();
        task.execute();
    }

    private void saoChepSQLiteFromAssestsToLocalPhone() {
        try {
            File dbFile = getDatabasePath(tenDatabase);
            if (!dbFile.exists()) {
                processCoppy();
                Toast.makeText(DanhSachTheGioiActivity.this, "Kết nối database thành công !", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ex) {
            Log.e("LOI, ", ex.toString());
        }
    }
    // lấy đường dẫn
    private String getDatabasePath() {
        return getApplicationInfo().dataDir + tenDuongDanLocalPhone + tenDatabase;
    }
    // thực hiện sao chép
    private void processCoppy() {
        try {
            InputStream myInput = getAssets().open("databases/"+tenDatabase);
            String duongDanLocalPhone = getDatabasePath();
            File f = new File(getApplicationInfo().dataDir + tenDuongDanLocalPhone);
            if (!f.exists()) {
                f.mkdir();
            }
            OutputStream myOutput = new FileOutputStream(duongDanLocalPhone);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (Exception ex) {
            Log.e("LOI: ", ex.toString());
        }
    }
    private void addEvents() {

        lvTheGioi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TheGioi theGioi = theGioiAdapter.getItem(position);
                Intent intent = new Intent(DanhSachTheGioiActivity.this, ChiTietActivity.class);
                intent.putExtra("TG", theGioi);
                startActivity(intent);
            }
        });
    }

    private void addControls() {
        //database=openOrCreateDatabase(tenDatabase,MODE_PRIVATE,null);
        lvTheGioi = findViewById(R.id.lvDanhSachTheGioi);
        theGioiAdapter = new TheGioiAdapter(DanhSachTheGioiActivity.this, R.layout.customlist_tg);
        lvTheGioi.setAdapter(theGioiAdapter);

        progressDialog = new ProgressDialog(DanhSachTheGioiActivity.this);
        progressDialog.setMessage("Processing...");
        progressDialog.setCanceledOnTouchOutside(false);
    }

    class DanhSachTheGioiTask extends AsyncTask<Void, Void, ArrayList<TheGioi>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
            database.delete(tenBang,null,null);
        }

        @Override
        protected void onPostExecute(ArrayList<TheGioi> theGiois) {
            super.onPostExecute(theGiois);
            progressDialog.dismiss();
            SaoChepDataVaoLocal(theGiois);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected ArrayList<TheGioi> doInBackground(Void... voids) {
            ArrayList<TheGioi> dsTheGioi = new ArrayList<>();
            try {
                URL url = new URL("https://ncovi.huynhhieu.com/api.php?code=external&fbclid=IwAR0-JmOpDsYwJPYkuiDf3CffCmBA-btGfVlTcJcQWY51CdRbUC-DKEw5kUI");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type","Charset=utf-8");
                InputStreamReader inputStreamReader=new InputStreamReader(connection.getInputStream(),"UTF-8");
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder=new StringBuilder();
                String line=null;
                while ((line=bufferedReader.readLine())!=null){
                    stringBuilder.append(line);
                }
                JSONObject object=new JSONObject(stringBuilder.toString());
                JSONArray jsonArray= object.getJSONArray("data");
                for(int i=0; i<jsonArray.length(); i++){
                    JSONObject object1=jsonArray.getJSONObject(i);

                    String cases=object1.getString("cases");
                    String recoved=object1.getString("recovered");
                    String contry=object1.getString("country");
                    String deaths=object1.getString("deaths");
                    TheGioi theGioi=new TheGioi(contry,cases,deaths,recoved);
                    dsTheGioi.add(theGioi);
                }
            } catch (Exception ex) {
                Log.e("LOI", ex.toString());
                Log.e("LOI2", ex.getMessage());
            }
            return dsTheGioi;
        }
    }

    private void SaoChepDataVaoLocal(ArrayList<TheGioi> theGiois) {
        for(int i=0; i<theGiois.size(); i++){
            TheGioi theGioi=theGiois.get(i);
            ContentValues values=new ContentValues();
            values.put("country",theGioi.getCountry());
            values.put("cases",theGioi.getCases());
            values.put("recovered",theGioi.getRecovered());
            values.put("deaths",theGioi.getDeaths());
            database.insert(tenBang,null,values);
        }
        Toast.makeText(DanhSachTheGioiActivity.this,"OK",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_danhsach_tg, menu);
        MenuItem searchItem = menu.findItem(R.id.mnuTimKiem);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Nhập tên quốc gia");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String text) {
                xuLyTimKiem(text);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void xuLyTimKiem(String text) {
        if(text.isEmpty()){
            theGioiAdapter.clear();
            theGioiAdapter.addAll(dsNguon);
        }
        ArrayList<TheGioi>dsTK=new ArrayList<>();
        for(TheGioi theGioi:dsNguon){
            if(theGioi.getCountry().toLowerCase().contains(text.toLowerCase())){
                dsTK.add(theGioi);
            }
        }
        theGioiAdapter.clear();
        theGioiAdapter.addAll(dsTK);
    }
}
