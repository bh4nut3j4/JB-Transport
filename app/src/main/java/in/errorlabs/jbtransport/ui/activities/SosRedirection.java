package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import in.errorlabs.jbtransport.R;

public class SosRedirection extends AppCompatActivity {
    String lat,lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_redirection);
        Bundle bundle = getIntent().getExtras();
        try{
             lat = bundle.getString(getString(R.string.latitude));
             lng = bundle.getString(getString(R.string.longitude));
        }catch (Exception e){
         e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(),lat+lng,Toast.LENGTH_SHORT).show();
        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+lat+","+lng+getString(R.string.buslocationfewsecondsago));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
            finish();
        }else {
            Toast.makeText(getApplicationContext(), R.string.nogmaps,Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
