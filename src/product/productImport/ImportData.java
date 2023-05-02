package product.productImport;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class ImportData {

    private final StringProperty itemID;
    private final StringProperty categroy;
    private final StringProperty desc;
    private final StringProperty unitPrice;
    private final StringProperty effectiveFrom;
    private final StringProperty effectiveTo;

    public ImportData(String itemID, String categroy, String desc, String unitPrice,
                      String effectiveFrom, String effectiveTo) {
        this.itemID = new SimpleStringProperty(itemID);
        this.categroy = new SimpleStringProperty(categroy);
        this.desc = new SimpleStringProperty(desc);
        this.unitPrice = new SimpleStringProperty(unitPrice);
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

    public String getCategroy() {
        return categroy.get();
    }

    public StringProperty categroyProperty() {
        return categroy;
    }

    public void setCategroy(String categroy) {
        this.categroy.set(categroy);
    }

    public String getDesc() {
        return desc.get();
    }

    public StringProperty descProperty() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc.set(desc);
    }

    public String getUnitPrice() {
        return unitPrice.get();
    }

    public StringProperty unitPriceProperty() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice.set(unitPrice);
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
