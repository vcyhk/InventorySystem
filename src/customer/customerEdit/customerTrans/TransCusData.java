package customer.customerEdit.customerTrans;

import common.PageData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class TransCusData extends PageData {
    private final StringProperty tid;
    private final StringProperty location;
    private final StringProperty tranDT;
    private final StringProperty tranAmount;
    private final StringProperty discount;
    private final StringProperty netAmount;

    public TransCusData(String tid, String location, String tranDT, String tranAmount, String discount,
                        String netAmount) {
        this.tid = new SimpleStringProperty(tid);
        this.location = new SimpleStringProperty(location);
        this.tranDT = new SimpleStringProperty(tranDT);
        this.tranAmount = new SimpleStringProperty(tranAmount);
        this.discount = new SimpleStringProperty(discount);
        this.netAmount = new SimpleStringProperty(netAmount);

    }


    public String getTid() {
        return tid.get();
    }

    public StringProperty tidProperty() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid.set(tid);
    }

    public String getLocation() {
        return location.get();
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getTranDT() {
        return tranDT.get();
    }

    public StringProperty tranDTProperty() {
        return tranDT;
    }

    public void setTranDT(String tranDT) {
        this.tranDT.set(tranDT);
    }

    public String getTranAmount() {
        return tranAmount.get();
    }

    public StringProperty tranAmountProperty() {
        return tranAmount;
    }

    public void setTranAmount(String tranAmount) {
        this.tranAmount.set(tranAmount);
    }

    public String getDiscount() {
        return discount.get();
    }

    public StringProperty discountProperty() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount.set(discount);
    }

    public String getNetAmount() {
        return netAmount.get();
    }

    public StringProperty netAmountProperty() {
        return netAmount;
    }

    public void setNetAmount(String netAmount) {
        this.netAmount.set(netAmount);
    }

}
