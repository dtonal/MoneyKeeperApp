package de.dtonal.moneykeeperapp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by dtonal on 07.10.16.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class SumForStore {
    private String store;
    private Double sum;
    private Double percent;

    public SumForStore() {
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public Double getSum() {
        return sum;
    }

    public void setSum(Double sum) {
        this.sum = sum;
    }

    public Double getPercent() {
        return percent;
    }

    public void setPercent(Double percent) {
        this.percent = percent;
    }
}
