package account.repository;

import account.entity.Salary;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SalaryRepository extends CrudRepository<Salary, Integer> {

    Salary save(Salary entity);
    List<Salary> findByPeriodAndEmployee(String period, String employee);

    List<Salary> findByEmployee(String employee);

}