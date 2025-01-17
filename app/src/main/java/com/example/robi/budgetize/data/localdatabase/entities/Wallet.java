package com.example.robi.budgetize.data.localdatabase.entities;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "wallets")
public class Wallet {

    public Wallet(String name,double financialStatus, double financialGoal,String currency) {
        this.id = System.nanoTime();
        this.name = name;
        this.financialGoal = financialGoal;
        this.financialStatus = financialStatus;
        this.currency = currency;

        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        this.lastDate = strDate;
    }

    @PrimaryKey
    private long id;

    @ColumnInfo(name = "wallet_name")
    private String name;

    @ColumnInfo(name = "financial_goal")
    private double financialGoal;

    @ColumnInfo(name = "financial_status")
    private double financialStatus;//view of current status of the wallet
    //private int ArrayList<IncomeObj> incomesArr;
    //private int ArrayList<ExpenseObj> expenseArr;

    @ColumnInfo(name = "latest_income_update")
    private String lastDate;

    @ColumnInfo(name = "currency")
    private String currency;

    public String getLastDate() {
        return lastDate;
    }

    public void setLastDate(String lastDate) {
        this.lastDate = lastDate;
    }

    //we need a view which calculate ie

    public void setFinancialGoal(double financialGoal) {
        this.financialGoal = financialGoal;
    }

    public void setFinancialStatus(double financialStatus) {
        this.financialStatus = financialStatus;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getFinancialGoal() {
        return financialGoal;
    }

    public double getFinancialStatus() {
        return financialStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
