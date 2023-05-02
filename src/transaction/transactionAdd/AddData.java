package transaction.transactionAdd;

import common.PageData;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import transaction.TransactionData;

public class AddData extends PageData {
    private final StringProperty tid;
    private final StringProperty itemNo;
    private final StringProperty itemCat;
    private final StringProperty itemDesc;
    private final StringProperty unitPrice;
    private final StringProperty noOfUnit;
    private final StringProperty discount;
    private final StringProperty itemsTotal;
    private CheckBox remark;

    public AddData(String tid, String itemNo, String itemCat, String itemDesc, String unitPrice,
                   String noOfUnit, String discount, String itemsTotal) {
        this.tid = new SimpleStringProperty(tid);
        this.itemNo = new SimpleStringProperty(itemNo);
        this.itemCat = new SimpleStringProperty(itemCat);
        this.itemDesc = new SimpleStringProperty(itemDesc);
        this.unitPrice = new SimpleStringProperty(unitPrice);
        this.noOfUnit = new SimpleStringProperty(noOfUnit);
        this.discount = new SimpleStringProperty(discount);
        this.itemsTotal = new SimpleStringProperty(itemsTotal);
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

    public String getItemNo() {
        return itemNo.get();
    }

    public StringProperty itemNoProperty() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo.set(itemNo);
    }

    public String getItemCat() {
        return itemCat.get();
    }

    public StringProperty itemCatProperty() {
        return itemCat;
    }

    public void setItemCat(String itemCat) {
        this.itemCat.set(itemCat);
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

    public String getNoOfUnit() {
        return noOfUnit.get();
    }

    public StringProperty noOfUnitProperty() {
        return noOfUnit;
    }

    public void setNoOfUnit(String noOfUnit) {
        this.noOfUnit.set(noOfUnit);
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

    public CheckBox getRemark() {
        return remark;
    }

    public void setRemark(CheckBox remark) {
        this.remark = remark;
    }

}
