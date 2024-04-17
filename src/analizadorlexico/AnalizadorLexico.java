/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package analizadorlexico;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorLexico {

    private static int currentIndex = 0;

    public static void main(String[] args) {
        String sourceCode = """
            int x := 5;
            int y := 10;
            if x <= 10 {
                print "bfhjk";
                y := x + y;
            } else {
                for i := 1 to 5 {
                    print "k";
                }
            }
            """;

        // EXPRESIÓN IDENTIFICADORES:
        String identifierPattern = "[a-zA-Z][a-zA-Z0-9]{0,14}";
        // EXPRESIÓN CONSTANTES ENTERAS:
        String integerConstantPattern = "([1-9][0-9]{0,1}|100|0)";
        // EXPRESIÓN OPERADORES:
        String operatorPattern = "\\+|\\-|\\*|\\/|:=|>=|<=|>|<|=|<>|\\{|\\}|\\[|\\]|\\(|\\)|,|;|\\.\\.";
        // EXPRESIÓN CADENAS DE CARACTERES:
        String stringPattern = "\"[bfhjk]+\"";
        // EXPRESIÓN PALABRAS RESERVADAS:
        String keywordPattern = "\\b(if|else|for|print|int)\\b";

        String combinedPattern = String.format("(%s)|(%s)|(%s)|(%s)|(%s)",
                identifierPattern, integerConstantPattern, operatorPattern, stringPattern, keywordPattern);

        Pattern pattern = Pattern.compile(combinedPattern);
        Matcher matcher = pattern.matcher(sourceCode);

        Map<String, String> symbolTable = new HashMap<>();

        // Análisis léxico e impresión de tokens
        while (matcher.find()) {
            String token = matcher.group();
            if (token.matches(identifierPattern)) {
                if (token.matches(keywordPattern)) {
                    System.out.println("--Palabra reservada: " + token);
                    symbolTable.put(token, "palabra_reservada");
                } else {
                    System.out.println("--Identificador: " + token);
                    symbolTable.put(token, "identificador");
                }
            } else if (token.matches(integerConstantPattern)) {
                System.out.println("--Constante entera: " + token);
                symbolTable.put(token, "constante_numerica");
            } else if (token.matches(operatorPattern)) {
                System.out.println("--Operador: " + token);
            } else if (token.matches(stringPattern)) {
                System.out.println("--Cadena de caracteres: " + token);
            } else {
                System.out.println("--Token no reconocido: " + token);
            }
        }

        // Análisis sintáctico
        if (analizarPrograma(sourceCode)) {
            System.out.println("Análisis sintáctico exitoso.");
        } else {
            System.out.println("Error de sintaxis en la posición: " + currentIndex);
        }

        // Imprimir la tabla de símbolos
        System.out.println("\nTabla de símbolos:");
        for (Map.Entry<String, String> entry : symbolTable.entrySet()) {
            System.out.println("Nombre: " + entry.getKey() + ", Tipo: " + entry.getValue());
        }

        // Imprimir reglas de la gramática BNF
        System.out.println("\nGramática BNF:");
        System.out.println("<programa> ::= <declaracion> | <declaracion> <programa>");
        System.out.println("<declaracion> ::= <asignacion> | <estructura_control> | <impresion>");
        System.out.println("<asignacion> ::= <variable> \":=\" <expresion> \";\"");
        System.out.println("<estructura_control> ::= \"if\" <expresion> \"{\" <declaracion> \"}\" \"else\" \"{\" <declaracion> \"}\"");
        System.out.println("<impresion> ::= \"print\" <cadena_caracteres> \";\"");
        System.out.println("<expresion> ::= <expresion_aritmetica> | <constante> | <variable>");
        System.out.println("<expresion_aritmetica> ::= <expresion> <operador> <expresion>");
        System.out.println("<operador> ::= \"+\" | \"-\" | \"*\" | \"/\" | \":=\" | \">=\" | \"<=\" | \">\" | \"<\" | \"=\" | \"<>\"");
        System.out.println("<cadena_caracteres> ::= \"\\\"\" <caracteres> \"\\\"\"");
        System.out.println("<caracteres> ::= <caracter> | <caracter> <caracteres>");
        System.out.println("<caracter> ::= \"b\" | \"f\" | \"h\" | \"j\" | \"k\"");
        System.out.println("<variable> ::= <identificador>");
        System.out.println("<constante> ::= <entero>");
        System.out.println("<identificador> ::= [a-zA-Z][a-zA-Z0-9]{0,14}");
        System.out.println("<entero> ::= ([1-9][0-9]{0,1}|100|0)");
    }

    private static boolean analizarPrograma(String sourceCode) {
        currentIndex = 0;
        return programa(sourceCode);
    }

    private static boolean programa(String sourceCode) {
        return declaracion(sourceCode);
    }

    private static boolean declaracion(String sourceCode) {
        return asignacion(sourceCode) || estructuraControl(sourceCode) || impresion(sourceCode);
    }

    private static boolean asignacion(String sourceCode) {
        if (currentIndex >= sourceCode.length()) return false;
        int savedIndex = currentIndex;
        if (variable(sourceCode) && matchString(sourceCode, ":=") && expresion(sourceCode) && matchString(sourceCode, ";")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;
    }

    private static boolean estructuraControl(String sourceCode) {
        if (currentIndex >= sourceCode.length()) return false;
        int savedIndex = currentIndex;
        if (matchString(sourceCode, "if") && expresion(sourceCode) && matchString(sourceCode, "{") && declaracion(sourceCode) && matchString(sourceCode, "}") && matchString(sourceCode, "else") && matchString(sourceCode, "{") && declaracion(sourceCode) && matchString(sourceCode, "}")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;
    }

    private static boolean impresion(String sourceCode) {
        if (currentIndex >= sourceCode.length()) return false;
        int savedIndex = currentIndex;
        if (matchString(sourceCode, "print") && cadenaCaracteres(sourceCode) && matchString(sourceCode, ";")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;
    }

    private static boolean expresion(String sourceCode) {
        return expresionAritmetica(sourceCode) || constante(sourceCode) || variable(sourceCode);
    }

    private static boolean expresionAritmetica(String sourceCode) {
        if (currentIndex >= sourceCode.length()) return false;
        int savedIndex = currentIndex;
        if (expresion(sourceCode) && operador(sourceCode) && expresion(sourceCode)) {
            return true;
        }
        currentIndex = savedIndex;
        return false;
    }

    private static boolean operador(String sourceCode) {
        return matchString(sourceCode, "+") || matchString(sourceCode, "-") || matchString(sourceCode, "*") || matchString(sourceCode, "/") ||
                matchString(sourceCode, ":=") || matchString(sourceCode, ">=") || matchString(sourceCode, "<=") || matchString(sourceCode, ">") ||
                matchString(sourceCode, "<") || matchString(sourceCode, "=") || matchString(sourceCode, "<>");
    }

    private static boolean cadenaCaracteres(String sourceCode) {
        if (currentIndex >= sourceCode.length()) return false;
        int savedIndex = currentIndex;
        if (matchString(sourceCode, "\"") && caracteres(sourceCode) && matchString(sourceCode, "\"")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;
    }

    private static boolean caracteres(String sourceCode) {
        if (currentIndex >= sourceCode.length()) return false;
        int savedIndex = currentIndex;
        if (caracter(sourceCode) && caracteres(sourceCode)) {
            return true;
        }
        currentIndex = savedIndex;
        return true;
    }

    private static boolean caracter(String sourceCode) {
        if (currentIndex >= sourceCode.length()) return false;
        char currentChar = sourceCode.charAt(currentIndex);
        if (currentChar == 'b' || currentChar == 'f' || currentChar == 'h' || currentChar == 'j' || currentChar == 'k') {
            currentIndex++;
            return true;
        }
        return false;
    }

    private static boolean variable(String sourceCode) {
        return identificador(sourceCode);
    }

    private static boolean constante(String sourceCode) {
        return entero(sourceCode);
    }

    private static boolean identificador(String sourceCode) {
        if (currentIndex >= sourceCode.length()) return false;
        char currentChar = sourceCode.charAt(currentIndex);
        if (Character.isLetter(currentChar)) {
            currentIndex++;
            return restoIdentificador(sourceCode);
        }
        return false;
    }

    private static boolean restoIdentificador(String sourceCode) {
        if (currentIndex >= sourceCode.length()) return true;
        char currentChar = sourceCode.charAt(currentIndex);
        if (Character.isLetterOrDigit(currentChar)) {
            currentIndex++;
            return restoIdentificador(sourceCode);
        }
        return true;
    }

    private static boolean entero(String sourceCode) {
        if (currentIndex >= sourceCode.length()) return false;
        int savedIndex = currentIndex;
        if (matchPattern(sourceCode, "([1-9][0-9]{0,1}|100|0)")) {
            return true;
        }
        currentIndex = savedIndex;
        return false;
    }

    private static boolean matchString(String sourceCode, String str) {
        if (currentIndex + str.length() <= sourceCode.length() && sourceCode.substring(currentIndex, currentIndex + str.length()).equals(str)) {
            currentIndex += str.length();
            return true;
        }
        return false;
    }

    private static boolean matchPattern(String sourceCode, String patternStr) {
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(sourceCode.substring(currentIndex));
        if (matcher.find() && matcher.start() == 0) {
            currentIndex += matcher.group().length();
            return true;
        }
        return false;
    }
}
