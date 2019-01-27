package cracker.com.mantle.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import cracker.com.mantle.R;

public class CalendarViewHolder extends RecyclerView.ViewHolder {

    public TextView dayView;

    public CalendarViewHolder(View itemView) {
        super(itemView);
        dayView = itemView.findViewById(R.id.holder_day_text);
    }
}
