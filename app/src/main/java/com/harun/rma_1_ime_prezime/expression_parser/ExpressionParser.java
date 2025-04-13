package com.harun.rma_1_ime_prezime.expression_parser;

public class ExpressionParser {
    private String in;
    private int pos;

    public double parse(String s) throws Exception {
        in = s.replaceAll("\\s+","");
        pos = 0;
        double v = parseExpr();
        if (pos < in.length()) throw new Exception("Unexpected: " + in.charAt(pos));
        return v;
    }

    private double parseExpr() throws Exception {
        double x = parseTerm();
        while (true) {
            if      (match('+')) x += parseTerm();
            else if (match('-')) x -= parseTerm();
            else return x;
        }
    }

    private double parseTerm() throws Exception {
        double x = parseFactor();
        while (true) {
            if (match('*')) x *= parseFactor();
            else if (match('/')) {
                double divisor = parseFactor();
                if (divisor == 0) throw new Exception("Cannot divide by zero");
                x /= divisor;
            }
            else if (match('%')) x %= parseFactor();
            else return x;
        }
    }

    private double parseFactor() throws Exception {
        if (match('+')) return parseFactor();
        if (match('-')) return -parseFactor();

        double x;
        if (match('(')) {
            x = parseExpr();
            if (!match(')')) throw new Exception("Missing ')'");
        } else if (isLetter(peek())) {
            String fn = parseIdent();
            if (!match('(')) throw new Exception("Missing '(' after " + fn);
            double arg = parseExpr();
            if (!match(')')) throw new Exception("Missing ')' after argument");
            x = applyFunc(fn, arg);
        } else if (isDigit(peek()) || peek()=='.') {
            int start = pos;
            while (isDigit(peek()) || peek()=='.') pos++;
            x = Double.parseDouble(in.substring(start, pos));
        } else {
            throw new Exception("Unexpected: " + peek());
        }

        if (match('^')) x = Math.pow(x, parseFactor());
        return x;
    }

    private boolean match(char c) {
        if (peek()==c) { pos++; return true; }
        return false;
    }
    private char peek() {
        return pos < in.length() ? in.charAt(pos) : '\0';
    }
    private boolean isDigit(char c) { return c>='0' && c<='9'; }
    private boolean isLetter(char c) { return (c>='a'&&c<='z')||(c>='A'&&c<='Z'); }
    private String parseIdent() {
        int st = pos;
        while (isLetter(peek())) pos++;
        return in.substring(st, pos);
    }
    private double applyFunc(String f, double v) throws Exception {
        switch (f) {
            case "sin": return Math.sin(Math.toRadians(v));
            case "cos": return Math.cos(Math.toRadians(v));
            case "tan": return Math.tan(Math.toRadians(v));
            case "log": return Math.log10(v);
            case "sqrt":   return Math.sqrt(v);
            default: throw new Exception("Unknown func: " + f);
        }
    }
}
