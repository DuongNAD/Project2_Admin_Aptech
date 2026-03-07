-- phpMyAdmin SQL Dump
-- version 5.2.1
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th3 07, 2026 lúc 07:19 AM
-- Phiên bản máy phục vụ: 10.4.32-MariaDB
-- Phiên bản PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

-- Cơ sở dữ liệu: `elearning_system`

-- Cấu trúc bảng cho bảng `admins`
CREATE TABLE `admins` (
  `admin_id` int(11) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `password_hash` varchar(255) NOT NULL,
  `role` varchar(50) DEFAULT 'admin',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `admins` (`admin_id`, `full_name`, `email`, `password_hash`, `role`, `created_at`) VALUES
(1, 'Super Admin', 'admin@elearning.com', '$2a$10$EIFzOM8E0S0QkR1S2z1l1eB1yOOT005P7.i8E0bC.00A.7E0T/', 'super_admin', '2026-03-04 17:01:13');

-- Cấu trúc bảng cho bảng `articles`
CREATE TABLE `articles` (
  `article_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `content` longtext DEFAULT NULL,
  `image_url` varchar(500) DEFAULT NULL,
  `tags` varchar(100) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `articles` (`article_id`, `title`, `description`, `content`, `image_url`, `tags`, `created_at`) VALUES
(1, 'DeepSeek-R1: Kẻ thách thức GPT-4?', 'Phân tích mô hình AI mới với khả năng suy luận vượt trội...', 'Trí tuệ nhân tạo đang bước vào một kỷ nguyên mới, và sự ra mắt của DeepSeek-R1 đang làm rúng động cả cộng đồng công nghệ. Không giống như các mô hình ngôn ngữ lớn (LLM) đóng kín, DeepSeek-R1 đi theo hướng mã nguồn mở (Open-source) nhưng lại sở hữu khả năng suy luận (Reasoning) và giải quyết vấn đề toán học, lập trình đáng kinh ngạc.\n\nTrong các bài kiểm tra benchmark gần đây, R1 đã cho thấy hiệu năng tiệm cận, thậm chí vượt trội hơn GPT-4o ở một số tác vụ logic phức tạp. Điểm đặc biệt của mô hình này là cơ chế "chuỗi suy nghĩ" (Chain-of-Thought) được tối ưu hóa cực tốt, giúp AI "nghĩ" ra các bước giải quyết vấn đề một cách minh bạch trước khi đưa ra câu trả lời cuối cùng.\n\nLiệu đây có phải là dấu chấm hết cho thế độc tôn của OpenAI? Dù chưa thể khẳng định, nhưng chắc chắn DeepSeek-R1 đang mở ra cơ hội tiếp cận AI đỉnh cao cho các nhà phát triển độc lập với chi phí rẻ hơn rất nhiều.', 'https://img.freepik.com/free-photo/ai-technology-brain-background-digital-transformation-concept_53876-124672.jpg', '#AI News', '2026-02-10 08:22:39'),
(2, 'Lộ trình Backend Java 2026', 'Tổng hợp kỹ năng Spring Boot 3, Microservices...', 'Java chưa bao giờ là một ngôn ngữ "lỗi thời". Bước sang năm 2026, vai trò của một Backend Developer chuyên Java đòi hỏi sự cập nhật liên tục để đáp ứng các hệ thống tải cao (High-load systems).\n\nLộ trình năm nay nhấn mạnh vào việc làm chủ Java 21+ với các tính năng đột phá như Virtual Threads (Project Loom) giúp xử lý đồng thời hàng triệu kết nối mà không ngốn tài nguyên RAM. Bên cạnh đó, bạn cần nắm vững kiến trúc Microservices, sử dụng thành thạo các Message Broker như Apache Kafka hoặc RabbitMQ để giao tiếp giữa các services.\n\nĐừng quên trang bị kiến thức về Cloud (AWS/GCP), kỹ năng viết Unit Test hiệu quả và hiểu sâu về cách tối ưu hóa truy vấn Database (SQL Tuning). Một Backend Dev giỏi không chỉ biết viết code chạy được, mà còn phải viết code dễ bảo trì và mở rộng!', 'https://img.freepik.com/free-vector/laptop-with-program-code-isometric-icon-software-development-programming-applications-dark-neon_39422-971.jpg', '#Career Path', '2026-02-10 08:22:39'),
(4, 'Lộ trình Backend Java Spring Boot 2026', 'Tổng hợp các kỹ năng cần thiết từ cơ bản đến nâng cao: Microservices, Docker, K8s và Cloud Native.', 'Nếu Java là nền móng thì Spring Boot chính là "vũ khí tối thượng" của mọi Backend Developer. Trong năm 2026, Spring Boot 3.x đã trở thành tiêu chuẩn bắt buộc. Việc chuyển đổi hoàn toàn sang Jakarta EE và hỗ trợ native image với GraalVM giúp các ứng dụng Spring khởi động trong tích tắc và tốn cực ít bộ nhớ.\n\nBạn cần tập trung vào việc xây dựng RESTful API chuẩn REST, bảo mật API với Spring Security (JWT, OAuth2) và quản lý dữ liệu hiệu quả với Spring Data JPA. Xu hướng hiện tại cũng yêu cầu Developer tích hợp sâu với các công cụ giám sát (Observability) như Prometheus, Grafana thông qua Spring Boot Actuator.\n\nĐể tiến xa hơn, hãy làm quen với Spring Cloud để xử lý các bài toán hệ thống phân tán như Service Discovery, API Gateway và Circuit Breaker.', 'https://img.freepik.com/free-vector/laptop-with-program-code-isometric-icon-software-development-programming-applications-dark-neon_39422-971.jpg', '#Java #Career', '2026-02-10 08:35:00'),
(5, 'Top 5 Xu hướng Công nghệ Web năm nay', 'Sự trỗi dậy của Server Components, WebAssembly và các framework siêu nhẹ đang thay đổi cách chúng ta code.', 'Thế giới Web Frontend và Backend đang thay đổi với tốc độ chóng mặt. Dưới đây là 5 xu hướng định hình bức tranh công nghệ Web trong năm nay:\n\nThứ nhất là sự trỗi dậy của Server Components (như React Server Components), giúp giảm thiểu lượng JavaScript gửi xuống trình duyệt, tăng tốc độ tải trang đáng kể. Thứ hai, WebAssembly (Wasm) đang mang sức mạnh của C++, Rust lên trình duyệt, cho phép chạy các ứng dụng đồ họa 3D, chỉnh sửa video mượt mà như app native.\n\nCác xu hướng tiếp theo bao gồm Edge Computing (đưa xử lý logic ra các server gần người dùng nhất), tích hợp AI tạo sinh (Generative AI) trực tiếp vào UI/UX, và cuối cùng là sự phổ biến của các framework siêu nhẹ (như Astro hay Qwik) tập trung tối đa vào hiệu năng (Performance).', 'https://img.freepik.com/free-photo/programming-background-with-person-working-with-codes-computer_23-2150010125.jpg', '#WebDev #Trends', '2026-02-10 08:35:00'),
(6, 'Flutter vs React Native: Cuộc chiến chưa hồi kết', 'So sánh chi tiết về hiệu năng, cộng đồng và khả năng tuyển dụng của hai Cross-platform framework hot nhất hiện nay.', 'Cuộc tranh luận xem đâu là framework Cross-platform tốt nhất để lập trình di động vẫn luôn rực lửa. React Native, với lợi thế sử dụng JavaScript và hệ sinh thái khổng lồ từ React, vẫn là lựa chọn hàng đầu cho các team Web muốn làm App nhanh chóng.\n\nTuy nhiên, Flutter của Google đang ngày càng lấn lướt nhờ hiệu năng mượt mà (chạy thẳng ở dạng mã máy) và trải nghiệm UI nhất quán trên mọi nền tảng (iOS, Android, Web, Desktop) nhờ engine đồ họa Impeller mới. Ngôn ngữ Dart của Flutter cũng được đánh giá là chặt chẽ và ít lỗi ngầm hơn JS.\n\nSự lựa chọn phụ thuộc vào team của bạn: Nếu có sẵn nhân lực mạnh về JS/React, hãy chọn React Native. Nếu muốn trải nghiệm UI mượt như app Native và hiệu ứng phức tạp, Flutter là chân ái.', 'https://img.freepik.com/free-vector/app-development-banner_33099-1720.jpg', '#Mobile #Flutter', '2026-02-10 08:35:51'),
(7, 'Docker & Kubernetes: Từ Zero đến Hero', 'Hướng dẫn triển khai hệ thống Microservices tự động hóa với CI/CD, giúp bạn nâng tầm kỹ năng DevOps.', 'Không quá lời khi nói rằng Docker và Kubernetes (K8s) đã định hình lại hoàn toàn ngành công nghiệp phần mềm. Quá trình "Container hóa" (Containerization) giúp đóng gói ứng dụng và mọi môi trường phụ thuộc vào một khối thống nhất, chấm dứt vĩnh viễn câu nói kinh điển: "Code chạy ngon trên máy tôi nhưng lỗi trên server".\n\nKhi ứng dụng lớn lên và có hàng trăm container cần quản lý, đó là lúc Kubernetes tỏa sáng. K8s hoạt động như một "nhạc trưởng" (Orchestration), tự động phân bổ tài nguyên, tự động khởi động lại container bị lỗi (Self-healing), và tăng giảm số lượng server (Auto-scaling) tùy theo lượng truy cập.\n\nNắm vững hai công cụ này là bước đệm bắt buộc để bạn bước chân vào con đường DevOps chuyên nghiệp và làm chủ Cloud Native.', 'https://img.freepik.com/free-vector/cloud-computing-modern-flat-concept-illustration_53876-112954.jpg', '#DevOps #Docker', '2026-02-10 08:35:51'),
(8, 'Blockchain không chỉ là Bitcoin', 'Khám phá ứng dụng thực tế của Smart Contracts trong Y tế, Tài chính và Chuỗi cung ứng.', 'Nhắc đến Blockchain, nhiều người chỉ nghĩ đến Bitcoin hay tiền điện tử. Tuy nhiên, công nghệ chuỗi khối này mang tiềm năng ứng dụng thực tế lớn hơn thế rất nhiều nhờ đặc tính phi tập trung (Decentralized), minh bạch và không thể bị giả mạo.\n\nHợp đồng thông minh (Smart Contracts) đang được ứng dụng mạnh mẽ trong chuỗi cung ứng (Supply Chain) để truy xuất nguồn gốc hàng hóa từ nông trại đến bàn ăn. Trong y tế, Blockchain giúp lưu trữ hồ sơ bệnh án an toàn và cho phép bệnh nhân kiểm soát ai được quyền xem dữ liệu của mình.\n\nĐặc biệt, công nghệ Web3 và Định danh phi tập trung (DID) hứa hẹn trả lại quyền làm chủ dữ liệu cho người dùng internet, thoát khỏi sự kiểm soát của các ông lớn công nghệ. Blockchain thực sự là một cuộc cách mạng về sự tin tưởng.', 'https://img.freepik.com/free-photo/cryptocurrency-coding-digital-black-background-open-source-blockchain-concept_53876-124641.jpg', '#Blockchain #Web3', '2026-02-10 08:35:51'),
(9, 'Python hay R: Ngôn ngữ nào cho Phân tích Dữ liệu?', 'Lựa chọn ngôn ngữ phù hợp để bắt đầu con đường trở thành Data Scientist chuyên nghiệp trong năm 2026.', 'Bước chân vào ngành Khoa học dữ liệu (Data Science), câu hỏi đầu tiên luôn là: Chọn Python hay R? Câu trả lời không hoàn toàn là "ngôn ngữ nào tốt hơn\", mà là "bạn đang muốn giải quyết bài toán gì".\n\nR được thiết kế bởi các nhà thống kê học, do đó nó sở hữu một thư viện khổng lồ cho các phân tích toán học chuyên sâu và biểu đồ cực kỳ đẹp mắt (điển hình là ggplot2). Nếu bạn làm việc trong học thuật, nghiên cứu y sinh hay thống kê thuần túy, R là vô đối.\n\nNgược lại, Python là một ngôn ngữ đa dụng (General-purpose). Nhờ các thư viện đỉnh cao như Pandas, NumPy và Scikit-Learn, Python đặc biệt mạnh mẽ trong việc xử lý dữ liệu lớn, dọn dẹp dữ liệu (Data Cleaning) và xây dựng các mô hình Machine Learning/Deep Learning. Trong môi trường doanh nghiệp (Production), Python thường được ưu tiên hơn.', 'https://img.freepik.com/free-vector/data-analysis-landing-page_52683-19364.jpg', '#DataScience #Python', '2026-02-10 08:35:51'),
(10, 'Kỹ năng mềm cho Developer: Đừng chỉ biết Code', 'Giao tiếp, làm việc nhóm và tư duy giải quyết vấn đề - Chìa khóa để thăng tiến lên vị trí Senior/Leader.', 'Bạn có thể là một siêu sao lập trình, fix bug thần tốc, thiết kế hệ thống hoàn hảo. Nhưng nếu không có kỹ năng mềm (Soft Skills), sự nghiệp của bạn sẽ rất khó để bứt phá lên vị trí Senior, Tech Lead hay Manager.\n\nKỹ năng quan trọng nhất chính là Giao tiếp (Communication). Khả năng giải thích một vấn đề kỹ thuật phức tạp cho khách hàng hoặc sếp (những người không hiểu về code) hiểu được là một nghệ thuật. Thứ hai là Kỹ năng làm việc nhóm (Teamwork). Code thường được viết trong tập thể, biết cách review code tinh tế, lắng nghe ý kiến người khác và không giữ "cái tôi" quá lớn là điều vô cùng cần thiết.\n\nNgoài ra, Khả năng quản lý thời gian và Tự học (Self-learning) cũng là những yếu tố sống còn giúp bạn tồn tại trong ngành công nghệ luôn thay đổi từng ngày này. Hãy nhớ: Code là công cụ, con người mới là yếu tố quyết định.', 'https://img.freepik.com/free-vector/teamwork-concept-landing-page_52683-20165.jpg', '#Career #SoftSkills', '2026-02-10 08:35:51');

-- Cấu trúc bảng cho bảng `cart_items`
CREATE TABLE `cart_items` (
  `cart_item_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `added_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cấu trúc bảng cho bảng `categories`
CREATE TABLE `categories` (
  `category_id` int(11) NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `categories` (`category_id`, `name`, `description`) VALUES
(2, 'Lập trình Web', 'Xây dựng website hiện đại với React, Next.js và Node.js'),
(3, 'Trí tuệ nhân tạo (AI)', 'Khám phá thế giới của Machine Learning và Deep Learning'),
(4, 'Thiết kế UI/UX', 'Thiết kế giao diện và trải nghiệm người dùng tinh tế'),
(5, 'Phát triển Mobile', 'Lập trình ứng dụng đa nền tảng với Flutter và React Native'),
(6, 'Khoa học dữ liệu', 'Phân tích và trực quan hóa dữ liệu chuyên sâu với Python'),
(8, 'Digital Marketing', 'Chiến lược truyền thông và quảng cáo đa kênh hiệu quả'),
(9, 'Kinh doanh khởi nghiệp', 'Kiến thức quản trị và vận hành doanh nghiệp trẻ'),
(10, 'Kỹ năng mềm', 'Phát triển bản thân qua giao tiếp và quản lý dự án'),
(11, 'Ngoại ngữ chuyên ngành', 'Tiếng Anh dành riêng cho dân kỹ thuật và lập trình');

-- Cấu trúc bảng cho bảng `certificates`
CREATE TABLE `certificates` (
  `certificate_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `issue_date` timestamp NOT NULL DEFAULT current_timestamp(),
  `pdf_url` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cấu trúc bảng cho bảng `chat_messages`
CREATE TABLE `chat_messages` (
  `msg_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `message_text` text NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `chat_messages` (`msg_id`, `user_id`, `message_text`, `created_at`) VALUES
(1, 4, 'Xin chào', '2026-02-28 08:11:17'),
(2, 2, 'Chào các bạn, mình là thành viên mới. Rất vui được học cùng mọi người!', '2026-03-06 08:00:00'),
(3, 4, 'Chào bạn nha! Khóa học này nhiều kiến thức hay lắm.', '2026-03-06 08:05:00'),
(4, 2, 'Tuyệt quá. Mọi người cho mình hỏi bài tập số 2 làm thế nào nhỉ?', '2026-03-06 08:10:00'),
(5, 4, 'Bạn thử xem lại video bài giảng số 4 phần cuối nhé, thầy có hướng dẫn chi tiết đó.', '2026-03-06 08:15:00'),
(6, 2, 'Cảm ơn bạn nhiều nha, mình fix được bug rồi!', '2026-03-06 08:20:00');

-- Cấu trúc bảng cho bảng `coding_exercises`
CREATE TABLE `coding_exercises` (
  `exercise_id` int(11) NOT NULL,
  `course_id` int(11) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `starter_code` text DEFAULT NULL,
  `test_input` text DEFAULT NULL,
  `expected_output` text DEFAULT NULL,
  `points` int(11) DEFAULT 10,
  `language` varchar(20) DEFAULT 'java'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `coding_exercises` (`exercise_id`, `course_id`, `title`, `description`, `starter_code`, `test_input`, `expected_output`, `points`, `language`) VALUES
(22, 48, 'Bài 1: Hello React World', '<h3>Đề bài:</h3><p>Hãy in ra màn hình dòng chữ: <b>Hello React</b></p>', 'console.log("Hello React");', NULL, 'Hello React', 10, 'nodejs'),
(23, 48, 'Bài 2: ES6 Arrow Function', '<h3>Đề bài:</h3><p>Viết một Arrow Function tính tổng 2 số 10 và 20 và in ra kết quả.</p>', 'const sum = (a, b) => a + b;\nconsole.log(sum(10, 20));', NULL, '30', 10, 'nodejs'),
(24, 48, 'Bài 3: Xử lý mảng (Map)', '<h3>Đề bài:</h3><p>Cho mảng [1, 2, 3]. Hãy in ra mảng mới với các giá trị gấp đôi. Format in: <b>2,4,6</b></p>', 'const arr = [1, 2, 3];\nconst doubled = arr.map(x => x * 2);\nconsole.log(doubled.join(","));', NULL, '2,4,6', 10, 'nodejs'),
(25, 56, 'Bài 1: Xin chào Java', '\r\n    <div class="theory-box">\r\n        <b>Kiến thức:</b> Mọi chương trình Java đều bắt đầu từ một <code>class</code> và hàm <code>main</code>.\r\n    </div>\r\n\r\n    <h3>1. Lệnh in ra màn hình</h3>\r\n    <p>Trong Java, lệnh in đầy đủ là:</p>\r\n    <code>System.out.println("Nội dung");</code>\r\n\r\n    <div class="example-box">\r\n        <b>Ví dụ:</b>\r\n        <pre>\r\npublic class Main {\r\n    public static void main(String[] args) {\r\n        System.out.println("Xin chao!");\r\n    }\r\n}</pre>\r\n    </div>\r\n\r\n    <h3>2. Nhiệm vụ</h3>\r\n    <p>Hoàn thành code để in ra dòng chữ: <b class="highlight">Hello Java</b></p>\r\n', 'public class Main {\n    public static void main(String[] args) {\n        System.out.println("Hello Java");\n    }\n}', NULL, 'Hello Java', 10, 'java'),
(26, 56, 'Bài 2: Tính tổng mảng', '<h3>Đề bài:</h3><p>Tính tổng các số từ 1 đến 5 (1+2+3+4+5) và in ra kết quả.</p>', 'public class Main {\n    public static void main(String[] args) {\n        int sum = 0;\n        for(int i=1; i<=5; i++) sum += i;\n        System.out.println(sum);\n    }\n}', NULL, '15', 10, 'java'),
(27, 53, 'Bài 3: Kiểm tra số chẵn lẻ', '<h3>Đề bài:</h3><p>Kiểm tra số 10 là chẵn hay lẻ? Nếu chẵn in <b>Even</b>, lẻ in <b>Odd</b>.</p>', 'public class Main {\n    public static void main(String[] args) {\n        int n = 10;\n        if(n % 2 == 0) System.out.println("Even");\n        else System.out.println("Odd");\n    }\n}', NULL, 'Even', 10, 'java'),
(28, 50, 'Bài 1: C++ Hello World', '<h3>Đề bài:</h3><p>Sử dụng <code>cout</code> để in ra: <b>Hello CPP</b></p>', '#include <iostream>\nusing namespace std;\n\nint main() {\n    cout << "Hello CPP";\n    return 0;\n}', NULL, 'Hello CPP', 10, 'cpp'),
(29, 50, 'Bài 2: Con trỏ cơ bản', '<h3>Đề bài:</h3><p>Khai báo biến a = 100. Dùng con trỏ để in ra giá trị của a.</p>', '#include <iostream>\nusing namespace std;\n\nint main() {\n    int a = 100;\n    int* ptr = &a;\n    cout << *ptr;\n    return 0;\n}', NULL, '100', 10, 'cpp');

-- Cấu trúc bảng cho bảng `coupons`
CREATE TABLE `coupons` (
  `coupon_id` int(11) NOT NULL,
  `code` varchar(50) NOT NULL,
  `discount_percent` float NOT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `expiration_date` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
-- Cấu trúc bảng cho bảng `courses`
CREATE TABLE `courses` (
  `course_id` int(11) NOT NULL,
  `category_id` int(11) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `subtitle` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `price` decimal(10,2) DEFAULT 0.00,
  `sale_price` decimal(10,2) DEFAULT NULL,
  `thumbnail_url` varchar(255) DEFAULT NULL,
  `language` varchar(50) DEFAULT NULL,
  `level` varchar(50) DEFAULT NULL,
  `status` varchar(20) DEFAULT 'active',
  `approved_by` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `view_count` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `courses` (`course_id`, `category_id`, `title`, `subtitle`, `description`, `price`, `sale_price`, `thumbnail_url`, `language`, `level`, `status`, `approved_by`, `created_at`, `updated_at`, `view_count`) VALUES
(47, 2, 'Fullstack Next.js 14 Masterclass', 'Xây dựng web app hiện đại', 'Học từ Zero đến Hero với Next.js, Tailwind CSS và Prisma.', 1200000.00, 890000.00, 'https://appxcontent.kaxa.in/paid_course3/2024-02-24-0.504368563426028.png', 'Vietnamese', 'Intermediate', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 124),
(48, 2, 'React Pro: Core & Advanced', 'Làm chủ React trong 30 ngày', 'Học sâu về Hook, Context API và tối ưu Performance.', 950000.00, 0.00, 'https://cdnp3.stackassets.com/7580642fa17f69c83ed0371da116ea6a7f71f2c9/store/b346549d3cfa935ed20a1ab2e3037bd41dc48495e918c8bd80a0e63681c3/product_51745_product_shots1.jpg', 'Vietnamese', 'Beginner', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 970),
(49, 3, 'Python for AI & Machine Learning', 'Nền tảng cho trí tuệ nhân tạo', 'Học Python chuyên sâu để xử lý dữ liệu và xây dựng Model AI.', 1500000.00, 1100000.00, 'https://www.clariontech.com/hubfs/Python%20for%20AI%20and%20Machine%20Learning%202.jpg', 'English', 'Beginner', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 480),
(50, 3, 'Deep Learning Essentials', 'Thực hành với TensorFlow', 'Xây dựng các mạng thần kinh nhân tạo ứng dụng thực tế.', 2000000.00, 1590000.00, 'https://d3i3lzopayriz.cloudfront.net/v3/book-files/9789359118918/front_img_cover.jpg', 'English', 'Advanced', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 490),
(51, 4, 'Figma Masterclass 2026', 'Thiết kế giao diện hiện đại', 'Làm chủ Figma từ Auto Layout đến Prototyping.', 800000.00, 550000.00, 'https://i.ytimg.com/vi/6br0oxRMdao/hq720.jpg?sqp=-oaymwEhCK4FEIIDSFryq4qpAxMIARUAAAAAGAElAADIQj0AgKJD&rs=AOn4CLCl2_tqEiw4fLzaLcHs11UJtlYcMw', 'Vietnamese', 'Beginner', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 11),
(52, 4, 'UX Research Methods', 'Nghiên cứu trải nghiệm người dùng', 'Hiểu người dùng để tạo ra sản phẩm đột phá.', 1100000.00, 0.00, 'https://cdn.prod.website-files.com/65c1ae21fb2191466dd6ce72/66027bf3be379e0603730978_658d2c78304ca15240ba010b_Different%2520UX%2520Research%2520Methods_%2520Exploring%2520Varied%2520Approaches.jpeg', 'Vietnamese', 'Intermediate', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 584),
(53, 5, 'Flutter: Build Apps fast', 'Lập trình app đa nền tảng', 'Một lần code cho cả iOS và Android với Flutter.', 1350000.00, 990000.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQul_my0Cuieu8g8rKPRKPnXOhT2XEOKSZK6A&s', 'Vietnamese', 'Intermediate', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 888),
(54, 5, 'Swift UI for iOS', 'Phát triển app iPhone hiện đại', 'Học Swift và SwiftUI để xây dựng ứng dụng mượt mà.', 1600000.00, 1200000.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTaYmqEr9IlAs1rt_fwSdvuqidQ3e1d-e9OCw&s', 'English', 'Advanced', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 688),
(55, 6, 'Data Analytics with SQL & PowerBI', 'Phân tích dữ liệu chuyên nghiệp', 'Sử dụng SQL và PowerBI để đưa ra quyết định kinh doanh.', 900000.00, 750000.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSIiDBMSpVW-NA4aIQ4Ff7AwgnLz_gS-I4wrg&s', 'Vietnamese', 'Beginner', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 776),
(56, 6, 'Big Data Processing with Spark', 'Xử lý dữ liệu lớn', 'Xây dựng hệ thống xử lý hàng tỷ bản ghi dữ liệu.', 2500000.00, 1990000.00, 'https://m.media-amazon.com/images/I/712GKzbyltL._AC_UF1000,1000_QL80_.jpg', 'English', 'Advanced', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 817),
(57, NULL, 'Ethical Hacking 101', 'Bảo mật hệ thống căn bản', 'Học cách hacker tấn công để bảo vệ hệ thống của bạn.', 1800000.00, 1450000.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS50rsBNooFSLZK85hz6bKymu-Mq4nra-RG0A&s', 'English', 'Intermediate', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 758),
(58, NULL, 'Network Security Fundamentals', 'An ninh mạng doanh nghiệp', 'Thiết lập tường lửa và hệ thống phòng thủ mạng.', 1200000.00, 0.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRczYyF0bIYlnjGHdSx323vx931KJhSIUxAgw&s', 'Vietnamese', 'Intermediate', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 340),
(59, 8, 'SEO Strategy for 2026', 'Tối ưu hóa công cụ tìm kiếm', 'Đưa website của bạn lên top đầu Google bền vững.', 700000.00, 490000.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTggUim75Jv2t1C3iy0ul5opmJg5c1Sa-BmDQ&s', 'Vietnamese', 'Beginner', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 428),
(60, 8, 'Facebook & Google Ads Mastery', 'Quảng cáo đa kênh hiệu quả', 'Kỹ thuật chạy ads tối ưu hóa ngân sách.', 1000000.00, 850000.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcReCr_WRAKPQkEPeLYQEiqj26X7ufDn69pinw&s', 'Vietnamese', 'Intermediate', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 118),
(61, 9, 'Startup Fundamentals', 'Khởi nghiệp từ con số 0', 'Từ ý tưởng đến gọi vốn và vận hành.', 2000000.00, 0.00, 'https://m.media-amazon.com/images/I/61lxubjjtHL._AC_UF1000,1000_QL80_.jpg', 'Vietnamese', 'Beginner', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 308),
(62, 9, 'Modern Management & Leadership', 'Quản trị và lãnh đạo hiện đại', 'Kỹ năng dẫn dắt đội ngũ trong kỷ nguyên số.', 1500000.00, 1290000.00, 'https://www.leadingsapiens.com/content/images/2022/05/3-Pillars-of-Modern-Leadership-model.001.png', 'Vietnamese', 'Advanced', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 188),
(63, 10, 'Time Management for Devs', 'Quản lý thời gian hiệu quả', 'Kỹ thuật Pomodoro và quy tắc 80/20 cho lập trình viên.', 500000.00, 250000.00, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT203wULvPu9QRwWDBf-K-Mi69_zcIN5fpvvg&s', 'Vietnamese', 'Beginner', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 14),
(64, 10, 'Public Speaking & Presentation', 'Kỹ năng thuyết trình lôi cuốn', 'Tự tin trình bày trước đám đông và sếp.', 600000.00, 0.00, 'https://miro.medium.com/1*ix5klKGb3U1W-5Rq2qQieg.jpeg', 'Vietnamese', 'Beginner', 'Active', NULL, '2026-02-08 13:43:58', '2026-02-08 14:14:07', 506),
(67, 2, 'Lập trình C: Nền tảng cho mọi ngôn ngữ', 'Khóa học Lập trình Web do N/A giảng dạy.', 'Khóa học C căn bản giúp bạn hiểu sâu về bộ nhớ, con trỏ và tư duy lập trình hệ thống. Bước đệm để học mọi ngôn ngữ khác.', 500000.00, 0.00, 'https://topdev.vn/blog/wp-content/uploads/2019/04/blog6.png', 'Vietnamese', 'Beginner', 'Active', NULL, '2026-02-14 08:43:22', '2026-03-05 12:47:57', 1200),
(68, 2, 'C++: Cấu trúc dữ liệu & Giải thuật', NULL, 'Chinh phục C++ từ cơ bản đến nâng cao, làm chủ OOP, STL và các thuật toán kinh điển để thi lập trình.', 800000.00, 599000.00, 'https://s3-hfx03.fptcloud.com/codelearnstorage/Upload/Blog/chon-lam-ngon-ngu-lap-trinh-chinh-63730427044.4842.jpg', NULL, 'Intermediate', 'active', NULL, '2026-02-14 08:43:22', '2026-02-14 08:46:02', 1500),
(69, 2, 'Java Core: Lập trình hướng đối tượng', NULL, 'Học Java bài bản từ cú pháp đến các concept OOP quan trọng như Kế thừa, Đa hình, Abstraction.', 1200000.00, 890000.00, 'https://user-images.githubusercontent.com/29374426/126056559-263bdade-6b6c-4e64-83a7-e21411391d64.png', NULL, 'Beginner', 'active', NULL, '2026-02-14 08:43:22', '2026-02-14 08:46:20', 2000);

-- Cấu trúc bảng cho bảng `customers`
CREATE TABLE `customers` (
  `id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cấu trúc bảng cho bảng `discussions`
CREATE TABLE `discussions` (
  `discussion_id` int(11) NOT NULL,
  `lesson_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `content` text NOT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cấu trúc bảng cho bảng `enrollments`
CREATE TABLE `enrollments` (
  `enrollment_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `enrolled_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `progress_percent` float DEFAULT 0,
  `status` varchar(50) DEFAULT 'active',
  `rating` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `enrollments` (`enrollment_id`, `user_id`, `course_id`, `enrolled_at`, `progress_percent`, `status`, `rating`) VALUES
(1, 4, 67, '2026-03-02 14:15:26', 66, 'active', 2),
(2, 4, 49, '2026-03-02 15:07:53', 100, 'active', 4),
(3, 2, 48, '2026-03-07 05:39:52', 0, 'active', 0),
(4, 2, 55, '2026-03-07 05:40:02', 0, 'active', 0);

-- Cấu trúc bảng cho bảng `exercise_completions`
CREATE TABLE `exercise_completions` (
  `completion_id` int(11) NOT NULL,
  `user_id` int(11) DEFAULT NULL,
  `exercise_id` int(11) DEFAULT NULL,
  `course_id` int(11) DEFAULT NULL,
  `completed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `exercise_completions` (`completion_id`, `user_id`, `exercise_id`, `course_id`, `completed_at`) VALUES
(1, 4, 42, 67, '2026-03-02 14:48:01'),
(2, 4, 43, 67, '2026-03-05 13:17:58');

-- Cấu trúc bảng cho bảng `lessons`
CREATE TABLE `lessons` (
  `lesson_id` int(11) NOT NULL,
  `section_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `content_type` enum('video','article','quiz') NOT NULL,
  `video_url` varchar(255) DEFAULT NULL,
  `duration_seconds` int(11) DEFAULT 0,
  `is_preview` tinyint(1) DEFAULT 0,
  `content` text DEFAULT NULL,
  `order_index` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `lessons` (`lesson_id`, `section_id`, `title`, `content_type`, `video_url`, `duration_seconds`, `is_preview`, `content`, `order_index`) VALUES
(1, 4, 'Bài 1: Giới thiệu khóa học & Lộ trình', 'video', 'https://example.com/video1', 300, 1, NULL, 1),
(2, 4, 'Bài 2: Cài đặt Python & Jupyter Notebook', 'video', 'https://example.com/video2', 750, 0, NULL, 2),
(3, 5, 'Bài 3: Biến, Kiểu dữ liệu & Toán tử', 'video', 'https://example.com/video3', 945, 0, NULL, 1),
(4, 5, 'Bài 4: Cấu trúc điều kiện & Vòng lặp', 'video', 'https://example.com/video4', 1100, 0, NULL, 2),
(5, 5, 'Bài 5: Hàm & Module trong Python', 'video', 'https://example.com/video5', 1200, 0, NULL, 3),
(6, 6, 'Bài 6: Machine Learning là gì?', 'video', 'https://example.com/video6', 615, 1, NULL, 1),
(7, 6, 'Bài 7: Giới thiệu thư viện NumPy & Pandas', 'video', 'https://example.com/video7', 1500, 0, NULL, 2),
(10, 7, 'Bài 8: Vẽ biểu đồ với Matplotlib', 'video', 'https://example.com/py8', 1200, 0, NULL, 1),
(11, 7, 'Bài 9: Biểu đồ nâng cao với Seaborn', 'video', 'https://example.com/py9', 1450, 0, NULL, 2),
(12, 8, 'Bài 10: Mạng Neural Network là gì?', 'video', 'https://example.com/py10', 900, 1, NULL, 1),
(13, 8, 'Bài 11: Xây dựng model đầu tiên với Keras', 'video', 'https://example.com/py11', 2100, 0, NULL, 2),
(14, 9, 'Bài 1: React là gì & Setup môi trường', 'video', 'https://example.com/react1', 600, 1, NULL, 1),
(15, 9, 'Bài 2: JSX & Virtual DOM hoạt động thế nào?', 'video', 'https://example.com/react2', 900, 0, NULL, 2),
(16, 9, 'Bài 3: Components, Props & Rendering', 'video', 'https://example.com/react3', 1200, 0, NULL, 3),
(17, 10, 'Bài 4: useState - Quản lý trạng thái', 'video', 'https://example.com/react4', 1500, 0, NULL, 1),
(18, 10, 'Bài 5: useEffect - Xử lý Side Effects', 'video', 'https://example.com/react5', 1650, 0, NULL, 2),
(19, 10, 'Bài 6: Xử lý sự kiện (Event Handling)', 'video', 'https://example.com/react6', 800, 0, NULL, 3),
(20, 11, 'Bài 7: Gọi API với Fetch & Axios', 'video', 'https://example.com/react7', 1300, 0, NULL, 1),
(21, 11, 'Bài 8: Custom Hooks nâng cao', 'video', 'https://example.com/react8', 1400, 0, NULL, 2),
(22, 11, 'Bài 9: Tối ưu với React.memo & useMemo', 'video', 'https://example.com/react9', 1800, 0, NULL, 3),
(23, 100, 'Bài 1: Giới thiệu ngôn ngữ C & Cài đặt Dev-C++', 'video', 'https://www.youtube.com/embed/KJgsSFOSQv0', 600, 1, NULL, 1),
(24, 100, 'Bài 2: Biến, Kiểu dữ liệu & Nhập xuất (printf/scanf)', 'video', 'https://www.youtube.com/embed/KJgsSFOSQv0', 900, 0, NULL, 2),
(25, 101, 'Bài 3: Câu lệnh điều kiện If-Else', 'video', 'https://www.youtube.com/embed/KJgsSFOSQv0', 1200, 0, NULL, 1),
(26, 102, 'Bài 4: Con trỏ (Pointer) là gì?', 'video', 'https://www.youtube.com/embed/KJgsSFOSQv0', 1500, 0, NULL, 1),
(27, 103, 'Bài 1: Input/Output với cin/cout', 'video', 'https://www.youtube.com/embed/M2M8qL2FjV0', 500, 1, NULL, 1),
(28, 103, 'Bài 2: Vector & String trong STL', 'video', 'https://www.youtube.com/embed/M2M8qL2FjV0', 1100, 0, NULL, 2),
(29, 104, 'Bài 3: Class và Object trong C++', 'video', 'https://www.youtube.com/embed/M2M8qL2FjV0', 1500, 0, NULL, 1),
(30, 105, 'Bài 1: Cài đặt JDK và IntelliJ IDEA', 'video', 'https://www.youtube.com/embed/grEKMHGYyns', 800, 1, NULL, 1),
(31, 105, 'Bài 2: Hàm Main và biến static', 'video', 'https://www.youtube.com/embed/grEKMHGYyns', 1000, 0, NULL, 2),
(32, 106, 'Bài 3: Kế thừa (Inheritance) & Đa hình (Polymorphism)', 'video', 'https://www.youtube.com/embed/grEKMHGYyns', 2000, 0, NULL, 1);

-- Cấu trúc bảng cho bảng `lesson_progress`
CREATE TABLE `lesson_progress` (
  `progress_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `lesson_name` varchar(255) NOT NULL,
  `completed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `lesson_progress` (`progress_id`, `user_id`, `course_id`, `lesson_name`, `completed_at`) VALUES
(1, 4, 67, 'Bài 1: Giới thiệu ngôn ngữ C & Cài đặt Dev-C++', '2026-03-02 14:15:52'),
(2, 4, 67, 'Bài 2: Biến, Kiểu dữ liệu & Nhập xuất (printf/scanf)', '2026-03-02 14:15:53'),
(3, 4, 67, 'Bài 3: Câu lệnh điều kiện If-Else', '2026-03-02 14:15:53'),
(4, 4, 67, 'Bài 4: Con trỏ (Pointer) là gì?', '2026-03-02 14:15:53'),
(11, 4, 49, 'Bài 1: Giới thiệu khóa học & Lộ trình', '2026-03-05 13:19:53'),
(12, 4, 49, 'Bài 2: Cài đặt Python & Jupyter Notebook', '2026-03-05 13:19:53'),
(13, 4, 49, 'Bài 3: Biến, Kiểu dữ liệu & Toán tử', '2026-03-05 13:19:53'),
(14, 4, 49, 'Bài 4: Cấu trúc điều kiện & Vòng lặp', '2026-03-05 13:19:53'),
(15, 4, 49, 'Bài 5: Hàm & Module trong Python', '2026-03-05 13:19:54'),
(16, 4, 49, 'Bài 6: Machine Learning là gì?', '2026-03-05 13:19:54'),
(17, 4, 49, 'Bài 7: Giới thiệu thư viện NumPy & Pandas', '2026-03-05 13:19:54'),
(18, 4, 49, 'Bài 8: Vẽ biểu đồ với Matplotlib', '2026-03-05 13:19:54'),
(19, 4, 49, 'Bài 9: Biểu đồ nâng cao với Seaborn', '2026-03-05 13:19:54'),
(20, 4, 49, 'Bài 10: Mạng Neural Network là gì?', '2026-03-05 13:19:55'),
(21, 4, 49, 'Bài 11: Xây dựng model đầu tiên với Keras', '2026-03-05 13:19:55');

-- Cấu trúc bảng cho bảng `notifications`
CREATE TABLE `notifications` (
  `notification_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `message` text DEFAULT NULL,
  `type` varchar(50) DEFAULT 'INFO',
  `is_read` tinyint(1) DEFAULT 0,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `notifications` (`notification_id`, `user_id`, `title`, `message`, `type`, `is_read`, `created_at`) VALUES
(1, 4, 'Đăng ký thành công', 'Bạn đã sở hữu khóa học: Lập trình C: Nền tảng cho mọi ngôn ngữ. Hãy bắt đầu học ngay!', 'SUCCESS', 1, '2026-03-02 14:15:26'),
(2, 4, 'Đăng ký thành công', 'Bạn đã sở hữu khóa học: Python for AI & Machine Learning. Hãy bắt đầu học ngay!', 'SUCCESS', 1, '2026-03-02 15:07:53'),
(3, 2, 'Đăng ký thành công', 'Bạn đã sở hữu khóa học: React Pro: Core & Advanced. Hãy bắt đầu học ngay!', 'SUCCESS', 0, '2026-03-07 05:39:52'),
(4, 2, 'Đăng ký thành công', 'Bạn đã sở hữu khóa học: Data Analytics with SQL & PowerBI. Hãy bắt đầu học ngay!', 'SUCCESS', 0, '2026-03-07 05:40:02');

-- Cấu trúc bảng cho bảng `orders`
CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `coupon_id` int(11) DEFAULT NULL,
  `total_amount` decimal(10,2) NOT NULL,
  `status` enum('pending','completed','failed','refunded') DEFAULT 'pending',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cấu trúc bảng cho bảng `order_details`
CREATE TABLE `order_details` (
  `id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `price_at_purchase` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cấu trúc bảng cho bảng `reviews`
CREATE TABLE `reviews` (
  `review_id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `rating` int(11) DEFAULT NULL CHECK (`rating` >= 1 and `rating` <= 5),
  `comment` text DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `reviews` (`review_id`, `user_id`, `course_id`, `rating`, `comment`, `created_at`) VALUES
(1, 4, 49, 4, 'Tốt mình nhận thấy khóa học rất đầy đủ. Mong giảng viên ra nhiều khoá học hay hơn', '2026-03-07 04:28:46'),
(2, 4, 67, 2, 'Khóa học khá tệ, mong tác giả cải thiện', '2026-03-07 04:29:17');

-- Cấu trúc bảng cho bảng `sections`
CREATE TABLE `sections` (
  `section_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `title` varchar(255) NOT NULL,
  `order_index` int(11) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `sections` (`section_id`, `course_id`, `title`, `order_index`) VALUES
(4, 49, 'Phần 1: Giới thiệu & Cài đặt môi trường', 1),
(5, 49, 'Phần 2: Kiến thức cơ bản Python', 2),
(6, 49, 'Phần 3: Làm quen với Machine Learning', 3),
(7, 49, 'Phần 4: Trực quan hóa dữ liệu (Data Visualization)', 4),
(8, 49, 'Phần 5: Deep Learning với TensorFlow', 5),
(9, 48, 'Chương 1: React Core Concepts & JSX', 1),
(10, 48, 'Chương 2: State Management & Hooks', 2),
(11, 48, 'Chương 3: Kết nối API & Performance', 3),
(100, 67, 'Chương 1: Khởi động với C', 1),
(101, 67, 'Chương 2: Cấu trúc rẽ nhánh & Vòng lặp', 2),
(102, 67, 'Chương 3: Con trỏ & Quản lý bộ nhớ', 3),
(103, 68, 'Chương 1: C++ Modern & STL', 1),
(104, 68, 'Chương 2: Lập trình hướng đối tượng (OOP)', 2),
(105, 69, 'Chương 1: Làm quen với Java & JVM', 1),
(106, 69, 'Chương 2: OOP - Tứ trụ của Java', 2);

-- Cấu trúc bảng cho bảng `users`
CREATE TABLE `users` (
  `user_id` int(11) NOT NULL,
  `user_name` varchar(100) DEFAULT NULL,
  `full_name` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `password_hash` varchar(255) DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  `role` enum('student','instructor','admin') DEFAULT 'student',
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `is_active` tinyint(1) DEFAULT 0,
  `auth_provider` varchar(50) DEFAULT 'LOCAL',
  `provider_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `users` (`user_id`, `user_name`, `full_name`, `email`, `password_hash`, `avatar_url`, `role`, `created_at`, `updated_at`, `is_active`, `auth_provider`, `provider_id`) VALUES
(2, 'fb_1216701987334963', 'Dương Nguyễn', NULL, NULL, 'https://platform-lookaside.fbsbx.com/platform/profilepic/?asid=1216701987334963&height=200&width=200&ext=1774854475&hash=AT-ACBQ0WyPdkEs0Eb6qEOPE', 'student', '2026-02-28 07:08:07', '2026-02-28 07:08:07', 1, 'FACEBOOK', '1216701987334963'),
(4, 'gg_103552638626408368850', 'Nguyễn Anh Dương', 'duonganhdn2000@gmail.com', NULL, '/userAvatar/4.jpg', 'student', '2026-02-28 07:14:05', '2026-03-06 13:01:19', 1, 'GOOGLE', '103552638626408368850'),
(5, 'tran_nam_99', 'Trần Hoài Nam', 'nam.tran@gmail.com', '$2y$10$abc123xyz456', '/userAvatar/5.jpg', 'student', '2026-03-06 01:00:00', '2026-03-06 01:00:00', 1, 'LOCAL', NULL),
(6, 'le_thi_hoa', 'Lê Thị Hoa', 'hoa.le@yahoo.com', '$2y$10$def789ghi012', '/userAvatar/6.jpg', 'student', '2026-03-06 02:15:00', '2026-03-06 02:15:00', 1, 'LOCAL', NULL),
(7, 'gg_1034567890', 'Phạm Minh', 'minh.pham@gmail.com', NULL, 'https://lh3.googleusercontent.com/a/mock1', 'student', '2026-03-06 03:30:00', '2026-03-06 03:30:00', 1, 'GOOGLE', NULL),
(8, 'fb_876543210', 'Vũ Quỳnh', NULL, NULL, 'https://platform-lookaside.fbsbx.com/mock2', 'student', '2026-03-06 04:45:00', '2026-03-06 04:45:00', 1, 'FACEBOOK', NULL),
(9, 'nguyen_kien', 'Nguyễn Trung Kiên', 'kien.nguyen@edu.vn', '$2y$10$jkl345mno678', '/userAvatar/7.jpg', 'instructor', '2026-03-06 07:00:00', '2026-03-06 07:00:00', 1, 'LOCAL', NULL);

-- Cấu trúc bảng cho bảng `verification_tokens`
CREATE TABLE `verification_tokens` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `token` varchar(255) NOT NULL,
  `type` varchar(50) NOT NULL,
  `expires_at` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Cấu trúc bảng cho bảng `wishlists`
CREATE TABLE `wishlists` (
  `user_id` int(11) NOT NULL,
  `course_id` int(11) NOT NULL,
  `added_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Chỉ mục cho bảng `admins`
ALTER TABLE `admins`
  ADD PRIMARY KEY (`admin_id`),
  ADD UNIQUE KEY `email` (`email`);

-- Chỉ mục cho bảng `articles`
ALTER TABLE `articles`
  ADD PRIMARY KEY (`article_id`);

-- Chỉ mục cho bảng `cart_items`
ALTER TABLE `cart_items`
  ADD PRIMARY KEY (`cart_item_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `course_id` (`course_id`);

-- Chỉ mục cho bảng `categories`
ALTER TABLE `categories`
  ADD PRIMARY KEY (`category_id`);

-- Chỉ mục cho bảng `certificates`
ALTER TABLE `certificates`
  ADD PRIMARY KEY (`certificate_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `course_id` (`course_id`);

-- Chỉ mục cho bảng `chat_messages`
ALTER TABLE `chat_messages`
  ADD PRIMARY KEY (`msg_id`),
  ADD KEY `user_id` (`user_id`);

-- Chỉ mục cho bảng `coding_exercises`
ALTER TABLE `coding_exercises`
  ADD PRIMARY KEY (`exercise_id`),
  ADD KEY `course_id` (`course_id`);

-- Chỉ mục cho bảng `coupons`
ALTER TABLE `coupons`
  ADD PRIMARY KEY (`coupon_id`),
  ADD UNIQUE KEY `code` (`code`);

-- Chỉ mục cho bảng `courses`
ALTER TABLE `courses`
  ADD PRIMARY KEY (`course_id`),
  ADD KEY `category_id` (`category_id`),
  ADD KEY `approved_by` (`approved_by`);

-- Chỉ mục cho bảng `customers`
ALTER TABLE `customers`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`);

-- Chỉ mục cho bảng `discussions`
ALTER TABLE `discussions`
  ADD PRIMARY KEY (`discussion_id`),
  ADD KEY `lesson_id` (`lesson_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `parent_id` (`parent_id`);

-- Chỉ mục cho bảng `enrollments`
ALTER TABLE `enrollments`
  ADD PRIMARY KEY (`enrollment_id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`course_id`),
  ADD KEY `course_id` (`course_id`);

-- Chỉ mục cho bảng `exercise_completions`
ALTER TABLE `exercise_completions`
  ADD PRIMARY KEY (`completion_id`),
  ADD UNIQUE KEY `user_id` (`user_id`,`exercise_id`);

-- Chỉ mục cho bảng `lessons`
ALTER TABLE `lessons`
  ADD PRIMARY KEY (`lesson_id`),
  ADD KEY `section_id` (`section_id`);

-- Chỉ mục cho bảng `lesson_progress`
ALTER TABLE `lesson_progress`
  ADD PRIMARY KEY (`progress_id`),
  ADD UNIQUE KEY `unique_progress` (`user_id`,`course_id`,`lesson_name`);

-- Chỉ mục cho bảng `notifications`
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`notification_id`),
  ADD KEY `user_id` (`user_id`);

-- Chỉ mục cho bảng `orders`
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `coupon_id` (`coupon_id`);

-- Chỉ mục cho bảng `order_details`
ALTER TABLE `order_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `course_id` (`course_id`);

-- Chỉ mục cho bảng `reviews`
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`review_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `course_id` (`course_id`);

-- Chỉ mục cho bảng `sections`
ALTER TABLE `sections`
  ADD PRIMARY KEY (`section_id`),
  ADD KEY `course_id` (`course_id`);

-- Chỉ mục cho bảng `users`
ALTER TABLE `users`
  ADD PRIMARY KEY (`user_id`),
  ADD UNIQUE KEY `email` (`email`);

-- Chỉ mục cho bảng `verification_tokens`
ALTER TABLE `verification_tokens`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

-- Chỉ mục cho bảng `wishlists`
ALTER TABLE `wishlists`
  ADD PRIMARY KEY (`user_id`,`course_id`),
  ADD KEY `course_id` (`course_id`);

-- AUTO_INCREMENT cho bảng `admins`
ALTER TABLE `admins`
  MODIFY `admin_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

-- AUTO_INCREMENT cho bảng `articles`
ALTER TABLE `articles`
  MODIFY `article_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

-- AUTO_INCREMENT cho bảng `cart_items`
ALTER TABLE `cart_items`
  MODIFY `cart_item_id` int(11) NOT NULL AUTO_INCREMENT;

-- AUTO_INCREMENT cho bảng `categories`
ALTER TABLE `categories`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

-- AUTO_INCREMENT cho bảng `certificates`
ALTER TABLE `certificates`
  MODIFY `certificate_id` int(11) NOT NULL AUTO_INCREMENT;

-- AUTO_INCREMENT cho bảng `chat_messages`
ALTER TABLE `chat_messages`
  MODIFY `msg_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

-- AUTO_INCREMENT cho bảng `coding_exercises`
ALTER TABLE `coding_exercises`
  MODIFY `exercise_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

-- AUTO_INCREMENT cho bảng `coupons`
ALTER TABLE `coupons`
  MODIFY `coupon_id` int(11) NOT NULL AUTO_INCREMENT;

-- AUTO_INCREMENT cho bảng `courses`
ALTER TABLE `courses`
  MODIFY `course_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=70;

-- AUTO_INCREMENT cho bảng `customers`
ALTER TABLE `customers`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- AUTO_INCREMENT cho bảng `discussions`
ALTER TABLE `discussions`
  MODIFY `discussion_id` int(11) NOT NULL AUTO_INCREMENT;

-- AUTO_INCREMENT cho bảng `enrollments`
ALTER TABLE `enrollments`
  MODIFY `enrollment_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

-- AUTO_INCREMENT cho bảng `exercise_completions`
ALTER TABLE `exercise_completions`
  MODIFY `completion_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

-- AUTO_INCREMENT cho bảng `lessons`
ALTER TABLE `lessons`
  MODIFY `lesson_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=33;

-- AUTO_INCREMENT cho bảng `lesson_progress`
ALTER TABLE `lesson_progress`
  MODIFY `progress_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=42;

-- AUTO_INCREMENT cho bảng `notifications`
ALTER TABLE `notifications`
  MODIFY `notification_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

-- AUTO_INCREMENT cho bảng `orders`
ALTER TABLE `orders`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT;

-- AUTO_INCREMENT cho bảng `order_details`
ALTER TABLE `order_details`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- AUTO_INCREMENT cho bảng `reviews`
ALTER TABLE `reviews`
  MODIFY `review_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

-- AUTO_INCREMENT cho bảng `sections`
ALTER TABLE `sections`
  MODIFY `section_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=107;

-- AUTO_INCREMENT cho bảng `users`
ALTER TABLE `users`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

-- AUTO_INCREMENT cho bảng `verification_tokens`
ALTER TABLE `verification_tokens`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

-- Các ràng buộc cho bảng `cart_items`
ALTER TABLE `cart_items`
  ADD CONSTRAINT `cart_items_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `cart_items_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE;

-- Các ràng buộc cho bảng `certificates`
ALTER TABLE `certificates`
  ADD CONSTRAINT `certificates_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `certificates_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE;

-- Các ràng buộc cho bảng `chat_messages`
ALTER TABLE `chat_messages`
  ADD CONSTRAINT `chat_messages_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`);

-- Các ràng buộc cho bảng `coding_exercises`
ALTER TABLE `coding_exercises`
  ADD CONSTRAINT `coding_exercises_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`);

-- Các ràng buộc cho bảng `courses`
ALTER TABLE `courses`
  ADD CONSTRAINT `courses_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `categories` (`category_id`) ON DELETE SET NULL,
  ADD CONSTRAINT `courses_ibfk_3` FOREIGN KEY (`approved_by`) REFERENCES `admins` (`admin_id`) ON DELETE SET NULL;

-- Các ràng buộc cho bảng `discussions`
ALTER TABLE `discussions`
  ADD CONSTRAINT `discussions_ibfk_1` FOREIGN KEY (`lesson_id`) REFERENCES `lessons` (`lesson_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `discussions_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `discussions_ibfk_3` FOREIGN KEY (`parent_id`) REFERENCES `discussions` (`discussion_id`) ON DELETE CASCADE;

-- Các ràng buộc cho bảng `enrollments`
ALTER TABLE `enrollments`
  ADD CONSTRAINT `enrollments_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `enrollments_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE;

-- Các ràng buộc cho bảng `lessons`
ALTER TABLE `lessons`
  ADD CONSTRAINT `lessons_ibfk_1` FOREIGN KEY (`section_id`) REFERENCES `sections` (`section_id`) ON DELETE CASCADE;

-- Các ràng buộc cho bảng `lesson_progress`
ALTER TABLE `lesson_progress`
  ADD CONSTRAINT `lesson_progress_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

-- Các ràng buộc cho bảng `notifications`
ALTER TABLE `notifications`
  ADD CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

-- Các ràng buộc cho bảng `orders`
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`coupon_id`) REFERENCES `coupons` (`coupon_id`) ON DELETE SET NULL;

-- Các ràng buộc cho bảng `order_details`
ALTER TABLE `order_details`
  ADD CONSTRAINT `order_details_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `order_details_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE;

-- Các ràng buộc cho bảng `reviews`
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reviews_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE;

-- Các ràng buộc cho bảng `sections`
ALTER TABLE `sections`
  ADD CONSTRAINT `sections_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE;

-- Các ràng buộc cho bảng `verification_tokens`
ALTER TABLE `verification_tokens`
  ADD CONSTRAINT `verification_tokens_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE;

-- Các ràng buộc cho bảng `wishlists`
ALTER TABLE `wishlists`
  ADD CONSTRAINT `wishlists_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  ADD CONSTRAINT `wishlists_ibfk_2` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE;

COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
