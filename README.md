## Game Server - Multiplayer Sorting Game

Máy chủ TCP phục vụ game sắp xếp đa người chơi. Server nhận kết nối từ nhiều client JavaFX, điều phối phòng chơi, xử lý lượt sắp xếp và đồng bộ trạng thái theo thời gian thực.

## 📋 Mục lục
- [Tổng quan](#tổng-quan)
- [Tính năng](#tính-năng)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Cấu trúc dự án](#cấu-trúc-dự-án)
- [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
- [Cài đặt và chạy](#cài-đặt-và-chạy)
- [Hướng dẫn vận hành](#hướng-dẫn-vận-hành)
- [Kiến trúc](#kiến-trúc)
- [Giao thức & message](#giao-thức--message)
- [Cấu hình](#cấu-hình)
- [Troubleshooting](#troubleshooting)

## 🎯 Tổng quan
- Lắng nghe trên `0.0.0.0:8989`.
- Sử dụng `ThreadPoolExecutor` để phục vụ hàng chục đến hàng trăm client cùng lúc.
- `ServerThreadBus` quản lý toàn bộ `ServerThread`, cho phép broadcast hoặc tìm kiếm client theo ID.
- `Main` khởi chạy singleton `Server`, đảm bảo chỉ có một tiến trình dịch vụ.

## ✨ Tính năng
### Quản lý kết nối
- Nhận/gán `clientId` tự động.
- Lưu trữ `ServerThread` đang hoạt động và loại bỏ khi client ngắt kết nối.
- Giới hạn hàng đợi kết nối và số luồng nhằm tránh quá tải.

### Quản lý phòng và trận đấu
- Tạo phòng, tham gia phòng, rời phòng.
- Đồng bộ danh sách người chơi, lượt chơi, trạng thái trận.
- Tính điểm dựa trên kết quả sắp xếp, thời gian phản hồi.

### Giao thức JSON
- Lắng nghe message từ client và phản hồi theo định dạng JSON chuẩn.
- Các action: đăng nhập, đăng ký, quản lý phòng, gameplay, thống kê.

### Ghi log và giám sát cơ bản
- Ghi log kết nối/giải phóng client.
- In thông báo khi server bắt đầu, có lỗi hoặc shutdown.

## 🛠 Công nghệ sử dụng
- **Java 17**: ngôn ngữ và runtime chính.
- **Socket API** (`java.net.ServerSocket`, `Socket`): hạ tầng TCP.
- **ThreadPoolExecutor**: quản lý đa luồng.
- **Collections đồng bộ**: chia sẻ trạng thái giữa các luồng.
- (Tuỳ chọn) **JSON parser** (nếu sử dụng trong `ServerThread`).

## 📁 Cấu trúc dự án
```
server/
├── src/
│   └── main/java/com/game_server/
│       ├── Main.java                       # Điểm vào chính, gọi Server.start()
│       └── controllers/
│           ├── Server.java                 # Quản lý socket, executor, singleton
│           ├── ServerThread.java           # Luồng xử lý từng client
│           └── ServerThreadBus.java        # Quản lý danh sách ServerThread
├── build.gradle (hoặc gradle wrapper)      # Cấu hình build
└── README.md
```

## 💻 Yêu cầu hệ thống
- **JDK**: 17 trở lên.
- **Gradle**: 7+ (hoặc dùng Gradle Wrapper `./gradlew`).
- **OS**: Windows / macOS / Linux.
- **Mạng**: cổng 8989 mở và không bị firewall chặn.

## 🚀 Cài đặt và chạy
### 1. Clone repo
```bash
git clone <repository-url>
cd server
```

### 2. Build
```bash
./gradlew clean build   # macOS/Linux
gradlew.bat clean build # Windows
```

### 3. Chạy server
```bash
./gradlew run
# hoặc
java -cp build/libs/<jar-name>.jar com.game_server.Main
```
Khi thành công, log hiển thị `Server is running on port 8989`.

## 🧭 Hướng dẫn vận hành
1. Khởi chạy server trước khi mở ứng dụng client.
2. Theo dõi log để biết client kết nối/ngắt.
3. Khi muốn dừng:
   - Nhấn `Ctrl+C` trong terminal, hoặc stop trong IDE.
   - Server sẽ `shutdown()` thread pool trong khối `finally`.
4. Để khởi động lại, đảm bảo port 8989 đã được giải phóng.

## 🏗 Kiến trúc
### Pattern chính
- **Singleton**: `Server` chỉ có một instance, truy cập qua `Server.getInstance()`.
- **Thread-per-connection**: mỗi client có một `ServerThread`, tái sử dụng qua thread pool.
- **Publisher/Subscriber đơn giản**: `ServerThreadBus` giữ danh sách thread để broadcast.

### Luồng hoạt động
1. `Main` → `Server.getInstance()` → `server.start()`.
2. `Server.start()`:
   - Tạo `ServerThreadBus`.
   - Khởi tạo `ThreadPoolExecutor`.
   - Chấp nhận kết nối bằng `serverSocket.accept()`.
3. Mỗi `Socket` mới:
   - Tạo `ServerThread` (chứa clientId, socket, bus).
   - Đăng ký vào `ServerThreadBus`.
   - Giao cho executor thực thi.
4. `ServerThread`:
   - Đọc JSON, xử lý action (login, join room, submit answer…).
   - Gửi phản hồi JSON tới client.
   - Khi client thoát, đóng socket, báo `ServerThreadBus.remove(...)`.

## 🔐 Giao thức & message
> Dưới đây là danh sách action khuyến nghị (cần đồng bộ với client):

### Client → Server
- `LOGIN`, `REGISTER`
- `GET_ONLINE_USERS`
- `INVITE_USER_TO_GAME`, `INVITE_USER_TO_GAME_RESPONSE`
- `CREATE_ROOM`, `JOIN_ROOM`, `LEAVE_ROOM`
- `SUBMIT_USER_ANSWER`, `QUIT_GAME`
- `INVITE_USER_TO_NEXT_GAME`
- `GET_RANKING`, `GET_MATCH_HISTORY`

### Server → Client
- `LOGIN_RESPONSE`, `REGISTER_RESPONSE`
- `GET_ONLINE_USERS_RESPONSE`
- `INVITE_USER_TO_GAME_REQUEST`, `INVITE_USER_TO_GAME_RESULT`
- `START_GAME`, `GAME_RESULT`, `GAME_FINAL_RESULT`, `CONTINUE_NEXT_GAME`
- `ROOM_UPDATED`
- `GET_RANKING_RESPONSE`, `GET_MATCH_HISTORY_RESPONSE`

Server nên chuẩn hóa:
- Mỗi message gồm `action`, `status`, `data`, `message`.
- Thời gian gửi dùng `epoch millis` hoặc ISO-8601.
- Kiểm tra quyền hạn trước khi xử lý action (ví dụ chỉ host mới được start game).

## ⚙️ Cấu hình
| Tham số | Mặc định | Mô tả |
|---------|----------|-------|
| `HOST`  | `0.0.0.0` | Địa chỉ lắng nghe. |
| `PORT`  | `8989`    | Cổng TCP. |
| `BACKLOG` | `50`   | Hàng đợi kết nối chờ accept. |
| `CORE_POOL_SIZE` | `10` | Số luồng tối thiểu trong executor. |
| `MAX_POOL_SIZE` | `100` | Số luồng tối đa. |
| `QUEUE_CAPACITY` | `8` | Kích thước hàng đợi task. |

Để thay đổi, sửa trực tiếp trong `Server.start()`:
```java
ServerSocket serverSocket = new ServerSocket(PORT, BACKLOG, InetAddress.getByName(HOST));
ThreadPoolExecutor executor = new ThreadPoolExecutor(
        CORE_POOL_SIZE,
        MAX_POOL_SIZE,
        10, TimeUnit.SECONDS,
        new ArrayBlockingQueue<>(QUEUE_CAPACITY)
);
```

## 🐛 Troubleshooting
### Không khởi động được server
- Kiểm tra port 8989 có đang bị chiếm (`lsof -i :8989`).
- Đảm bảo JDK 17 đã cài (`java -version`).
- Kiểm tra quyền mở socket (trên Linux/macOS cần quyền với port <1024).

### Client không kết nối được
- Ping server từ máy client.
- Kiểm tra firewall (Windows Defender, ufw, iptables).
- Đảm bảo server log báo “New client connected…”.

### Treo hoặc quá tải
- Giám sát log xem có ngoại lệ trong `ServerThread`.
- Tăng `MAX_POOL_SIZE` và `QUEUE_CAPACITY`.
- Thêm timeout đọc/ghi trong `ServerThread`.

### Lỗi JSON / thông điệp
- In log message toàn bộ trước khi parse.
- Xác nhận client-server cùng version giao thức.
- Log cả `clientId` để truy vết.

## 🔮 Hướng phát triển
- Thêm cơ chế xác thực token.
- Ghi log chuẩn (Logback/SLF4J).
- Tách lớp xử lý business riêng để dễ test.
- Viết test unit/integration cho `ServerThreadBus`.
- Tự động triển khai server bằng Docker/CI-CD.
# LTM-GameServer
