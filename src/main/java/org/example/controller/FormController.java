package org.example.controller;

import org.example.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Controller
public class FormController {

    private static final Map<String, String> OPERATORS = new HashMap<>() {{
        put("067", "Kyivstar");
        put("068", "Kyivstar");
        put("096", "Kyivstar");
        put("097", "Kyivstar");
        put("098", "Kyivstar");
        put("050", "Vodafone Україна");
        put("066", "Vodafone Україна");
        put("095", "Vodafone Україна");
        put("099", "Vodafone Україна");
        put("063", "lifecell");
        put("073", "lifecell");
        put("093", "lifecell");
        put("091", "ТриМоб");
        put("092", "PEOPLEnet");
        put("089", "Інтертелеком");
        put("094", "Інтертелеком");
    }};

    @GetMapping("/")
    public String showForm(Model model) {
        model.addAttribute("user", new User());
        return "form";
    }

    @PostMapping("/submit")
    public String submitForm(User user, Model model) {
        String phone = user.getPhone().replaceAll("\\D", "");
        String operator = getOperator(phone);
        boolean isEmailValid = isValidEmail(user.getEmail());
        String emailValidationMessage = isEmailValid ? "Email є дійсним." : "Email не є дійсним.";

        model.addAttribute("user", user);
        model.addAttribute("operator", operator);
        model.addAttribute("emailValidationMessage", emailValidationMessage);
        return "result";
    }

    @GetMapping("/api/operator/{phone}")
    public ResponseEntity<String> getOperatorByPhone(@PathVariable String phone) {
        String formattedPhone = phone.replaceAll("\\D", "");
        String operator = getOperator(formattedPhone);
        if (operator.equals("Невірний формат номера")) {
            return ResponseEntity.badRequest().body(operator);
        }
        return ResponseEntity.ok(operator);
    }

    private String getOperator(String phone) {
        phone = phone.replaceAll("\\D", "");

        if (phone.startsWith("380") && phone.length() == 12) {
            String code = phone.substring(2, 5);
            return OPERATORS.getOrDefault(code, "Невідомий оператор");
        } else if (phone.startsWith("0") && phone.length() == 10) {
            String code = phone.substring(0, 3);
            return OPERATORS.getOrDefault(code, "Невідомий оператор");
        } else if (phone.startsWith("+") && phone.length() == 13 && phone.startsWith("+380")) {
            String code = phone.substring(4, 7);
            return OPERATORS.getOrDefault(code, "Невідомий оператор");
        }

        return "Невірний формат номера";
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }
}
