package dao;

import entity.LoanRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class LoanRequestStore {
    private static final List<LoanRequest> REQUESTS = new ArrayList<>();

    public static String nextRequestId() {
        return "LN-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }

    public static void addRequest(LoanRequest request) {
        REQUESTS.add(0, request);
    }

    public static List<LoanRequest> getRequests() {
        return Collections.unmodifiableList(REQUESTS);
    }

    public static String formatForList(LoanRequest req) {
        String custId = (req.getCustomer() != null) ? req.getCustomer().getUserId() : "";
        String custName = (req.getCustomer() != null)
                ? (req.getCustomer().getFirstName() + " " + req.getCustomer().getSurname()).trim()
                : "";
        String customerDisplay = custName.isEmpty() ? custId : custId + " (" + custName + ")";

        return req.getRequestID()
                + " | Customer: " + customerDisplay
                + " | Amount: BWP " + String.format("%.2f", req.getRequestedAmount())
                + " | Purpose: " + req.getPurpose()
                + " | Status: " + req.getStatus();
    }
}