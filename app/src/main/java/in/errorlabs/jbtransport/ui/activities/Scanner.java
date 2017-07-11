package in.errorlabs.jbtransport.ui.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
    public static final int MY_PERMISSIONS_REQUEST_LOCATION=101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mScannerView = new ZXingScannerView(this);
        sharedPrefs = new SharedPrefs(this);
        setContentView(mScannerView);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        }else {
            try{
                Bundle bundle = getIntent().getExtras();
                fromIntent = bundle.getString("IntentFrom");
            }catch (Exception e){
                e.printStackTrace();
            }
            Snackbar.make(mScannerView,"Please Scan the BarCode present at backside of your ID card",Snackbar.LENGTH_INDEFINITE).show();

        }
    }

    @Override
    public void handleResult(Result result) {
        String data = result.getText();
        String ps3 = String.valueOf(data.charAt(2));
        String ps4 = String.valueOf(data.charAt(3));
        String code = ps3+ps4;
        String[] codes = {"67","J2","GE","17","EF"};
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


    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                new AlertDialog.Builder(this)
                        .setTitle("Camera")
                        .setMessage("Camera")
                        .setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions(Scanner.this,
                                        new String[]{Manifest.permission.CAMERA},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                    }
                } else {
                    checkLocationPermission();
                }
            }
        }
    }

}
