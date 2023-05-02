package customer.customerImport;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class ImportData {

    private final StringProperty cid;
    private final StringProperty fname;
    private final StringProperty lname;
    private final StringProperty gender;
    private final StringProperty agegroup;
    private final StringProperty country;
    private final StringProperty address1;
    private final StringProperty address2;
    private final StringProperty address3;
    private final StringProperty district;
    private final StringProperty email;
    private final StringProperty phone;

    public ImportData(String cid, String fname, String lname, String gender, String agegroup,
                      String country, String address1, String address2, String address3, String district, String email, String phone) {
        this.cid = new SimpleStringProperty(cid);
        this.fname = new SimpleStringProperty(fname);
        this.lname = new SimpleStringProperty(lname);
        this.gender = new SimpleStringProperty(gender);
        this.agegroup = new SimpleStringProperty(agegroup);
        this.country = new SimpleStringProperty(country);
        this.district = new SimpleStringProperty(district);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
        this.address1 = new SimpleStringProperty(address1);
        this.address2 = new SimpleStringProperty(address2);
        this.address3 = new SimpleStringProperty(address3);
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

    public String getAddress1() {
        return address1.get();
    }

    public StringProperty address1Property() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1.set(address1);
    }

    public String getAddress2() {
        return address2.get();
    }

    public StringProperty address2Property() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2.set(address2);
    }

    public String getAddress3() {
        return address3.get();
    }

    public StringProperty address3Property() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3.set(address3);
    }
}
