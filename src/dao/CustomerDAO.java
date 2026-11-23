package dao;

import entity.Customer;
import entity.IndividualCustomer;
import entity.CompanyCustomer;

import java.sql.*;

public class CustomerDAO{

    public Customer findByEmail(String email) {
        String sql = "SELECT * FROM customers WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String customerId = rs.getString("customer_id");
                String firstName = rs.getString("first_name");
                String surname = rs.getString("surname");
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                String customerType = rs.getString("customer_type");


                if ("INDIVIDUAL".equalsIgnoreCase(customerType)) {
                    String id_number = rs.getString("id_number");
                    String dateOfBirth = rs.getString("date_of_birth");

                    return new IndividualCustomer(
                            customerId, firstName, surname, username, passwordHash, address, phone, email, id_number, dateOfBirth
                    );
                } else {
                    String regNumber = rs.getString("company_registration_number");
                    String taxNumber = rs.getString("tax_identification_number");

                    return new CompanyCustomer(
                            customerId, firstName, surname, username, passwordHash, address, phone, email, regNumber, taxNumber
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean saveCustomer(Customer customer) {

        String sql = "INSERT INTO customers (customer_id, first_name, surname, username, password_hash, address, phone, email, customer_type, id_number, date_of_birth, company_registration_number, tax_identification_number) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            String customerType = (customer instanceof IndividualCustomer) ? "INDIVIDUAL" : "COMPANY";

            String idNumber = null;
            String dateOfBirth = null;
            String regNumber = null;
            String taxNumber = null;

            if (customer instanceof IndividualCustomer ind) {
                idNumber = ind.getIdNumber();
                dateOfBirth = ind.getDateOfBirth();
            } else if (customer instanceof CompanyCustomer comp) {
                regNumber = comp.getCompanyRegistrationNumber();
                taxNumber = comp.getTaxIdentificationNumber();
            }

            stmt.setString(1, customer.getUserId());
            stmt.setString(2, customer.getFirstName());
            stmt.setString(3, customer.getSurname());
            stmt.setString(4, customer.getUsername());
            stmt.setString(5, customer.getPasswordHash());
            stmt.setString(6, customer.getAddress());
            stmt.setString(7, customer.getPhone());
            stmt.setString(8, customer.getEmail());
            stmt.setString(9, customerType);
            stmt.setString(10, idNumber);
            stmt.setString(11, dateOfBirth);
            stmt.setString(12, regNumber);
            stmt.setString(13, taxNumber);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Customer findById(String customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customerId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String id = rs.getString("customer_id");
                String firstName = rs.getString("first_name");
                String surname = rs.getString("surname");
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String customerType = rs.getString("customer_type");

                if ("INDIVIDUAL".equalsIgnoreCase(customerType)) {
                    String idNumber = rs.getString("id_number");
                    String dateOfBirth = rs.getString("date_of_birth");
                    return new IndividualCustomer(
                            id, firstName, surname, username, passwordHash, address, phone, email, idNumber, dateOfBirth
                    );
                } else {
                    String regNumber = rs.getString("company_registration_number");
                    String taxNumber = rs.getString("tax_identification_number");
                    return new CompanyCustomer(
                            id, firstName, surname, username, passwordHash, address, phone, email, regNumber, taxNumber
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public java.util.List<Customer> findAll() {
        java.util.List<Customer> customers = new java.util.ArrayList<>();
        String sql = "SELECT * FROM customers";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String id = rs.getString("customer_id");
                String firstName = rs.getString("first_name");
                String surname = rs.getString("surname");
                String username = rs.getString("username");
                String passwordHash = rs.getString("password_hash");
                String address = rs.getString("address");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                String customerType = rs.getString("customer_type");

                if ("INDIVIDUAL".equalsIgnoreCase(customerType)) {
                    String idNumber = rs.getString("id_number");
                    String dateOfBirth = rs.getString("date_of_birth");
                    customers.add(new IndividualCustomer(
                            id, firstName, surname, username, passwordHash, address, phone, email, idNumber, dateOfBirth
                    ));
                } else {
                    String regNumber = rs.getString("company_registration_number");
                    String taxNumber = rs.getString("tax_identification_number");
                    customers.add(new CompanyCustomer(
                            id, firstName, surname, username, passwordHash, address, phone, email, regNumber, taxNumber
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customers;
    }
    public boolean insertCustomer(String userId, String firstName, String surname,
                                  String username, String email, String phone,
                                  String address, String passwordHash) {
        Customer customer = new IndividualCustomer(
                userId,
                firstName,
                surname,
                username,
                passwordHash,
                address,
                phone,
                email,
                "",
                ""
        );
        return saveCustomer(customer);
    }
}