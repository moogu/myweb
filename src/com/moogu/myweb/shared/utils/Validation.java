package com.moogu.myweb.shared.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Validation implements Serializable, IsSerializable {

    private static final long serialVersionUID = 1L;

    private final ArrayList<String> errors = new ArrayList<String>();

    public Validation(String... errors) {
        for (final String message : errors) {
            this.errors.add(message);
        }
    }

    public Validation() {

    }

    public void addErrorIf(String message, boolean condition) {
        if (condition) {
            this.errors.add(message);
        }
    }

    public void addSubValidation(Validation validation) {
        this.errors.addAll(validation.errors);
    }

    public void addSubValidation(String prefix, Validation validation) {
        for (final String error : validation.errors) {
            this.errors.add(prefix + error);
        }
    }

    public boolean hasError() {
        return !this.errors.isEmpty();
    }

    public void addError(String message) {
        this.errors.add(message);
    }

    public List<String> getErrors() {
        return new ArrayList<String>(this.errors);
    }

    public String toStringLn() {
        return this.toStringLn("<br/>");
    }

    public void assertnoError(String messagePrefix) {
        if (this.hasError()) {
            throw new IllegalStateException(messagePrefix + " : " + this.toStringLn("\n"));
        }
    }

    private String toStringLn(String endOfLine) {
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < this.errors.size(); i++) {
            stringBuilder.append(this.errors.get(i));
            if (i + 1 < this.errors.size()) {
                stringBuilder.append(endOfLine);
            }
        }
        return stringBuilder.toString();
    }

}