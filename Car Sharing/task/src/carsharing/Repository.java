package carsharing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

class Repository {

    private static final String SQL_GET_ALL_COMPANIES = "SELECT * FROM COMPANY";
    private static final String SQL_GET_ALL_AVAILABLE_CARS_BY_COMPANY_ID = "SELECT CAR.ID,CAR.NAME FROM CAR\n" +
            "LEFT JOIN CUSTOMER C on CAR.ID = C.RENTED_CAR_ID\n" +
            "WHERE CAR.COMPANY_ID =(?)" +
            "AND C.RENTED_CAR_ID IS NULL;\n";
    private static final String SQL_GET_ALL_CUSTOMERS = "SELECT * FROM CUSTOMER";
    private static final String SQL_GET_RENTED_CAR_BY_CUSTOMER_ID = "SELECT c.NAME,com.NAME\n" +
            "FROM CUSTOMER cus \n" +
            "JOIN CAR C on C.ID = cus.RENTED_CAR_ID\n" +
            "JOIN COMPANY com on com.ID = C.COMPANY_ID\n" +
            "WHERE cus.ID =(?)";

    private static final String SQL_ADD_COMPANY = "INSERT INTO COMPANY (name) VALUES (?);";
    private static final String SQL_ADD_CAR = "INSERT INTO CAR (name,company_id) VALUES(?,?);";
    private static final String SQL_ADD_CUSTOMER = "INSERT INTO CUSTOMER (name) VALUES (?);";

    private static final String SQL_ADD_RENTED_CAR_ID = "UPDATE CUSTOMER SET rented_car_id = (?)where ID=(?);";
    private static final String SQL_REMOVE_RENTED_CAR_ID = "UPDATE CUSTOMER SET rented_car_id = null WHERE ID=(?) AND RENTED_CAR_ID IS NOT NULL;";
    private static final String SQL_CHECK_FOR_RENTED_CAR = "SELECT rented_car_id FROM CUSTOMER WHERE ID=(?);";


    Connection conn;

    public Repository(Connection conn) {
        this.conn = conn;
    }

    List<Company> getAllCompanies() {
        List<Company> companies = new ArrayList<>();

        try {
            var ps = conn.prepareStatement(SQL_GET_ALL_COMPANIES);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                companies.add(new Company(rs.getInt(1), rs.getString(2)));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return companies;
    }

    List<Car> getAllCarsByCompanyId(int companyId) {
        List<Car> cars = new ArrayList<>();
        try {
            var ps = conn.prepareStatement(SQL_GET_ALL_AVAILABLE_CARS_BY_COMPANY_ID);
            ps.setInt(1, companyId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cars.add(new Car(rs.getInt(1), rs.getString(2), companyId));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return cars;
    }

    List<Customer> getAllCustomers() {
        List<Customer> customers = new ArrayList<>();
        try {
            var ps = conn.prepareStatement(SQL_GET_ALL_CUSTOMERS);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                customers.add(new Customer(rs.getInt(1), rs.getString(2)));
            }
            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return customers;
    }

    void addCompany(String name) {
        try {
            var ps = conn.prepareStatement(SQL_ADD_COMPANY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.executeUpdate();
            ps.close();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    void addCar(String name, int coppanyId) {
        try {
            var ps = conn.prepareStatement(SQL_ADD_CAR, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.setInt(2, coppanyId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void addCustomer(String name) {
        try {
            var ps = conn.prepareStatement(SQL_ADD_CUSTOMER, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, name);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
        }
    }

    public void addRentedCar(int id, int carId) {
        try {
            var ps = conn.prepareStatement(SQL_ADD_RENTED_CAR_ID, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, carId);
            ps.setInt(2, id);
            //System.out.println("Customer ID:"+id+ " Rented car ID: " + carId);

            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
        }
    }

    public String getCustomerRentedCarById(int customerId) {
        String result = null;
        try {
            var ps = conn.prepareStatement(SQL_GET_RENTED_CAR_BY_CUSTOMER_ID, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();


            result = "Your rented car:\n";
            if (rs.next()) {
                result += rs.getString(1) + "\nCompany:\n";
                result += rs.getString(2);
            } else return null;
            return result;
        } catch (SQLException ex) {
        }
        return result;
    }

    public boolean deleteCustomerRentedCarById(int customerId) {
        try {
            var ps = conn.prepareStatement(SQL_REMOVE_RENTED_CAR_ID, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, customerId);

            if (ps.executeUpdate() == 1) {
                return true;
            }

        } catch (SQLException ex) {
        }
        return false;
    }

    public boolean checkForRentedCar(int customerId) {
        try {
            var ps = conn.prepareStatement(SQL_CHECK_FOR_RENTED_CAR, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            rs.next();
            //System.out.println("Customer ID:"+customerId+ " Rented car ID: " + rs.getInt(1));
            if (rs.getInt(1)==0){
                //System.out.println("Rented car ID: " + rs.getInt(1));
                return true;
            }

        } catch (SQLException ex) {
        }
        return false;
    }
}