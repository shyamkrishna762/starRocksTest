package test1;

import java.sql.*;

public class Test {
    public static void main(String[] args) {
        String host = "localhost";
        //query_port in fe.conf
        String port = "9030";
        String user = "root";
        //password is empty by default
        String password = "";
        //connect to starrocks
        Connection conn = null;
        try {
            conn = getConn(host, port, user, password, "");
        } catch (Exception e) {
            System.out.println("connect to starrocks failed");
            e.printStackTrace();
            return;
        }
        System.out.println("connect to starrocks successfully");
        //create statement
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("create statement failed");
            e.printStackTrace();
            closeConn(conn);
            return;
        }
        System.out.println("create statement successfully");
        //create database
        try {
            stmt.execute("CREATE DATABASE IF NOT EXISTS db_test");
        } catch (SQLException e) {
            System.out.println("create database failed");
            e.printStackTrace();
            closeStmt(stmt);
            closeConn(conn);
            return;
        }
        System.out.println("create database successfully");
        //set db context
        try {
            stmt.execute("USE db_test");
        } catch (SQLException e) {
            System.out.println("set db context failed");
            e.printStackTrace();
            closeStmt(stmt);
            closeConn(conn);
            return;
        }
        System.out.println("set db context successfully");
        //create table
        try {
            stmt.execute("CREATE TABLE IF NOT EXISTS table_test(siteid INT, citycode SMALLINT, pv BIGINT SUM) " +
                    "AGGREGATE KEY(siteid, citycode) " +
                    "DISTRIBUTED BY HASH(siteid) BUCKETS 10 " +
                    "PROPERTIES(\"replication_num\" = \"1\")");
        } catch (Exception e) {
            System.out.println("create table failed");
            e.printStackTrace();
            closeStmt(stmt);
            closeConn(conn);
            return;
        }
        System.out.println("create table successfully");
        //insert data
        try {
            stmt.execute("INSERT INTO table_test values(1, 2, 3), (4, 5, 6), (1, 2, 4)");
        } catch (Exception e) {
            System.out.println("insert data failed");
            e.printStackTrace();
            closeStmt(stmt);
            closeConn(conn);
            return;
        }
        System.out.println("insert data successfully");
        //query data
        try {
            ResultSet result = stmt.executeQuery("SELECT * FROM table_test");
            System.out.println("data queried is :");
            while (result.next()) {
                int siteid = result.getInt("siteid");
                int citycode = result.getInt("citycode");
                int pv = result.getInt("pv");
                System.out.println("\t" + siteid + "\t" + citycode + "\t" + pv);
            }
        } catch (Exception e) {
            System.out.println("query data failed");
            e.printStackTrace();
            closeStmt(stmt);
            closeConn(conn);
            return;
        }
        //drop database
     /*   try {
            stmt.execute("DROP DATABASE IF EXISTS db_test");
        } catch (Exception e) {
            System.out.println("drop database failed");
            e.printStackTrace();
            closeStmt(stmt);
            closeConn(conn);
            return;
        }*/
        System.out.println("drop database successfully");
        closeStmt(stmt);
        closeConn(conn);
    }
    public static Connection getConn(String host, String port, String user, String password, String database) throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?user=" + user + "&password=" + password;
        return DriverManager.getConnection(url);
    }
    public static void closeConn(Connection conn) {
        try {
            conn.close();
            System.out.println("conn closed");
        } catch (Exception e) {
            System.out.println("close conn failed");
            e.printStackTrace();
        }
    }
    public static void closeStmt(Statement stmt) {
        try {
            stmt.close();
            System.out.println("stmt closed");
        } catch (Exception e) {
            System.out.println("close stmt failed");
            e.printStackTrace();
        }
    }
}