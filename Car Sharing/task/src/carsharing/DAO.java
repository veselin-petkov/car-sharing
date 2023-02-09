package carsharing;

import java.util.List;

public interface DAO<T> {

    T getOne(int id );
    List<T> getAll();
    T save(T obj);
    boolean delete(T obj);

}

class CompanyDao implements DAO<Car>{

    @Override
    public Car getOne(int id) {
        return null;
    }

    @Override
    public List<Car> getAll() {
        return null;
    }

    @Override
    public Car save(Car obj) {
        return null;
    }

    @Override
    public boolean delete(Car obj) {
        return false;
    }
}

class Company{
    int id;
    String name;

    public Company(int id, String name) {
        this.id = id;
        this.name = name;
    }
}

class Car {
    int id;
    String name;
    int companyID;

    public Car(int id, String name,  int companyID) {
        this.id = id;
        this.name = name;
        this.companyID = companyID;
    }
}

class Customer {
    int id;
    String name;
    int rentedCarId;

    public Customer(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setRentedCarId(int rentedCarId) {
        this.rentedCarId = rentedCarId;
    }

    public int getRentedCarId() {
        return rentedCarId;
    }
}