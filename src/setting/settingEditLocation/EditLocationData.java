package setting.settingEditLocation;

import common.PageData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class EditLocationData extends PageData {
    private final StringProperty location;

    public EditLocationData(String payment) {
        this.location = new SimpleStringProperty(payment);
    }

    public StringProperty locationProperty() {
        return location;
    }

    public void setLocation(String location) {
        this.location.set(location);
    }

    public String getLocation() {
        return location.get();
    }

}
