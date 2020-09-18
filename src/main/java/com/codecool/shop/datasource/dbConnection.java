package com.codecool.shop.datasource;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;

import org.postgresql.ds.PGSimpleDataSource;

import java.io.InputStream;
import java.util.Properties;


public class dbConnection {
    private static dbConnection dbConnection;
    private DataSource dataSource;
    private static String DatabaseName;
    private static String DbUser;
    private static String DbPassword;
    private static InputStream inputStream;

    private dbConnection() throws IOException {

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        getPropValues();

        dataSource.setDatabaseName(DatabaseName);
        dataSource.setUser(DbUser);
        dataSource.setPassword(DbPassword);
        this.dataSource = dataSource;

    }

    private static String getPropValues() throws IOException {
        try {
            Properties prop = new Properties();
            String propFileName = "connection.properties";

            inputStream = dbConnection.class.getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }

            DatabaseName = prop.getProperty("database");
            DbUser = prop.getProperty("user");
            DbPassword = prop.getProperty("password");

        } catch (Exception e) {
            System.out.println("Exception: " + e);
        } finally {
            inputStream.close();
        }
        return null;
    }


    public static dbConnection getInstance() throws SQLException, IOException {

        if(dbConnection == null){
            dbConnection = new dbConnection();
        }
        return dbConnection;
    }

    public DataSource getDataSource(){
        return this.dataSource;
    }


}
