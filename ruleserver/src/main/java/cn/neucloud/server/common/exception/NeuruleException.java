
package cn.neucloud.server.common.exception;

public class NeuruleException extends Exception {

    private static final long serialVersionUID = 1L;

    private NeuruleErrorCode errorCode;

    public NeuruleException() {
        super();
    }

    public NeuruleException(NeuruleErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public NeuruleException(String message, NeuruleErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public NeuruleException(String message, Throwable cause, NeuruleErrorCode errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public NeuruleException(Throwable cause, NeuruleErrorCode errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    public NeuruleErrorCode getErrorCode() {
        return errorCode;
    }

}
