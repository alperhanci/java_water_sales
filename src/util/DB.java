package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class DB {

    private String path = "jdbc:sqlite:db/WaterSales.db";
    public Connection conn = null;
    public static List<String> ls = new ArrayList<>();

    public DB() {
        try {
            //connections to db
            conn = DriverManager.getConnection(path); // burası yanlış olursa aşağıyı yazdırmaz bile.
            System.out.println("Connect Success");
        } catch (Exception e) {
            System.err.println("Connect Error : " + e);
        }
    }

    // ADMIN LOGIN FUNCTION
    public List<String> login(String userName, String pass) { // Kullanıcıdan aldığımız değerleri DB'de var mı yok mu işlemini yaptırıyoruz.

        try {
            String query = "SELECT * FROM admin WHERE mail = ? and pass = ? "; // Varlığını kontrol etmeliyiz pass & email'in.
            PreparedStatement pre = conn.prepareStatement(query); // preden dönen prametreleri ata!
            pre.setString(1, userName);
            pre.setString(2, pass);
            ResultSet rs = pre.executeQuery();

            if (rs.next()) { // True dönüyorsa, admin doğru giriş yapmıştır demek.
                ls.add("" + rs.getInt("aid")); // list içi data ile dolduruldu.
                ls.add(rs.getString("name"));
                ls.add(rs.getString("mail"));
                ls.add(rs.getString("pass"));
            }
            pre.close();
            conn.close();

        } catch (Exception e) {
            System.err.println("Login error: " + e);
        }
        return ls;
    }

    // DATA CUSTOMER FUNCTION
    public DefaultTableModel fncAllCustomer(String txt) {
        DefaultTableModel dtm = new DefaultTableModel();

        String q = "";
        if (txt.equals("")) {
            q = "select * from customer"; // boş ise ona göre işlem yaptırdık. Yani DB'den gelen datalar saf şekilde gözükecek.
        } else {
            q = "SELECT * FROM customer WHERE name like '%" + txt + "%' or mail like '%" + txt + "%' or tel like '%" + txt + "%'";
        }

        dtm.addColumn("Cid");  // Column'ları oluştrduk ve aşağıda içlerini dolduracağız.
        dtm.addColumn("Name");
        dtm.addColumn("Mail ");
        dtm.addColumn("Tel");
        dtm.addColumn("Address");

        try {
            PreparedStatement pre = conn.prepareStatement(q); // dataları getirir, bu iki satır sayesinde.
            ResultSet rs = pre.executeQuery();

            while (rs.next()) {
                int cid = rs.getInt("cid");
                String nm = rs.getString("name");
                String mail = rs.getString("mail");
                String tl = rs.getString("tel");
                String adr = rs.getString("address");
                Object[] row = {cid, nm, mail, tl, adr};
                dtm.addRow(row);
                //System.out.println("name: " +rs.getString("name")); // sutunü yazıp değeri yazdırttım.
            }
            pre.close();
            conn.close();

        } catch (Exception e) {
            System.err.println("fncAllCustomer error: " + e);
        }

        // Add column
        return dtm;
    }

    // CUSTOMER INSTERT
    public int customerInsert(String name, String mail, String tel, String address) {
        int statu = 0;

        try {
            String query = "insert into customer values(null,?,?,?,?)"; // tabloya bak db'den. ilki id ondan null;
            PreparedStatement pre = conn.prepareStatement(query);
            pre.setString(1, name);
            pre.setString(2, mail);
            pre.setString(3, tel);
            pre.setString(4, address);
            statu = pre.executeUpdate(); // sayısını atıyorum statüye. doğru gelmişse sayısı artacak.

            pre.close();
            conn.close();

        } catch (Exception e) {
            System.err.println("customerError: " + e);
        }

        return statu;
    }

    // CUSTOMER DELETE
    public int customerDelete(int cid) {
        int statu = 0;
        try {
            String query = "delete from customer where cid = ?";
            PreparedStatement pre = conn.prepareStatement(query);
            pre.setInt(1, cid);
            statu = pre.executeUpdate();

            pre.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("customer delete error: " + e);
        }
        return statu;
    }

    //UPDATE - inserti aldım. cid lazım ama ekledim params a
    public int customerUpdate(int cid, String name, String mail, String tel, String address) {
        int statu = 0;

        try {
            String query = "update customer set name = ?, mail = ? , tel = ?, address = ? where cid = ?"; // tabloya bak db'den. ilki id ondan null;
            PreparedStatement pre = conn.prepareStatement(query);
            pre.setString(1, name);
            pre.setString(2, mail);
            pre.setString(3, tel);
            pre.setString(4, address);
            pre.setInt(5, cid);
            statu = pre.executeUpdate(); // sayısını atıyorum statüye. doğru gelmişse sayısı artacak.

            pre.close();
            conn.close();

        } catch (Exception e) {
            System.err.println("customerError: " + e);
        }

        return statu;
    }

    // DATA CUSTOMER FUNCTION
    public DefaultTableModel fncAllOrder() {
        DefaultTableModel dtm = new DefaultTableModel();

        String q = "SELECT * FROM orderWater INNER JOIN customer on orderWater.cid = customer.cid where statu != 3";

        dtm.addColumn("OID");  // Column'ları oluştrduk ve aşağıda içlerini dolduracağız.
        dtm.addColumn("Name");
        dtm.addColumn("Tel");
        dtm.addColumn("Address");
        dtm.addColumn("Size");
        dtm.addColumn("Statu");
        dtm.addColumn("Date");

        try {
            PreparedStatement pre = conn.prepareStatement(q); // dataları getirir, bu iki satır sayesinde.
            ResultSet rs = pre.executeQuery();

            while (rs.next()) {
                int oid = rs.getInt("oid");
                String name = rs.getString("name");
                String tel = rs.getString("tel");
                String address = rs.getString("address");
                int size = rs.getInt("size");
                int statu = rs.getInt("statu");
                String statuString = "";
                if (statu == 1) {
                    statuString = "in progress";
                }
                if (statu == 2) {
                    statuString = "delivery";
                }

                String date = rs.getString("date");

                Object[] row = {oid, name, tel, address, size, statuString, date};
                dtm.addRow(row);
                //System.out.println("name: " +rs.getString("name")); // sutunü yazıp değeri yazdırttım.
            }
            pre.close();
            conn.close();

        } catch (Exception e) {
            System.err.println("fncAllCustomer error: " + e);
        }

        // Add column
        return dtm;
    }

    // NEW ORDER!
    public int newOrder(int cid, int size) {
        //vt bağlan
        int statu = -1;
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            String ts = sdf.format(timestamp);

            String query = "insert into orderWater values (null,?,?,1,'" + ts + "' )";
            PreparedStatement pre = conn.prepareStatement(query); // dataları getirir, bu iki satır sayesinde.
            pre.setInt(1, cid);
            pre.setInt(2, size);
            statu = pre.executeUpdate();
            
            pre.close();
            conn.close();
        } catch (Exception e) {
            System.err.println("newOrder error" + e);
        }
        return statu;
    }

    // ORDER STATU CHANGE 
    public int orderStatuChange(int oid, int newStatu) {
        int statu = -1;
        try {
            String query = "update orderWater set statu = ? where oid = ?"; // order ıd ye ait satırı bul, daha sonra statu değerini change et.
            PreparedStatement pre = conn.prepareStatement(query);
            pre.setInt(1,newStatu);
            pre.setInt(2, oid); // veritabanında etkilemedik istediğim anları yazdım.
            statu = pre.executeUpdate();
            
        } catch (Exception e) {
            System.err.println("Order Statu Change Error: " + e);
        }

        return statu;
    }
}
