package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Alok on 08-10-2016.
 */
public class TattooInfo {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("responseData")
    @Expose
    private ArrayList<ResponseDato> responseData = new ArrayList<ResponseDato>();

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The statusCode
     */
    public Integer getStatusCode() {
        return statusCode;
    }

    /**
     *
     * @param statusCode
     * The statusCode
     */
    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    /**
     *
     * @return
     * The message
     */
    public String getMessage() {
        return message;
    }

    /**
     *
     * @param message
     * The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     *
     * @return
     * The responseData
     */
    public ArrayList<ResponseDato> getResponseData() {
        return responseData;
    }

    /**
     *
     * @param responseData
     * The responseData
     */
    public void setResponseData(ArrayList<ResponseDato> responseData) {
        this.responseData = responseData;
    }


    public class ResponseDato implements Serializable{

        @SerializedName("tattoo_id")
        @Expose
        private String tattooId;
        @SerializedName("tattoo_cate_id")
        @Expose
        private String tattooCateId;
        @SerializedName("tattoo_user_id")
        @Expose
        private String tattooUserId;
        @SerializedName("tattoo_name")
        @Expose
        private String tattooName;
        @SerializedName("tattoo_status")
        @Expose
        private String tattooStatus;
        @SerializedName("share_count")
        @Expose
        private String shareCount;
        @SerializedName("tattoo_likes")
        @Expose
        private String tattooLikes;
        @SerializedName("tattoo_dislikes")
        @Expose
        private String tattooDislikes;
        @SerializedName("tattoo_views")
        @Expose
        private String tattooViews;
        @SerializedName("tattoo_date")
        @Expose
        private String tattooDate;
        @SerializedName("tattoo_image")
        @Expose
        private String tattooImage;
        @SerializedName("total_count")
        @Expose
        private Integer totalCount;
        @SerializedName("year")
        @Expose
        private Integer year;
        @SerializedName("month")
        @Expose
        private Integer month;
        @SerializedName("week")
        @Expose
        private Integer week;
        @SerializedName("day")
        @Expose
        private Integer day;
        @SerializedName("hour")
        @Expose
        private Integer hour;
        @SerializedName("minute")
        @Expose
        private Integer minute;
        @SerializedName("second")
        @Expose
        private Integer second;
        @SerializedName("is_liked")
        @Expose
        private Integer isLiked;

        /**
         *
         * @return
         * The tattooId
         */
        public String getTattooId() {
            return tattooId;
        }

        /**
         *
         * @param tattooId
         * The tattoo_id
         */
        public void setTattooId(String tattooId) {
            this.tattooId = tattooId;
        }

        /**
         *
         * @return
         * The tattooCateId
         */
        public String getTattooCateId() {
            return tattooCateId;
        }

        /**
         *
         * @param tattooCateId
         * The tattoo_cate_id
         */
        public void setTattooCateId(String tattooCateId) {
            this.tattooCateId = tattooCateId;
        }

        /**
         *
         * @return
         * The tattooUserId
         */
        public String getTattooUserId() {
            return tattooUserId;
        }

        /**
         *
         * @param tattooUserId
         * The tattoo_user_id
         */
        public void setTattooUserId(String tattooUserId) {
            this.tattooUserId = tattooUserId;
        }

        /**
         *
         * @return
         * The tattooName
         */
        public String getTattooName() {
            return tattooName;
        }

        /**
         *
         * @param tattooName
         * The tattoo_name
         */
        public void setTattooName(String tattooName) {
            this.tattooName = tattooName;
        }

        /**
         *
         * @return
         * The tattooStatus
         */
        public String getTattooStatus() {
            return tattooStatus;
        }

        /**
         *
         * @param tattooStatus
         * The tattoo_status
         */
        public void setTattooStatus(String tattooStatus) {
            this.tattooStatus = tattooStatus;
        }

        /**
         *
         * @return
         * The shareCount
         */
        public String getShareCount() {
            return shareCount;
        }

        /**
         *
         * @param shareCount
         * The share_count
         */
        public void setShareCount(String shareCount) {
            this.shareCount = shareCount;
        }

        /**
         *
         * @return
         * The tattooLikes
         */
        public String getTattooLikes() {
            return tattooLikes;
        }

        /**
         *
         * @param tattooLikes
         * The tattoo_likes
         */
        public void setTattooLikes(String tattooLikes) {
            this.tattooLikes = tattooLikes;
        }

        /**
         *
         * @return
         * The tattooDislikes
         */
        public String getTattooDislikes() {
            return tattooDislikes;
        }

        /**
         *
         * @param tattooDislikes
         * The tattoo_dislikes
         */
        public void setTattooDislikes(String tattooDislikes) {
            this.tattooDislikes = tattooDislikes;
        }

        /**
         *
         * @return
         * The tattooViews
         */
        public String getTattooViews() {
            return tattooViews;
        }

        /**
         *
         * @param tattooViews
         * The tattoo_views
         */
        public void setTattooViews(String tattooViews) {
            this.tattooViews = tattooViews;
        }

        /**
         *
         * @return
         * The tattooDate
         */
        public String getTattooDate() {
            return tattooDate;
        }

        /**
         *
         * @param tattooDate
         * The tattoo_date
         */
        public void setTattooDate(String tattooDate) {
            this.tattooDate = tattooDate;
        }

        /**
         *
         * @return
         * The tattooImage
         */
        public String getTattooImage() {
            return tattooImage;
        }

        /**
         *
         * @param tattooImage
         * The tattoo_image
         */
        public void setTattooImage(String tattooImage) {
            this.tattooImage = tattooImage;
        }

        /**
         *
         * @return
         * The totalCount
         */
        public Integer getTotalCount() {
            return totalCount;
        }

        /**
         *
         * @param totalCount
         * The total_count
         */
        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        /**
         *
         * @return
         * The year
         */
        public Integer getYear() {
            return year;
        }

        /**
         *
         * @param year
         * The year
         */
        public void setYear(Integer year) {
            this.year = year;
        }

        /**
         *
         * @return
         * The month
         */
        public Integer getMonth() {
            return month;
        }

        /**
         *
         * @param month
         * The month
         */
        public void setMonth(Integer month) {
            this.month = month;
        }

        /**
         *
         * @return
         * The week
         */
        public Integer getWeek() {
            return week;
        }

        /**
         *
         * @param week
         * The week
         */
        public void setWeek(Integer week) {
            this.week = week;
        }

        /**
         *
         * @return
         * The day
         */
        public Integer getDay() {
            return day;
        }

        /**
         *
         * @param day
         * The day
         */
        public void setDay(Integer day) {
            this.day = day;
        }

        /**
         *
         * @return
         * The hour
         */
        public Integer getHour() {
            return hour;
        }

        /**
         *
         * @param hour
         * The hour
         */
        public void setHour(Integer hour) {
            this.hour = hour;
        }

        /**
         *
         * @return
         * The minute
         */
        public Integer getMinute() {
            return minute;
        }

        /**
         *
         * @param minute
         * The minute
         */
        public void setMinute(Integer minute) {
            this.minute = minute;
        }

        /**
         *
         * @return
         * The second
         */
        public Integer getSecond() {
            return second;
        }

        /**
         *
         * @param second
         * The second
         */
        public void setSecond(Integer second) {
            this.second = second;
        }

        /**
         *
         * @return
         * The isLiked
         */
        public Integer getIsLiked() {
            return isLiked;
        }

        /**
         *
         * @param isLiked
         * The is_liked
         */
        public void setIsLiked(Integer isLiked) {
            this.isLiked = isLiked;
        }

    }

}
