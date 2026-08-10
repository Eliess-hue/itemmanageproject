package fr.itemmanage.itemmanage.exception;

public class InvalidRequestException extends BusinessRuleException {
    public InvalidRequestException(String message) {
        super(message);
    }
}