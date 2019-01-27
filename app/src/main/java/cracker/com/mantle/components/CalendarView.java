package cracker.com.mantle.components;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Calendar;

import cracker.com.mantle.R;
import cracker.com.mantle.adapter.CalendarAdapter;
import cracker.com.mantle.model.CalendarModel;

public class CalendarView extends RelativeLayout implements View.OnClickListener {


    private TextView dateView;
    private ImageView prevView;
    private ImageView nextView;
    private RecyclerView calendarView;
    private String currentYearMonth;

    private String[] englishMonths = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December" };

    public CalendarView(Context context) {
        super(context);
        initialize();
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    private void initialize() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.view_calendar, this, false);
        addView(view);

        dateView = view.findViewById(R.id.text_calendar_date);
        prevView = view.findViewById(R.id.icon_calendar_prev);
        nextView = view.findViewById(R.id.icon_calendar_next);
        calendarView = view.findViewById(R.id.list_calendar);
        calendarView.setLayoutManager(new GridLayoutManager(getContext(), 7));
        calendarView.setAdapter(new CalendarAdapter());

        currentYearMonth = String.format("%04d%02d", Calendar.getInstance().get(Calendar.YEAR), (Calendar.getInstance().get(Calendar.MONTH) + 1));
        String dateText = String.format("%s %d", getMonth(Integer.parseInt(currentYearMonth.substring(4,6))), Integer.parseInt(currentYearMonth.substring(0,4)));
        dateView.setText(dateText);
        ((CalendarAdapter) calendarView.getAdapter()).setData(new CalendarModel().getData(getYearMonth()));

        prevView.setOnClickListener(this);
        nextView.setOnClickListener(this);
    }

    private String getYearMonth() {
        return currentYearMonth;
    }

    private String getMonth(int month) {
        return englishMonths[--month];
    }

    private void onNextClick() {
        int year = Integer.parseInt(currentYearMonth.substring(0, 4));
        int month = Integer.parseInt(currentYearMonth.substring(4, 6));

        if(month == 12) {
            year++;
            month = 1;
        } else {
            month++;
        }
        currentYearMonth = String.format("%04d%02d",year, month);
        String dateText = String.format("%s %d", getMonth(month), year);
        dateView.setText(dateText);

        ((CalendarAdapter) calendarView.getAdapter()).setData(new CalendarModel().getData(currentYearMonth));
    }

    private void onPrevClick() {
        int year = Integer.parseInt(currentYearMonth.substring(0, 4));
        int month = Integer.parseInt(currentYearMonth.substring(4, 6));

        if(month == 1) {
            year--;
            month = 12;
        } else {
            month--;
        }
        currentYearMonth = String.format("%04d%02d",year, month);
        String dateText = String.format("%s %d", getMonth(month), year);
        dateView.setText(dateText);

        ((CalendarAdapter) calendarView.getAdapter()).setData(new CalendarModel().getData(currentYearMonth));
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.icon_calendar_next:
                onNextClick();
                break;
            case R.id.icon_calendar_prev:
                onPrevClick();
                break;
        }
    }
}
