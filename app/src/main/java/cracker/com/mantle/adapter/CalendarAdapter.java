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

public class CalendarAdapter extends RecyclerView.Adapter<CalendarViewHolder> {

    private ArrayList<CalendarModel> items = new ArrayList<>();

    public void setData(ArrayList<CalendarModel> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_day, parent, false);
        return new CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CalendarViewHolder holder, int position) {
        CalendarModel item = items.get(position);

        if (item != null) {
            if (!item.isDayInThisMonth()) {
                holder.dayView.setVisibility(View.GONE);
            } else {
                holder.dayView.setVisibility(View.VISIBLE);
                holder.dayView.setText(item.getDay() + "");
                /*if (item.isToday()) {
                    holder.checkView.setVisibility(View.VISIBLE);
                } else {
                    holder.checkView.setVisibility(View.GONE);
                }*/

                if (item.getDayOfWeek() == Calendar.SUNDAY) {
                    holder.dayView.setTextColor(Color.parseColor("#d41616"));
                } else if (item.getDayOfWeek() == Calendar.SATURDAY) {
                    holder.dayView.setTextColor(Color.parseColor("#0072dc"));
                } else {
                    holder.dayView.setTextColor(Color.parseColor("#000000"));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
