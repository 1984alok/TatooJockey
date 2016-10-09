package model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Alok on 26-09-2016.
 */
public class TattoCatagoryResponse {
    @SerializedName("cate_id")
    @Expose
    private String cateId;
    @SerializedName("cate_name")
    @Expose
    private String cateName;
    @SerializedName("parent_id")
    @Expose
    private String parentId;
    @SerializedName("cate_url")
    @Expose
    private String cateUrl;
    @SerializedName("cate_status")
    @Expose
    private String cateStatus;

    /**
     *
     * @return
     * The cateId
     */
    public String getCateId() {
        return cateId;
    }

    /**
     *
     * @param cateId
     * The cate_id
     */
    public void setCateId(String cateId) {
        this.cateId = cateId;
    }

    /**
     *
     * @return
     * The cateName
     */
    public String getCateName() {
        return cateName;
    }

    /**
     *
     * @param cateName
     * The cate_name
     */
    public void setCateName(String cateName) {
        this.cateName = cateName;
    }

    /**
     *
     * @return
     * The parentId
     */
    public String getParentId() {
        return parentId;
    }

    /**
     *
     * @param parentId
     * The parent_id
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     *
     * @return
     * The cateUrl
     */
    public String getCateUrl() {
        return cateUrl;
    }

    /**
     *
     * @param cateUrl
     * The cate_url
     */
    public void setCateUrl(String cateUrl) {
        this.cateUrl = cateUrl;
    }

    /**
     *
     * @return
     * The cateStatus
     */
    public String getCateStatus() {
        return cateStatus;
    }

    /**
     *
     * @param cateStatus
     * The cate_status
     */
    public void setCateStatus(String cateStatus) {
        this.cateStatus = cateStatus;
    }
}
