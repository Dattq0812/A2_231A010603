# Lab A2: Đồng hồ bấm giờ (Stopwatch) - Yêu cầu nâng cao NC1

## Giới thiệu
Đây là dự án thực hành môn Lập trình trên thiết bị di động (Lab A2), xây dựng một ứng dụng Đồng hồ bấm giờ có khả năng xử lý mượt mà các sự kiện vòng đời (Lifecycle) của Android.

Mục tiêu chính của bản cập nhật này là hoàn thành **Yêu cầu nâng cao 1 (NC1)**: Bổ sung tính năng ghi nhận **Vòng (Lap)**, hiển thị danh sách các vòng đếm và đảm bảo dữ liệu này không bị mất khi xoay màn hình thiết bị hoặc khi ứng dụng bị thu hồi tài nguyên dưới nền.

## Các tính năng chính
*   **Bắt đầu / Tạm dừng / Đặt lại:** Hoạt động chính xác dựa trên hàm `SystemClock.elapsedRealtime()` của hệ thống.
*   **Tính năng Vòng (Lap) - NC1:**
    *   Cho phép người dùng đánh dấu thời gian hiện tại mỗi khi bấm nút "Vòng (Lap)".
    *   Hiển thị danh sách các vòng đếm ngay bên dưới màn hình, vòng mới nhất sẽ được đẩy lên đầu.
    *   Nút Vòng tự động bị vô hiệu hóa (disabled) khi đồng hồ đang ở trạng thái Tạm dừng hoặc Đặt lại.
*   **Bảo toàn dữ liệu (State Restoration):** Khắc phục triệt để lỗi mất dữ liệu khi xoay màn hình bằng cách lưu trữ toàn bộ trạng thái (thời gian, trạng thái chạy, danh sách vòng) vào đối tượng `Bundle` thông qua `onSaveInstanceState`.

## Kỹ thuật triển khai NC1
1. **Giao diện (XML):** Thêm nút `btnLap` và sử dụng `ScrollView` bọc ngoài `TextView` để có thể cuộn xem danh sách nếu số lượng vòng đếm quá dài.
2. **Cấu trúc dữ liệu:** Sử dụng `ArrayList<String>` để lưu danh sách các chuỗi mốc thời gian.
3. **Lưu trạng thái (Bundle):**
    *   Trong `onSaveInstanceState()`, danh sách được lưu vào `Bundle` bằng lệnh `outState.putStringArrayList(KEY_LAPS, lapList)`.
    *   Trong `onCreate()`, danh sách được trích xuất lại từ Bundle thông qua `savedInstanceState.getStringArrayList(KEY_LAPS)`. Nhờ vậy, chuỗi UI và dữ liệu luôn nguyên vẹn bất chấp thay đổi cấu hình thiết bị.

## Thông tin sinh viên
* **Sinh viên thực hiện:** Trần Quốc Đạt
* **MSSV:** 231A010603
* **Môn học:** Lập trình trên các thiết bị di động