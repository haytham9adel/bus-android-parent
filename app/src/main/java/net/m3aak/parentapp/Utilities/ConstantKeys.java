package net.m3aak.parentapp.Utilities;

/**
 * Created by BD-2 on 8/17/2015.
 */
public class ConstantKeys {
    //http://we4it.net/Tracking_bus/webservices/add_student_absent
    //  public static String SERVER_URL = "http://192.168.1.26:8080/Tracking_bus/webservices/";
    //   public static String IMAGE_URL = "http://192.168.1.26:8080/Tracking_bus/resources/dashboard/uploads/student/";
//    public static String IMAGE_URL = "http://m3aak.net/resources/dashboard/uploads/student/";
//    public static String SERVER_URL = "http://m3aak.net/webservices/";
//    public static String SCHOOL_IMAGE_URL = "http://m3aak.net/resources/dashboard/uploads/school/";
    //TODO : update way to school
    // /maaknewfiles/student

    public static String BASE_URL = "http://m3aak.net/";

    public static String IMAGE_URL =BASE_URL+ "maaknewfiles/student/";
    public static String SERVER_URL = BASE_URL +"webservices/";
    public static String SCHOOL_IMAGE_URL =  BASE_URL + "maaknewfiles/school/";

    public static String NOTY_UTL = SERVER_URL + "notification?method=all_notification&route_id=";
    public static String USER_EMAIL = "user_email";
    public static String USER_PASS = "user_pass";
    public static String CONTACT_NO = "contact_number";
    public static String MOBILE_NO = "mobile_number";
    public static String ROLE = "role";
    public static String SCHOOL_ID = "school_id";
    public static String USER_NAME = "user_name";
    public static String RESULT = "result";
    public static String USER_ID = "user_id";
    public static String FIRST_NAME = "first_name";
    public static String MIDDLE_NAME = "middle_name";
    public static String FAMILY_NAME = "family_name";
    public static String PARENT_ID = "s_parent_id";
    public static String Reciever_ID = "school_admin";
    public static String COUNT_CHAT_NOTI = "COUNT_CHAT_NOTI";
    public static String COUNT_OTHER_NOTI = "COUNT_OTHER_NOTI";
    public static String ISCHATVISIBLE = "ISCHATVISIBLE";
    public static String STUD_ROUTEID = "STUD_ROUTEID";
    public static String ROUTEID = "ROUTEID";
    public static String SCHOOL_LOGO = "school_logo";
    public static String IS_FIRST_TIME = "is_first_time";

    /* {"user_email":"pk@mailinator.com","school_admin":"9","role":"Parent","noti_on":0,"user_name":"prakash.kumar","wrong_route_on":0,
    "checked_out_on":0,"chat_on":0,"max_speed":50,"middle_name":"Kumar11","contact_number":"123456779","checked_in_on":0,
    "speed_on":0,"result":"success","school_id":3,"user_id":12,"mobile_number":"9981472471","lang":0,
    "responseMessage":"Login successfull","first_name":"Prakash1","family_name":"Singhal"}*/
    public static String school_name = "school_name";
    public static String Setting_Language = "Setting_Language";
    public static String Setting_notisound = "Setting_notisound";
    public static String Setting_msgsound = "Setting_msgsound";

    public static String Setting_CheckedInNoti = "Setting_CheckedInNoti";
    public static String Setting_CheckedOutNoti = "Setting_CheckedOutNoti";
    public static String Setting_SpeedOnOff = "Setting_Speed";
    public static String Setting_WrongRoute = "Setting_WrongRoute";
    public static String Setting_Speed = "Setting_max_speed";

    public static String Setting_CheckedInNotiSMS = "Setting_CheckedInNoti_sms";
    public static String Setting_CheckedOutNotiSMS = "Setting_CheckedOutNoti_sms";
    public static String Setting_SpeedOnOffSMS = "Setting_Speed_sms";
    public static String Setting_WrongRouteSMS = "Setting_WrongRoute_sms";
    public static String Setting_SpeedSMS = "Setting_sms_max_speed";

    public static String Setting_DriverNoti = "Setting_driver_noti";
    public static String Setting_DriverNotiSMS = "Setting_driver_notisms";
    public static String Setting_MorningNoti = "Setting_morning_noti";
    public static String Setting_MorningNotiSMS = "Setting_morning_notisms";
    public static String Setting_EveningNoti = "Setting_evening_noti";
    public static String Setting_EveningNotiSms = "Setting_evening_notisms";
    public static String SCHOOL_ADMIN_NAME = "school_admin_name";

    public static boolean click = false;
    public static String IS_REMEMBER = "is_remember";
    public static String ALREADY_LOGIN = "ALREADY_LOGIN";
}
