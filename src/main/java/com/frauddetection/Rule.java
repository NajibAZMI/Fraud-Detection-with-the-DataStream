package com.frauddetection;

import java.io.Serializable;
import java.util.List;

public class Rule implements Serializable {
    private int ruleId;
    private String ruleState;  // "ACTIVE" ou "INACTIVE"
    private String groupingKeyNames;  // "payerId,beneficiaryId,transactionType"
    private String aggregatorFunctionType;  // "SUM", "AVERAGE", etc.
    private String limitOperatorType;  // "GREATER", "LESS", etc.
    private double limit;  // Seuil de la règle
    private String windowMinutes;  // Durée de la fenêtre en minutes

    // Constructeur
    public Rule(int ruleId, String ruleState, String groupingKeyNames, String aggregatorFunctionType,
                String limitOperatorType, double limit, String windowMinutes) {
        this.ruleId = ruleId;
        this.ruleState = ruleState;
        this.groupingKeyNames = groupingKeyNames;
        this.aggregatorFunctionType = aggregatorFunctionType;
        this.limitOperatorType = limitOperatorType;
        this.limit = limit;
        this.windowMinutes = windowMinutes;
    }

    // Getters et setters
    public int getRuleId() {
        return ruleId;
    }

    public boolean isActive() {
        return "ACTIVE".equals(ruleState);
    }

    public void setRuleId(int ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleState() {
        return ruleState;
    }

    public void setRuleState(String ruleState) {
        this.ruleState = ruleState;
    }

    public String getGroupingKeyNames() {
        return groupingKeyNames;
    }

    public void setGroupingKeyNames(String groupingKeyNames) {
        this.groupingKeyNames = groupingKeyNames;
    }

    public String getAggregatorFunctionType() {
        return aggregatorFunctionType;
    }

    public void setAggregatorFunctionType(String aggregatorFunctionType) {
        this.aggregatorFunctionType = aggregatorFunctionType;
    }

    public String getLimitOperatorType() {
        return limitOperatorType;
    }

    public void setLimitOperatorType(String limitOperatorType) {
        this.limitOperatorType = limitOperatorType;
    }

    public double getLimit() {
        return limit;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public String getWindowMinutes() {
        return windowMinutes;
    }

    public void setWindowMinutes(String windowMinutes) {
        this.windowMinutes = windowMinutes;
    }



    @Override
    public String toString() {
        return "Rule{" +
                "ruleId=" + ruleId +
                ", ruleState='" + ruleState + '\'' +
                ", groupingKeyNames='" + groupingKeyNames + '\'' +
                ", aggregatorFunctionType='" + aggregatorFunctionType + '\'' +
                ", limitOperatorType='" + limitOperatorType + '\'' +
                ", limit=" + limit +
                ", windowMinutes='" + windowMinutes + '\'' +
                '}';
    }
}