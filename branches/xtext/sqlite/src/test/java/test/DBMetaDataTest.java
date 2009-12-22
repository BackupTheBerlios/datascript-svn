package test;

import java.sql.*;
import org.junit.*;
import static org.junit.Assert.*;

/** These tests are designed to stress Statements on memory databases. */
public class DBMetaDataTest
{
    private Connection conn;
    private Statement stat;
    private DatabaseMetaData meta;

    @BeforeClass public static void forName() throws Exception {
        Class.forName("org.sqlite.JDBC");
    }

    @Before public void connect() throws Exception {
        conn = DriverManager.getConnection("jdbc:sqlite:");
        stat = conn.createStatement();
        stat.executeUpdate(
            "create table test (id integer primary key, fn, sn);");
        stat.executeUpdate("create view testView as select * from test;");
        meta = conn.getMetaData();
    }

    @After public void close() throws SQLException {
        meta = null;
        stat.close();
        conn.close();
    }

    @Test public void getTables() throws SQLException {
        ResultSet rs = meta.getTables(null, null, null, null);
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals(rs.getString("TABLE_NAME"), "TEST"); // 3
        assertEquals(rs.getString("TABLE_TYPE"), "TABLE"); // 4
        assertTrue(rs.next());
        assertEquals(rs.getString("TABLE_NAME"), "TESTVIEW");
        assertEquals(rs.getString("TABLE_TYPE"), "VIEW");
        rs.close();

        rs = meta.getTables(null, null, "bob", null);
        assertFalse(rs.next());
        rs.close();
        rs = meta.getTables(null, null, "test", null);
        assertTrue(rs.next());
        assertFalse(rs.next());
        rs.close();
        rs = meta.getTables(null, null, "test%", null);
        assertTrue(rs.next());
        assertTrue(rs.next());
        rs.close();

        rs = meta.getTables(null, null, null, new String[] { "table" });
        assertTrue(rs.next());
        assertEquals(rs.getString("TABLE_NAME"), "TEST");
        assertFalse(rs.next());
        rs.close();

        rs = meta.getTables(null, null, null, new String[] { "view" });
        assertTrue(rs.next());
        assertEquals(rs.getString("TABLE_NAME"), "TESTVIEW");
        assertFalse(rs.next());
        rs.close();
    }

    @Test public void getTableTypes() throws SQLException {
        ResultSet rs = meta.getTableTypes();
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals(rs.getString("TABLE_TYPE"), "TABLE");
        assertTrue(rs.next());
        assertEquals(rs.getString("TABLE_TYPE"), "VIEW");
        assertFalse(rs.next());
    }

    @Test public void getTypeInfo() throws SQLException {
        ResultSet rs = meta.getTypeInfo();
        assertNotNull(rs);
        assertTrue(rs.next());
        assertEquals(rs.getString("TYPE_NAME"), "BLOB");
        assertTrue(rs.next());
        assertEquals(rs.getString("TYPE_NAME"), "INTEGER");
        assertTrue(rs.next());
        assertEquals(rs.getString("TYPE_NAME"), "NULL");
        assertTrue(rs.next());
        assertEquals(rs.getString("TYPE_NAME"), "REAL");
        assertTrue(rs.next());
        assertEquals(rs.getString("TYPE_NAME"), "TEXT");
        assertFalse(rs.next());
    }

    @Test public void getColumns() throws SQLException {
        ResultSet rs = meta.getColumns(null, null, "test", "id");
        assertTrue(rs.next());
        assertEquals(rs.getString("TABLE_NAME"), "test");
        assertEquals(rs.getString("COLUMN_NAME"), "id");
        assertFalse(rs.next());

        rs = meta.getColumns(null, null, "test", "fn");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "fn");
        assertFalse(rs.next());

        rs = meta.getColumns(null, null, "test", "sn");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "sn");
        assertFalse(rs.next());

        rs = meta.getColumns(null, null, "test", "%");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "id");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "fn");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "sn");
        assertFalse(rs.next());

        rs = meta.getColumns(null, null, "test", "%n");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "fn");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "sn");
        assertFalse(rs.next());

        rs = meta.getColumns(null, null, "test%", "%");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "id");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "fn");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "sn");
        assertFalse(rs.next());

        rs = meta.getColumns(null, null, "%", "%");
        assertTrue(rs.next());
        assertEquals(rs.getString("TABLE_NAME"), "test");
        assertEquals(rs.getString("COLUMN_NAME"), "id");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "fn");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "sn");
        assertFalse(rs.next());

        rs = meta.getColumns(null, null, "doesnotexist", "%");
        assertFalse(rs.next());
    }

    @Test public void columnOrderOfgetTables() throws SQLException {
        ResultSet rs = meta.getTables(null, null, null, null);
        assertTrue(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 10);
        assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
        assertEquals(rsmeta.getColumnName(2), "TABLE_SCHEM");
        assertEquals(rsmeta.getColumnName(3), "TABLE_NAME");
        assertEquals(rsmeta.getColumnName(4), "TABLE_TYPE");
        assertEquals(rsmeta.getColumnName(5), "REMARKS");
        assertEquals(rsmeta.getColumnName(6), "TYPE_CAT");
        assertEquals(rsmeta.getColumnName(7), "TYPE_SCHEM");
        assertEquals(rsmeta.getColumnName(8), "TYPE_NAME");
        assertEquals(rsmeta.getColumnName(9), "SELF_REFERENCING_COL_NAME");
        assertEquals(rsmeta.getColumnName(10), "REF_GENERATION");
    }

    @Test public void columnOrderOfgetTableTypes() throws SQLException {
        ResultSet rs = meta.getTableTypes();
        assertTrue(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 1);
        assertEquals(rsmeta.getColumnName(1), "TABLE_TYPE");
    }

    @Test public void columnOrderOfgetTypeInfo() throws SQLException {
        ResultSet rs = meta.getTypeInfo();
        assertTrue(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 18);
        assertEquals(rsmeta.getColumnName(1), "TYPE_NAME");
        assertEquals(rsmeta.getColumnName(2), "DATA_TYPE");
        assertEquals(rsmeta.getColumnName(3), "PRECISION");
        assertEquals(rsmeta.getColumnName(4), "LITERAL_PREFIX");
        assertEquals(rsmeta.getColumnName(5), "LITERAL_SUFFIX");
        assertEquals(rsmeta.getColumnName(6), "CREATE_PARAMS");
        assertEquals(rsmeta.getColumnName(7), "NULLABLE");
        assertEquals(rsmeta.getColumnName(8), "CASE_SENSITIVE");
        assertEquals(rsmeta.getColumnName(9), "SEARCHABLE");
        assertEquals(rsmeta.getColumnName(10), "UNSIGNED_ATTRIBUTE");
        assertEquals(rsmeta.getColumnName(11), "FIXED_PREC_SCALE");
        assertEquals(rsmeta.getColumnName(12), "AUTO_INCREMENT");
        assertEquals(rsmeta.getColumnName(13), "LOCAL_TYPE_NAME");
        assertEquals(rsmeta.getColumnName(14), "MINIMUM_SCALE");
        assertEquals(rsmeta.getColumnName(15), "MAXIMUM_SCALE");
        assertEquals(rsmeta.getColumnName(16), "SQL_DATA_TYPE");
        assertEquals(rsmeta.getColumnName(17), "SQL_DATETIME_SUB");
        assertEquals(rsmeta.getColumnName(18), "NUM_PREC_RADIX");
    }

    @Test public void columnOrderOfgetColumns() throws SQLException {
        ResultSet rs = meta.getColumns(null, null, "test", null);
        assertTrue(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 22);
        assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
        assertEquals(rsmeta.getColumnName(2), "TABLE_SCHEM");
        assertEquals(rsmeta.getColumnName(3), "TABLE_NAME");
        assertEquals(rsmeta.getColumnName(4), "COLUMN_NAME");
        assertEquals(rsmeta.getColumnName(5), "DATA_TYPE");
        assertEquals(rsmeta.getColumnName(6), "TYPE_NAME");
        assertEquals(rsmeta.getColumnName(7), "COLUMN_SIZE");
        assertEquals(rsmeta.getColumnName(8), "BUFFER_LENGTH");
        assertEquals(rsmeta.getColumnName(9), "DECIMAL_DIGITS");
        assertEquals(rsmeta.getColumnName(10), "NUM_PREC_RADIX");
        assertEquals(rsmeta.getColumnName(11), "NULLABLE");
        assertEquals(rsmeta.getColumnName(12), "REMARKS");
        assertEquals(rsmeta.getColumnName(13), "COLUMN_DEF");
        assertEquals(rsmeta.getColumnName(14), "SQL_DATA_TYPE");
        assertEquals(rsmeta.getColumnName(15), "SQL_DATETIME_SUB");
        assertEquals(rsmeta.getColumnName(16), "CHAR_OCTET_LENGTH");
        assertEquals(rsmeta.getColumnName(17), "ORDINAL_POSITION");
        assertEquals(rsmeta.getColumnName(18), "IS_NULLABLE");
        // should be SCOPE_CATALOG, but misspelt in the standard
        assertEquals(rsmeta.getColumnName(19), "SCOPE_CATLOG");
        assertEquals(rsmeta.getColumnName(20), "SCOPE_SCHEMA");
        assertEquals(rsmeta.getColumnName(21), "SCOPE_TABLE");
        assertEquals(rsmeta.getColumnName(22), "SOURCE_DATA_TYPE");
    }

    // the following functions always return an empty resultset, so
    // do not bother testing their parameters, only the column types

    @Test public void columnOrderOfgetProcedures() throws SQLException {
        ResultSet rs = meta.getProcedures(null, null, null);
        assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 8);
        assertEquals(rsmeta.getColumnName(1), "PROCEDURE_CAT");
        assertEquals(rsmeta.getColumnName(2), "PROCEDURE_SCHEM");
        assertEquals(rsmeta.getColumnName(3), "PROCEDURE_NAME");
        // currently (Java 1.5), cols 4,5,6 are undefined
        assertEquals(rsmeta.getColumnName(7), "REMARKS");
        assertEquals(rsmeta.getColumnName(8), "PROCEDURE_TYPE");
    }

    @Test public void columnOrderOfgetProcedurColumns() throws SQLException {
        ResultSet rs = meta.getProcedureColumns(null, null, null, null);
        assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 13);
        assertEquals(rsmeta.getColumnName(1), "PROCEDURE_CAT");
        assertEquals(rsmeta.getColumnName(2), "PROCEDURE_SCHEM");
        assertEquals(rsmeta.getColumnName(3), "PROCEDURE_NAME");
        assertEquals(rsmeta.getColumnName(4), "COLUMN_NAME");
        assertEquals(rsmeta.getColumnName(5), "COLUMN_TYPE");
        assertEquals(rsmeta.getColumnName(6), "DATA_TYPE");
        assertEquals(rsmeta.getColumnName(7), "TYPE_NAME");
        assertEquals(rsmeta.getColumnName(8), "PRECISION");
        assertEquals(rsmeta.getColumnName(9), "LENGTH");
        assertEquals(rsmeta.getColumnName(10), "SCALE");
        assertEquals(rsmeta.getColumnName(11), "RADIX");
        assertEquals(rsmeta.getColumnName(12), "NULLABLE");
        assertEquals(rsmeta.getColumnName(13), "REMARKS");
    }

    @Test public void columnOrderOfgetSchemas() throws SQLException {
        ResultSet rs = meta.getSchemas();
        assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 2);
        assertEquals(rsmeta.getColumnName(1), "TABLE_SCHEM");
        assertEquals(rsmeta.getColumnName(2), "TABLE_CATALOG");
    }

    @Test public void columnOrderOfgetCatalogs() throws SQLException {
        ResultSet rs = meta.getCatalogs();
        assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 1);
        assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
    }

    @Test public void columnOrderOfgetColumnPrivileges() throws SQLException {
        ResultSet rs = meta.getColumnPrivileges(null, null, null, null);
        assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 8);
        assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
        assertEquals(rsmeta.getColumnName(2), "TABLE_SCHEM");
        assertEquals(rsmeta.getColumnName(3), "TABLE_NAME");
        assertEquals(rsmeta.getColumnName(4), "COLUMN_NAME");
        assertEquals(rsmeta.getColumnName(5), "GRANTOR");
        assertEquals(rsmeta.getColumnName(6), "GRANTEE");
        assertEquals(rsmeta.getColumnName(7), "PRIVILEGE");
        assertEquals(rsmeta.getColumnName(8), "IS_GRANTABLE");
    }

    @Test public void columnOrderOfgetTablePrivileges() throws SQLException {
        ResultSet rs = meta.getTablePrivileges(null, null, null);
        assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 7);
        assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
        assertEquals(rsmeta.getColumnName(2), "TABLE_SCHEM");
        assertEquals(rsmeta.getColumnName(3), "TABLE_NAME");
        assertEquals(rsmeta.getColumnName(4), "GRANTOR");
        assertEquals(rsmeta.getColumnName(5), "GRANTEE");
        assertEquals(rsmeta.getColumnName(6), "PRIVILEGE");
        assertEquals(rsmeta.getColumnName(7), "IS_GRANTABLE");
    }

    @Test public void columnOrderOfgetBestRowIdentifier() throws SQLException {
        ResultSet rs = meta.getBestRowIdentifier(null, null, null, 0, false);
        assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 8);
        assertEquals(rsmeta.getColumnName(1), "SCOPE");
        assertEquals(rsmeta.getColumnName(2), "COLUMN_NAME");
        assertEquals(rsmeta.getColumnName(3), "DATA_TYPE");
        assertEquals(rsmeta.getColumnName(4), "TYPE_NAME");
        assertEquals(rsmeta.getColumnName(5), "COLUMN_SIZE");
        assertEquals(rsmeta.getColumnName(6), "BUFFER_LENGTH");
        assertEquals(rsmeta.getColumnName(7), "DECIMAL_DIGITS");
        assertEquals(rsmeta.getColumnName(8), "PSEUDO_COLUMN");
    }

    @Test public void columnOrderOfgetVersionColumns() throws SQLException {
        ResultSet rs = meta.getVersionColumns(null, null, null);
        assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 8);
        assertEquals(rsmeta.getColumnName(1), "SCOPE");
        assertEquals(rsmeta.getColumnName(2), "COLUMN_NAME");
        assertEquals(rsmeta.getColumnName(3), "DATA_TYPE");
        assertEquals(rsmeta.getColumnName(4), "TYPE_NAME");
        assertEquals(rsmeta.getColumnName(5), "COLUMN_SIZE");
        assertEquals(rsmeta.getColumnName(6), "BUFFER_LENGTH");
        assertEquals(rsmeta.getColumnName(7), "DECIMAL_DIGITS");
        assertEquals(rsmeta.getColumnName(8), "PSEUDO_COLUMN");
    }

    @Test public void columnOrderOfgetPrimaryKeys() throws SQLException {
        ResultSet rs;
        ResultSetMetaData rsmeta;

        stat.executeUpdate("create table nopk (c1, c2, c3, c4);");
        stat.executeUpdate("create table pk1 (col1 primary key, col2, col3);");
        stat.executeUpdate("create table pk2 (col1, col2 primary key, col3);");
        stat.executeUpdate("create table pk3 (col1, col2, col3, col4, "
                + "primary key (col2, col3));");

        rs = meta.getPrimaryKeys(null, null, "nopk");
        assertFalse(rs.next());
        rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 6);
        assertEquals(rsmeta.getColumnName(1), "TABLE_CAT");
        assertEquals(rsmeta.getColumnName(2), "TABLE_SCHEM");
        assertEquals(rsmeta.getColumnName(3), "TABLE_NAME");
        assertEquals(rsmeta.getColumnName(4), "COLUMN_NAME");
        assertEquals(rsmeta.getColumnName(5), "KEY_SEQ");
        assertEquals(rsmeta.getColumnName(6), "PK_NAME");
        rs.close();

        rs = meta.getPrimaryKeys(null, null, "pk1");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "col1");
        assertFalse(rs.next());
        rs.close();

        rs = meta.getPrimaryKeys(null, null, "pk2");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "col2");
        assertFalse(rs.next());
        rs.close();

        rs = meta.getPrimaryKeys(null, null, "pk3");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "col2");
        assertTrue(rs.next());
        assertEquals(rs.getString("COLUMN_NAME"), "col3");
        assertFalse(rs.next());
        rs.close();
    }

    /* TODO
    @Test public void columnOrderOfgetImportedKeys() throws SQLException {
    @Test public void columnOrderOfgetExportedKeys() throws SQLException {
    @Test public void columnOrderOfgetCrossReference() throws SQLException {
    @Test public void columnOrderOfgetTypeInfo() throws SQLException {
    @Test public void columnOrderOfgetIndexInfo() throws SQLException {
    @Test public void columnOrderOfgetSuperTypes() throws SQLException {
    @Test public void columnOrderOfgetSuperTables() throws SQLException {
    @Test public void columnOrderOfgetAttributes() throws SQLException {*/

    @Test public void columnOrderOfgetUDTs() throws SQLException {
        ResultSet rs = meta.getUDTs(null, null, null, null);
        assertFalse(rs.next());
        ResultSetMetaData rsmeta = rs.getMetaData();
        assertEquals(rsmeta.getColumnCount(), 7);
        assertEquals(rsmeta.getColumnName(1), "TYPE_CAT");
        assertEquals(rsmeta.getColumnName(2), "TYPE_SCHEM");
        assertEquals(rsmeta.getColumnName(3), "TYPE_NAME");
        assertEquals(rsmeta.getColumnName(4), "CLASS_NAME");
        assertEquals(rsmeta.getColumnName(5), "DATA_TYPE");
        assertEquals(rsmeta.getColumnName(6), "REMARKS");
        assertEquals(rsmeta.getColumnName(7), "BASE_TYPE");
    }

    @Test public void version() throws SQLException {
        assertNotNull(meta.getDatabaseProductVersion());
        assertTrue(
            "pure".equals(meta.getDriverVersion()) ||
            "native".equals(meta.getDriverVersion())
        );
    }
}
