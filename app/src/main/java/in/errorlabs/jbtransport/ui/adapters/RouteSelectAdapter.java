package in.errorlabs.jbtransport.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.ui.models.RouteSelectModel;

/**
 * Created by root on 6/29/17.
 */

public class RouteSelectAdapter extends RecyclerView.Adapter<RouteSelectAdapter.RouteSelectViewHolder> {
    Context context;
    List<RouteSelectModel> list;
    boolean isclick=false;

    public RouteSelectAdapter(List<RouteSelectModel> list,Context context) {
        this.context = context;
        this.list = list;
    }



    @Override
    public RouteSelectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        holder.setRouteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s,e,f;
                s=holder.routeStartPoint.getText().toString();
                e=holder.rouetEndPoint.getText().toString();
                f=holder.fullRoute.getText().toString();
                final NiftyDialogBuilder dialogBuilder= NiftyDialogBuilder.getInstance(v.getContext());
                dialogBuilder
                        .withTitle("Select Primary Route")
                        .withMessage("Are you sure you want to set this route as your primary route ?"+"\n\n\t"
                        +s+"<-->"+e+"\n\n"
                        +"Full Route :"+"\n\n\t"
                        +f)
                        .withEffect(Effectstype.Fall)
                        .withDialogColor("#1565c0")
                        .withDividerColor("#11000000")
                        .withButton1Text("Proceed")
                        .withButton2Text("Cancel")
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialogBuilder.dismiss();
                            }
                        })
                        .show();
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

        public RouteSelectViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
}
