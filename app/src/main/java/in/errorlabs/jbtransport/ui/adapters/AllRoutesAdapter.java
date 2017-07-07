package in.errorlabs.jbtransport.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.activities.RouteFullDetails;
import in.errorlabs.jbtransport.ui.models.RouteSelectModel;
import in.errorlabs.jbtransport.utils.SharedPrefs;

/**
 * Created by root on 7/4/17.
 */

public class AllRoutesAdapter extends RecyclerView.Adapter<AllRoutesAdapter.AllRoutesViewHolder> {
    private Context context;
    List<RouteSelectModel> list;
    private boolean isclick=false;
    SharedPrefs sharedPrefs;

    public AllRoutesAdapter(List<RouteSelectModel> list,Context context) {
        this.context = context;
        this.list = list;
    }
    @Override
    public AllRoutesAdapter.AllRoutesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        sharedPrefs = new SharedPrefs(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.allroutes_model,parent,false);
        return new AllRoutesAdapter.AllRoutesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AllRoutesAdapter.AllRoutesViewHolder holder, int position) {
        RouteSelectModel model = list.get(position);
        Log.d("TAGG",model.toString());
        holder.routeNumber.setText(model.getRouteNumber());
        holder.routeStartPoint.setText(model.getRouteStartPoint());
        holder.rouetEndPoint.setText(model.getRouteEndPoint());
        holder.viaPoints.setText(model.getRouteViaPoint());
        holder.fullRoute.setText(model.getRouteFullPath());
        holder.fcmRouteID.setText(model.getFcmRouteID());
        holder.routeImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isclick) {
                    holder.rel.setVisibility(View.VISIBLE);
                    isclick = true;
                } else {
                    holder.rel.setVisibility(View.GONE);
                    isclick = false;
                }
            }
        });
        holder.viewfulldetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String number = holder.routeNumber.getText().toString();
                Intent intent= new Intent(v.getContext(),RouteFullDetails.class);
                intent.putExtra("DetailsView",number);
                v.getContext().startActivity(intent);
                ((Activity)context).overridePendingTransition(R.anim.slidein, R.anim.slideout);
            }
        });
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public class AllRoutesViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.startpoint)TextView routeStartPoint;
        @BindView(R.id.endpoint)TextView rouetEndPoint;
        @BindView(R.id.routenumber)TextView routeNumber;
        @BindView(R.id.viapoints)TextView viaPoints;
        @BindView(R.id.fullroute)TextView fullRoute;
        @BindView(R.id.fcmrouteID)TextView fcmRouteID;
        @BindView(R.id.routeimage)ImageView routeImg;
        @BindView(R.id.r2)RelativeLayout rel;
        @BindView(R.id.viewfulldetails)Button viewfulldetails;


        public AllRoutesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}