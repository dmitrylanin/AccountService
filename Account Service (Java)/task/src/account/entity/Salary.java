package account.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Salary {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String employee;
    private String period;
    private double salary;

    public Integer getId() {
        return id;
    }
    public String getEmployee() {
        return employee;
    }

    public String getPeriod() {
        return period;
    }
    public double getSalary() {
        return salary;
    }
    public void setSalary(double salary) {
        this.salary = salary;
    }
    public void setPeriod(String period) {
        this.period = period;
    }
}