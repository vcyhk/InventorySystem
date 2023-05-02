package customer;

import common.PageData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class CustomerData extends PageData {

    private final StringProperty cid;
    private final StringProperty fname;
    private final StringProperty lname;
    private final StringProperty gender;
    private final StringProperty agegroup;
    private final StringProperty country;
    private final StringProperty address;
    private final StringProperty district;
    private final StringProperty email;
    private final StringProperty phone;
    private CheckBox remark;

    public CustomerData(String cid, String fname, String lname, String gender, String agegroup,
                        String country, String address, String district, String email, String phone) {
        this.cid = new SimpleStringProperty(cid);
        this.fname = new SimpleStringProperty(fname);
        this.lname = new SimpleStringProperty(lname);
        this.gender = new SimpleStringProperty(gender);
        this.agegroup = new SimpleStringProperty(agegroup);
        this.country = new SimpleStringProperty(country);
        this.address = new SimpleStringProperty(address);
        this.district = new SimpleStringProperty(district);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.remark = new CheckBox();
    }


    public String getCid() {
        return cid.get();
    }

    public StringProperty cidProperty() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid.set(cid);
    }

    public String getFname() {
        return fname.get();
    }

    public StringProperty fnameProperty() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname.set(fname);
    }

    public String getLname() {
        return lname.get();
    }

    public StringProperty lnameProperty() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname.set(lname);
    }

    public String getGender() {
        return gender.get();
    }

    public StringProperty genderProperty() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public String getAgegroup() {
        return agegroup.get();
    }

    public StringProperty agegroupProperty() {
        return agegroup;
    }

    public void setAgegroup(String agegroup) {
        this.agegroup.set(agegroup);
    }

    public String getCountry() {
        return country.get();
    }

    public StringProperty countryProperty() {
        return country;
    }

    public void setCountry(String country) {
        this.country.set(country);
    }

    public String getAddress() {
        return address.get();
    }

    public StringProperty addressProperty() {
        return address;
    }

    public void setAddress(String address) {
        this.address.set(address);
    }

    public String getDistrict() {
        return district.get();
    }

    public StringProperty districtProperty() {
        return district;
    }

    public void setDistrict(String district) {
        this.district.set(district);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getPhone() {
        return phone.get();
    }

    public StringProperty phoneProperty() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }

    public CheckBox getRemark() {
        return remark;
    }

    public void setRemark(CheckBox remark) {
        this.remark = remark;
    }
}
