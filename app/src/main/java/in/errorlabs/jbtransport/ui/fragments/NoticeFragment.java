package in.errorlabs.jbtransport.ui.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.constants.HomeConstants;
import in.errorlabs.jbtransport.utils.Constants;
import okhttp3.OkHttpClient;

/**
 * A simple {@link Fragment} subclass.
 */
public class NoticeFragment extends Fragment implements LoaderManager.LoaderCallbacks<Void>{
    OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build();
    @BindView(R.id.frame_lay)FrameLayout framelay;
    @BindView(R.id.notice_card_view)CardView noticecard;
    @BindView(R.id.noticebard_text)TextView notice_text;
    @BindView(R.id.notice_last_updated_text)TextView notice_timestamp;
    public static final String NOTICE_TITLE="title";
    public static final String NOTICE_MESSAGE="msg";
    public static final String NOTICE_LAST_UPDATED="last";
    public static final int STRING_LOADER_ID=99;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notice, container, false);
        ButterKnife.bind(this,view);
        getData();
        return view;
    }

    private void getData() {
        LoaderManager loaderManager = getLoaderManager();
        Loader<Object> details = loaderManager.getLoader(STRING_LOADER_ID);
        if (details == null) {
            loaderManager.initLoader(STRING_LOADER_ID, null, this);
        } else {
            loaderManager.restartLoader(STRING_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<Void> onCreateLoader(int id, Bundle args) {
        if (id==STRING_LOADER_ID){
            return new AsyncTaskLoader<Void>(getContext()) {
                @Override
                protected void onStartLoading(){
                        forceLoad();
                }
                @Override
                public Void loadInBackground() {
                    fetchData();
                    return null;
                }
            };
        }
        return null;
    }

    private void fetchData() {
        AndroidNetworking.post(Constants.NoticeUrl)
                .setOkHttpClient(okHttpClient)
                .setPriority(Priority.HIGH)
                .addBodyParameter(Constants.AppKey, getString(R.string.transportAppKey))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response.length()>0){
                            if (response.has(Constants.NoticeName)){
                                try {
                                    JSONArray noticeArray = response.getJSONArray(Constants.NoticeName);
                                    JSONObject noticeObject = noticeArray.getJSONObject(0);
                                    String msg = noticeObject.getString(HomeConstants.noticemessage);
                                    String timeStamp =noticeObject.getString(HomeConstants.noticetimestamp);
                                    if (msg.length()>0 && timeStamp.length()>0){
                                        framelay.setVisibility(View.VISIBLE);
                                        notice_text.setText(msg);
                                        notice_timestamp.setText(timeStamp);
                                    }else {
                                        noticecard.setVisibility(View.GONE);
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }else if (response.has(Constants.ErrorSelecting)){
                                framelay.setVisibility(View.GONE);
                            }
                            else {
                                showDataError();
                            }
                        }else {
                            showDataError();
                        }
                    }
                    @Override
                    public void onError(ANError anError) {
                        //showError();
                    }
                });
    }

    public void showDataError() {
       // Snackbar.make(rootView, getString(R.string.tryagainlater), Snackbar.LENGTH_INDEFINITE).show();
    }

    @Override
    public void onLoadFinished(Loader<Void> loader, Void data) {

    }

    @Override
    public void onLoaderReset(Loader<Void> loader) {
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString(NOTICE_TITLE,notice_text.getText().toString());
        savedInstanceState.putString(NOTICE_MESSAGE,notice_timestamp.getText().toString());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try {
            notice_text.setText(savedInstanceState.getString(NOTICE_TITLE));
            notice_timestamp.setText(savedInstanceState.getString(NOTICE_MESSAGE));
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
