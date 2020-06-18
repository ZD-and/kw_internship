package jp.kinwork.Common;

import java.util.LinkedList;

import jp.kinwork.Common.ClassDdl.ProfessionalCareer;
import jp.kinwork.Common.ClassDdl.Qualification;
import jp.kinwork.Common.ClassDdl.Resume;
import jp.kinwork.Common.ClassDdl.SchoolCareer;
import jp.kinwork.Common.ClassDdl.User;
import jp.kinwork.Common.ClassDdl.UserBasic;
import jp.kinwork.Common.ClassDdl.UserToken;

/**
* Created by zml98 on 2018/04/02.
*/

public class PostDate {

    private String email;
    private String emailConform;
    private String password;
    private String passwordConform;
    private String passwordOld;
    private String token;
    private String userId;
    private String validateCode;
    private String postCode;
    private String resumeId;
    private String professionalCareerId;
    private String schoolCareerId;
    private String qualificationId;
    private String keyword;
    private String address;
    private String employmentStatus;
    private String yearlyIncome;
    private String page;
    private String jobId;
    private String employerUserId;
    private String title;
    private String releaseDate;
    private String isFromKinwork;
    private String url;
    private String likeJobId;
    private String currentPage;
    private String status;
    private String employerId;
    private String mailTitle;
    private String mailContent;
    private String my_mail_id;
    private String savedResumeId;
    private String resumeName;
    private String company;
    private String from;
    private String kinworkTag;
    private String contentShorts;
    private String contentShortsHighlight;
    private String order;
    private String flag;
    private String type;

    private LinkedList<String> details = new LinkedList<String>();

    private jp.kinwork.Common.ClassDdl.User User;
    private jp.kinwork.Common.ClassDdl.UserToken UserToken;
    private jp.kinwork.Common.ClassDdl.UserBasic UserBasic;
    private jp.kinwork.Common.ClassDdl.Resume Resume;
    private jp.kinwork.Common.ClassDdl.ProfessionalCareer ProfessionalCareer;
    private jp.kinwork.Common.ClassDdl.SchoolCareer SchoolCareer;
    private jp.kinwork.Common.ClassDdl.Qualification Qualification;

    public String getcompany() {
        return company;
    }
    public void setcompany(String company) {
        this.company = company;
    }
    public String getfrom() {
        return from;
    }
    public void setfrom(String from) {
        this.from = from;
    }
    public String getkinworkTag() {
        return kinworkTag;
    }
    public void setkinworkTag(String kinworkTag) {
        this.kinworkTag = kinworkTag;
    }
    public String getcontentShorts() {
        return contentShorts;
    }
    public void setcontentShorts(String contentShorts) {
        this.contentShorts = contentShorts;
    }
    public String getcontentShortsHighlight() {
        return contentShortsHighlight;
    }
    public void setcontentShortsHighlight(String contentShortsHighlight) {
        this.contentShortsHighlight = contentShortsHighlight;
    }
    public String getresumeName() {
        return resumeName;
    }
    public void setresumeName(String resumeName) {
        this.resumeName = resumeName;
    }
    public String getsavedResumeId() {
        return savedResumeId;
    }
    public void setsavedResumeId(String savedResumeId) {
        this.savedResumeId = savedResumeId;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setEmailConform(String emailConform) {
        this.emailConform = emailConform;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setPasswordConform(String passwordConform) {
        this.passwordConform = passwordConform;
    }
    public void setPasswordOld(String passwordOld) {
        this.passwordOld = passwordOld;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }
    public void setValidateCode(String validateCode) {
        this.validateCode = validateCode;
    }
    public void setPostcode(String postCode) {
        this.postCode = postCode;
    }
    public void setResumeId(String resumeId) {
        this.resumeId = resumeId;
    }
    public void setProfessionalCareerId(String professionalCareerId) {
        this.professionalCareerId = professionalCareerId;
    }
    public void setSchoolCareerId(String schoolCareerId) {
        this.schoolCareerId = schoolCareerId;
    }
    public void setQualificationId(String qualificationId) {
        this.qualificationId = qualificationId;
    }
    public void setkeyword(String keyword) {
        this.keyword = keyword;
    }
    public void setaddress(String address) {
        this.address = address;
    }
    public void setemploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }
    public void setyearlyIncome(String yearlyIncome) {
        this.yearlyIncome = yearlyIncome;
    }
    public void setpage(String page) {
        this.page = page;
    }
    public void setjobId(String jobId) {
        this.jobId = jobId;
    }
    public void setemployerUserId(String employerUserId) {
        this.employerUserId = employerUserId;
    }
    public void settitle(String title) {
        this.title = title;
    }
    public void setisFromKinwork(String isFromKinwork) {
        this.isFromKinwork = isFromKinwork;
    }
    public void setreleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    public void seturl(String url) {
        this.url = url;
    }
    public void setcurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public void setmailTitle(String mailTitle) {
        this.mailTitle = mailTitle;
    }
    public void setmailContent(String mailContent) {
        this.mailContent = mailContent;
    }
    public void setemployerId(String employerId) {
        this.employerId = employerId;
    }
    public void setMyMailId(String my_mail_id) {
        this.my_mail_id = my_mail_id;
    }

    public User getUser() {
        return User;
    }
    public void setUser(User User) {
        this.User = User;
    }
    public UserToken getUserToken() {
        return UserToken;
    }
    public void setUserToken(UserToken UserToken) {
        this.UserToken = UserToken;
    }
    public UserBasic getUserBasic() {
        return UserBasic;
    }
    public void setUserBasic(UserBasic UserBasic) {
        this.UserBasic = UserBasic;
    }
    public ProfessionalCareer getProfessionalCareer() {
        return ProfessionalCareer;
    }
    public void setProfessionalCareer(ProfessionalCareer ProfessionalCareer) {
        this.ProfessionalCareer = ProfessionalCareer;
    }
    public SchoolCareer getSchoolCareer() {
        return SchoolCareer;
    }
    public void setSchoolCareer(SchoolCareer SchoolCareer) {
        this.SchoolCareer = SchoolCareer;
    }
    public Qualification getQualification() {
        return Qualification;
    }
    public void setQualification(Qualification Qualification) {
        this.Qualification = Qualification;
    }
    public void setResume(Resume Resume) {
        this.Resume = Resume;
    }
    public void setdetails(int i,String value) {
        this.details.add(i,value);
    }
    public void setOrder(String order) {
        this.order = order;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }
    public void setType(String type) {
        this.type = type;
    }

}
