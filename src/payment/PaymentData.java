package payment;

import common.PageData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class PaymentData extends PageData {
    private final StringProperty tid;
    private final StringProperty paymentMethod;
    private final StringProperty paymentAmount;
    private final StringProperty transactionDate;
    private CheckBox remark;

    public PaymentData(String tid, String paymentMethod, String paymentAmount, String transactionDate) {
        this.tid = new SimpleStringProperty(tid);
        this.paymentMethod = new SimpleStringProperty(paymentMethod);
        this.paymentAmount = new SimpleStringProperty(paymentAmount);
        this.transactionDate = new SimpleStringProperty(transactionDate);
        this.remark = new CheckBox();
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

    public String getPaymentMethod() {
        return paymentMethod.get();
    }

    public StringProperty paymentMethodProperty() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod.set(paymentMethod);
    }

    public String getPaymentAmount() {
        return paymentAmount.get();
    }

    public StringProperty paymentAmountProperty() {
        return paymentAmount;
    }

    public void setPaymentAmount(String paymentAmount) {
        this.paymentAmount.set(paymentAmount);
    }

    public CheckBox getRemark() {
        return remark;
    }

    public void setRemark(CheckBox remark) {
        this.remark = remark;
    }

    public String getTransactionDate() {
        return transactionDate.get();
    }

    public StringProperty transactionDateProperty() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate.set(transactionDate);
    }

}
