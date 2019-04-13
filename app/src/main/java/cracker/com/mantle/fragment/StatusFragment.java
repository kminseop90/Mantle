package cracker.com.mantle.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import cracker.com.mantle.R;
import cracker.com.mantle.service.CrackerManager;
import cracker.com.mantle.service.DataStreamListener;

public class StatusFragment extends Fragment implements View.OnClickListener, DataStreamListener {

        public static final String TAG = StatusFragment.class.getSimpleName();
    private TextView todayView;
    private ImageView circleView;
    private TextView stepVIew;
    private TextView statusView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_status, container, false);

        initializeViews(v);
        initializeData();
        return v;
    }

    private void initializeViews(View parentView) {
        todayView = parentView.findViewById(R.id.text_status_today_date);
        circleView = parentView.findViewById(R.id.img_circle);
        statusView = parentView.findViewById(R.id.text_status_message);
        stepVIew = parentView.findViewById(R.id.text_status_step);
        CrackerManager.getInstance().addDataStreamListeners(this);
    }

    private void initializeData() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) +1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        todayView.setText(String.format("%d월%d일", month, day));
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onDataReceive(String msg) {

    }

    @Override
    public void onHeartReceive(final String msg) {
        Log.d(TAG, "onHeartReceive: " + msg);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int heartValue = Integer.valueOf(msg.split(" ")[3]);
                if(heartValue <= 100) {
                    circleView.setImageResource(R.drawable.status_circle_01);
                    statusView.setTextColor(Color.parseColor("#7186c7"));
                    stepVIew.setText("현재 피로도 1단계");
                } else if (heartValue > 100 && heartValue <= 110) {
                    circleView.setImageResource(R.drawable.status_circle_02);
                    statusView.setTextColor(Color.parseColor("#f5cb23"));
                    stepVIew.setText("현재 피로도 2단계");
                } else if(heartValue > 110 && heartValue <= 120) {
                    circleView.setImageResource(R.drawable.status_circle_03);
                    statusView.setTextColor(Color.parseColor("#f5a623"));
                    stepVIew.setText("현재 피로도 3단계");
                } else {
                    circleView.setImageResource(R.drawable.status_circle_04);
                    statusView.setTextColor(Color.parseColor("#c84040"));
                    stepVIew.setText("현재 피로도 4단계");
                }
            }
        });
    }
}