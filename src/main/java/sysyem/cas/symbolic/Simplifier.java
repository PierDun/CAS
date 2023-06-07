package sysyem.cas.symbolic;

import sysyem.cas.expression.Actions;
import sysyem.cas.expression.Expression;

import java.util.Objects;

public class Simplifier {
    public Expression simplify (Expression expression) {
        Expression result = new Expression();
        if (isAction(expression)) {
            switch (Actions.valueOf(expression.head)) {
                case PLUS -> {
                    result.head = "PLUS";

                    for (Expression arg: expression.arguments) {
                        if (!Objects.equals(arg.head, "0")) {
                            result.arguments.add(arg);
                        }
                    }

                    if (result.arguments.size() == 1) {
                        result.head = result.arguments.get(0).head;
                        result.arguments = result.arguments.get(0).arguments;
                    }
                }

                case MINUS -> {
                    result.head = "MINUS";

                    if (Objects.equals(expression.arguments.get(0).head, "0")) {
                        result.arguments.add(expression.arguments.get(0));
                    }

                    for (Expression arg: expression.arguments) {
                        if (!Objects.equals(arg.head, "0")) {
                            result.arguments.add(arg);
                        }
                    }

                    if (result.arguments.size() == 1) {
                        result.head = result.arguments.get(0).head;
                        result.arguments = result.arguments.get(0).arguments;
                    }
                }

                case MULTIPLY -> {
                    result.head = "MULTIPLY";

                    for (Expression arg: expression.arguments) {
                        if (!Objects.equals(arg.head, "1")) {
                            result.arguments.add(arg);
                        }
                    }

                    for (Expression arg: expression.arguments) {
                        if (Objects.equals(arg.head, "0")) {
                            result.arguments.clear();
                            result.head = "0";
                            break;
                        }
                    }

                    if (result.arguments.size() == 1) {
                        result.head = result.arguments.get(0).head;
                        result.arguments = result.arguments.get(0).arguments;
                    }
                }

                case DIVIDE -> {
                    result = expression;
                    if (Objects.equals(expression.arguments.get(1).head, "1")) {
                        result.head = expression.arguments.get(0).head;
                        result.arguments = expression.arguments.get(0).arguments;
                        break;
                    }

                    if (Objects.equals(expression.arguments.get(0).head, "0")) {
                        result.head = "0";
                        result.arguments.clear();
                    }

                    if (result.arguments.size() == 1) {
                        result.head = result.arguments.get(0).head;
                        result.arguments.clear();
                    }
                }

                case SIN, TAN -> {
                    result = expression;
                    if (Objects.equals(expression.arguments.get(0).head, "0")) {
                        result.head = "0";
                        result.arguments.clear();
                    }
                }

                case COS -> {
                    result = expression;
                    if (Objects.equals(expression.arguments.get(0).head, "0")) {
                        result.head = "1";
                        result.arguments.clear();
                    }
                }

                case POWER -> {
                    result = expression;
                    if (Objects.equals(expression.arguments.get(1).head, "1")) {
                        result.head = expression.arguments.get(0).head;
                        result.arguments = expression.arguments.get(0).arguments;
                        break;
                    }

                    if (Objects.equals(expression.arguments.get(1).head, "0")) {
                        result.head = "1";
                        result.arguments.clear();
                    }

                }

                case LN, LOG -> {
                    result = expression;
                    if (Objects.equals(expression.arguments.get(0).head, "1")) {
                        result.head = "0";
                        result.arguments.clear();
                    }
                }

                default -> {
                    return expression;
                }
            }
        } else {
            return expression;
        }

        return result;
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