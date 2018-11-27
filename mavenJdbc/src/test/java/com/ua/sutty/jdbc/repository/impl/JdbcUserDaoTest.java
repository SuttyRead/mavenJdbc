package com.ua.sutty.jdbc.repository.impl;

import com.ua.sutty.jdbc.domain.User;
import org.apache.commons.dbcp2.BasicDataSource;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.h2.tools.RunScript;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.sql.Date;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JdbcUserDaoTest {

    private static final String SQL_DATA_SET = "src/main/resources/dataset.xml";
    private static final String SQL_SCHEMA = "src/main/resources/schema.sql";
    private static final String SQL_DATABASE = "test";
    private static IDatabaseTester databaseTester = null;

    @BeforeClass
    public static void createSchema() throws Exception {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
        String url = resourceBundle.getString("jdbc.url");
        String user = resourceBundle.getString("jdbc.username");
        String password = resourceBundle.getString("jdbc.password");
        RunScript.execute(url, user, password, SQL_SCHEMA, Charset.forName("UTF-8"), false);
    }

    @Before
    public void importDataSet() throws Exception {
        IDataSet dataSet = readDataSet();
        beforeStart(dataSet);
    }

    private IDataSet readDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new File(SQL_DATA_SET));
    }

    private void beforeStart(IDataSet dataSet) throws Exception {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
        String driver = resourceBundle.getString("jdbc.driver");
        String url = resourceBundle.getString("jdbc.url");
        String user = resourceBundle.getString("jdbc.username");
        String password = resourceBundle.getString("jdbc.password");
        databaseTester = new JdbcDatabaseTester(driver, url, user, password);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

    @Test(expected = NullPointerException.class)
    public void testCreateNull(){
        JdbcUserDao jdbcUserDao = new JdbcUserDao();
        jdbcUserDao.setBasicDataSource(dataSource());
        jdbcUserDao.create(null);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNull(){
        JdbcUserDao jdbcUserDao = new JdbcUserDao();
        jdbcUserDao.setBasicDataSource(dataSource());
        jdbcUserDao.update(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNull(){
        JdbcUserDao jdbcUserDao = new JdbcUserDao();
        jdbcUserDao.setBasicDataSource(dataSource());
        jdbcUserDao.remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void testFindByEmailNull() {
        JdbcUserDao jdbcUserDao = new JdbcUserDao();
        jdbcUserDao.setBasicDataSource(dataSource());
        jdbcUserDao.findByEmail(null);
    }

    @Test(expected = NullPointerException.class)
    public void testFindByLoginNull() {
        JdbcUserDao jdbcUserDao = new JdbcUserDao();
        jdbcUserDao.setBasicDataSource(dataSource());
        jdbcUserDao.findByLogin(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWhereIdNull() {
        User user = new User(null, "login", "password", "email", "firstname",
            "lastname", new Date(System.currentTimeMillis()), 1L);
        JdbcUserDao jdbcUserDao = new JdbcUserDao();
        jdbcUserDao.setBasicDataSource(dataSource());
        jdbcUserDao.remove(user);
    }

    @Test
    public void testCreate() throws Exception {
        JdbcUserDao jdbcUserDao = new JdbcUserDao();
        jdbcUserDao.setBasicDataSource(dataSource());
        User user = new User(4L, "fourthUser", "123",
            "fourthUser@gmail.com", "fourth", "user",
            new Date(System.currentTimeMillis()), 3L);
        jdbcUserDao.create(user);
        assertEquals(4, databaseTester.getConnection().createDataSet()
            .getTable("user").getRowCount());
        assertEquals(databaseTester.getConnection().createDataSet()
            .getTable("user")
            .getValue(3, "login"), user.getLogin());
        assertEquals(databaseTester.getConnection().createDataSet()
            .getTable("user")
            .getValue(3, "email"), user.getEmail());
    }

    @Test
    public void testUpdate() throws Exception {
        JdbcUserDao jdbcUserDao = new JdbcUserDao();
        jdbcUserDao.setBasicDataSource(dataSource());
        User user = new User(3L, "thirdUser", "123", "thirdUser@gmail.com",
            "Third", "User",
            new Date(System.currentTimeMillis()), 3L);
        jdbcUserDao.update(user);
        assertEquals(databaseTester.getConnection().createDataSet().getTable("user")
            .getValue(2, "login"), user.getLogin());
        assertEquals(databaseTester.getConnection().createDataSet().getTable("user")
            .getValue(2, "email"), user.getEmail());
        assertEquals(databaseTester.getConnection().createDataSet().getTable("user")
            .getValue(2, "first_name"), user.getFirstName());
    }

    @Test
    public void testRemove() throws Exception {
        JdbcUserDao jdbcUserDao = new JdbcUserDao();
        jdbcUserDao.setBasicDataSource(dataSource());
        User user = new User(2L, "secondUser", "123", "secondUser@gmail.com",
            "second", "user",
            new Date(System.currentTimeMillis()), 2L);
        jdbcUserDao.remove(user);
        assertEquals(2,
            databaseTester.getConnection().createDataSet().getTable("user").getRowCount());
    }

    @Test
    public void testFindByLogin() throws Exception {
        JdbcUserDao jdbcUserDao = new JdbcUserDao(dataSource(), "test");
        String userName = String.valueOf(databaseTester.getConnection()
            .createDataSet().getTable("user").getValue(0, "login"));
        User user = jdbcUserDao.findByLogin(userName);
        assertNotNull("Test find user by login", user);
    }

    @Test
    public void testFindByEmail() throws Exception {
        JdbcUserDao jdbcUserDao = new JdbcUserDao(dataSource(), "test");
        String userMail = String.valueOf(databaseTester.getConnection()
            .createDataSet().getTable("user").getValue(0, "email"));
        User user = jdbcUserDao.findByEmail(userMail);
        assertNotNull("Test find user by email", user);
    }

    private BasicDataSource dataSource() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
        BasicDataSource basicDataSource = new BasicDataSource();
        basicDataSource.setUrl(resourceBundle.getString("jdbc.url"));
        basicDataSource.setUsername(resourceBundle.getString("jdbc.username"));
        basicDataSource.setPassword(resourceBundle.getString("jdbc.password"));
        return basicDataSource;
    }

}
