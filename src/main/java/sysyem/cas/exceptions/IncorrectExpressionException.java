package sysyem.cas.exceptions;

public class IncorrectExpressionException extends Throwable {
    public IncorrectExpressionException(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    private String errorMsg;

    public void setErrorMsg (String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getErrorMsg () {
        return errorMsg;
    }
}
