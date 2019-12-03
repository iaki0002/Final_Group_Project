package com.example.finalgroupproject.currency;

public class CurrencyConversion {

    private long id;
    private String baseCurrency;
    private String targetCurrency;
    private String date;
    private double baseAmount;
    private double baseResult;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(double baseAmount) {
        this.baseAmount = baseAmount;
    }

    public double getBaseResult() {
        return baseResult;
    }

    public void setBaseResult(double baseResult) {
        this.baseResult = baseResult;
    }

    public CurrencyConversion(long id, String baseCurrency, String targetCurrency, String date, double baseAmount, double baseResult) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.date = date;
        this.baseAmount = baseAmount;
        this.baseResult = baseResult;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(String targetCurrency) {
        this.targetCurrency = targetCurrency;
    }
}
