package com.payneteasy.pos.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;

public enum Configs {

      HTTP_SERVER_ADDRESS                  ( "0.0.0.0"                              )
    , HTTP_SERVER_PORT                     ( 8081                                   )
    , HTTP_SERVER_SELECTORS                ( 1                                      )
    , HTTP_SERVER_MIN_WORKERS              ( 2                                      )
    , HTTP_SERVER_MAX_WORKERS              ( 10                                     )
    , API_KEY                              ( "56CC8E1F-85B3-404E-BB26-7671D45016D6" )
    ;

    private static final Logger LOG = LoggerFactory.getLogger(Configs.class);

    static {
        LOG.info("Version is " + getVersion());
        LOG.info("Configuration (you can change it with -D option or with env variable)");
        int maxNameLength = getMaxNameLength();
        for (Configs config : Configs.values()) {
            String value = config.name().contains("PASSWORD") ? "***"  : config.asString();
            LOG.info("    {} = {}", padRight(config.propertyName, maxNameLength), value);
        }
    }

    private static int getMaxNameLength() {
        return Arrays.stream(Configs.values()).mapToInt(value -> value.propertyName.length()).max().orElse(10);
    }


    private static String padRight(String aText, int aLength) {
        StringBuilder sb = new StringBuilder(aLength);
        sb.append(aText);
        while(sb.length() < aLength) {
            sb.append(" ");
        }
        return sb.toString();
    }


    Configs(String aDefaultValue) {
        propertyName = name();
        defaultValue = aDefaultValue;
    }

    Configs(long aDefaultValue) {
        this(String.valueOf(aDefaultValue));
    }

    private final String propertyName;
    private final String defaultValue;

    public static String getVersion() {
        String version = Configs.class.getPackage().getImplementationVersion();
        return version != null ? version : "unknown";
    }

    public String asString() {
        String value = System.getProperty(propertyName, null);
        if(value == null) {
            value = System.getenv(propertyName);
        }
        if(value == null) {
            value = defaultValue;
        }
        return value;
    }

    public int asInt() {
        return Integer.parseInt(asString());
    }

    public boolean asBoolean() {
        return Boolean.parseBoolean(asString());
    }

    public int asMilliseconds() {
        return asInt() * 1_000;
    }

    public File asFile() {
        return new File(asString());
    }
}
