package entity;

public class LoanOfficer extends BankStaff{
    public LoanOfficer(String staffId, String name, String email) {
        super(staffId, name, email);
    }

    @Override
    public String getRole() {
        return "LOAN_OFFICER";
    }

    public boolean reviewLoanRequest(LoanRequest request, boolean approve) {
        if(approve) {
            request.setStatus("APPROVED");
        }else {
            request.setStatus("REJECTED");
        }
        return true;
    }
}
