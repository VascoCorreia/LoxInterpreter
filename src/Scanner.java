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
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd(current)) {
            start = current;
            scanToken();
        }
        peek();
        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd(int position) {
        return position >= source.length();
    }

    void scanToken() {
        char charBeingConsumed = peek();
        current++;
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
                if (match('/')){
                    comment();
                } else{
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
                if(Character.isDigit(charBeingConsumed)){
                     while (!isAtEnd(current) && (Character.isDigit(peek()) || peek() == '.')){    
                        current++;
                    } 
                    String lexeme = source.substring(start, current);
                    Double value = Double.valueOf(lexeme);
                    tokens.add(new Token(NUMBER, lexeme, value, line));
                    break;
                }
                Lox.error(line, "Unexpected character.");
                break;
        }
    }

    private char peek() {
        if(isAtEnd(current)) return '\0';
        return source.charAt(current);
    }

    private void comment() {
        while(!isAtEnd(current) && peek() != '\n'){
            current++;
        }
    }

    private boolean match(char expected) {
        if (isAtEnd(current))
            return false;

        var currentChar = peek();
        if (currentChar != expected)
            return false;

        current++;
        return true;
    }

    private void addToken(TokenType type, Object literal) {
        String lexeme = source.substring(start, current);
        tokens.add(new Token(type, lexeme, literal, line));
    }

    private void string(){
        while(!match('"')){
            if(isAtEnd(current)){
                Lox.error(line, "String not finished with \"");
                return;  
            } else if (peek() == '\n'){
                line++;
            }

            current++;
        }

        String value = source.substring(start + 1, current - 1);
        //current++;

        //tokens.add(new Token(STRING, text, text, line));
        addToken(STRING, value);
    }
}