package sysyem.cas.exceptions;

public class DividingByZeroException extends Throwable {
    public DividingByZeroException (String errorMsg) {
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