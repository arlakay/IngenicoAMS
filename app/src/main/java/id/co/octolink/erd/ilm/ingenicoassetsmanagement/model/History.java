package id.co.octolink.erd.ilm.ingenicoassetsmanagement.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by e_er_de on 13/04/2017.
 */

public class History {

    @SerializedName("inventory_id")
    @Expose
    private String inventoryId;
    @SerializedName("staff_code")
    @Expose
    private String staffCode;
    @SerializedName("staff_code_in")
    @Expose
    private String staffCodeIn;
    @SerializedName("staff_code_out")
    @Expose
    private String staffCodeOut;
    @SerializedName("import_time")
    @Expose
    private String importTime;
    @SerializedName("customer_code")
    @Expose
    private String customerCode;
    @SerializedName("mid_code")
    @Expose
    private String midCode;
    @SerializedName("tid_code")
    @Expose
    private String tidCode;
    @SerializedName("brand_code_old")
    @Expose
    private String brandCodeOld;
    @SerializedName("brand_code_new")
    @Expose
    private String brandCodeNew;
    @SerializedName("serial_number_old")
    @Expose
    private String serialNumberOld;
    @SerializedName("serial_number_new")
    @Expose
    private String serialNumberNew;
    @SerializedName("iccid_old")
    @Expose
    private String iccidOld;
    @SerializedName("iccid_new")
    @Expose
    private String iccidNew;
    @SerializedName("device_status")
    @Expose
    private String deviceStatus;
    @SerializedName("record_status")
    @Expose
    private String recordStatus;

    public String getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getStaffCode() {
        return staffCode;
    }

    public void setStaffCode(String staffCode) {
        this.staffCode = staffCode;
    }

    public String getStaffCodeIn() {
        return staffCodeIn;
    }

    public void setStaffCodeIn(String staffCodeIn) {
        this.staffCodeIn = staffCodeIn;
    }

    public String getStaffCodeOut() {
        return staffCodeOut;
    }

    public void setStaffCodeOut(String staffCodeOut) {
        this.staffCodeOut = staffCodeOut;
    }

    public String getImportTime() {
        return importTime;
    }

    public void setImportTime(String importTime) {
        this.importTime = importTime;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    public String getMidCode() {
        return midCode;
    }

    public void setMidCode(String midCode) {
        this.midCode = midCode;
    }

    public String getTidCode() {
        return tidCode;
    }

    public void setTidCode(String tidCode) {
        this.tidCode = tidCode;
    }

    public String getBrandCodeOld() {
        return brandCodeOld;
    }

    public void setBrandCodeOld(String brandCodeOld) {
        this.brandCodeOld = brandCodeOld;
    }

    public String getBrandCodeNew() {
        return brandCodeNew;
    }

    public void setBrandCodeNew(String brandCodeNew) {
        this.brandCodeNew = brandCodeNew;
    }

    public String getSerialNumberOld() {
        return serialNumberOld;
    }

    public void setSerialNumberOld(String serialNumberOld) {
        this.serialNumberOld = serialNumberOld;
    }

    public String getSerialNumberNew() {
        return serialNumberNew;
    }

    public void setSerialNumberNew(String serialNumberNew) {
        this.serialNumberNew = serialNumberNew;
    }

    public String getIccidOld() {
        return iccidOld;
    }

    public void setIccidOld(String iccidOld) {
        this.iccidOld = iccidOld;
    }

    public String getIccidNew() {
        return iccidNew;
    }

    public void setIccidNew(String iccidNew) {
        this.iccidNew = iccidNew;
    }

    public String getDeviceStatus() {
        return deviceStatus;
    }

    public void setDeviceStatus(String deviceStatus) {
        this.deviceStatus = deviceStatus;
    }

    public String getRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(String recordStatus) {
        this.recordStatus = recordStatus;
    }
}
