package transaction.transactionEdit;

import common.PageData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.CheckBox;

public class TransactionDetailData extends PageData {
    private final StringProperty tid;
    private final StringProperty itemID;
    private final StringProperty numberOfUnit;
    private final StringProperty discount;
    private final StringProperty itemsTotal;
    private final StringProperty itemCategory;
    private final StringProperty itemDesc;
    private final StringProperty unitPrice;
    private CheckBox remark;


    public TransactionDetailData(String tid, String itemID, String numberOfUnit, String discount,
                                 String itemsTotal, String itemCategory, String itemDesc, String unitPrice){
        this.tid = new SimpleStringProperty(tid);
        this.itemID = new SimpleStringProperty(itemID);
        this.numberOfUnit = new SimpleStringProperty(numberOfUnit);
        this.discount = new SimpleStringProperty(discount);
        this.itemsTotal = new SimpleStringProperty(itemsTotal);
        this.itemCategory = new SimpleStringProperty(itemCategory);
        this.itemDesc = new SimpleStringProperty(itemDesc);
        this.unitPrice = new SimpleStringProperty(unitPrice);
        this.remark = new CheckBox();
    }
    public CheckBox getRemark() {
        return remark;
    }

    public void setRemark(CheckBox remark) {
        this.remark = remark;
    }

    public String getAll(){
        return tid.get()+" "+itemID.get()+" "+numberOfUnit.get()+" "+discount.get()+" "+itemsTotal.get()+" "+itemCategory.get()+" "+
                itemDesc.get()+" "+unitPrice.get() ;
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

    public String getItemID() {
        return itemID.get();
    }

    public StringProperty itemIDProperty() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID.set(itemID);
    }

    public String getNumberOfUnit() {
        return numberOfUnit.get();
    }

    public StringProperty numberOfUnitProperty() {
        return numberOfUnit;
    }

    public void setNumberOfUnit(String numberOfUnit) {
        this.numberOfUnit.set(numberOfUnit);
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

    public String getItemsTotal() {
        return itemsTotal.get();
    }

    public StringProperty itemsTotalProperty() {
        return itemsTotal;
    }

    public void setItemsTotal(String itemsTotal) {
        this.itemsTotal.set(itemsTotal);
    }
    public String getItemCategory() {
        return itemCategory.get();
    }

    public StringProperty itemCategoryProperty() {
        return itemCategory;
    }

    public void setItemCategory(String itemCategory) {
        this.itemCategory.set(itemCategory);
    }

    public String getItemDesc() {
        return itemDesc.get();
    }

    public StringProperty itemDescProperty() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc.set(itemDesc);
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
}
