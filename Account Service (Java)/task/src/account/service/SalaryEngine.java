package account.service;

import account.entity.AppUser;
import account.entity.Salary;
import account.response.CustomErrorMessage;
import account.repository.AppUserRepository;
import account.repository.SalaryRepository;
import account.response.UserSalary;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class SalaryEngine {
    private final SalaryRepository salaryRepository;
    private final AppUserRepository userRepository;
    SalaryEngine(SalaryRepository salaryRepository, AppUserRepository userRepository){
        this.salaryRepository = salaryRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity getUserSalaries (Authentication auth, String period){
        AppUser user = userRepository.findAppUserByEmail(auth.getName().toLowerCase()).get();

        if(userRepository.findAppUserByEmail(user.getEmail()).get().isBlocked()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new CustomErrorMessage(
                            HttpStatus.UNAUTHORIZED.value(),
                            "User account is locked",
                            "/api/empl/payment",
                            "Unauthorized"));
        }

        List<Salary> salaries;
        if(period == null){
            salaries = salaryRepository.findByEmployee(user.getEmail());
        }else if(period.startsWith("1") && Integer.parseInt(period.substring(0, 2))>12){
            throw new BusinessException("Wrong date in request!");
        }else{
           salaries = salaryRepository.findByPeriodAndEmployee(period, user.getEmail());
        }

        Collections.reverse(salaries);
        List<UserSalary> userSalaryList = salaries.stream()
                .map(x -> new UserSalary(user.getName(), user.getLastname(), convertPeriod(x.getPeriod()), convertSum(x.getSalary())))
                        .collect(Collectors.toList());

        if(userSalaryList.size()==1){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userSalaryList.get(0));
        }else{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(userSalaryList);
        }
    }
    public boolean checkSalaryBeforeInsert(ArrayList<Salary> salaries){
        if(!checkIncorrectEmployee(salaries)){
            throw new BusinessException("Error!");
        }

        if(!checkDuplicatedPeriods(salaries)){
            throw new BusinessException("Error!");
        }

        String errorText = checkIncorrectPeriodsOrSalaries(salaries);
        if(!errorText.equals("")){
            throw new BusinessException(errorText);
        }

        registerSalary(salaries);

        return true;
    }

    public boolean checkSalaryBeforeUpdate(Salary salary){
        if(salaryRepository.findByPeriodAndEmployee(salary.getPeriod(), salary.getEmployee()).size() != 1){
            throw new BusinessException("Error!");
        }

        StringBuilder wrongDataStrBuilder = new StringBuilder("");

        if (salary.getSalary()<0){
            wrongDataStrBuilder.append("Salary must be non negative!, ");
        }

        String periodStr = salary.getPeriod().substring(0, 2);
        int periodInt = 0;
        if(periodStr.startsWith("1")){
            periodInt = Integer.parseInt(periodStr);
        }else{
            periodInt = Integer.parseInt(periodStr.substring(1));
        }

        if(periodInt>12){
            wrongDataStrBuilder.append("Wrong date! ");
        }

        String wrongData = wrongDataStrBuilder.toString().trim();
        if(wrongData.endsWith(",")){
            wrongData.substring(0, wrongData.length()-2);
        };


        if(!wrongData.equals("")){
            throw new BusinessException(wrongData);
        }

        updateSalary(salary);
        return true;
    }

    public boolean updateSalary(Salary salary){
        Salary oldSalary = salaryRepository.findByPeriodAndEmployee(salary.getPeriod(), salary.getEmployee()).get(0);
        oldSalary.setSalary((float)(salary.getSalary()/100));
        oldSalary.setPeriod(salary.getPeriod());
        salaryRepository.save(oldSalary);
        return true;
    }

    private boolean checkDuplicatedPeriods(List<Salary> salaries){
        Map<String, List<String>> employees = salaries.stream()
                .collect(Collectors.groupingBy(Salary::getEmployee, Collectors.mapping(Salary::getPeriod, toList())));

        for(Map.Entry<String, List<String>> entry : employees.entrySet()){
            List<String> periods = entry.getValue();
            Set<String> uniquePeriods = new HashSet<>(periods);
            if(periods.size()>uniquePeriods.size()){
                return false;
            }
        }
        return true;
    }

    private boolean checkIncorrectEmployee(List<Salary> salaries){
        List<String> employees = salaries.stream()
                .map(x-> x.getEmployee())
                .distinct()
                .collect(Collectors.toList());

        if(employees.size() != userRepository.findAllByEmail(employees).size()){
            return false;
        }else{
            return true;
        }
    }

    private String checkIncorrectPeriodsOrSalaries(List<Salary> salaries){
       Map<String, List<String>> employees = salaries.stream()
                .collect(Collectors.groupingBy(Salary::getEmployee, Collectors.mapping(Salary::getPeriod, toList())));

       StringBuilder wrongDataStrBuilder = new StringBuilder("");

        for (int i=0; i<salaries.size(); i++){
            if (salaries.get(i).getSalary()<0){
                wrongDataStrBuilder.append("payments[" + i + "].salary: Salary must be non negative!, ");
            }

            String periodStr = salaries.get(i).getPeriod().substring(0, 2);
            int periodInt = 0;
            if(periodStr.startsWith("1")){
                periodInt = Integer.parseInt(periodStr);
            }else{
                periodInt = Integer.parseInt(periodStr.substring(1));
            }

            if(periodInt>12){
                wrongDataStrBuilder.append("payments[" + i + "].period: Wrong date!,");
            }
        }

        String wrongData = wrongDataStrBuilder.toString().trim();
        if(wrongData.endsWith(",")){
            wrongData = wrongData.substring(0, wrongData.length()-2);
        };

        return wrongData;
    }

    @Transactional
    public boolean registerSalary(List<Salary> salaries){
        salaries.forEach(x -> x.setSalary((float)(x.getSalary()/100)));
        salaries.forEach(x-> salaryRepository.save(x));
        return true;
    }
    private String convertPeriod(String period){
        String newPeriod = "";
        String year = period.substring(period.length()-4);
        String prefix = period.substring(0, 2);
        if(period.startsWith("0")){
            prefix = prefix.substring(1);
        }
        switch (prefix){
            case "1":
                newPeriod = "January-" + year;
                break;
            case "2":
                newPeriod = "February-" + year;
                break;
            case "3":
                newPeriod = "March-" + year;
                break;
            case "4":
                newPeriod = "April-" + year;
                break;
            case "5":
                newPeriod = "May-" + year;
                break;
            case "6":
                newPeriod = "June-" + year;
                break;
            case "7":
                newPeriod = "July-" + year;
                break;
            case "8":
                newPeriod = "August-" + year;
                break;
            case "9":
                newPeriod = "September-" + year;
                break;
            case "10":
                newPeriod = "October-" + year;
                break;
            case "11":
                newPeriod = "November-" + year;
                break;
            case "12":
                newPeriod = "December-" + year;
                break;
        }
        return newPeriod;
    }

    private String convertSum(double salary){
        return ((int) Math.floor(salary)) + " dollar(s) " + (Math.round((salary - Math.floor(salary)) * 100.0)) + " cent(s)";
    }
}