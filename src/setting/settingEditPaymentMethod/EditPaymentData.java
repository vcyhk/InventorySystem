package setting.settingEditPaymentMethod;

import common.PageData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class EditPaymentData extends PageData {
    private final StringProperty payment;

    public EditPaymentData(String payment) {
        this.payment = new SimpleStringProperty(payment);
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
}
