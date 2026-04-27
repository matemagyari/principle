package org.tindalos.principle.internal.infrastructure.service.jdepend.classdependencies;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * see https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3.4
 */
final class SignatureParser {
    private static final char EOF = (char) -1;
    private static final String NOT_IDENT = ".;[/<>:";
    private static final String BASE_TYPES = "BCDFIJSZ";

    private final String s;
    private char c;
    private int pos;
    private final Set<String> packages = new HashSet<>();

    private SignatureParser(String s) {
        this.s = s;
        pos = 0;
        read();
    }

    static SignatureParser parseClassSignature(String signature) {
        final SignatureParser parser = new SignatureParser(signature);
        parser.classSignature();
        return parser;
    }


    static SignatureParser parseFieldSignature(String signature) {
        final SignatureParser parser = new SignatureParser(signature);
        parser.fieldTypeSignature(false);
        return parser;
    }

    static SignatureParser parseMethodSignature(String signature) {
        final SignatureParser parser = new SignatureParser(signature);
        parser.methodTypeSignature();
        return parser;
    }

    Collection<String> getPackages() {
        return packages;
    }

    private void classSignature() {
        if (is('<')) {
            formalTypeParameters();
        }
        do {
            classTypeSignature();
        } while (!is(EOF));
    }

    private void formalTypeParameters() {
        read('<');
        do {
            formalTypeParameter();
        } while (!is('>'));
        read('>');
    }

    private void formalTypeParameter() {
        identifier();
        classBound();
        while (is(':')) {
            interfaceBound();
        }
    }

    private void classBound() {
        read(':');
        fieldTypeSignature(true);
    }

    private void interfaceBound() {
        read(':');
        fieldTypeSignature(false);
    }

    private void fieldTypeSignature(boolean opt) {
        if (is('L')) {
            classTypeSignature();
        } else if (is('T')) {
            typeVariableSignature();
        } else if (is('[')) {
            arrayTypeSignature();
        } else if (!opt) {
            throw new RuntimeException("FieldTypeSignature expected [" + s + "]:" + pos);
        }
    }

    private void classTypeSignature() {
        read('L');
        final StringBuilder signature = new StringBuilder();
        signature.append(classIdentifier());
        while (!is('$') && !is(';') && !is('<')) {
            signature.append('.');
            read();
            signature.append(classIdentifier());
        }
        String id = signature.toString();
        if (is('$')) {
            id = classTypeSignatureSuffix(id);
        } else {
            if (is('<')) {
                typeArguments();
            }
        }
        final int packageSeparator = id.lastIndexOf('.');
        packages.add(packageSeparator < 0 ? id : id.substring(0, packageSeparator));
        read(';');
    }

    private String classTypeSignatureSuffix(String base) {
        read('$');
        final String id = classIdentifier();
        final String name = base + "$" + id;
        if (is('<')) {
            typeArguments();
        }
        return name;
    }

    private void typeArguments() {
        read('<');
        do {
            typeArgument();
        } while (!is('>'));
        read('>');
    }

    private void typeArgument() {
        if (is('*')) {
            read('*');
        } else {
            if (is('+')) {
                read('+');
            } else if (is('-')) {
                read('-');
            }
            fieldTypeSignature(false);
        }
    }

    private void arrayTypeSignature() {
        read('[');
        typeSignature();
    }

    private void typeSignature() {
        if (isBaseType()) {
            read();
        } else {
            fieldTypeSignature(false);
        }
    }

    private boolean isBaseType() {
        return BASE_TYPES.indexOf(c) >= 0;
    }

    private void typeVariableSignature() {
        read('T');
        identifier();
        read(';');
    }

    private void methodTypeSignature() {
        if (is('<')) {
            formalTypeParameters();
        }
        read('(');
        while (!is(')')) {
            typeSignature();
        }
        read(')');
        returnType();
        while (is('^')) {
            throwsSignature();
        }
    }

    private void throwsSignature() {
        read('^');
        if (is('L')) {
            classTypeSignature();
        } else if (is('T')) {
            typeVariableSignature();
        } else {
            throw new RuntimeException("ClassType or TypeVariable signature expected [" + s + "]:" + pos);
        }
    }

    private void returnType() {
        if (is('V')) {
            read();
        } else {
            typeSignature();
        }
    }

    private String classIdentifier() {
        return identifier(true);
    }

    private String identifier() {
        return identifier(false);
    }

    private String identifier(boolean clazz) {
        StringBuilder identifierBuilder = new StringBuilder();
        do {
            identifierBuilder.append(c);
            read();
        } while (NOT_IDENT.indexOf(c) < 0 && (!clazz || c != '$'));
        return identifierBuilder.toString();
    }

    private boolean is(char ch) {
        return c == ch;
    }

    private char read() {
        return c = (pos == s.length() ? EOF : s.charAt(pos++));
    }

    private char read(char ch) {
        if (c != ch) {
            throw new RuntimeException("'" + ch + "' expected [" + s + "]:" + pos);
        }
        return read();
    }
}
