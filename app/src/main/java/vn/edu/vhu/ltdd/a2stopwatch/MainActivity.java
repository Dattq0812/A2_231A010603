package vn.edu.vhu.ltdd.a2stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A2_231A010603";

    private static final String KEY_RUNNING = "running";
    private static final String KEY_ACCUMULATED = "accumulated";
    private static final String KEY_START = "start";
    private static final String KEY_RECREATE = "recreate";
    private static final String KEY_LAPS = "laps";

    // Khóa để lưu trạng thái CheckBox (NC2)
    private static final String KEY_STOP_ON_BG = "stop_on_bg";

    private TextView tvTime, tvStatus, tvRecreate, tvLaps;
    private Button btnStartPause, btnReset, btnLap;

    // Khai báo CheckBox (NC2)
    private CheckBox cbStopOnBackground;

    private boolean running = false;
    private long accumulated = 0L;
    private long startTime = 0L;
    private int recreateCount = 0;
    private ArrayList<String> lapList = new ArrayList<>();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            updateTimeText();
            handler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        tvTime = findViewById(R.id.tvTime);
        tvStatus = findViewById(R.id.tvStatus);
        tvRecreate = findViewById(R.id.tvRecreate);
        tvLaps = findViewById(R.id.tvLaps);

        btnStartPause = findViewById(R.id.btnStartPause);
        btnReset = findViewById(R.id.btnReset);
        btnLap = findViewById(R.id.btnLap);

        // Ánh xạ CheckBox từ giao diện (NC2)
        cbStopOnBackground = findViewById(R.id.cbStopOnBackground);

        if (savedInstanceState != null) {
            running = savedInstanceState.getBoolean(KEY_RUNNING);
            accumulated = savedInstanceState.getLong(KEY_ACCUMULATED);
            startTime = savedInstanceState.getLong(KEY_START);
            recreateCount = savedInstanceState.getInt(KEY_RECREATE) + 1;

            lapList = savedInstanceState.getStringArrayList(KEY_LAPS);
            if (lapList == null) lapList = new ArrayList<>();

            // Khôi phục trạng thái đánh dấu của CheckBox (NC2)
            boolean isStopOnBgChecked = savedInstanceState.getBoolean(KEY_STOP_ON_BG, false);
            cbStopOnBackground.setChecked(isStopOnBgChecked);

            Log.d(TAG, "onCreate: KHÔI PHỤC trạng thái, running=" + running);
        }

        btnStartPause.setOnClickListener(v -> {
            if (running) pauseStopwatch();
            else startStopwatch();
        });

        btnReset.setOnClickListener(v -> resetStopwatch());

        btnLap.setOnClickListener(v -> {
            if (running) {
                String currentTime = tvTime.getText().toString();
                String lapRecord = "Vòng " + (lapList.size() + 1) + ": " + currentTime;
                lapList.add(lapRecord);
                updateLapUi();
                Log.i(TAG, lapRecord);
            }
        });

        updateUi();
        updateLapUi();
    }

    private long elapsed() {
        return running ? accumulated + (SystemClock.elapsedRealtime() - startTime) : accumulated;
    }

    private void startStopwatch() {
        running = true;
        startTime = SystemClock.elapsedRealtime();
        startTicking();
        updateUi();
    }

    private void pauseStopwatch() {
        accumulated += SystemClock.elapsedRealtime() - startTime;
        running = false;
        stopTicking();
        updateUi();
    }

    private void resetStopwatch() {
        running = false;
        accumulated = 0L;
        startTime = 0L;
        lapList.clear();
        stopTicking();
        updateUi();
        updateLapUi();
    }

    private void startTicking() {
        handler.removeCallbacks(ticker);
        handler.post(ticker);
    }

    private void stopTicking() {
        handler.removeCallbacks(ticker);
    }

    private void updateTimeText() {
        long ms = elapsed();
        long phut = ms / 60000;
        long giay = (ms % 60000) / 1000;
        long phanMuoi = (ms % 1000) / 100;
        tvTime.setText(String.format(Locale.getDefault(), "%02d:%02d.%d", phut, giay, phanMuoi));
    }

    private void updateUi() {
        updateTimeText();
        btnStartPause.setText(running ? R.string.pause : R.string.start);
        tvStatus.setText(running ? R.string.status_running : R.string.status_paused);
        tvRecreate.setText(getString(R.string.recreate_count, recreateCount));
        btnLap.setEnabled(running);
    }

    private void updateLapUi() {
        StringBuilder sb = new StringBuilder();
        for (int i = lapList.size() - 1; i >= 0; i--) {
            sb.append(lapList.get(i)).append("\n");
        }
        tvLaps.setText(sb.toString());
    }

    @Override
    protected void onStart() { super.onStart(); }

    @Override
    protected void onResume() {
        super.onResume();
        if (running) startTicking();
        updateUi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTicking();
    }

    // --- XỬ LÝ SỰ KIỆN NC2 ---
    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");

        // Kiểm tra xem CheckBox có đang được tick và đồng hồ có đang chạy không (NC2)
        if (cbStopOnBackground.isChecked() && running) {
            pauseStopwatch();
            Log.i(TAG, "NC2: Đồng hồ tự động tạm dừng vì ứng dụng ra nền.");
        }
    }

    @Override
    protected void onDestroy() {
        stopTicking();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_RUNNING, running);
        outState.putLong(KEY_ACCUMULATED, accumulated);
        outState.putLong(KEY_START, startTime);
        outState.putInt(KEY_RECREATE, recreateCount);
        outState.putStringArrayList(KEY_LAPS, lapList);

        // Lưu trạng thái của CheckBox vào Bundle để duy trì khi xoay màn hình (NC2)
        outState.putBoolean(KEY_STOP_ON_BG, cbStopOnBackground.isChecked());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}