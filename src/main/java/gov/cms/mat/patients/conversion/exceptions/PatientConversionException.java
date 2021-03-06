package gov.cms.mat.patients.conversion.exceptions;

public class PatientConversionException extends RuntimeException {
    public PatientConversionException(String message) {
        super(message);
    }

    public PatientConversionException(String message, Throwable cause) {
        super(message, cause);
    }
}
