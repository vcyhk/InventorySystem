package promotion.promotionImport;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class ImportData {

    private final StringProperty itemID;
    private final StringProperty discount;
    private final StringProperty effectiveFrom;
    private final StringProperty effectiveTo;

    public ImportData(String itemID, String discount,
                      String effectiveFrom, String effectiveTo) {
        this.itemID = new SimpleStringProperty(itemID);
        this.discount = new SimpleStringProperty(discount);
        this.effectiveFrom = new SimpleStringProperty(effectiveFrom);
        this.effectiveTo = new SimpleStringProperty(effectiveTo);
    }


    public String getItemID() {
        return itemID.get();
    }

    public StringProperty itemIDProperty() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID.set(itemID);
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

    public String getEffectiveFrom() {
        return effectiveFrom.get();
    }

    public StringProperty effectiveFromProperty() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(String effectiveFrom) {
        this.effectiveFrom.set(effectiveFrom);
    }

    public String getEffectiveTo() {
        return effectiveTo.get();
    }

    public StringProperty effectiveToProperty() {
        return effectiveTo;
    }

    public void setEffectiveTo(String effectiveTo) {
        this.effectiveTo.set(effectiveTo);
    }

}
