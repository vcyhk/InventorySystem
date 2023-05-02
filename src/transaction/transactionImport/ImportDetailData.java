package transaction.transactionImport;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ImportDetailData {
    //TransactionDetail
    private final StringProperty TransactionID;
    private final StringProperty ItemID;
    private final StringProperty NumberOfUnit;
    private final StringProperty Discount;
    private final StringProperty ItemsTotal;
    private final StringProperty UnitPrice;

    public ImportDetailData(String TransactionID, String ItemID, String NumberOfUnit, String Discount, String ItemsTotal, String UnitPrice) {
        this.TransactionID = new SimpleStringProperty(TransactionID);
        this.ItemID = new SimpleStringProperty(ItemID);
        this.NumberOfUnit = new SimpleStringProperty(NumberOfUnit);
        this.Discount = new SimpleStringProperty(Discount);
        this.ItemsTotal = new SimpleStringProperty(ItemsTotal);
        this.UnitPrice = new SimpleStringProperty(UnitPrice);
    }

    public String getUnitPrice() {
        return UnitPrice.get();
    }

    public StringProperty unitPriceProperty() {
        return UnitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.UnitPrice.set(unitPrice);
    }

    public String getTransactionID() {
        return TransactionID.get();
    }

    public StringProperty transactionIDProperty() {
        return TransactionID;
    }

    public void setTransactionID(String transactionID) {
        this.TransactionID.set(transactionID);
    }

    public String getItemID() {
        return ItemID.get();
    }

    public StringProperty itemIDProperty() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        this.ItemID.set(itemID);
    }

    public String getNumberOfUnit() {
        return NumberOfUnit.get();
    }

    public StringProperty numberOfUnitProperty() {
        return NumberOfUnit;
    }

    public void setNumberOfUnit(String numberOfUnit) {
        this.NumberOfUnit.set(numberOfUnit);
    }

    public String getDiscount() {
        return Discount.get();
    }

    public StringProperty discountProperty() {
        return Discount;
    }

    public void setDiscount(String discount) {
        this.Discount.set(discount);
    }

    public String getItemsTotal() {
        return ItemsTotal.get();
    }

    public StringProperty itemsTotalProperty() {
        return ItemsTotal;
    }

    public void setItemsTotal(String itemsTotal) {
        this.ItemsTotal.set(itemsTotal);
    }

    public String toString(){

        return TransactionID+" "+ ItemID+" "+NumberOfUnit+" "+Discount+" "+ ItemsTotal;
    }
}