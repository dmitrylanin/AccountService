package account.response;

public class UserSalary {

    private String name;
    private String lastname;
    private String period;
    private String salary;

    public UserSalary(String name, String lastname, String month, String salary) {
        this.name = name;
        this.lastname = lastname;
        this.period = month;
        this.salary = salary;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
