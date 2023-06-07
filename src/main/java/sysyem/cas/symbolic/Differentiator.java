package sysyem.cas.symbolic;

import sysyem.cas.expression.Actions;
import sysyem.cas.expression.Expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Differentiator {
    public Expression differentiate (Expression expression) {
        Expression dif = new Expression();
        if (isAction(expression)) {
            switch (Actions.valueOf(expression.head)) {
                case PLUS -> {
                    dif.head = "PLUS";
                    for (Expression arg: expression.arguments) {
                        Expression expression1 = new Expression("DIF");
                        expression1.arguments.add(arg);
                        dif.arguments.add(expression1);
                    }
                }
                case MINUS -> {
                    dif.head = "MINUS";
                    for (Expression arg: expression.arguments) {
                        Expression expression1 = new Expression("DIF");
                        expression1.arguments.add(arg);
                        dif.arguments.add(expression1);
                    }
                }
                case MULTIPLY -> {
                    dif.head = "PLUS";
                    int a = expression.arguments.size();
                    for (int i = 0; i < a; i++) {
                        Expression expression1 = new Expression("MULTIPLY");

                        for (int j = 0; j < a; j++) {
                            if (i == j) {
                                Expression expression2 = new Expression("DIF");
                                expression2.arguments.add(expression.arguments.get(j));
                                expression1.arguments.add(expression2);
                            } else {
                                expression1.arguments.add(expression.arguments.get(j));
                            }
                        }
                        dif.arguments.add(expression1);
                    }
                }
                case DIVIDE -> {
                    dif.head = "DIVIDE";
                    dif.arguments.add(new Expression());
                    dif.arguments.get(0).head = "MINUS";

                    dif.arguments.get(0).arguments.add(new Expression("MULTIPLY"));
                    Expression expression1 = new Expression("DIF");
                    expression1.arguments.add(expression.arguments.get(0));
                    dif.arguments.get(0).arguments.get(0).arguments.add(expression1);
                    dif.arguments.get(0).arguments.get(0).arguments.add(expression.arguments.get(1));

                    dif.arguments.get(0).arguments.add(new Expression("MULTIPLY"));
                    Expression expression2 = new Expression("DIF");
                    expression2.arguments.add(expression.arguments.get(1));
                    dif.arguments.get(0).arguments.get(1).arguments.add(expression2);
                    dif.arguments.get(0).arguments.get(1).arguments.add(expression.arguments.get(0));

                    Expression argument2 = new Expression("POWER",
                            new ArrayList<>(Arrays.asList(expression.arguments.get(1), new Expression("2"))));
                    /*argument2.arguments.add(expression.arguments.get(1));
                    argument2.arguments.add(new Expression("2"));*/
                    dif.arguments.add(argument2);
                }
                case SIN -> {
                    dif.head = "MULTIPLY";
                    dif.arguments.add(new Expression("COS"));
                    dif.arguments.get(0).arguments.add(expression.arguments.get(0));

                    Expression expression1 = new Expression("DIF");
                    expression1.arguments.add(expression.arguments.get(0));
                    dif.arguments.add(expression1);
                }
                case COS -> {
                    dif.head = "MINUS";
                    dif.arguments.add(new Expression("0"));

                    dif.arguments.add(new Expression("MULTIPLY"));

                    dif.arguments.get(1).arguments.add(new Expression("SIN"));
                    dif.arguments.get(1).arguments.get(0).arguments.add(expression.arguments.get(0));

                    Expression expression1 = new Expression("DIF");
                    expression1.arguments.add(expression.arguments.get(0));
                    dif.arguments.get(1).arguments.add(expression1);
                }
                case TAN -> {
                    dif.head = "DIVIDE";
                    Expression expression1 = new Expression("DIF");
                    expression1.arguments.add(expression.arguments.get(0));
                    dif.arguments.add(expression1);

                    dif.arguments.add(new Expression("POWER"));

                    dif.arguments.get(1).arguments.add(new Expression("COS"));
                    dif.arguments.get(1).arguments.get(0).arguments.add(expression.arguments.get(0));

                    dif.arguments.get(1).arguments.add(new Expression("2"));
                }
                case CTG -> {
                    dif.head = "MINUS";

                    dif.arguments.add(new Expression("0"));

                    dif.arguments.add(new Expression("DIVIDE"));

                    Expression expression1 = new Expression("DIF");
                    expression1.arguments.add(expression.arguments.get(0));
                    dif.arguments.get(1).arguments.add(expression1);

                    dif.arguments.get(1).arguments.add(new Expression("POWER"));

                    dif.arguments.get(1).arguments.get(1).arguments.add(new Expression("SIN"));
                    dif.arguments.get(1).arguments.get(1).arguments.get(0).arguments.add(expression.arguments.get(0));

                    dif.arguments.get(1).arguments.get(1).arguments.add(new Expression("2"));
                }
                case LOG -> {
                    dif.head = "DIVIDE";

                    dif.arguments.add(new Expression("MINUS"));

                    dif.arguments.get(0).arguments.add(new Expression("MULTIPLY"));

                    dif.arguments.get(0).arguments.get(0).arguments.add(new Expression("DIVIDE"));
                    Expression expression1 = new Expression("DIF");
                    expression1.arguments.add(expression.arguments.get(0));
                    dif.arguments.get(0).arguments.get(0).arguments.get(0).arguments.add(expression1);
                    dif.arguments.get(0).arguments.get(0).arguments.get(0).arguments.add(expression.arguments.get(0));

                    dif.arguments.get(0).arguments.get(0).arguments.add(new Expression("LN"));
                    dif.arguments.get(0).arguments.get(0).arguments.get(1).arguments.add(expression.arguments.get(1));

                    dif.arguments.get(0).arguments.add(new Expression("MULTIPLY"));

                    dif.arguments.get(0).arguments.get(1).arguments.add(new Expression("DIVIDE"));
                    Expression expression2 = new Expression("DIF");
                    expression2.arguments.add(expression.arguments.get(1));
                    dif.arguments.get(0).arguments.get(1).arguments.get(0).arguments.add(expression2);
                    dif.arguments.get(0).arguments.get(1).arguments.get(0).arguments.add(expression.arguments.get(1));

                    dif.arguments.get(0).arguments.get(1).arguments.add(new Expression("LN"));
                    dif.arguments.get(0).arguments.get(1).arguments.get(1).arguments.add(expression.arguments.get(0));

                    dif.arguments.add(new Expression("POWER"));

                    dif.arguments.get(1).arguments.add(new Expression("LN"));
                    dif.arguments.get(1).arguments.get(0).arguments.add(expression.arguments.get(1));

                    dif.arguments.get(1).arguments.add(new Expression("2"));
                }
                case LN -> {
                    dif.head = "DIVIDE";
                    Expression expression1 = new Expression("DIF");
                    expression1.arguments.add(expression.arguments.get(0));
                    dif.arguments.add(expression1);
                    dif.arguments.add(expression.arguments.get(0));
                }
                case POWER -> {
                    dif.head = "MULTIPLY";
                    dif.arguments.add(new Expression("POWER"));
                    dif.arguments.get(0).arguments.add(expression.arguments.get(0));
                    dif.arguments.get(0).arguments.add(expression.arguments.get(1));

                    dif.arguments.add(new Expression("PLUS"));

                    dif.arguments.get(1).arguments.add(new Expression("MULTIPLY"));
                    Expression expression1 = new Expression("DIF");
                    expression1.arguments.add(expression.arguments.get(1));
                    dif.arguments.get(1).arguments.get(0).arguments.add(expression1);

                    dif.arguments.get(1).arguments.get(0).arguments.add(new Expression("LN"));
                    dif.arguments.get(1).arguments.get(0).arguments.get(1).arguments.add(expression.arguments.get(0));

                    dif.arguments.get(1).arguments.add(new Expression("DIVIDE"));

                    dif.arguments.get(1).arguments.get(1).arguments.add(new Expression("MULTIPLY"));
                    dif.arguments.get(1).arguments.get(1).arguments.get(0).arguments.add(expression.arguments.get(1));
                    Expression expression2 = new Expression("DIF");
                    expression2.arguments.add(expression.arguments.get(0));
                    dif.arguments.get(1).arguments.get(1).arguments.get(0).arguments.add(expression2);

                    dif.arguments.get(1).arguments.get(1).arguments.add(expression.arguments.get(0));
                }
            }
        } else {
            if (Objects.equals(expression.head, "X"))
                dif.head = "1";
            if (isNumber(expression))
                dif.head = "0";
        }
        return dif;
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