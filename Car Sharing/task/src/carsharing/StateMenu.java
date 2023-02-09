package carsharing;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.Supplier;

class StateMenu {
    public static void main(String[] args) throws SQLException {
        new Dialog().run();
        Connection connection = DriverManager.getConnection("jdbc:h2:file:./src/carsharing/db/carsharing");
        connection.setAutoCommit(true);

    }
}

class Dialog {
    Connection connection = DriverManager.getConnection("jdbc:h2:file:./src/carsharing/db/carsharing");
    Statement stmt = connection.createStatement();
    PreparedStatement preparedStatement;

    Repository repository = new Repository(DriverManager.getConnection("jdbc:h2:file:./src/carsharing/db/carsharing"));
    static final Scanner SCANNER = new Scanner(System.in);

    Dialog() throws SQLException {
    }

    static String readln() {
        return SCANNER.nextLine();
    }

    public void run() throws SQLException {
        connection.setAutoCommit(true);
        //stmt.execute("drop table if exists CUSTOMER");
        //stmt.execute("drop table IF EXISTS car;");
        //stmt.execute("drop table IF EXISTS company;");
        stmt.executeUpdate(company);
        stmt.executeUpdate(car);
        stmt.executeUpdate(customer);
        stmt.execute("UPDATE CUSTOMER SET rented_car_id = null WHERE ID=1 AND RENTED_CAR_ID IS NOT NULL;");
        State state = mainMenu;
        while (state != null) {
            state = state.get();
        }
    }

    String company = "CREATE TABLE IF NOT EXISTS COMPANY " +
            "(id INTEGER NOT NULL AUTO_INCREMENT, " +
            " name VARCHAR(255) UNIQUE NOT NULL, " +
            " PRIMARY KEY (id));";

    String car = """
            CREATE TABLE IF NOT EXISTS CAR
            (id INTEGER NOT NULL AUTO_INCREMENT,
            name VARCHAR(255) UNIQUE NOT NULL,
            company_id INTEGER NOT NULL,
            PRIMARY KEY ( id ),
            FOREIGN KEY (company_id) REFERENCES COMPANY(id));
            """;

    String customer = """
            CREATE TABLE IF NOT EXISTS CUSTOMER
            (id INTEGER NOT NULL AUTO_INCREMENT,
            name VARCHAR(255) UNIQUE NOT NULL,
            rented_car_id INTEGER,
            PRIMARY KEY ( id ),
            FOREIGN KEY (rented_car_id) REFERENCES CAR(id));
            """;


    String listCompanies = "SELECT id, name FROM COMPANY;";
    String listCarsByCompanyId = "SELECT * FROM CAR WHERE company_id = (?);";

    int companyId;
    int customerId;
    Menu mainMenu = new Menu(
            new MenuItem(1, "Log in as a manager", () -> this.managerMenu),
            new MenuItem(2, "Log in as a customer", () -> {
                try {
                    if (listCustomers() == null) {
                        System.out.println("The customer list is empty!\n");
                        return this.mainMenu;
                    }
                    return listCustomers();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }),
            new MenuItem(3, "Create a customer", () -> {
                try {
                    return this.createCustomer(this.mainMenu);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }),
            new MenuItem(0, "Exit", () -> null)
    );
    Menu managerMenu = new Menu(
            new MenuItem(1, "Company list", () -> {
                try {
                    if (listCompanies() == null) {
                        System.out.println("The company list is empty!\n");

                        return this.managerMenu;
                    } else {
                        return listCompanies();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }),
            new MenuItem(2, "Create a company", () -> {
                try {
                    return createCompany(this.managerMenu);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }),
            new MenuItem(0, "Back", () -> mainMenu)
    );
    Menu carDisplayMenu = new Menu(
            new MenuItem(1, "Car list", () -> {
                try {
                    return listCars(companyId, this.carDisplayMenu);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }),
            new MenuItem(2, "Create a car", () -> {
                try {
                    createCar(companyId, null);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return this.carDisplayMenu;
            }),
            new MenuItem(0, "Back", () -> managerMenu)
    );
    Menu customerMenu = new Menu(
            new MenuItem(1, "Rent a car",
                    () -> {
                        if (checkForRentedCar(customerId)) {
                            try {
                                System.out.println("Choose a company:");
                                return listCompaniesForRent();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            System.out.println("You've already rented a car!");
                            return this.customerMenu;
                        }
                    }
            ),
            new MenuItem(2, "Return a rented car",
                    () -> {
                        if (returnRentedCar(customerId) != null) {
                            System.out.println("You've returned a rented car!\n");
                            return this.customerMenu;
                        } else {
                            System.out.println("You didn't rent a car!\n");
                            return this.customerMenu;
                        }
                    }),
            new MenuItem(3, "My rented car",
                    () -> {
                        if (this.customerRentedCar(customerId) != null) {
                            return this.customerMenu;
                        } else {
                            System.out.println("You didn't rent a car!\n");
                            return this.customerMenu;
                        }
                    }),

            new MenuItem(0, "Back", () -> mainMenu)
    );

    Menu companies = new Menu();
    Menu cars = new Menu();
    Menu customers = new Menu();

    State prepare(String beverageName, State next) {
        System.out.println("Here is your cup of "
                + beverageName);
        return next;
    }

    State listCompanies() throws SQLException {
        companies = new Menu();
        int rowId = 1;
        for (var el : repository.getAllCompanies()) {
            companies.addMenuItem(new MenuItem(rowId++, el.name,
                    () -> {
                        companyId = el.id;
                        return choosenCompany(el.name, carDisplayMenu);
                    }
            ));
        }
        if (Arrays.stream(companies.items).allMatch(Objects::isNull)) return null;


        companies.addMenuItem(new MenuItem(0, "Back", managerMenu));
        return companies;
    }

    State listCompaniesForRent() throws SQLException {
        companies = new Menu();
        int rowId = 1;
        for (var el : repository.getAllCompanies()) {
            companies.addMenuItem(new MenuItem(rowId++, el.name,
                    () -> {
                        companyId = el.id;
                        try {
                            return listCars(companyId);
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
            ));
        }
        companies.addMenuItem(new MenuItem(0, "Back", managerMenu));
        return companies;
    }

    State listCustomers() throws SQLException {
        customers = new Menu();
        int rowId = 1;
        for (var el : repository.getAllCustomers()) {
            customers.addMenuItem(new MenuItem(rowId++, el.name,
                    () -> {
                        customerId = el.id;
                        return customerMenu;
                    }
            ));
        }
        if (Arrays.stream(customers.items).allMatch(Objects::isNull)) return null;

        customers.addMenuItem(new MenuItem(0, "Back", mainMenu));
        return customers;
    }


    State listCars(int companyId) throws SQLException {
        cars = new Menu();
        List<Car> newCarList = repository.getAllCarsByCompanyId(companyId);
        for (var el : newCarList) {
            cars.addMenuItem(new MenuItem(el.id, el.name,
                    () -> {
                        System.out.println("You rented '" + el.name + "'");
                        return rentACar(customerId, el.id);
                    }
            ));
        }
        return cars;
    }

    State listCars(int companyId, State next) throws SQLException {

        preparedStatement = connection.prepareStatement(listCarsByCompanyId);
        preparedStatement.setInt(1, companyId);
        ResultSet rs = preparedStatement.executeQuery();
        if (!rs.next()) {
            System.out.println("The car list is empty!");
        } else {
            System.out.println("Car list:");
            int rowCounter = 1;
            do {
                System.out.println(rowCounter++ + ". " + rs.getString(2));
            } while (rs.next());
        }
        System.out.println();
        return next;
    }

    State createCar(int companyId, State next) throws SQLException {
        System.out.println("Enter the car name:");
        repository.addCar(readln(), companyId);
        System.out.println("The car was added!");
        return next;
    }

    State createCompany(State next) throws SQLException {
        System.out.println("Enter the company name:");
        repository.addCompany(readln());
        System.out.println("The company was created!");

        return next;
    }

    State createCustomer(State next) throws SQLException {
        System.out.println("Enter the customer name:");
        repository.addCustomer(readln());
        System.out.println("The customer was added!");
        return next;
    }

    State choosenCompany(String name, State next) {
        System.out.println("'" + name + "' company");
        return next;
    }

    State rentACar(int customerId, int carId) {
        repository.addRentedCar(customerId, carId);
        return customerMenu;
    }

    State customerRentedCar(int customerId) {
        String print = repository.getCustomerRentedCarById(customerId);
        if (print == null) return null;

        System.out.println(print + "\n");
        return customerMenu;
    }

    State returnRentedCar(int customerId) {
        if (repository.deleteCustomerRentedCarById(customerId)) {
            return customerMenu;
        }
        return null;
    }

    boolean checkForRentedCar(int customerId) {
       // System.out.println("Customer ID:" + customerId);
        if (repository.checkForRentedCar(customerId)) {
            return true;
        } else return false;
    }

    interface State extends Supplier<State> {
    }

    record MenuItem(int id, String title, State option) {
    }

    class Menu implements State {
        MenuItem[] items;

        public Menu(MenuItem... items) {
            this.items = items;
        }

        private State show() {
            for (var item : items) {
                System.out.printf("%d. %s%n",
                        item.id, item.title);
            }
            int option = 0;
            try {
                option = Integer.parseInt(readln());
                for (var item : items) {
                    if (item.id == option) {
                        return item.option;
                    }
                }
            } catch (NumberFormatException e) {
                // do nothing
            }
            System.out.println("Please enter a valid option");
            return this;
        }

        public void addMenuItem(MenuItem menuItem) {

            MenuItem[] tmpItems = new MenuItem[items.length + 1];
            for (int i = 0; i < items.length; i++) {
                tmpItems[i] = items[i];
            }
            tmpItems[items.length] = menuItem;

            items = tmpItems;
        }

        @Override
        public State get() {
            return show();
        }
    }

}