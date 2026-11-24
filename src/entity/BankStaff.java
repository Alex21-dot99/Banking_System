package entity;

public abstract class BankStaff {
    private String staffId;
    private String name;
    private String email;

    public BankStaff(String staffId, String name, String email) {
        this.staffId = staffId;
        this.name = name;
        this.email = email;
    }

    public String getStaffId() {
        return staffId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public abstract String getRole();
}
