package entity;

import java.util.Date;

public class LoanRequest {
    private String requestID;
    private Customer customer;
    private double requestedAmount;
    private String purpose;
    private String status;
    private Date requestDate;

    public LoanRequest(String requestID, Customer customer, double requestedAmount, String purpose, String status, Date requestDate) {
        this.requestID = requestID;
        this.customer = customer;
        this.requestedAmount = requestedAmount;
        this.purpose = purpose;
        this.status = "PENDING";
        this.requestDate = new Date();
    }

    public String getRequestID() {
        return requestID;
    }

    public Customer getCustomer() {
        return customer;
    }

    public double getRequestedAmount() {
        return requestedAmount;
    }

    public String getPurpose() {
        return purpose;
    }

    public String getStatus() {
        return status;
    }

    public Date getRequestDate() {
        return requestDate;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
