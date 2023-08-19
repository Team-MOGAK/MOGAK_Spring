package com.mogak.spring.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * ******************************* Global Error CodeList ***************************************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     * *********************************************************************************************
     */

    NOT_EXIST_JOB(HttpStatus.NOT_FOUND, "존재하지 않는 직업입니다"),
    NOT_EXIST_ADDRESS(HttpStatus.NOT_FOUND, "존재하지 않는 지역입니다"),
    NOT_VALID_NICKNAME(HttpStatus.CONFLICT, "올바른 닉네임이 아닙니다"),
    NOT_VALID_EMAIL(HttpStatus.CONFLICT, "올바른 이메일 형식이 아닙니다"),
    NOT_EXIST_USER(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다"),
    NOT_EXIST_CATEGORY(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다"),
    NOT_EXIST_OTHER_CATEGORY(HttpStatus.BAD_REQUEST, "기타 카테고리가 존재하지 않습니다"),
    WRONG_STATE_CHANGE(HttpStatus.BAD_REQUEST, "잘못된 상태 변경입니다"),
    NOT_EXIST_MOGAK(HttpStatus.NOT_FOUND, "존재하지 않는 모각입니다"),
    CAN_MODIFIED_BEFORE_START_MOGAK(HttpStatus.BAD_REQUEST, "시작 날짜는 시작하기 전에만 수정 가능합니다"),
    NOT_VALID_START_DATE(HttpStatus.BAD_REQUEST, "잘못 입력된 시작 날짜입니다"),
    INVERSE_START_END(HttpStatus.BAD_REQUEST, "시작 날짜와 종료 날짜가 역전 되었습니다"),

    NOT_START_JOGAK(HttpStatus.BAD_REQUEST, "시작하지 않은 조각입니다"),
    ALREADY_START_JOGAK(HttpStatus.CONFLICT, "이미 시작한 조각입니다"),
    ALREADY_END_JOGAK(HttpStatus.CONFLICT, "이미 종료한 조각입니다"),
    WRONG_START_MIDNIGHT_JOGAK(HttpStatus.BAD_REQUEST, "자정을 넘어서 조각을 시작할 수 없습니다"),
    NOT_EXIST_JOGAK(HttpStatus.NOT_FOUND, "존재하지 않는 조각입니다"),
    OVERDUE_DEADLINE_JOGAK(HttpStatus.BAD_REQUEST, "기한을 넘긴 조각입니다"),
    WRONG_CREATE_JOGAK(HttpStatus.BAD_REQUEST, "진행중인 모각만 조각을 생성할 수 있습니다"),

    NOT_EXIST_DAY(HttpStatus.NOT_FOUND, "존재하지 않는 요일입니다"),
    ALREADY_EXIST_USER(HttpStatus.CONFLICT, "이미 존재하는 유저입니다"),

    EXCEED_MAX_NUM_POST(HttpStatus.BAD_REQUEST, "최대 글자수 350자를 초과하였습니다"),
    NOT_HAVE_IMAGE(HttpStatus.BAD_REQUEST, "이미지가 존재하지 않습니다"),
    NOT_EXIST_POST(HttpStatus.NOT_FOUND, "존재하지 않는 게시물입니다"),
    ALREADY_CREATE_LIKE(HttpStatus.INTERNAL_SERVER_ERROR, "이미 좋아요를 누른 게시물입니다"),

    EXCEED_MAX_NUM_COMMENT(HttpStatus.BAD_REQUEST, "최대 글자수 200자를 초과하였습니다"),
    NOT_EXIST_COMMENT(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다"),

    WRONG_TOKEN(HttpStatus.BAD_REQUEST, "만료되었거나 잘못된 토큰입니다"),

    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "찾을 수 없습니다"),
    NOT_SUPPORTED_METHOD_ERROR(HttpStatus.METHOD_NOT_ALLOWED, "지원하지 않는 HTTP Method 요청입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버와의 연결에 실패했습니다"),
    ALREADY_CREATE_FOLLOW(HttpStatus.CONFLICT, "이미 존재하는 팔로우입니다"),
    NOT_EXIST_FOLLOW(HttpStatus.NOT_FOUND, "존재하지 않는 팔로우입니다"),
    ;

    private final HttpStatus status;
//    private final String code;
    private final String message;

    public static ErrorCode findByMessage(String message) {
        for (ErrorCode response : values()) {
            if (message.equals(response.message)) {
                return response;
            }
        }
        return null;
    }
}
