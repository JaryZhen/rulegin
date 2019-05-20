
package com.github.rulegin.common.exception;

public class RuleginException extends Exception {

    private static final long serialVersionUID = 1L;

    private RuleginErrorCode errorCode;

    public RuleginException() {
        super();
    }

    public RuleginException(RuleginErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public RuleginException(String message, RuleginErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public RuleginException(String message, Throwable cause, RuleginErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public RuleginException(Throwable cause, RuleginErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public RuleginErrorCode getErrorCode() {
        return errorCode;
    }

}
