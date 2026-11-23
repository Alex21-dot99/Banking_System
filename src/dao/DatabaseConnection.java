package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
   private static final String URL = "jdbc:sqlite:banking_system.db";
   private static boolean initialized = false;

   public static Connection getConnection() throws SQLException {
       Connection conn = DriverManager.getConnection(URL);
       if (!initialized) {
           initializeDatabase(conn);
           initialized = true;
       }
       return conn;
   }

   private static void initializeDatabase(Connection conn) throws SQLException {
       String createCustomers = "CREATE TABLE IF NOT EXISTS customers (" +
               "customer_id TEXT PRIMARY KEY, " +
               "first_name TEXT, " +
               "surname TEXT, " +
               "username TEXT, " +
               "password_hash TEXT, " +
               "address TEXT, " +
               "phone TEXT, " +
               "email TEXT UNIQUE, " +
               "customer_type TEXT, " +
               "id_number TEXT, " +
               "date_of_birth TEXT, " +
               "company_registration_number TEXT, " +
               "tax_identification_number TEXT" +
               ");";

       String createAccounts = "CREATE TABLE IF NOT EXISTS accounts (" +
               "account_number TEXT PRIMARY KEY, " +
               "customer_id TEXT, " +
               "balance REAL, " +
               "date_opened TEXT, " +
               "account_type TEXT, " +
               "overdraft_limit REAL DEFAULT 0, " +
               "FOREIGN KEY (customer_id) REFERENCES customers(customer_id)" +
               ");";

       String insertTestCustomer = "INSERT OR IGNORE INTO customers " +
               "(customer_id, first_name, surname, username, password_hash, address, phone, email, customer_type, id_number, date_of_birth) " +
               "VALUES ('CUST-001', 'Alex', 'Tlhako', 'alex5150', 'kingswitch', 'Francistown, Botswana', '78563551', 'alex@gmail.com', 'INDIVIDUAL', '557114127', '27-07-2006');";

       String insertTestAccount = "INSERT OR IGNORE INTO accounts " +
               "(account_number, customer_id, balance, date_opened, account_type) " +
               "VALUES ('ACC-SAV-101', 'CUST-001', 1500.00, '2026-09-15', 'SAVINGS');";

       String seedMoreCustomers = "INSERT OR IGNORE INTO customers " +
               "(customer_id, first_name, surname, username, password_hash, address, phone, email, customer_type, id_number, date_of_birth, company_registration_number, tax_identification_number) VALUES " +
               "('CUST-002', 'Olorato', 'Mothomedi', 'olorato_m', 'olo2006', 'Gaborone, Botswana', '72214546', 'olorato@gmail.com', 'INDIVIDUAL', '648291034', '14-03-2005', NULL, NULL), " +
               "('CUST-003', 'Rebaone', 'Bathusi', 'ribza_b', 'ribza99', 'Palapye, Botswana', '72345678', 'ribza@gmail.com', 'INDIVIDUAL', '519384726', '22-11-2001', NULL, NULL), " +
               "('CUST-004', 'Grace', 'Tlhako', 'grace_t', 'gracy68', 'Bobonong, Botswana', '73456789', 'gracy@gmail.com', 'INDIVIDUAL', '837465912', '05-06-1998', NULL, NULL), " +
               "('CUST-005', 'Uendjipa', 'Kajuu', 'uj_kajuu', 'ujkajuu88', 'Kasane, Botswana', '74567890', 'ujkajuu@gmail.com', 'INDIVIDUAL', '492817365', '19-09-2004', NULL, NULL), " +
               "('CUST-006', 'Neo', 'Seoforeng', 'neo_s', 'neoza66', 'Serowe, Botswana', '75678901', 'neo@gmail.com', 'INDIVIDUAL', '761524389', '30-01-1995', NULL, NULL), " +
               "('CUST-007', 'Botswana Tech', 'Solutions', 'bw_tech', 'corp1234', 'CBD, Gaborone', '3912345', 'info@bwtech.co.bw', 'COMPANY', NULL, NULL, 'BW00001234567', 'C0192837465'), " +
               "('CUST-008', 'NKK', 'Express', 'NKK_express', 'corp1334', 'Block 7, Francistown', '2419876', 'nkk@express.co.bw', 'COMPANY', NULL, NULL, 'BW00002345678', 'C0293847561'), " +
               "('CUST-009', 'Okavango', 'Safari Tours', 'okavango_tours', 'corp1434', 'Shorobe Road, Maun', '6861122', 'bookings@okavango.co.bw', 'COMPANY', NULL, NULL, 'BW00003456789', 'C0394857612'), " +
               "('CUST-010', 'Morupule', 'Engineering', 'morupule_eng', 'corp1534', 'Industrial Site, Palapye', '4920044', 'finance@morupuleeng.co.bw', 'COMPANY', NULL, NULL, 'BW00004567890', 'C0495867123');";

       String seedMoreAccounts = "INSERT OR IGNORE INTO accounts " +
               "(account_number, customer_id, balance, date_opened, account_type, overdraft_limit) VALUES " +
               "('ACC-SAV-102', 'CUST-002', 3200.00, '2026-08-10', 'SAVINGS', 0), " +
               "('ACC-CHQ-102', 'CUST-002', 1850.00, '2026-08-12', 'CHEQUE', 1000.00), " +
               "('ACC-INV-103', 'CUST-003', 12500.00, '2026-07-01', 'INVESTMENT', 0), " +
               "('ACC-CHQ-104', 'CUST-004', 4600.00, '2026-06-20', 'CHEQUE', 2500.00), " +
               "('ACC-SAV-105', 'CUST-005', 850.00, '2026-10-01', 'SAVINGS', 0), " +
               "('ACC-INV-106', 'CUST-006', 25000.00, '2026-05-15', 'INVESTMENT', 0), " +
               "('ACC-SAV-106', 'CUST-006', 5000.00, '2026-05-15', 'SAVINGS', 0), " +
               "('ACC-CHQ-107', 'CUST-007', 45000.00, '2026-03-11', 'CHEQUE', 10000.00), " +
               "('ACC-INV-108', 'CUST-008', 80000.00, '2026-02-18', 'INVESTMENT', 0), " +
               "('ACC-CHQ-108', 'CUST-008', 18400.00, '2026-02-18', 'CHEQUE', 5000.00), " +
               "('ACC-SAV-109', 'CUST-009', 32000.00, '2026-04-22', 'SAVINGS', 0), " +
               "('ACC-CHQ-110', 'CUST-010', 60000.00, '2026-01-09', 'CHEQUE', 15000.00), " +
               "('ACC-INV-110', 'CUST-010', 110000.00, '2026-01-09', 'INVESTMENT', 0);";

       String createTransactions = "CREATE TABLE IF NOT EXISTS transactions (" +
               "transaction_id TEXT PRIMARY KEY, " +
               "account_number TEXT, " +
               "transaction_date TEXT, " +
               "amount REAL, " +
               "transaction_type TEXT, " +
               "description TEXT, " +
               "balance_after REAL, " +
               "FOREIGN KEY (account_number) REFERENCES accounts(account_number)" +
               ");";

       String createStaff = "CREATE TABLE IF NOT EXISTS staff (" +
               "staff_id TEXT PRIMARY KEY, " +
               "name TEXT, " +
               "email TEXT UNIQUE, " +
               "password_hash TEXT, " +
               "role TEXT" +
               ");";

       String seedStaff = "INSERT OR IGNORE INTO staff (staff_id, name, email, password_hash, role) VALUES " +
               "('STF-001', 'Admin User', 'admin@bank.bw', 'admin123', 'Bank Administrator'), " +
               "('STF-002', 'Loan Officer', 'loans@bank.bw', 'loan123', 'Loan Officer'), " +
               "('STF-003', 'Branch Manager', 'manager@bank.bw', 'manager123', 'Bank Manager');";

       try (Statement stmt = conn.createStatement()) {
           stmt.execute("PRAGMA foreign_keys = ON;");
           stmt.execute(createCustomers);
           stmt.execute(createAccounts);
           stmt.execute(createTransactions);
           stmt.execute(insertTestCustomer);
           stmt.execute(insertTestAccount);
           stmt.execute(seedMoreCustomers);
           stmt.execute(seedMoreAccounts);
           stmt.execute(createStaff);
           stmt.execute(seedStaff);

       }
   }
}
