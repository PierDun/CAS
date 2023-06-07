package sysyem.cas.validator;

import sysyem.cas.exceptions.ArgumentsAmountException;
import sysyem.cas.exceptions.DividingByZeroException;
import sysyem.cas.exceptions.IncorrectExpressionException;
import sysyem.cas.exceptions.IncorrectLogarithmException;
import sysyem.cas.expression.Actions;
import sysyem.cas.expression.Expression;

public class Validator {
    public void validate (Expression expression) throws DividingByZeroException, IncorrectLogarithmException, ArgumentsAmountException, IncorrectExpressionException {
        if (isAction(expression)) {
            switch (Actions.valueOf(expression.head)) {
                case PLUS, MINUS, MULTIPLY -> {
                    if (expression.arguments.size() < 2)
                        throw new ArgumentsAmountException("Operation of " + expression.head + " must contain 2 or more arguments");
                }
                case DIVIDE -> {
                    if (expression.arguments.size() != 2)
                        throw new ArgumentsAmountException("Operation of " + expression.head + " can contain only 2 arguments");

                    if (isNumber(expression.arguments.get(1))) {
                        double base = Double.parseDouble(expression.arguments.get(1).head);
                        if (base == 0)
                            throw new DividingByZeroException ("Dividing by zero is not allowed");
                    }
                }
                case SIN, COS, TAN, CTG -> {
                    if (expression.arguments.size() != 1)
                        throw new ArgumentsAmountException("Operation of " + expression.head + " can contain only 1 argument");
                }
                case LN -> {
                    if (expression.arguments.size() != 1)
                        throw new ArgumentsAmountException("Operation of " + expression.head + " can contain only 1 argument");

                    if (isNumber(expression.arguments.get(0))) {
                        double base = Double.parseDouble(expression.arguments.get(0).head);
                        if (base <= 0)
                            throw new IncorrectLogarithmException ("Can't evaluate " + expression.head + " of this number: " + expression.arguments.get(0).head);
                    }
                }
                case LOG -> {
                    if (expression.arguments.size() != 2)
                        throw new ArgumentsAmountException("Operation of " + expression.head + " can contain only 2 arguments");

                    if (isNumber(expression.arguments.get(0))) {
                        double base = Double.parseDouble(expression.arguments.get(0).head);
                        if (base <= 0)
                            throw new IncorrectLogarithmException ("Can't evaluate " + expression.head + " of this number: " + expression.arguments.get(0).head);
                    }

                    if (isNumber(expression.arguments.get(1))) {
                        double base = Double.parseDouble(expression.arguments.get(1).head);
                        if (base <= 0 || base == 1)
                            throw new IncorrectLogarithmException ("Can't evaluate " + expression.head + " with this base: " + expression.arguments.get(1).head);
                    }
                }
            }
        } else {
            if (isNumber(expression)) {
                if (!expression.arguments.isEmpty())
                    throw new IncorrectExpressionException("Number can't have arguments");
            }
        }

        for (Expression arg: expression.arguments) {
            validate(arg);
        }
    }

    private boolean isAction (Expression expression) {
        for (Actions type : Actions.values()) {
            if (type.name().equals(expression.head))
                return true;
        }
        return false;
    }

    private boolean isNumber (Expression expression) {
        try {
            Double.valueOf(expression.head);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}