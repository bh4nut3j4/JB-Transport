package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import in.errorlabs.jbtransport.R;

public class SosRedirection extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos_redirection);
        Bundle bundle = getIntent().getExtras();
        String lat = bundle.getString("latitude");
        String lng = bundle.getString("longitude");
        Toast.makeText(getApplicationContext(),lat+lng,Toast.LENGTH_SHORT).show();
        Uri gmmIntentUri = Uri.parse("geo:0,0?q="+lat+","+lng+"(Current Bus Location)");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                mapIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }else {
            Toast.makeText(getApplicationContext(), R.string.nogmaps,Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
