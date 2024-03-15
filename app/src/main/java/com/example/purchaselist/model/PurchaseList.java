package com.example.purchaselist.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Builder
@AllArgsConstructor
public class PurchaseList{
    private int id;
    private List<Purchase> purchases;
    private String title;
    public boolean isBought(){
        boolean isBought = purchases.stream().allMatch(Purchase::isBought);
        return isBought && !purchases.isEmpty();
    }

    public float priceSum(){
        return purchases.stream()
                .map(Purchase::priceSum)
                .reduce(0.00f, Float::sum);
    }
}
