package com.example.purchaselist.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Purchase implements Serializable {
    private int id;
    private String product;
    private double count;
    private float price;
    private boolean isBought;
    private int purchaseList;

    public float priceSum(){
        return (float) (price*count);
    }
}
