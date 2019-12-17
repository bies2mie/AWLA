package com.badrul.awla;

public class Job {


    private String jobID;
    private String jobPosition;
    private String jobDetails;
    private String jobOpenDate;
    private String jobCloseDate;
    private String jobCategory;
    private String companyID;
    private String companyName;
    private String companyLogo;


    public Job(String jobID, String jobPosition, String jobDetails, String jobOpenDate, String jobCloseDate,String jobCategory, String companyID, String companyName, String companyLogo) {

        this.jobID = jobID;
        this.jobPosition = jobPosition;
        this.jobDetails = jobDetails;
        this.jobOpenDate = jobOpenDate;
        this.jobCloseDate = jobCloseDate;
        this.jobCategory = jobCategory;
        this.companyID = companyID;
        this.companyName = companyName;
        this.companyLogo = companyLogo;

    }

    public String getJobID() {
        return jobID;
    }

    public String getJobPosition() {
        return jobPosition;
    }

    public String getJobDetails() {
        return jobDetails;
    }

    public String getJobOpenDate() {
        return jobOpenDate;
    }


    public String getJobCloseDate() {
        return jobCloseDate;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public String getCompanyID() {
        return companyID;
    }

    public String getCompanyName() {
        return companyName;
    }
    public String getCompanyLogo() {
        return companyName;
    }
}
