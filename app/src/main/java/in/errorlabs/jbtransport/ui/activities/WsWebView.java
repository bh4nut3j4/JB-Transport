package in.errorlabs.jbtransport.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import net.steamcrafted.loadtoast.LoadToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.utils.Connection;

public class WsWebView extends AppCompatActivity {
    @BindView(R.id.webview)WebView webView;
    String url;
    LoadToast lt;
    boolean load = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ws_web_view);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        String name =bundle.getString(getString(R.string.url));
        try {
            if (name.equals(getString(R.string.err_lbs))){
                url = getString(R.string.errorlabsurl);
                load=true;
            }else if (name.equals(getString(R.string.acmname))){
                url = getString(R.string.acmurl);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        lt=new LoadToast(this);
        Connection connection = new Connection(getApplicationContext());
        boolean checkinternet = connection.isInternet();
        if (checkinternet){
            if (load){
                Toast.makeText(getApplicationContext(),getString(R.string.errurl), Toast.LENGTH_SHORT).show();
            }
            loadpage();
        }else {
            Snackbar.make(webView,getString(R.string.nointernet),Toast.LENGTH_SHORT).show();
        }
    }

    private void loadpage() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), getString(R.string.cnctng), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                lt.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                lt.success();

            }

        });
        webView.loadUrl(url);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
