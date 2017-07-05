package in.errorlabs.jbtransport.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
            Log.v("TAG", "TRUEE");
        }else {
            Log.v("TAG", "FALSE");
        }


        Log.v("TAG", result.getText()); // Prints scan results
        //Log.v("TAG", result.getResultMetadata().toString());
        Log.v("TAG", String.valueOf(result.getTimestamp()));
        Log.v("TAG", result.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)
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
