package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.Arrays;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void handleResult(Result result) {
        String data = result.getText();
        String ps3 = String.valueOf(data.charAt(2));
        String ps4 = String.valueOf(data.charAt(3));
        String code = ps3+ps4;
        String[] codes = {"67","J2","GE"};
        Log.v("TAG", ps3+ps4);
        if (Arrays.asList(codes).contains(code)){
            Intent intent = new Intent(getApplicationContext(),Complaints.class);
            intent.putExtra("ScannerCode",data);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(getApplicationContext(),"Invalid Identity",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

}
