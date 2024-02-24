package account.controller;

import account.model.Auth;
import account.service.BusinessException;
import account.model.ChangedPassword;
import account.response.CustomErrorMessage;
import account.service.AuthEngine;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;


@Validated
@RestController
public class AuthController {
    private final AuthEngine authEngine;

    public AuthController(AuthEngine authEngine){
        this.authEngine = authEngine;
    }

    @PostMapping("/api/auth/signup")
    public ResponseEntity<Object> signup(@Valid @RequestBody Auth auth){
        return authEngine.newUserRegistration(auth);
    }

    @PostMapping("/api/auth/changepass")
    public ResponseEntity<Object> changePass(@Valid @RequestBody ChangedPassword changedPassword){
        return authEngine.changePass(changedPassword);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity handleException(Exception e, WebRequest request){
        CustomErrorMessage body = new CustomErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                "Password length must be 12 chars minimum!",
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                "Bad Request");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<CustomErrorMessage> handleException(BusinessException e, WebRequest request){
        CustomErrorMessage body = new CustomErrorMessage(
                HttpStatus.BAD_REQUEST.value(),
                e.getMessage(),
                ((ServletWebRequest)request).getRequest().getRequestURI().toString(),
                "Bad Request");

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}