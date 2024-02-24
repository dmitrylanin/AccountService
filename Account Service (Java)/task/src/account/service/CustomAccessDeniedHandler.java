package account.service;

import account.response.CustomErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static account.entity.Action.ACCESS_DENIED;

@Configurable
public class CustomAccessDeniedHandler implements AccessDeniedHandler{

    @Autowired
    private AuditActionsWriter auditActionsWriter;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException{

        AppUserAdapter appUser = (AppUserAdapter) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        auditActionsWriter.auditActionNotice(
                ACCESS_DENIED,
                appUser.getUsername(),
                request.getRequestURI(),
                request.getRequestURI()
        );

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String json = ow.writeValueAsString(new CustomErrorMessage(
                HttpStatus.FORBIDDEN.value(),
                "Access Denied!",
                request.getRequestURI(),
                "Forbidden"));

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(json);
    }
}