package com.ducvt.news.fw.constant;

import lombok.Getter;

@Getter
public enum MessageEnum {
	SHB_SUCCESS("000000", "SHB Success"),
	SHB_INVALID_REQUEST("000001", "Request Không Hợp Lệ"),
	INVALID_POLICY("200001", "Chính Sách Tín Dụng Không Phù Hợp"),
	DUPLICATE_CUSTOMER("200002", "Khách Hàng Bị Trùng"),
	INVALID_CONDITION("200003", "Khách Hàng Không Thoả Mãn Điều Kiện Sản Phẩm"),
	INVALID_SCOPE("200004", "Không Thuộc Vùng Hoạt Động"),
	EXCEED_KYC("300001", "Vượt Quá Số Lần eKYC"),
	INVALID_IMAGE("300002", "Ảnh Không Thoả Mãn"),
	KYC_ERROR("300003", "Lỗi eKYC"),
	OCR_NOT_MATCH("300004", "Thông Tin OCR Không Khớp"),
	UNIQUE_LINK_ABOUT_TO_EXPIRE("300005", "Unique link sắp hết hạn"),
	UNIQUE_LINK_EXPIRED("300006", "Unique link hết hạn"),
	PAYOUT_CANCEL("300007","Huỷ giải ngân"),
	OTHER_ERROR("900001", "Lỗi Khác"),

	INVALID_REQUEST("0000001", "Request Is Invalid"),
	CONTRACT_NOT_FOUND("0000002", "Contract Is Not Found"),
	CONTRACT_SIGNED("0000003", "Contract Is Signed"),
	INVALID_DECISION("0000004", "Decision Is Invalid"),
	CONTRACT_IS_REJECTED("0000005", "Contract Is Rejected"),
	LEAD_ID_NOT_FOUND("0000006", "LeadID/WalletID Is Not Match"),
	INVALID_ACCESS_KEY("0000007", "Access Key Is Invalid"),
	INVALID_SIGNATURE("0000008", "Signature Is Invalid"),
	CONTRACT_NOT_SIGNED("0000009", "Contract Is Not Signed"),

	SUCCESS("success", "Success"),
	GENERAL_ERROR("general_error", "Any error occur"),
	BAD_REQUEST("bad_request", "Bad request"),

	NOT_FOUND_TOPIC_BY_KEY("topic key not found", "Không tìm thấy topic theo topic key"),
	NOT_FOUND_USER_BY_ID("user id not found", "Không tìm thấy tài khoản theo id"),
	NOT_FOUND_NEWS_BY_ID("news id not found", "Không tìm thấy tin tức theo id"),
	NOT_FOUND_SOURCE_BY_ID("source id not found", "Không tìm thấy nguồn tin theo id"),

	NEWS_SAVED_ALREADY("news saved already", "Tin đã được lưu trước đó"),

	PROCESS_CRAWL_TIME("process_crawl_time_error", "Lỗi xử lý thời gian crawl"),

	DUPLICATE_USERNAME("duplicate_username", "Tên đăng nhập đã tồn tại"),
	DUPLICATE_SOURCE_NAME("duplicate_source_name", "Tên nguồn tin đã tồn tại"),
	EMPTY_USERNAME("empty_username", "Tên đăng nhập không được để trống"),
	EMPTY_SOURCE_NAME("empty_source_name", "Tên nguồn tin không được để trống"),
	EMPTY_SOURCE_MODE("empty_source_mode", "Chế độ nguồn tin không được để trống"),
	EMPTY_SOURCE_FREQUENCY("empty_source_frequency", "Tần suất không được để trống ở chế độ tần suất"),
	EMPTY_SOURCE_CRAWLS("empty_source_crawl", "Thông tin cấu hình tùy chỉnh không được để trống ở chế độ tùy chỉnh"),
	EMPTY_SOURCE_CRAWL_TIME("empty_source_crawl_time", "Thời gian không được để trống ở chế độ tùy chỉnh"),
	EMPTY_SOURCE_CRAWL_URL("empty_source_crawl_url", "Đường dẫn không được để trống ở chế độ tùy chỉnh"),
	EMPTY_SOURCE_CRAWL_TOPIC_KEY("empty_source_crawl_topic_key", "Chủ đề không được để trống ở chế độ tùy chỉnh"),
	EMPTY_SOURCE_STATUS("empty_status", "Trạng thái nguồn tin không được để trống"),
	DUPLICATE_EMAIL("duplicate_email", "Email đã tồn tại"),
	WRONG_ACCOUNT("wrong_account", "Sai tên đăng nhập hoặc mật khẩu"),
	LOCKED_ACCOUNT("locked_account", "Tài khoản của bạn đã bị khóa");


	private final String code;
	private final String message;

	MessageEnum(String code, String message) {
		this.code = code;
		this.message = message;
	}

}
