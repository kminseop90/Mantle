package cracker.com.mantle;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class NotiActivity extends BaseActivity implements View.OnClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noti);

        findViewById(R.id.icon_noti_count_plus).setOnClickListener(countClickListener);
        findViewById(R.id.icon_noti_count_minus).setOnClickListener(countClickListener);
        findViewById(R.id.button_noti_save).setOnClickListener(this);
    }


    View.OnClickListener countClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int id = view.getId();
            int count = getCount();
            switch (id) {
                case R.id.icon_noti_count_minus:
                    setCount(--count);
                    break;
                case R.id.icon_noti_count_plus:
                    setCount(++count);
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.button_noti_save:
                finish();
                break;
        }
    }

    private int getCount() {
        TextView countView = findViewById(R.id.text_noti_count);
        String countString = countView.getText().toString();
        return Integer.parseInt(countString);
    }

    private void setCount(int count) {
        TextView countView = findViewById(R.id.text_noti_count);
        countView.setText(String.format("%03d", count));
    }
}
