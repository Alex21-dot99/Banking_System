package entity;

public class IndividualCustomer extends Customer {
    private String IdNumber;
    private String dateOfBirth;

    public IndividualCustomer(String userId, String firstName, String surname, String username, String passwordHash, String address, String phone, String email, String idNumber, String dateOfBirth) {
        super(userId, firstName, surname, username, passwordHash, address, phone, email);
        IdNumber = idNumber;
        this.dateOfBirth = dateOfBirth;
    }

    @Override
    public String getCustomerType() {
        return "INDIVIDUAL";
    }

    public String getIdNumber() {
        return IdNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }
}
