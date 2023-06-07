package sysyem.cas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sysyem.cas.evaluator.Evaluator;
import sysyem.cas.exceptions.ArgumentsAmountException;
import sysyem.cas.exceptions.DividingByZeroException;
import sysyem.cas.exceptions.IncorrectExpressionException;
import sysyem.cas.exceptions.IncorrectLogarithmException;
import sysyem.cas.expression.Expression;
import sysyem.cas.symbolic.Simplifier;
import sysyem.cas.validator.Validator;

@RestController
@RequestMapping("/api")
public class ExpressionController {
    @PostMapping(value = "/calculate", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> calculate (@RequestBody Expression expression) {
        try {
            Validator validator = new Validator();
            validator.validate(expression);

            Evaluator evaluator = new Evaluator();
            Expression result = evaluator.evaluate(expression);

            Simplifier simp = new Simplifier();
            simp.simplify(result);

            return new ResponseEntity<>(evaluator.trace, HttpStatus.OK);
        } catch (IncorrectLogarithmException e) {
            return new ResponseEntity<>(e.getErrorMsg(), HttpStatus.BAD_REQUEST);
        } catch (DividingByZeroException e) {
            return new ResponseEntity<>(e.getErrorMsg(), HttpStatus.BAD_REQUEST);
        } catch (ArgumentsAmountException e) {
            return new ResponseEntity<>(e.getErrorMsg(), HttpStatus.BAD_REQUEST);
        } catch (IncorrectExpressionException e) {
            return new ResponseEntity<>(e.getErrorMsg(), HttpStatus.BAD_REQUEST);
        }
    }
}