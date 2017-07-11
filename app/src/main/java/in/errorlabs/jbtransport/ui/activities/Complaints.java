package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.utils.Constants;
import okhttp3.OkHttpClient;

public class Complaints extends AppCompatActivity {
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    String idData;
    @BindView(R.id.title)
    EditText title;
    @BindView(R.id.description)
    EditText description;
    @BindView(R.id.submit)
    Button submit;
    LoadToast loadToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        idData = bundle.getString("ScannerCode");
        loadToast = new LoadToast(this);
        title.addTextChangedListener(titlewatcher);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = title.getText().toString();
                String desc = description.getText().toString();
                if (subject.length() > 0) {
                    if (desc.length() > 0) {
                        submitComplaint(subject, desc, idData);
                    } else {
                        Snackbar.make(title, R.string.descrription_required, Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(title, R.string.subject_required, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void submitComplaint(String subject, String desc, String idData) {
        loadToast.show();
        AndroidNetworking.post(Constants.ComplaintsURL)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.AppKey, getString(R.string.transportAppKey))
                .addBodyParameter(Constants.ComplaintSubject, subject)
                .addBodyParameter(Constants.ComplaintDesc, desc)
                .addBodyParameter(Constants.ComplaintID, idData)
                .setOkHttpClient(okHttpClient)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            loadToast.success();
                            Log.d("TAG",response.toString());
                            if (!response.has(getString(R.string.AuthError)) && !response.has(getString(R.string.ErrorSelecting))) {
                                Toast.makeText(getApplicationContext(), R.string.submitted_successdull, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finish();
                            } else {
                                showError();
                            }
                        } else {
                            showError();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        loadToast.error();
                        showError();
                    }
                });
    }

    TextWatcher titlewatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (count < 0) {
                Snackbar.make(title, R.string.subject_required, Snackbar.LENGTH_LONG).show();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    public void showError() {
        Snackbar.make(title, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).show();
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
