package com.ua.sutty.jdbc.repository.impl;

import com.ua.sutty.jdbc.domain.Role;
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
import java.sql.SQLException;
import java.util.ResourceBundle;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JdbcRoleDaoTest {

    private static final String SQL_DATA_SET = "src/main/resources/dataset.xml";
    private static final String SQL_SCHEMA = "src/main/resources/schema.sql";
    private static final String SQL_DATABASE = "test";
    private static IDatabaseTester databaseTester = null;

    @BeforeClass
    public static void createSchema() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
        String url = resourceBundle.getString("jdbc.url");
        String user = resourceBundle.getString("jdbc.username");
        String password = resourceBundle.getString("jdbc.password");

//        String schema = resourceBundle.getString("schema.sql");
        try {
            RunScript.execute(url, user, password, SQL_SCHEMA,
                Charset.forName("UTF-8"), false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
        ResourceBundle resourceBundle;
        resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
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
    public void testCreateNull() {
        JdbcRoleDao jdbcRoleDao = new JdbcRoleDao();
        jdbcRoleDao.setBasicDataSource(dataSource());
        jdbcRoleDao.create(null);
    }

    @Test(expected = NullPointerException.class)
    public void testUpdateNull() {
        JdbcRoleDao jdbcRoleDao = new JdbcRoleDao();
        jdbcRoleDao.setBasicDataSource(dataSource());
        jdbcRoleDao.update(null);
    }

    @Test(expected = NullPointerException.class)
    public void testRemoveNull() {
        JdbcRoleDao jdbcRoleDao = new JdbcRoleDao();
        jdbcRoleDao.setBasicDataSource(dataSource());
        jdbcRoleDao.remove(null);
    }

    @Test(expected = NullPointerException.class)
    public void testFindByNameNull() {
        JdbcRoleDao jdbcRoleDao = new JdbcRoleDao();
        jdbcRoleDao.setBasicDataSource(dataSource());
        jdbcRoleDao.findByName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRemoveWhereIdNull() {
        Role role = new Role();
        role.setId(null);
        role.setName("role");
        JdbcRoleDao jdbcRoleDao = new JdbcRoleDao();
        jdbcRoleDao.setBasicDataSource(dataSource());
        jdbcRoleDao.remove(role);
    }

    @Test
    public void testCreate() throws Exception {
        JdbcRoleDao jdbcRoleDao = new JdbcRoleDao();
        jdbcRoleDao.setBasicDataSource(dataSource());
        Role someRole = new Role(4L, "createRole");
        jdbcRoleDao.create(someRole);
        assertEquals(4, databaseTester.getConnection().createDataSet()
            .getTable("role").getRowCount());
        assertEquals(someRole.getName(), databaseTester.getConnection()
            .createDataSet().getTable("role").getValue(3, "Name"));
    }

    @Test
    public void testUpdate() throws Exception {
        JdbcRoleDao jdbcRoleDao = new JdbcRoleDao();
        jdbcRoleDao.setBasicDataSource(dataSource());
        Role role = new Role(3L, "updatedRole");
        jdbcRoleDao.update(role);
        assertEquals(3, databaseTester.getConnection().createDataSet()
            .getTable("role").getRowCount());
        assertEquals("Test update user ", databaseTester.getConnection()
            .createDataSet().getTable("role")
            .getValue(2, "name"), role.getName());
    }

    @Test
    public void testRemove() throws Exception {
        JdbcRoleDao jdbcRoleDao = new JdbcRoleDao();
        jdbcRoleDao.setBasicDataSource(dataSource());
        Role role = new Role(1L, "removeRole");
        jdbcRoleDao.remove(role);
        assertEquals("Size should be 2", 2, databaseTester.getConnection()
            .createDataSet().getTable("role").getRowCount());
    }

    @Test
    public void testFindByName() throws Exception {
        JdbcRoleDao jdbcRoleDao = new JdbcRoleDao();
        jdbcRoleDao.setBasicDataSource(dataSource());
        String roleName = String.valueOf(databaseTester.getConnection().createDataSet().
            getTable("role").getValue(2, "name"));
        Role role = jdbcRoleDao.findByName(roleName);
        assertNotNull(role);
    }

    private BasicDataSource dataSource() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle(SQL_DATABASE);
        BasicDataSource basicDataSource = null;
        if (resourceBundle != null) {
            basicDataSource = new BasicDataSource();
            basicDataSource.setUrl(resourceBundle.getString("jdbc.url"));
            basicDataSource.setUsername(resourceBundle.getString("jdbc.username"));
            basicDataSource.setPassword(resourceBundle.getString("jdbc.password"));
        }
        return basicDataSource;
    }

}
