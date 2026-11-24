package entity;

public class BankManager extends BankStaff{
    public BankManager(String staffId, String name, String email) {
        super(staffId, name, email);
    }

    @Override
    public String getRole() {
        return "BANK_MANAGER";
    }
}
