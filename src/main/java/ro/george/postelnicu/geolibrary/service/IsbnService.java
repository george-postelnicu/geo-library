package ro.george.postelnicu.geolibrary.service;

import org.springframework.stereotype.Service;
import ro.george.postelnicu.geolibrary.exception.EntityValidationException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ro.george.postelnicu.geolibrary.model.EntityName.BOOK;

@Service
public class IsbnService {
    private final Pattern pattern;

    public IsbnService() {
        pattern = Pattern.compile("^(?:ISBN(?:-1[03])?:? )?(?=[-0-9 ]{17}$|[-0-9X ]{13}$|[0-9X]{10}$)(?:97[89][- ]?)?[0-9]{1,5}[- ]?(?:[0-9]+[- ]?){2}[0-9X]$");
    }

    public void isValid(String subject) {
        Matcher matcher = pattern.matcher(subject);

        if (matcher.matches()) {
            if (subject.contains("ISBN-13") || subject.contains("ISBN-10")) {
                subject = subject.substring("ISBN-13".length());
            }
            String[] chars = subject.replaceAll("[^0-9X]", "").split("");
            String last = chars[chars.length - 1];
            int sum = 0;
            int digit = 10;
            String check;

            if (chars.length == 10) {
                // Compute the ISBN-10 check digit
                for (int i = 0; i < chars.length - 1; i++) {
                    sum += digit * Integer.parseInt(chars[i]);
                    digit -= 1;
                }
                int remainder = 11 - (sum % 11);
                if (remainder == 10) {
                    check = "X";
                } else if (remainder == 11) {
                    check = "0";
                } else {
                    check = String.valueOf(remainder);
                }
            } else {
                // Compute the ISBN-13 check digit
                for (int i = 0; i < chars.length - 1; i++) {
                    sum += ((i % 2) * 2 + 1) * Integer.parseInt(chars[i]);
                }
                int remainder = sum % 10;
                check = (remainder == 0) ? "0" : String.valueOf(10 - remainder);
            }

            if (!check.equals(last)) {
                throw new EntityValidationException(BOOK, "Invalid ISBN check digit");
            }
        } else {
            throw new EntityValidationException(BOOK, "Invalid ISBN");
        }
    }
}