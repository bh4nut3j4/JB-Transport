package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.Arrays;

import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.utils.SharedPrefs;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler{
    private ZXingScannerView mScannerView;
    String fromIntent;
    SharedPrefs sharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        sharedPrefs = new SharedPrefs(this);
        setContentView(mScannerView);
        try{
            Bundle bundle = getIntent().getExtras();
            fromIntent = bundle.getString("IntentFrom");
        }catch (Exception e){
            e.printStackTrace();
        }
        Snackbar.make(mScannerView,"Please Scan the BarCode present at backside of your ID card",Snackbar.LENGTH_INDEFINITE).show();
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
            mScannerView.stopCamera();
            if (fromIntent.equals("SOS")){
                sharedPrefs.setRollNumber(data);
                startActivity(new Intent(getApplicationContext(),Sos.class));
                finish();
            }else {
                Intent intent = new Intent(getApplicationContext(),Complaints.class);
                intent.putExtra("ScannerCode",data);
                startActivity(intent);
                finish();
            }

        }else {
            Toast.makeText(getApplicationContext(), R.string.invalid_identity,Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_back_up_in, R.anim.push_back_up_out);
    }

}
