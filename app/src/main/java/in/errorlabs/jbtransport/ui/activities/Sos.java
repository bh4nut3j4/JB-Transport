package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;

import net.steamcrafted.loadtoast.LoadToast;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.utils.Constants;
import in.errorlabs.jbtransport.utils.SharedPrefs;

public class Sos extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    public static final int RC_SIGN_IN=1;
    @BindView(R.id.namearea)TextView name;
    @BindView(R.id.email)TextView email;
    @BindView(R.id.rollnumber)TextView rollnumber;
    @BindView(R.id.request)Button request;
    SharedPrefs sharedPrefs;
    LoadToast loadToast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sos);
        ButterKnife.bind(this);
        sharedPrefs = new SharedPrefs(this);
        firebaseAuth = FirebaseAuth.getInstance();
        loadToast = new LoadToast(this);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user!=null){
                    onSignedInInitialize(user.getDisplayName(),user.getEmail());
                }else {
                    startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                    .setLogo(R.drawable.busleft)
                    .setProviders(
                            AuthUI.GOOGLE_PROVIDER
                    )
                    .build(), RC_SIGN_IN);
                }
            }
        };

        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = FirebaseInstanceId.getInstance().getToken();
                String routeId = sharedPrefs.getSelectedRouteFcmID();
                String username = sharedPrefs.getUserName();
                String useremail = sharedPrefs.getEmail();
                String roll = sharedPrefs.getRollNumber();
                if (token!=null && token.length()>0 && routeId!=null && routeId.length()>0){
                    requestLocation(token,routeId,useremail,username,roll);
                }
            }
        });
    }

    private void requestLocation(String token,String routeID,String useremail,String username,String rollnumber) {
        loadToast.show();
        AndroidNetworking.post(Constants.FirebaseRequest)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.AppKey,getString(R.string.AuthError))
                .addBodyParameter(getString(R.string.receiverfcmtoken),token)
                .addBodyParameter(getString(R.string.receiverrouteID),routeID)
                .addBodyParameter(getString(R.string.gmailname),username)
                .addBodyParameter(getString(R.string.gmailemail),useremail)
                .addBodyParameter(getString(R.string.rollnumber),rollnumber)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length() > 0) {
                            loadToast.success();
                            if (!response.has(getString(R.string.AuthError)) && !response.has(getString(R.string.ErrorSelecting))) {
                                Toast.makeText(getApplicationContext(), R.string.requestsent, Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finish();
                            } else if (response.has(getString(R.string.invalidtime))){
                                Toast.makeText(getApplicationContext(),"Not available at this time", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                finish();
                            }else {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==RC_SIGN_IN){
            if (resultCode==RESULT_OK){

            }else if (resultCode==RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), R.string.signincancelled,Toast.LENGTH_SHORT).show();
                onBackPressed();
                overridePendingTransition(R.anim.push_back_up_in, R.anim.push_back_up_out);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sos, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            onBackPressed();
        }else if (item.getItemId()==R.id.logout){
            sharedPrefs.setEmail(null);
            sharedPrefs.setRollNumber(null);
            sharedPrefs.setUserName(null);
            AuthUI.getInstance().signOut(this);
            startActivity(new Intent(getApplicationContext(),HomeActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSignedInInitialize(String displayName,String e_mail) {
        sharedPrefs.setUserName(displayName);
        sharedPrefs.setEmail(e_mail);
        name.setText(getString(R.string.helloname)+" "+displayName);
        email.setText(e_mail);
        rollnumber.setText(sharedPrefs.getRollNumber());
        if (sharedPrefs.getRollNumber()==null){
            Intent i = new Intent(getApplicationContext(),Scanner.class);
            i.putExtra("IntentFrom","SOS");
            startActivity(i);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.push_back_up_in, R.anim.push_back_up_out);
    }

    @Override
    protected void onResume(){
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause(){
        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    public void showError() {
        Snackbar.make(name, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).show();
    }
}
