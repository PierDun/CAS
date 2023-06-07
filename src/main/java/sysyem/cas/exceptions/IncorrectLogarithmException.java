package sysyem.cas.exceptions;

public class IncorrectLogarithmException extends Throwable {
    public IncorrectLogarithmException(String errorMsg) {
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