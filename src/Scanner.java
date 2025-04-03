package src;

import static src.TokenType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Scanner {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int currentIndex = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd(currentIndex)) {
            start = currentIndex;
            scanToken();
        }

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd(int position) {
        return position >= source.length();
    }

    void scanToken() {
        char charBeingConsumed = peek();
        currentIndex++;
        switch (charBeingConsumed) {
            case '(':
                addToken(LEFT_PAREN, null);
                break;
            case ')':
                addToken(RIGHT_PAREN, null);
                break;
            case '{':
                addToken(LEFT_BRACE, null);
                break;
            case '}':
                addToken(RIGHT_BRACE, null);
                break;
            case ',':
                addToken(COMMA, null);
                break;
            case '.':
                addToken(DOT, null);
                break;
            case '-':
                addToken(MINUS, null);
                break;
            case '+':
                addToken(PLUS, null);
                break;
            case ';':
                addToken(SEMICOLON, null);
                break;
            case '*':
                addToken(STAR, null);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG, null);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER, null);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS, null);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL, null);
                break;
            case '"':
                string();
                break;
            case '/':
                if (match('/')) {
                    comment();
                } else {
                    addToken(SLASH, null);
                }
                break;
            case ' ':
            case '\t':
            case '\r':
                break;
            case '\n':
                line++;
                break;
            default:
                if (Character.isDigit(charBeingConsumed)) {
                    number();
                    break;
                }

                Lox.error(line, "Unexpected character.");
                break;
        }
    }

    private void number() {
        while (Character.isDigit(peek())) {
            currentIndex++;
        }
        if (peek() == '.' && Character.isDigit(peekNext())) {
            currentIndex++;
            while (Character.isDigit(peek())) {
                currentIndex++;
            }
        }

        String lexeme = source.substring(start, currentIndex);
        Double value = Double.valueOf(lexeme);
        tokens.add(new Token(NUMBER, lexeme, value, line));
    }

    private char peek() {
        if (isAtEnd(currentIndex))
            return '\0'; // return null
        return source.charAt(currentIndex);
    }

    private char peekNext() {
        if (isAtEnd(currentIndex + 1))
            return '\0'; // return null
        return source.charAt(currentIndex + 1);
    }

    private void comment() {
        while (!isAtEnd(currentIndex) && peek() != '\n') {
            currentIndex++;
        }
    }

    private boolean match(char expected) {
        if (isAtEnd(currentIndex))
            return false;

        var currentChar = peek();
        if (currentChar != expected)
            return false;

        currentIndex++;
        return true;
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, currentIndex);
        tokens.add(new Token(type, lexeme, literal, line));
    }

    private void string() {
        while (!match('"')) {
            if (isAtEnd(currentIndex)) {
                Lox.error(line, "String not finished with \"");
                return;
            } else if (peek() == '\n') {
                line++;
            }

            currentIndex++;
        }

        String value = source.substring(start + 1, currentIndex - 1);
        // current++;

        // tokens.add(new Token(STRING, text, text, line));
        addToken(STRING, value);
    }
}