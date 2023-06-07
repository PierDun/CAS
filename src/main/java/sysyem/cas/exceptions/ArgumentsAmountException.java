package sysyem.cas.exceptions;

public class ArgumentsAmountException extends Throwable{
    public ArgumentsAmountException(String errorMsg) {
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