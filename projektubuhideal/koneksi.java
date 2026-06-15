package com.mycompany.projektubuhideal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class koneksi {
    public static Connection getKoneksi(){
        Connection conn = null;
        try {
            String url = "jdbc:mysql://localhost:3306/tb_ideal";
            String user = "root";
            String password = "";
            
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e){
            JOptionPane.showMessageDialog(null, "Gagal koneksi database: " + e.getMessage());
        }
        return conn;
    }
}