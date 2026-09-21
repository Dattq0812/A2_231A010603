package vn.edu.vhu.ltdd.a2stopwatch;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Button;
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
    private static final String KEY_LAPS = "laps"; // Key để lưu danh sách vòng (NC1)

    private TextView tvTime, tvStatus, tvRecreate, tvLaps;
    private Button btnStartPause, btnReset, btnLap;

    private boolean running = false;
    private long accumulated = 0L;
    private long startTime = 0L;
    private int recreateCount = 0;

    // Biến lưu trữ danh sách các vòng đếm (NC1)
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
        tvLaps = findViewById(R.id.tvLaps); // Ánh xạ TextView hiển thị danh sách Vòng

        btnStartPause = findViewById(R.id.btnStartPause);
        btnReset = findViewById(R.id.btnReset);
        btnLap = findViewById(R.id.btnLap); // Ánh xạ nút Vòng

        if (savedInstanceState != null) {
            running = savedInstanceState.getBoolean(KEY_RUNNING);
            accumulated = savedInstanceState.getLong(KEY_ACCUMULATED);
            startTime = savedInstanceState.getLong(KEY_START);
            recreateCount = savedInstanceState.getInt(KEY_RECREATE) + 1;

            // Khôi phục danh sách Vòng (NC1)
            lapList = savedInstanceState.getStringArrayList(KEY_LAPS);
            if (lapList == null) lapList = new ArrayList<>();

            Log.d(TAG, "onCreate: KHÔI PHỤC trạng thái, running=" + running);
        }

        btnStartPause.setOnClickListener(v -> {
            if (running) pauseStopwatch();
            else startStopwatch();
        });

        btnReset.setOnClickListener(v -> resetStopwatch());

        // Xử lý sự kiện bấm nút Vòng (NC1)
        btnLap.setOnClickListener(v -> {
            if (running) { // Chỉ cho phép ghi vòng khi đồng hồ đang chạy
                String currentTime = tvTime.getText().toString();
                String lapRecord = "Vòng " + (lapList.size() + 1) + ": " + currentTime;
                lapList.add(lapRecord);
                updateLapUi();
                Log.i(TAG, lapRecord);
            }
        });

        updateUi();
        updateLapUi(); // Hiển thị danh sách vòng khi mở app (hoặc sau khi xoay màn hình)
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
        lapList.clear(); // Xóa danh sách vòng (NC1)
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

        // Vô hiệu hóa nút Vòng nếu đồng hồ đang dừng
        btnLap.setEnabled(running);
    }

    // Hàm cập nhật danh sách hiển thị các vòng (NC1)
    private void updateLapUi() {
        StringBuilder sb = new StringBuilder();
        for (int i = lapList.size() - 1; i >= 0; i--) { // Hiển thị vòng mới nhất lên đầu
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

    @Override
    protected void onStop() { super.onStop(); }

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

        // Lưu danh sách Vòng vào Bundle trước khi Activity bị hủy (NC1)
        outState.putStringArrayList(KEY_LAPS, lapList);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}