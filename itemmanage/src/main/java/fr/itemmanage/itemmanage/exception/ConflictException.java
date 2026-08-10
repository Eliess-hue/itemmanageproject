package fr.itemmanage.itemmanage.exception;

public class ConflictException extends BusinessRuleException {
    public ConflictException(String message) {
        super(message);
    }
}