package com.lkgroup.ecommerce.common.validation.validators;

import com.zliio.disposable.Disposable;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.xbill.DNS.MXRecord;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Type;
import org.xbill.DNS.lookup.LookupFailedException;
import org.xbill.DNS.lookup.LookupResult;
import org.xbill.DNS.lookup.LookupSession;
import com.lkgroup.ecommerce.common.validation.support.ApplicationContextHolder;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

//@Component
public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

    private Optional<LookupSession> lookupSession = Optional.empty();

    public EmailValidator() {
    }

    public EmailValidator(LookupSession lookupSession) {
        this.lookupSession = Optional.of(lookupSession);
    }

    private static final Logger logger = LoggerFactory.getLogger(EmailValidator.class);
    public static final Pattern EMAIL_REGEX = Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])", Pattern.CASE_INSENSITIVE);

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if (email == null || email.isBlank()) {
            return true;
        }

        if (!EMAIL_REGEX.matcher(email).matches()) {
            return false;
        }

        Disposable disposable = new Disposable();

        int lastIndex = email.lastIndexOf("@");
        String domain = email;
        if (lastIndex >= 0) {
            domain = domain.substring(lastIndex + 1);
        }

        if (!disposable.validateDomain(domain)) {
            if (context != null) {
                context
                        .buildConstraintViolationWithTemplate("{restricted.email.domain}")
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
            }
            return false;
        }

        try {
            if (!validateEmailMX(domain))
                return false;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (LookupFailedException e) {
            return false;
        } catch (Throwable e) {
            logger.error("Unhandled exception validating email MX record", e);
        }

        return true;
    }

    private boolean validateEmailMX(String domain) throws Throwable {

        //Validate the email MX server exists
        if (lookupSession == null)
            lookupSession = ApplicationContextHolder.tryGetBean(LookupSession.class);
        if (lookupSession.isEmpty()) {
            logger.error("Expected LookupSession bean to be available. Unable to validate email MX record!");
            return true;
        }

        Name mxLookup = Name.fromString(domain);
        LookupResult lookupResult;
        try {
            lookupResult = lookupSession.get().lookupAsync(mxLookup, Type.MX)
                    .toCompletableFuture()
                    .get();
        } catch (ExecutionException e) {
            throw e.getCause();
        }

        if (logger.isDebugEnabled()) {
            if (lookupResult.getRecords().isEmpty()) {
                logger.debug("Invalid email domain: {} has no MX record", domain);
            } else {
                for (Record rec : lookupResult.getRecords()) {
                    MXRecord mx = ((MXRecord) rec);
                    logger.debug("Valid email domain: {} has MX record: {} with preference: {}", domain, mx.getTarget(), mx.getPriority());
                }
            }
        }

        return !lookupResult.getRecords().isEmpty();
    }
}
