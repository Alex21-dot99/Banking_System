package entity;

public class CompanyCustomer extends Customer
{
    private String companyRegistrationNumber;
    private String taxIdentificationNumber;

    public CompanyCustomer(String userId, String firstName, String surname, String username, String passwordHash, String address, String phone, String email, String companyRegistrationNumber, String taxIdentificationNumber) {
        super(userId, firstName, surname, username, passwordHash, address, phone, email);
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.taxIdentificationNumber = taxIdentificationNumber;
    }

    @Override
    public String getCustomerType() {
        return "COMPANY";
    }

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public String getTaxIdentificationNumber() {
        return taxIdentificationNumber;
    }
}
