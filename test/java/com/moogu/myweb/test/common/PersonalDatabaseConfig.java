package com.moogu.myweb.test.common;

import org.apache.commons.lang.StringUtils;

public class PersonalDatabaseConfig {

    public final String userName;

    public final String password;

    public final String urlConnection;

    public PersonalDatabaseConfig() {
        final String envUserName = System.getProperty("ilms.schema.username");

        // Use config defined in environment vars
        if (envUserName != null) {
            System.out.println("Found env property ilms.schema.username = " + envUserName);
            this.userName = envUserName;
            this.password = System.getProperty("ilms.schema.password");
            this.urlConnection = System.getProperty("ilms.schema.url");

            // use default
        } else {
            System.out.println("Env property ilms.schema.username not found");
            this.userName = this.getSchemaName();
            this.password = "ILMSDEVOWN_123";
            this.urlConnection = "jdbc:oracle:thin:@dbodilms01d:48896:ILMS01D";
        }
        System.out.println("Use db config : " + this);

    }

    private String getSchemaName() {
        final String user = System.getProperty("user.name");
        if (user == null) {
            throw new RuntimeException("User could not be identified");
        }
        if (user.equals("i03752")) {
            return "ILMS_DEV2_OWN";
        } else if (user.equals("i19451")) {
            return "ILMS_DEV1_OWN";
        } else if (user.equals("i21726")) {
            return "ILMS_DEV3_OWN";
        }
        return "ILMS_DEV4_OWN";
    }

    public String getHost() {
        return StringUtils.substringAfterLast(this.urlConnection, ":");
    }

    @Override
    public String toString() {
        return "user name = " + this.userName
                        + ", password = "
                        + this.password
                        + ", urlConnection = "
                        + this.urlConnection;
    }
}