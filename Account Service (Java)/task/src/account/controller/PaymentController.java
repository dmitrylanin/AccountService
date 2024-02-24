package account.controller;

import account.entity.Salary;
import account.service.BusinessException;
import account.response.CustomErrorMessage;
import account.response.CustomSuccessMessage;
import account.service.SalaryEngine;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;

@RestController
public class PaymentController {

    private final SalaryEngine salaryEngine;

    public PaymentController(SalaryEngine salaryEngine) {
        this.salaryEngine = salaryEngine;
    }

    @GetMapping("/api/empl/payment")
    public ResponseEntity getPayment(Authentication auth,
                                     @RequestParam(name = "period", required = false) String period){
        return salaryEngine.getUserSalaries(auth, period);
    }

    @PostMapping("/api/acct/payments")
    public ResponseEntity uploadPayroll(@RequestBody ArrayList<Salary> salaries){
        boolean marker = salaryEngine.checkSalaryBeforeInsert(salaries);
        if(marker){
            CustomSuccessMessage successMessage = new CustomSuccessMessage();
            successMessage.setStatus("Added successfully!");

            return ResponseEntity.status(HttpStatus.OK)
                    .body(successMessage);

        }
        return null;
    }

    @PutMapping("/api/acct/payments")
    public ResponseEntity updateInfo(@RequestBody Salary salary){
        boolean marker = salaryEngine.checkSalaryBeforeUpdate(salary);
        if(marker) {
            CustomSuccessMessage successMessage = new CustomSuccessMessage();
            successMessage.setStatus("Updated successfully!");

            return ResponseEntity.status(HttpStatus.OK)
                    .body(successMessage);
        }
        return null;
    }


    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ResponseEntity<CustomErrorMessage> handleException(BusinessException e, WebRequest request){
        CustomErrorMessage body = new CustomErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                "Bad Request");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}
