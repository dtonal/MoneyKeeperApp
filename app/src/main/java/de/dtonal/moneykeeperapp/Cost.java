package de.dtonal.moneykeeperapp;

/**
 * Created by dtonal on 02.10.16.
 */

public class Cost {
    private double value;
    private String category;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    private String comment;

    public Cost(double value, String category, String comment) {
        this.value = value;
        this.category = category;
        this.comment = comment;
    }
}
