//package carsharing;
//
//import java.sql.*;
//import java.util.Scanner;
//
//public class Main {
//    public static void logInDisplayMenu() {
//        System.out.println("1. Log in as a manager");
//        System.out.println("0. Exit");
//    }
//
//    public static void companyDisplayMenu() {
//        System.out.println();
//        System.out.println("1. Company list");
//        System.out.println("2. Create a company");
//        System.out.println("0. Back");
//    }
//
//    public static void carDisplayMenu() {
//        System.out.println("1. Car list");
//        System.out.println("2. Create a car");
//        System.out.println("0. Back");
//
//    }
//
//    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        Class.forName("org.h2.Driver");
//        Connection connection = DriverManager.getConnection("jdbc:h2:file:./src/carsharing/db/carsharing");
//        //Connection connection = DriverManager.getConnection("jdbc:h2:file:./src/carsharing/carsharing");
//
//        Statement stmt = connection.createStatement();
//        connection.setAutoCommit(true);
//        //stmt.execute("drop table IF EXISTS car;");
//        //stmt.execute("drop table IF EXISTS company;");
//
//        String company = "CREATE TABLE IF NOT EXISTS COMPANY " +
//                "(id INTEGER NOT NULL AUTO_INCREMENT, " +
//                " name VARCHAR(255) UNIQUE NOT NULL, " +
//                " PRIMARY KEY (id));";
//
//        String car = """
//        CREATE TABLE IF NOT EXISTS CAR
//        (id INTEGER NOT NULL AUTO_INCREMENT,
//        name VARCHAR(255) UNIQUE NOT NULL,
//        company_id INTEGER NOT NULL,
//        PRIMARY KEY ( id ),
//        FOREIGN KEY (company_id) REFERENCES COMPANY(id));
//        """;
//
//        String customer = """
//        CREATE TABLE IF NOT EXISTS CUSTOMER
//        (id INTEGER NOT NULL AUTO_INCREMENT,
//        name VARCHAR(255) UNIQUE NOT NULL,
//        rented_car_id INTEGER,
//        PRIMARY KEY ( id ),
//        FOREIGN KEY (rented_car_id) REFERENCES CAR(id));
//        """;
//
//        stmt.executeUpdate(company);
//        stmt.executeUpdate(car);
//        stmt.executeUpdate(customer);
//
//        String createCompany = "INSERT INTO COMPANY" + "VALUES (?);";
//        String listCompanies = "SELECT id, name FROM COMPANY;";
//        String listCarsByCompanyId = "SELECT * FROM CAR WHERE company_id = (?);";
//
//
//        ResultSet test = stmt.executeQuery("SELECT * FROM CAR WHERE ID = (2);");
//
//
//        PreparedStatement statement;
//
//        Scanner scanner = new Scanner(System.in);
//        final int COMPANY_NAME_COLUMN = 2;
//        final int CAR_NAME_COLUMN = 2;
//
//        boolean companyMenu = false;
//        boolean outerMenu = false;
//        boolean carMenu = false;
//        int innerOption;
//        do {
//            logInDisplayMenu();
//            switch (scanner.nextInt()) {
//                case 1:
//                    do {
//                        companyDisplayMenu();
//                        innerOption = scanner.nextInt();
//                        switch (innerOption) {
//                            case 1:
//                                ResultSet rs = stmt.executeQuery(listCompanies);
//                                if (!rs.next()) {
//                                    System.out.println("The company list is empty!");
//                                } else {
//                                    System.out.println("Choose the company:");
//                                    do {
//                                        System.out.println(rs.getInt(1) + ". " + rs.getString(2));
//                                    } while (rs.next());
//                                    System.out.println("0. Back");
//                                    int carCompanyId = scanner.nextInt();
//                                    if (carCompanyId == 0) break;
//
//
//                                    statement = connection.prepareStatement("SELECT * from COMPANY WHERE id=?");
//                                    statement.setInt(1, carCompanyId);
//                                    ResultSet companyName = statement.executeQuery();
//
//                                    //companyName.next();
//                                    //System.out.println(companyName);
//                                    //System.out.println("'" + companyName.getString("name") + "' company");
//                                    do {
//                                        carMenu = false;
//                                        carDisplayMenu();
//                                        int carCompanyOption = scanner.nextInt();
//                                        switch (carCompanyOption) {
//                                            case 1:
//                                                statement = connection.prepareStatement(listCarsByCompanyId);
//                                                statement.setInt(1, carCompanyId);
//                                                rs = statement.executeQuery();
//                                                if (!rs.next()) {
//                                                    System.out.println("The car list is empty!");
//                                                    break;
//                                                } else {
//                                                    System.out.println("Car list:");
//                                                    int rowCounter = 1;
//                                                    do {
//                                                        System.out.println(rowCounter++ + ". " + rs.getString(2));
//                                                    } while (rs.next());
//                                                }
//                                                System.out.println();
//                                                break;
//                                            case 2:
//                                                System.out.println("Enter the car name:");
//                                                scanner.nextLine();
//                                                String carName = scanner.nextLine();
//
//                                                statement = connection.prepareStatement("INSERT INTO CAR (name,company_id) VALUES (?,?);");
//                                                statement.setString(1, carName);
//                                                statement.setInt(2, carCompanyId);
//                                                statement.execute();
//                                                System.out.println("The car was added!");
//                                                break;
//                                            case 0:
//                                                carMenu = true;
//                                                break;
//                                        }
//                                    } while (!carMenu);
//                                }
//                                break;
//                            case 2:
//                                System.out.println("Enter the company name:");
//                                scanner.nextLine();
//                                String companyName = scanner.nextLine();
//                                statement = connection.prepareStatement("INSERT INTO COMPANY (name) VALUES (?);");
//                                statement.setString(1, companyName);
//                                statement.execute();
//                                System.out.println("The company was created!");
//                                break;
//                            case 0:
//                                companyMenu = true;
//                                break;
//                        }
//                    } while (!companyMenu);
//                    break;
//                case 0:
//                    outerMenu = true;
//                    break;
//            }
//        } while (!outerMenu);
//    }
//}