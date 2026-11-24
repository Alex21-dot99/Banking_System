package entity;

public class BankAdministrator extends BankStaff{
    public BankAdministrator(String staffId, String name, String email) {
        super(staffId, name, email);
    }

    @Override
    public String getRole() {
        return "BANK_ADMINISTRATOR";
    }
}
