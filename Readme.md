# Lab A2: Đồng hồ bấm giờ (Stopwatch) - Yêu cầu nâng cao NC1 & NC2

## Giới thiệu
Đây là dự án thực hành môn Lập trình trên thiết bị di động (Lab A2), xây dựng một ứng dụng Đồng hồ bấm giờ có khả năng xử lý mượt mà các sự kiện vòng đời (Lifecycle) của Android.

Mục tiêu của bản cập nhật này là hoàn thành **Yêu cầu nâng cao 1 (NC1)** (Tính năng Vòng đếm) và **Yêu cầu nâng cao 2 (NC2)** (Tính năng tự động dừng khi ra nền), đồng thời đảm bảo dữ liệu không bị mất khi xoay màn hình thiết bị hoặc khi ứng dụng bị thu hồi tài nguyên.

## Các tính năng chính
*   **Bắt đầu / Tạm dừng / Đặt lại:** Hoạt động chính xác dựa trên hàm `SystemClock.elapsedRealtime()` của hệ thống.
*   **Tính năng Vòng (Lap) - NC1:**
    *   Cho phép người dùng đánh dấu thời gian hiện tại mỗi khi bấm nút "Vòng (Lap)".
    *   Hiển thị danh sách các vòng đếm ngay bên dưới màn hình, vòng mới nhất sẽ được đẩy lên đầu.
    *   Nút Vòng tự động bị vô hiệu hóa (disabled) khi đồng hồ đang ở trạng thái Tạm dừng hoặc Đặt lại.
*   **Tính năng Dừng khi ra nền (Stop on Background) - NC2:**
    *   Cung cấp tuỳ chọn CheckBox "Dừng khi ra nền".
    *   Khi được đánh dấu, hệ thống sẽ tự động tạm dừng đồng hồ nếu người dùng thoát ứng dụng ra màn hình chính (nhấn phím Home) hoặc mở một ứng dụng khác đè lên.
*   **Bảo toàn dữ liệu (State Restoration):** Khắc phục triệt để lỗi mất dữ liệu khi xoay màn hình bằng cách lưu trữ toàn bộ trạng thái (thời gian, trạng thái chạy, danh sách vòng, trạng thái CheckBox) vào đối tượng `Bundle` thông qua `onSaveInstanceState`.

## Kỹ thuật triển khai
### 1. Triển khai NC1 (Tính năng Vòng)
*   **Giao diện:** Thêm nút `btnLap` và sử dụng `ScrollView` bọc ngoài `TextView` để cuộn xem danh sách.
*   **Cấu trúc dữ liệu:** Sử dụng `ArrayList<String>` để lưu danh sách các chuỗi mốc thời gian.
*   **Lưu trạng thái:** Sử dụng `outState.putStringArrayList(KEY_LAPS, lapList)` trong `onSaveInstanceState` và khôi phục bằng `savedInstanceState.getStringArrayList(KEY_LAPS)` trong `onCreate`.

### 2. Triển khai NC2 (Tính năng Dừng khi ra nền)
*   **Can thiệp vòng đời (Lifecycle):** Bổ sung logic kiểm tra vào hàm `onStop()` (được gọi khi ứng dụng bị ẩn hoàn toàn). Nếu `cbStopOnBackground.isChecked()` và đồng hồ đang chạy (`running == true`), hệ thống gọi hàm `pauseStopwatch()`.
*   **Bảo toàn trạng thái UI:** Trạng thái đánh dấu của CheckBox được lưu trữ vào Bundle bằng lệnh `outState.putBoolean(KEY_STOP_ON_BG, cbStopOnBackground.isChecked())` để không bị mất dấu tick khi người dùng xoay ngang màn hình.

## Thông tin sinh viên
* **Sinh viên thực hiện:** Trần Quốc Đạt
* **MSSV:** 231A010603
* **Lớp:** 252INT440707
* **Khoa:** Công nghệ thông tin - Đại học Văn Hiến