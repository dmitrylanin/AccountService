package account.service;

import account.repository.AppUserRepository;

import java.util.Arrays;


public class PasswordCheck {
    private final String[] hackedPasswors = {"PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember"};

    private final AppUserRepository repository;

    public PasswordCheck(AppUserRepository repository) {
        this.repository = repository;
    }

    public boolean checkPassword(String password){
        if(password.length()<12){
            throw new BusinessException("Password length must be 12 chars minimum!");
        }else if(Arrays.stream(hackedPasswors).anyMatch(password::equals)){
            throw new BusinessException("The password is in the hacker's database!");
        }else{
            return true;
        }
    }
}
