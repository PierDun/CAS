package sysyem.cas.expression;

import java.util.ArrayList;
import java.util.Objects;

public class Expression {
    public String head;

    public ArrayList<Expression> arguments = new ArrayList<>();

    public Expression() {}

    public Expression (String head) {
        this.head = head;
    }

    public Expression (String head, ArrayList<Expression> arguments) {
        this.head = head;
        this.arguments = arguments;
    }

    @Override
    public Object clone() {
        Expression expression;
        try {
            expression = (Expression) super.clone();
        } catch (CloneNotSupportedException e) {
            if (!arguments.isEmpty()) {
                ArrayList<Expression> copy = new ArrayList<>(arguments.size());

                for (Expression arg: arguments) {
                    copy.add((Expression) arg.clone());
                }

                expression = new Expression(this.head, copy);
            } else
                expression = new Expression(this.head);
        }

        return expression;
    }

    public String toString() {
        String equation = "";
        if (isAction()) {
            switch (Actions.valueOf(head)) {
                case PLUS -> {
                    StringBuilder s = new StringBuilder("(");
                    for (Expression arg : arguments) {
                        s.append(arg.toString()).append(" + ");
                    }
                    s.delete(s.length() - 3, s.length());
                    s.append(")");
                    equation = s.toString();
                }
                case MINUS -> {
                    StringBuilder s = new StringBuilder("(");
                    for (Expression arg : arguments) {
                        s.append(arg.toString()).append(" - ");
                    }
                    s.delete(s.length() - 3, s.length());
                    s.append(")");
                    equation = s.toString();
                }
                case MULTIPLY -> {
                    StringBuilder s = new StringBuilder();
                    for (Expression arg : arguments) {
                        s.append(arg.toString()).append(" * ");
                    }
                    s.delete(s.length() - 3, s.length());
                    equation = s.toString();
                }
                case DIVIDE -> {
                    equation = arguments.get(0).toString() + " / (" +  arguments.get(1).toString() + ")";
                }
                case SIN -> {
                    equation = "sin(" + arguments.get(0).toString() + ")";
                }
                case COS -> {
                    equation = "cos(" + arguments.get(0).toString() + ")";
                }
                case TAN -> {
                    equation = "tan(" + arguments.get(0).toString() + ")";
                }
                case CTG -> {
                    equation = "ctg(" + arguments.get(0).toString() + ")";
                }
                case LOG -> {
                    equation = "log " + arguments.get(1).toString() + " " + arguments.get(0).toString();
                }
                case LN -> {
                    equation = "ln (" + arguments.get(0).toString() + ")";
                }
                case POWER -> {
                    equation = "(" + arguments.get(0).toString() + ") ^ (" + arguments.get(1).toString() + ")";
                }
            };
        } else if (Objects.equals(head, "DIF")) {
            equation = equation + "(" + arguments.get(0).toString() + ")'";
        }
        else equation = equation + head;
        return equation;
    }

    private boolean isAction () {
        for (Actions type : Actions.values()) {
            if (type.name().equals(head))
                return true;
        }
        return false;
    }
}