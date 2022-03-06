package com.miaoshaproject.daoobject;

import java.util.Date;

public class PromoDO {
    private Integer id;

    private String promoNaem;

    //mybatis使用的是java.util的date
    private Date startDate;

    private Date endDate;

    private Integer itemId;

    private Double promoItemPrice;

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPromoNaem() {
        return promoNaem;
    }

    public void setPromoNaem(String promoNaem) {
        this.promoNaem = promoNaem;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Double getPromoItemPrice() {
        return promoItemPrice;
    }

    public void setPromoItemPrice(Double promoItemPrice) {
        this.promoItemPrice = promoItemPrice;
    }
}