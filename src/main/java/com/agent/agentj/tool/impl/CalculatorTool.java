package com.agent.agentj.tool.impl;

import com.agent.agentj.tool.AgentTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Tool that evaluates arithmetic expressions like "347 * 19" or "(100 + 50) / 3".
 *
 * Implemented with a hand-written recursive descent parser so there are no
 * extra dependencies. This is the standard way to parse math expressions:
 *
 *   expression = term (('+' | '-') term)*
 *   term       = factor (('*' | '/') factor)*
 *   factor     = '(' expression ')' | '-' factor | number
 *
 * The three levels (expression → term → factor) encode operator precedence:
 * * and / bind tighter than + and -.
 */
@Slf4j
@Component
public class CalculatorTool implements AgentTool {

    @Override
    public String getName() {
        return "calculator";
    }

    @Override
    public String getDescription() {
        return "Evaluates a mathematical expression and returns the numeric result. " +
               "Supports +, -, *, / and parentheses. Example: '(100 + 50) * 3'.";
    }

    @Override
    public Map<String, Object> getInputSchema() {
        Map<String, Object> expression = new LinkedHashMap<>();
        expression.put("type", "string");
        expression.put("description", "The math expression to evaluate, e.g. '347 * 19' or '(10 + 5) / 3'");

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("expression", expression);

        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", List.of("expression"));
        return schema;
    }

    @Override
    public String execute(Map<String, Object> input) {
        String expression = (String) input.get("expression");
        log.info("[calculator] evaluating: {}", expression);
        try {
            double result = new Parser(expression).parse();
            // Return integer string when result is a whole number
            if (result == Math.floor(result) && !Double.isInfinite(result)) {
                return String.valueOf((long) result);
            }
            return String.valueOf(result);
        } catch (Exception e) {
            return "Error evaluating expression: " + e.getMessage();
        }
    }

    // -----------------------------------------------------------------------
    // Recursive descent parser
    // -----------------------------------------------------------------------

    private static class Parser {

        private final String input;
        private int pos;

        Parser(String input) {
            this.input = input.replaceAll("\\s+", ""); // strip whitespace
            this.pos = 0;
        }

        double parse() {
            double result = expression();
            if (pos < input.length()) {
                throw new IllegalArgumentException("Unexpected character at position " + pos + ": '" + input.charAt(pos) + "'");
            }
            return result;
        }

        /** expression = term (('+' | '-') term)* */
        private double expression() {
            double result = term();
            while (pos < input.length()) {
                char op = input.charAt(pos);
                if (op != '+' && op != '-') break;
                pos++;
                double right = term();
                result = (op == '+') ? result + right : result - right;
            }
            return result;
        }

        /** term = factor (('*' | '/') factor)* */
        private double term() {
            double result = factor();
            while (pos < input.length()) {
                char op = input.charAt(pos);
                if (op != '*' && op != '/') break;
                pos++;
                double right = factor();
                if (op == '/' && right == 0) throw new ArithmeticException("Division by zero");
                result = (op == '*') ? result * right : result / right;
            }
            return result;
        }

        /** factor = '(' expression ')' | '-' factor | number */
        private double factor() {
            if (pos < input.length() && input.charAt(pos) == '(') {
                pos++; // consume '('
                double result = expression();
                if (pos >= input.length() || input.charAt(pos) != ')') {
                    throw new IllegalArgumentException("Missing closing parenthesis");
                }
                pos++; // consume ')'
                return result;
            }
            if (pos < input.length() && input.charAt(pos) == '-') {
                pos++; // consume unary minus
                return -factor();
            }
            return number();
        }

        /** Reads a decimal number like 347 or 3.14 */
        private double number() {
            int start = pos;
            while (pos < input.length() && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.')) {
                pos++;
            }
            if (start == pos) {
                throw new IllegalArgumentException("Expected a number at position " + pos);
            }
            return Double.parseDouble(input.substring(start, pos));
        }
    }
}
