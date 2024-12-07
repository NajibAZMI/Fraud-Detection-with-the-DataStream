package com.frauddetection;

public class RuleAlertStats {
    private String ruleId;
    private int alertCount;
    private String description;

    public RuleAlertStats(String ruleId, int alertCount,String description) {
        this.ruleId = ruleId;
        this.alertCount = alertCount;
        this.description = description;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public int getAlertCount() {
        return alertCount;
    }

    public void setAlertCount(int alertCount) {
        this.alertCount = alertCount;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

