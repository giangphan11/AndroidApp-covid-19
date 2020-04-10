package phanbagiang.com.covid19;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import phanbagiang.com.model.TheGioi;

public class ChiTietActivity extends AppCompatActivity {
    TextView txTNhiem, txtBP, txtTV, txtTenQG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chi_tiet);
        addControls();
        addEvents();
    }

    private void addControls() {
        txtTenQG=findViewById(R.id.txtTenQGTT);
        txTNhiem=findViewById(R.id.txtNhiemTT);
        txtBP=findViewById(R.id.txtBPTT);
        txtTV=findViewById(R.id.txtTVTT);

        Intent intent=getIntent();
        TheGioi theGioi= (TheGioi) intent.getSerializableExtra("TG");
        txtTenQG.setText(theGioi.getCountry());
        txTNhiem.setText(theGioi.getCases());
        txtBP.setText(theGioi.getRecovered());
        txtTV.setText(theGioi.getDeaths());
    }

    private void addEvents() {

    }
}
