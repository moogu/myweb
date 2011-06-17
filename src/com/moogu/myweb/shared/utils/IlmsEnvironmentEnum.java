package com.moogu.myweb.shared.utils;

public enum IlmsEnvironmentEnum {

    DEV("DEV"), TEST("TEST"), QA("QA"), PROD("PROD");

    private final String environment;

    public static IlmsEnvironmentEnum toEnum(String text) {
        for (final IlmsEnvironmentEnum type : IlmsEnvironmentEnum.values()) {
            if (type.environment.equalsIgnoreCase(text)) {
                return type;
            }
        }
        return null;
    }

    IlmsEnvironmentEnum(String environment) {
        this.environment = environment;
    }

    @Override
    public String toString() {
        return this.environment;
    }
}
