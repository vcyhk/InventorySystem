package transaction;

import common.PageData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class TransactionData extends PageData {
    private final StringProperty tid;
    private final StringProperty cid;
    private final StringProperty location;
    private final StringProperty tranDT;
    private final StringProperty tranAmt;
    private final StringProperty disAmt;
    private final StringProperty netAmt;
    private final StringProperty payment;
    private CheckBox remark;

    public TransactionData(String tid, String cid, String location, String tranDT, String tranAmt,
                           String disAmt, String netAmt) {
        this.tid = new SimpleStringProperty(tid);
        this.cid = new SimpleStringProperty(cid);
        this.location = new SimpleStringProperty(location);
        this.tranDT = new SimpleStringProperty(tranDT);
        this.tranAmt = new SimpleStringProperty(tranAmt);
        this.disAmt = new SimpleStringProperty(disAmt);
        this.netAmt = new SimpleStringProperty(netAmt);
        this.payment = new SimpleStringProperty(null);
        this.remark = new CheckBox();
    }

    public String getPayment() {
        return payment.get();
    }

    public StringProperty paymentProperty() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment.set(payment);
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

    public String getCid() {
        return cid.get();
    }

    public StringProperty cidProperty() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid.set(cid);
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

    public String getTranAmt() {
        return tranAmt.get();
    }

    public StringProperty tranAmtProperty() {
        return tranAmt;
    }

    public void setTranAmt(String tranAmt) {
        this.tranAmt.set(tranAmt);
    }

    public String getDisAmt() {
        return disAmt.get();
    }

    public StringProperty disAmtProperty() {
        return disAmt;
    }

    public void setDisAmt(String disAmt) {
        this.disAmt.set(disAmt);
    }

    public String getNetAmt() {
        return netAmt.get();
    }

    public StringProperty netAmtProperty() {
        return netAmt;
    }

    public void setNetAmt(String netAmt) {
        this.netAmt.set(netAmt);
    }

    public CheckBox getRemark() {
        return remark;
    }

    public void setRemark(CheckBox remark) {
        this.remark = remark;
    }
}
