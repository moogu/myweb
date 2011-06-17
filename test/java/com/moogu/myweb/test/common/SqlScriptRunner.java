package com.moogu.myweb.test.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * To run conveniently on your personal schema, use eclipse runner and set "${workspace_loc}/BE_FORTIS_ILMS_WEB/sql" as
 * argument for running on your personal schema. Please check that your personal schema password is set accordingly to
 * {@link PersonalDatabaseConfig#getPassword()} When running on your personal schema some script modification are
 * prepocessed in order to run the script on a schema which is not ILMS_OWN.
 * 
 * To run on the shared schema (CMLI01D.ILMS_OWN), use eclipse runner and set
 * "${workspace_loc}/BE_FORTIS_ILMS_WEB/sql common".
 * 
 * 
 * 
 * @author i19451
 * 
 */
public class SqlScriptRunner {

    /**
     * SQL script are not schema agnostic (they have statement like "create Table ILMS.DB_MY_TABLE". So we can't run it
     * on a discret schema. So we use a DB dedicated to test our SQL scripts.
     */
    private static final String DEV_DB_USERNAME = "ILMS_OWN";

    private static final String DEV_DB_PASSORD = "ILMSOWN_29032011";

    private static final String DEV_DB_HOST = "ILMS01D";

    private static final String[] IGNORED_SQL_FOLDER = new String[] { "drop" };

    private static final Logger LOGGER = Logger.getLogger(SqlScriptRunner.class);

    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("Usage : SqlScriptRunner [absolute Sql Root Folder]\n e.g. ${workspace_loc}/BE_FORTIS_ILMS_WEB/sql personal");
            return;
        }
        final String sqlRootFolder = args[0];
        boolean personal = true;
        if (args.length == 2 && args[1].equals("common")) {
            personal = false;
        }
        new SqlScriptRunner().runAllScriptsInSqlPlus(sqlRootFolder, personal);
    }

    private void cleanDb(PersonalDatabaseConfig dbConf, boolean personal) {
        final String userName;
        if (personal) {
            userName = dbConf.userName;
        } else {
            userName = SqlScriptRunner.DEV_DB_USERNAME;
        }
        Connection connection = null;
        try {
            connection = this.createConnection(dbConf, personal);
            this.dropAll("PROCEDURE", connection, userName);
            this.dropAll("TRIGGER", connection, userName);
            this.dropAll("VIEW", connection, userName);
            this.dropAll("TABLE", connection, userName, " cascade constraints");
            this.dropAll("INDEX", connection, userName);
            this.dropAll("SEQUENCE", connection, userName);
            connection.commit();
        } catch (final Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (final SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Connection createConnection(PersonalDatabaseConfig dbConf, boolean personal) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection connection = null;
            if (personal) {
                connection = DriverManager.getConnection(dbConf.urlConnection, dbConf.userName, dbConf.password);
                if (connection == null) {
                    throw new IllegalArgumentException("Can't create JDBC conncetion with = " + dbConf);
                }
            } else {
                final String urlConnection = "jdbc:oracle:thin:@dbod" + SqlScriptRunner.DEV_DB_HOST.toLowerCase()
                                + ":48896:"
                                + SqlScriptRunner.DEV_DB_HOST;
                connection = DriverManager.getConnection(
                                urlConnection,
                                SqlScriptRunner.DEV_DB_USERNAME,
                                SqlScriptRunner.DEV_DB_PASSORD);
                if (connection == null) {
                    throw new IllegalArgumentException("Can't create JDBC conncetion with url = " + urlConnection
                                    + ", username = "
                                    + SqlScriptRunner.DEV_DB_USERNAME
                                    + ", password = "
                                    + SqlScriptRunner.DEV_DB_PASSORD);
                }
            }

            return connection;
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * We need to add "exit" at the end of each files.
     */
    private File createTempEnhancedFile(File source, boolean personal, PersonalDatabaseConfig dbConfig) {
        try {
            final File result = File.createTempFile("_" + source.getName(), "");
            final FileWriter fileWriter = new FileWriter(result);
            String content = FileUtils.readFileToString(source);

            // If we run the scripts against personal schema, we must remove 
            // all stuff related to the explicit ILMS_OWN schema. 
            if (personal) {
                content = StringUtils.replace(content, "ILMS_OWN", dbConfig.userName.toUpperCase());
                content = StringUtils.replace(content, "ilms_own", dbConfig.userName.toUpperCase());
            }
            fileWriter.append(content);
            fileWriter.append("\rEXIT\r");
            fileWriter.close();
            return result;
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void dropAll(String objectType, Connection connection, String userName) throws SQLException {
        this.dropAll(objectType, connection, userName, "");
    }

    private void dropAll(String objectType, Connection connection, String userName, String suffix) throws SQLException {
        final List<String> dbObjectNames = this.getObjectOfType(connection, objectType);
        for (final String name : dbObjectNames) {
            final Statement statement = connection.createStatement();
            final String sql = "DROP " + objectType + " " + userName + "." + name + " " + suffix;
            try {
                SqlScriptRunner.LOGGER.info(sql);
                statement.execute(sql);
            } catch (final SQLException e) {

                SqlScriptRunner.LOGGER.warn("command sql failed : " + sql + "\n" + e.getMessage() + "\n");
                SqlScriptRunner.LOGGER.warn("-------------------------------------------------------------------");

            }
            statement.close();
        }
    }

    private List<File> getAllSqlFilesOrdered(File sqlRootFolder) {
        SqlScriptRunner.LOGGER.info("Scanning sql folders under " + sqlRootFolder.getAbsolutePath() + " ...");
        final List<File> result = new ArrayList<File>(100);
        //File lastFolder = null;
        for (final File folder : this.getSqlFolders(sqlRootFolder)) {
            result.addAll(this.getSqlFilesInsideFolder(folder));
            //lastFolder = folder;
        }

        // Let's replay twice scripts in last folder,
        // to ensure that scripts are re-playable.
        //result.addAll(this.getSqlFilesInsideFolder(lastFolder));
        return result;
    }

    private List<String> getObjectOfType(Connection connection, String type) throws SQLException {

        final Statement statement = connection.createStatement();
        final ResultSet resultSet = statement.executeQuery("select OBJECT_NAME from USER_OBJECTS where OBJECT_TYPE='" + type
                        + "'");
        final List<String> result = new ArrayList<String>();
        while (resultSet.next()) {
            result.add(resultSet.getString(1));
        }
        resultSet.close();
        statement.close();
        return result;
    }

    private List<File> getSqlFilesInsideFolder(File folder) {
        SqlScriptRunner.LOGGER.info("Scanning sql files under " + folder.getAbsolutePath() + " ...");
        final List<String> fileNames = new ArrayList<String>(20);
        for (final String fileName : folder.list()) {
            if (fileName.toLowerCase().endsWith(".sql")) {
                fileNames.add(fileName);
            }
        }
        Collections.sort(fileNames);
        final List<File> files = new ArrayList<File>(fileNames.size());
        for (final String fileName : fileNames) {
            files.add(new File(folder, fileName));
        }
        return files;
    }

    private List<File> getSqlFolders(File sqlRootFolder) {
        final List<String> fileNames = new ArrayList<String>(20);
        for (final File file : sqlRootFolder.listFiles()) {
            if (!ArrayUtils.contains(SqlScriptRunner.IGNORED_SQL_FOLDER, file.getName()) && file.isDirectory()
                            && !file.getName().startsWith(".")) {
                fileNames.add(file.getName());
            }
        }
        Collections.sort(fileNames);
        final List<File> files = new ArrayList<File>(fileNames.size());
        for (final String fileName : fileNames) {
            files.add(new File(sqlRootFolder, fileName));
        }
        return files;
    }

    private String getSqlPlusCommandLine(File file, boolean personal, PersonalDatabaseConfig dbConfig) {
        if (!personal) {
            return "sqlplus " + SqlScriptRunner.DEV_DB_USERNAME
                            + "/"
                            + SqlScriptRunner.DEV_DB_PASSORD
                            + "@"
                            + SqlScriptRunner.DEV_DB_HOST
                            + " @"
                            + file.getAbsolutePath();
        }
        return "sqlplus " + dbConfig.userName
                        + "/"
                        + dbConfig.password
                        + "@"
                        + dbConfig.getHost()
                        + " @"
                        + file.getAbsolutePath();
    }

    /**
     * @param sqlRootFolder the folder containing directly release_xxx folders
     * @param personal false to run the script on the shared schema, true to run on personnal shema
     */
    private void runAllScriptsInSqlPlus(File sqlRootFolder, boolean personal) {
        final PersonalDatabaseConfig dbConfig = new PersonalDatabaseConfig();
        this.cleanDb(dbConfig, personal);
        for (final File file : this.getAllSqlFilesOrdered(sqlRootFolder)) {
            final File enhancedFile = this.createTempEnhancedFile(file, personal, dbConfig);
            final String scriptFullName = file.getParentFile().getName() + "/" + file.getName();
            final String commandLine = this.getSqlPlusCommandLine(enhancedFile, personal, dbConfig);
            SqlScriptRunner.LOGGER.info("Running " + scriptFullName);
            SqlScriptRunner.LOGGER.debug("Run command " + commandLine);
            try {
                ExternalCommandHelper.run(enhancedFile.getParentFile(), commandLine, "ERROR at line ");
            } catch (final RuntimeException e) {
                throw new RuntimeException("Error in script " + scriptFullName);
            }
        }
        SqlScriptRunner.LOGGER.info("All SQL scripts have been processed succefully.");
    }

    private void runAllScriptsInSqlPlus(String sqlRootFolder, boolean personal) {
        this.runAllScriptsInSqlPlus(new File(sqlRootFolder), personal);
    }

}
