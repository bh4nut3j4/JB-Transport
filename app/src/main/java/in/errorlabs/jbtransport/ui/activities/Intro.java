package in.errorlabs.jbtransport.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.app.NavigationPolicy;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import in.errorlabs.jbtransport.R;
import in.errorlabs.jbtransport.utils.SharedPrefs;


public class Intro extends IntroActivity {

    SharedPrefs sharedPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        setFullscreen(true);
        super.onCreate(savedInstanceState);
        sharedPrefs=new SharedPrefs(this);
        setFinishEnabled(true);
        setButtonBackVisible(false);
        setButtonNextVisible(false);
        setNavigationPolicy(new NavigationPolicy() {
            @Override
            public boolean canGoForward(int position) {
                return position != 6;
            }
            @Override
            public boolean canGoBackward(int position) {
                return position != 0;
            }
        });
        addSlide(new SimpleSlide.Builder()
                .title("JB Group Transport Department")
                .description("Welcome")
                .image(R.drawable.bus_front)
                .background(R.color.color_canteen)
                .backgroundDark(R.color.color_dark_canteen)
                .build());
        addSlide(new SimpleSlide.Builder()
                .description("How this app works?")
                .image(R.drawable.qstn)
                .background(R.color.color_custom_fragment_2)
                .backgroundDark(R.color.color_dark_custom_fragment_2)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title("Primary Route")
                .description("Select your primary(daily) route")
                .image(R.drawable.routebus)
                .background(R.color.color_material_bold)
                .backgroundDark(R.color.color_dark_material_bold)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title("Real Time Notifications")
                .description("Get real time notification updates whenever there is a change in buses or routes")
                .image(R.drawable.notif)
                .background(R.color.color_custom_fragment_1)
                .backgroundDark(R.color.color_dark_custom_fragment_1)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title("Bus Location Request")
                .description("Missed the bus? Get current bus location with a single tap")
                .image(R.drawable.busloc)
                .background(R.color.color_material_motion)
                .backgroundDark(R.color.color_dark_material_motion)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title("Route Map and Live Traffic")
                .description("Full route map with boarding point indicators, along with real-time live traffic")
                .image(R.drawable.traffic)
                .background(R.color.color_custom_fragment_1)
                .backgroundDark(R.color.color_dark_custom_fragment_1)
                .build());
        addSlide(new SimpleSlide.Builder()
                .title("Complaints")
                .description("Register a complaint anonymously")
                .image(R.drawable.complaintsintro)
                .background(R.color.color_permissions)
                .backgroundDark(R.color.color_dark_permissions)
                .buttonCtaLabel("Get Started")
                .buttonCtaClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        sharedPrefs.setFirstOpen();
                        startActivity(new Intent(getApplicationContext(),Splash.class));
                            finish();
                    }
                })
                .build());
    }


}
