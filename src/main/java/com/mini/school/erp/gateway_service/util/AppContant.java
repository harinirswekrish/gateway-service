package com.mini.school.erp.gateway_service.util;

public class AppContant {

    public static final String AUTHORIZATION_VALID = "Authorization header is missing or invalid";
    public static final String INVALID_TOKEN = "Invalid token";
    public static final String TOKEN_EXPIRED = "Token has been expired";
    public static final String ADMIN_VALIDATE = "Only Admin can create students";
    public static final String ACCESS_DENIED_STUDENT = "Access denied: only Admin or Staff can view students";
    public static final String ACCESS_DENIED_COURSE = "Access denied: only Admin or Staff can view courses";
    public static final String  COURSE_VALIDATE = "Only ADMIN can create courses";
    public static final String ADMIN_ENROLL_STUDENT = "Only Admin can enroll students";
    public static final String ACCESS_DENIED_ENROLL = "Access denied: only Admin or Staff can view enrollments";
    public static final String ADMIN_REVERT = "Only Admin can revert enrollments";
    public static final String STAFF_MARK_ATTENDANCE = "Only Staff can mark attendance";
    public static final String ACCESS_DENIED_VIEW_ATTENDANCE = "Access denied: only Admin or Staff can view attendance";
    public static final String ADMIN = "ADMIN";
    public static final String STAFF = "STAFF";
}

