package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.steamcrafted.loadtoast.LoadToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.utils.Connection;
import in.errorlabs.jbtransport.utils.SharedPrefs;

public class Splash extends AppCompatActivity {
    SharedPrefs sharedPrefs;
    Connection connection;
    LoadToast loadToast;
    @BindView(R.id.linear)RelativeLayout linearLayout;
    @BindView(R.id.progress_bar)ProgressBar progressBar;
    @BindView(R.id.conn_text)TextView conn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        loadToast = new LoadToast(this);
        loadToast.show();
        ButterKnife.bind(this);
        sharedPrefs = new SharedPrefs(this);
        connection = new Connection(this);
        if (connection.isInternet()){
            if (sharedPrefs.getRouteSelected()){
                loadToast.success();
                startActivity(new Intent(getApplicationContext(),HomeActivity.class));
                finish();
            }else {
                loadToast.success();
                startActivity(new Intent(getApplicationContext(),RouteSelectActivity.class));
                finish();
            }
        }else {
            progressBar.setVisibility(View.GONE);
            conn.setVisibility(View.GONE);
            Snackbar.make(linearLayout,getString(R.string.nointernet),Snackbar.LENGTH_INDEFINITE).show();
        }
    }
}
