package com.frauddetection;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction implements Serializable {
    private String transactionId;
    private String payerId;
    private double amount;
    private String beneficiaryId;
    private String transactionType;
    private long timestamp;
    public Transaction() {
    }
    public Transaction(String transactionId, String payerId, long timestamp,double amount, String beneficiaryId, String transactionType) {
        this.transactionId = transactionId;
        this.payerId = payerId;
        this.amount = amount;
        this.beneficiaryId = beneficiaryId;
        this.transactionType = transactionType;
        this.timestamp=timestamp;
    }
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
    public String getPayerId() {
        return payerId;
    }
    public void setPayerId(String payerId) {
        this.payerId = payerId;
    }
    public double getAmount() {
        return amount;
    }
    public void setAmount(double amount) {
        this.amount = amount;
    }
    public String getBeneficiaryId() {
        return beneficiaryId;
    }
    public void setBeneficiaryId(String beneficiaryId) {
        this.beneficiaryId = beneficiaryId;
    }
    public String getTransactionType() {
        return transactionType;
    }
    public long getTimeTamp() {
        return timestamp;
    }
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date(timestamp));
    }
    @Override
    public String toString() {
        return "Transaction{id=" + transactionId + ", payerId=" + payerId + ", amount=" + amount + ", beneficiaryId=" + beneficiaryId + ", type=" + transactionType + "}";
    }
}