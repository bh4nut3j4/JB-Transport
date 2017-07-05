package in.errorlabs.jbtransport.ui.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.activities.MapViewActivity;
import in.errorlabs.jbtransport.ui.activities.HomeActivity;
import in.errorlabs.jbtransport.ui.models.RouteSelectModel;
import in.errorlabs.jbtransport.utils.SharedPrefs;

/**
 * Created by root on 6/29/17.
 */

public class RouteSelectAdapter extends RecyclerView.Adapter<RouteSelectAdapter.RouteSelectViewHolder> {
    private Context context;
    List<RouteSelectModel> list;
    private boolean isclick=false;
    SharedPrefs sharedPrefs;

    public RouteSelectAdapter(List<RouteSelectModel> list,Context context) {
        this.context = context;
        this.list = list;
    }
    @Override
    public RouteSelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        sharedPrefs = new SharedPrefs(parent.getContext());
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.route_select_model,parent,false);
        return new RouteSelectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RouteSelectViewHolder holder, int position) {
        RouteSelectModel model = list.get(position);
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
        holder.viewongooglemap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String routeNumber = holder.routeNumber.getText().toString();
                Intent intent = new Intent(context, MapViewActivity.class);
                intent.putExtra("Gmaps",routeNumber);
                context.startActivity(intent);
            }
        });
        holder.setRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String s,e,f;
                s=holder.routeStartPoint.getText().toString();
                e=holder.rouetEndPoint.getText().toString();
                f=holder.fullRoute.getText().toString();
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setTitle(s+"<-->"+e);
                builder.setIcon(R.drawable.route);
                builder.setMessage(context.getString(R.string.fullroute)+"\n\n"
                        +f+"\n\n\n"+context.getString(R.string.areyousure));
                builder.setPositiveButton(R.string.proceed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String routeNumber = holder.routeNumber.getText().toString();
                        String fcmRouteId = holder.fcmRouteID.getText().toString();
                        Toast.makeText(context,routeNumber+fcmRouteId,Toast.LENGTH_SHORT).show();
                        if (routeNumber.length()>0&&fcmRouteId.length()>0){
                            sharedPrefs.setSelectedRouteNumber(routeNumber);
                            sharedPrefs.setSelectedRouteFcmID(fcmRouteId);
                            sharedPrefs.setRouteSelected();
                            Intent intent= new Intent(context,HomeActivity.class);
                            v.getContext().startActivity(intent);
                            ((Activity)context).finish();
                        }else {
                            Toast.makeText(context,R.string.tryagainlater,Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.setNegativeButton(R.string.cancel,null);
                builder.show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class RouteSelectViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.startpoint)TextView routeStartPoint;
        @BindView(R.id.endpoint)TextView rouetEndPoint;
        @BindView(R.id.routenumber)TextView routeNumber;
        @BindView(R.id.viapoints)TextView viaPoints;
        @BindView(R.id.fullroute)TextView fullRoute;
        @BindView(R.id.fcmrouteID)TextView fcmRouteID;
        @BindView(R.id.routeimage)ImageView routeImg;
        @BindView(R.id.r2)RelativeLayout rel;
        @BindView(R.id.setroutebutton)Button setRouteBtn;
        @BindView(R.id.googlemaps)LinearLayout viewongooglemap;


        public RouteSelectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
