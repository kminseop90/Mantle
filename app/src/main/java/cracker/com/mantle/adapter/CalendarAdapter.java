package cracker.com.mantle.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Calendar;

import cracker.com.mantle.R;
import cracker.com.mantle.model.CalendarModel;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> implements CalendarViewHolder.OnCalendarItemClick {

    private ArrayList<CalendarModel> items = new ArrayList<>();
    private OnCalendarAdapterItemClick onCalendarAdapterItemClick;

    public CalendarAdapter(OnCalendarAdapterItemClick onCalendarAdapterItemClick) {
        this.onCalendarAdapterItemClick = onCalendarAdapterItemClick;
    }

    public void setData(ArrayList<CalendarModel> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_day, parent, false);
        CalendarViewHolder calendarViewHolder = new CalendarViewHolder(view);
        calendarViewHolder.setOnCalendarItemClick(this);
        return calendarViewHolder;
    }

    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        CalendarModel item = items.get(position);
        holder.bindView(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public void onCalendarItemClick(CalendarModel calendarModel) {
        if(onCalendarAdapterItemClick != null) {
            onCalendarAdapterItemClick.onCalendarAdapterItemClick(calendarModel);
        }
    }

    public void setOnCalendarAdapterItemClick(OnCalendarAdapterItemClick onCalendarAdapterItemClick) {
        this.onCalendarAdapterItemClick = onCalendarAdapterItemClick;
    }

    public interface OnCalendarAdapterItemClick {
        void onCalendarAdapterItemClick(CalendarModel calendarModel);
    }
}
