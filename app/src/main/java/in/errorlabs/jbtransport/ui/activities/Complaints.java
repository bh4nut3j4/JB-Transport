package in.errorlabs.jbtransport.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import in.errorlabs.jbtransport.R;

public class Complaints extends AppCompatActivity {
    String idData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);
        Bundle bundle = getIntent().getExtras();
        idData = bundle.getString("ScannerCode");
    }
}
