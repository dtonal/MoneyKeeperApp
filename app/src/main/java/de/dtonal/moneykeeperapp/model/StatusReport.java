package de.dtonal.moneykeeperapp.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

/**
 * Represents a StatusReport object from the backend via api.
 * Created by dtonal on 15.10.16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusReport {

    private Date date;
    private Double dailyBudget;
    private Double weeklyBudget;
    private Double monthlyBudget;
    private Double dayState;
    private Double weekState;

    public StatusReport() {
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getDailyBudget() {
        return dailyBudget;
    }

    public void setDailyBudget(Double dailyBudget) {
        this.dailyBudget = dailyBudget;
    }

    public Double getWeeklyBudget() {
        return weeklyBudget;
    }

    public void setWeeklyBudget(Double weeklyBudget) {
        this.weeklyBudget = weeklyBudget;
    }

    public Double getMonthlyBudget() {
        return monthlyBudget;
    }

    public void setMonthlyBudget(Double monthlyBudget) {
        this.monthlyBudget = monthlyBudget;
    }

    public Double getDayState() {
        return dayState;
    }

    public void setDayState(Double dayState) {
        this.dayState = dayState;
    }

    public Double getWeekState() {
        return weekState;
    }

    public void setWeekState(Double weekState) {
        this.weekState = weekState;
    }

    public Double getMonthState() {
        return monthState;
    }

    public void setMonthState(Double monthState) {
        this.monthState = monthState;
    }

    public int getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    private Double monthState;
    private int dayOfWeek;
    private int dayOfMonth;


}
