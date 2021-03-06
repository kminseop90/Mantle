package cracker.com.mantle.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;

import cracker.com.mantle.R;
import cracker.com.mantle.model.CalendarModel;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    private CalendarModel calendar;
    public TextView dayView;
    public ImageView notiView;
    private OnCalendarItemClick onCalendarItemClick;


    public CalendarViewHolder(View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        dayView = itemView.findViewById(R.id.holder_day_text);
        notiView = itemView.findViewById(R.id.image_day_noti);
    }

    public void bindView(CalendarModel calendar) {
        if (calendar != null) {
            this.calendar = calendar;

            if (!calendar.isDayInThisMonth()) {
                dayView.setVisibility(View.GONE);
            } else {
                dayView.setVisibility(View.VISIBLE);
                dayView.setText(calendar.getDay() + "");

                if(calendar.isDayInThisMonth() && calendar.getNoti() != null) {
                    notiView.setVisibility(View.VISIBLE);
                    dayView.setTextColor(Color.WHITE);
                }

                if (calendar.getDayOfWeek() == Calendar.SUNDAY) {
                    dayView.setTextColor(Color.parseColor("#d41616"));
                } else if (calendar.getDayOfWeek() == Calendar.SATURDAY) {
                    dayView.setTextColor(Color.parseColor("#0072dc"));
                } else {
                    dayView.setTextColor(Color.parseColor("#000000"));
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(onCalendarItemClick != null && calendar.getNoti() != null) {
            onCalendarItemClick.onCalendarItemClick(calendar);
        }
    }

    public void setOnCalendarItemClick(OnCalendarItemClick onCalendarItemClick) {
        this.onCalendarItemClick = onCalendarItemClick;
    }

    public interface OnCalendarItemClick {
        void onCalendarItemClick(CalendarModel calendarModel);
    }
}
