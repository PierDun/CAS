package sysyem.cas.evaluator;

import sysyem.cas.expression.Expression;
import sysyem.cas.symbolic.Differentiator;
import sysyem.cas.symbolic.Simplifier;

import java.util.ArrayList;
import java.util.Objects;

public class Evaluator {
    public ArrayList<Expression> trace = new ArrayList<>();

    public Expression evaluate (Expression expression) {
        Expression before = (Expression) expression.clone();
        Expression result = expression;

        Simplifier simplifier = new Simplifier();
        result = simplifier.simplify(result);

        if (!Objects.equals(before.toString(), result.toString())) {
            trace.add(before);
            trace.add((Expression) result.clone());
            result = evaluate(result);
        }

        if (!result.arguments.isEmpty()) {
            before = (Expression) result.clone();
            result.arguments.replaceAll(this::evaluate);

            if (!Objects.equals(before.toString(), result.toString())) {
                trace.add(before);
                trace.add((Expression) result.clone());
            }
        }

        if (Objects.equals(expression.head, "DIF")) {
            before = (Expression) result.clone();
            Differentiator dif = new Differentiator();
            result = dif.differentiate(expression.arguments.get(0));

            if (!Objects.equals(before.toString(), result.toString())) {
                trace.add(before);
                trace.add((Expression) result.clone());
            }
            result = evaluate(result);
        }

        before = (Expression) result.clone();
        result = simplifier.simplify(result);

        if (!Objects.equals(before.toString(), result.toString())) {
            trace.add(before);
            trace.add((Expression) result.clone());
            result = evaluate(result);
        }

        return result;
    }
}