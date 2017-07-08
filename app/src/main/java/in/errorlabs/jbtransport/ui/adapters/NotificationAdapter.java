package in.errorlabs.jbtransport.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.models.NotificationModel;

/**
 * Created by root on 7/7/17.
 */

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

    Context context;
    List<NotificationModel> list;

    public NotificationAdapter(Context context, List<NotificationModel> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public NotificationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.notifications_model,parent,false);
        return new NotificationAdapter.NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NotificationViewHolder holder, int position) {
        NotificationModel model = list.get(position);
        if (model.getHeading()!=null || model.getHeading().length()>0){
            holder.heading.setText(model.getHeading());
        }else {
            holder.message.setVisibility(View.GONE);
        }
        if (model.getMessage()!=null || model.getMessage().length()>0){
            holder.message.setText(model.getMessage());
        }else {
            holder.message.setVisibility(View.GONE);
        }
        if (model.getTimeStamp()!=null || model.getTimeStamp().length()>0){
            String time = model.getTimeStamp().substring(0,10);
            holder.timstamp.setText(time);
        }else {
            holder.timstamp.setVisibility(View.GONE);
        }
    }

    public Date compareTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.notification_heading)TextView heading;
        @BindView(R.id.notifiaction_message)TextView message;
        @BindView(R.id.notification_timestamp)TextView timstamp;
        public NotificationViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
