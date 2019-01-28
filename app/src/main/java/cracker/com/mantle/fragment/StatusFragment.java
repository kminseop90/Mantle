package cracker.com.mantle.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Calendar;

import cracker.com.mantle.R;

public class StatusFragment extends Fragment implements View.OnClickListener {


    private TextView todayView;

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
}