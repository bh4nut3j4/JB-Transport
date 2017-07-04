package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.SharedPrefs;

public class Splash extends AppCompatActivity {
    SharedPrefs sharedPrefs;
    Connection connection;
    @BindView(R.id.linear)LinearLayout linearLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        sharedPrefs = new SharedPrefs(this);
        connection = new Connection(this);
        if (connection.isInternet()){
           // String value = getIntent().getStringExtra(getString(R.string.DetailsView));
//            Bundle bundle = getIntent().getExtras();
//            String value = bundle.getString("DetailsView");
//            Toast.makeText(getApplicationContext(),"splash"+value,Toast.LENGTH_SHORT);
//            if (value!=null && value.length()>0){
//                Intent intent= new Intent(getApplicationContext(),HomeActivity.class);
//                intent.putExtra(getString(R.string.DetailsView),value);
//                startActivity(intent);
//                finish();
//            }
            if (sharedPrefs.getRouteSelected()){
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            } else if (sharedPrefs.getAlreadySkipped()){
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            }else {
                startActivity(new Intent(getApplicationContext(),RouteSelectActivity.class));
                finish();
            }
        }else {
            Snackbar.make(linearLayout,getString(R.string.nointernet),Snackbar.LENGTH_INDEFINITE).show();
        }
    }
}
