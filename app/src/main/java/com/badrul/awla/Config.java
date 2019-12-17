package com.badrul.awla;

public class Config {

    public static final String URL_API = "https://awla.senangpark.com/api/";
    public static final String SHARED_PREF_NAME = "awla";

    //This would be used to store the phone number of current logged in user
    public static final String ID_SHARED_PREF = "userEmail";

    //We will use this to store the boolean in sharedpreference to track user is loggedin or not
    public static final String LOGGEDIN_SHARED_PREF = "loggedin";

    //For User
    public static final String U_USER_ID = "u_userID";
    public static final String U_USER_NAME = "u_userName";
    public static final String U_USER_EMAIL = "u_userPhone";
    public static final String U_USER_PHONE = "u_userEmail";
    public static final String U_USER_AGE = "u_userAge";
    public static final String U_USER_WORKEXP = "u_userWorkExp";
    public static final String U_USER_TOKEN = "u_userToken";

    public static final String J_JOB_ID = "j_jobID";
    public static final String J_JOB_POSITION = "j_jobPosition";
    public static final String J_JOB_DETAILS= "j_jobDetails";
    public static final String J_JOB_OPEN_DATE = "j_jobOpenDate";
    public static final String J_JOB_CLOSE_DATE= "j_jobCloseDate";
    public static final String J_JOB_CATEGORY = "j_jobCategory";
    public static final String J_COMPANY_ID = "j_CompanyID";
    public static final String J_COMPANY_NAME = "j_CompanyName";
    public static final String J_COMPANY_LOGO = "j_CompanyLogo";
    private String jobID;
    private String jobPosition;
    private String jobDetails;
    private String jobOpenDate;
    private String jobCloseDate;
    private String jobCategory;
    private String companyID;
    private String companyName;
}
