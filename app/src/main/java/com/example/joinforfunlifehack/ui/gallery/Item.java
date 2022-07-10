package com.example.joinforfunlifehack.ui.gallery;

import java.sql.Date;

public class Item {
    String itemName, expirationDate;

    public Item() {

    }

    public Item(String expirationDate, String itemName) {
        this.itemName = itemName;
        this.expirationDate = expirationDate;
    }

    public String getItemName() {
        return itemName;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setExpirationDate(String expirationDate) {
        this.expirationDate = expirationDate;
    }
}
