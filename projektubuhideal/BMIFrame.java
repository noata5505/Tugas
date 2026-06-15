/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.projektubuhideal;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;

public class BMIFrame extends JFrame {

    private JTextField txtNama, txtUsia, txtBerat, txtTinggi;
    private JRadioButton rbLaki, rbPerempuan;
    private ButtonGroup bgJK;

    private JLabel lblHasilBMI, lblStatus, lblBeratIdeal, lblSaran;
    private JProgressBar progressBMI;

    private DefaultTableModel tableModel;
    private JTable tabelRiwayat;

    private static final String HOST = "jdbc:mysql://localhost:3306/db_bmi";
    private static final String USER = "root";
    private static final String PASS = "";

    public BMIFrame() {
        setTitle("Aplikasi Pengukur Berat Badan & BMI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 950);
        setLocationRelativeTo(null);
        setResizable(true);
        initComponents();
        muatRiwayat();
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(HOST, USER, PASS);
    }

    private void buatTabel() {
        String sql = """
            CREATE TABLE IF NOT EXISTS riwayat_bmi (
                id      INT AUTO_INCREMENT PRIMARY KEY,
                nama    VARCHAR(100),
                jk      VARCHAR(20),
                usia    INT,
                berat   DOUBLE,
                tinggi  DOUBLE,
                bmi     DOUBLE,
                status  VARCHAR(50),
                tanggal DATETIME DEFAULT CURRENT_TIMESTAMP
            )
            """;
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error buat tabel: " + e.getMessage());
        }
    }

    private void simpanRiwayat(String nama, String jk, int usia,
                                double berat, double tinggi,
                                double bmi, String status) {
        String sql = "INSERT INTO riwayat_bmi (nama, jk, usia, berat, tinggi, bmi, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nama);
            ps.setString(2, jk);
            ps.setInt(3, usia);
            ps.setDouble(4, berat);
            ps.setDouble(5, tinggi);
            ps.setDouble(6, bmi);
            ps.setString(7, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error simpan: " + e.getMessage());
        }
    }

    private void muatRiwayat() {
        tableModel.setRowCount(0);
        String sql = "SELECT nama, jk, usia, berat, tinggi, bmi, status, tanggal " +
                     "FROM riwayat_bmi ORDER BY tanggal DESC";
        try (Connection c = connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getString("nama"),
                    rs.getString("jk"),
                    rs.getInt("usia"),
                    rs.getDouble("berat") + " kg",
                    rs.getDouble("tinggi") + " cm",
                    String.format("%.1f", rs.getDouble("bmi")),
                    rs.getString("status"),
                    rs.getString("tanggal")
                });
            }
        } catch (SQLException e) {
            System.out.println("Error load riwayat: " + e.getMessage());
        }
    }

    private void initComponents() {
        buatTabel();

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(243, 240, 255));
        mainPanel.setBorder(new EmptyBorder(18, 18, 18, 18));

        mainPanel.add(buatPanelInput());
        mainPanel.add(Box.createVerticalStrut(16));
        mainPanel.add(buatPanelHasil());
        mainPanel.add(Box.createVerticalStrut(16));
        mainPanel.add(buatPanelRiwayat());

        JScrollPane scrollUtama = new JScrollPane(mainPanel);
        scrollUtama.setBorder(null);
        scrollUtama.getVerticalScrollBar().setUnitIncrement(16);

        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        statusBar.setBackground(new Color(238, 237, 254));
        statusBar.setBorder(new MatteBorder(2, 0, 0, 0, new Color(175, 169, 236)));
        JLabel lblStat = new JLabel("● Aplikasi siap digunakan");
        lblStat.setForeground(new Color(60, 52, 137));
        lblStat.setFont(new Font("Arial", Font.PLAIN, 13));
        statusBar.add(lblStat);

        setLayout(new BorderLayout());
        add(scrollUtama, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private JPanel buatPanelInput() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(175, 169, 236), 2, true),
            new EmptyBorder(0, 0, 16, 0)
        ));

        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        header.setBackground(new Color(127, 119, 221));
        JLabel lblHeader = new JLabel("  Input Data Pasien");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(lblHeader);
        panel.add(header, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(16, 20, 8, 20));
        GridBagConstraints gbc = new GridBagConstraints();

        txtNama = buatTextField();
        tambahForm(form, gbc, "Nama :", txtNama, null, 0);

        rbLaki = new JRadioButton("Laki-laki", true);
        rbPerempuan = new JRadioButton("Perempuan");
        rbLaki.setBackground(Color.WHITE);
        rbPerempuan.setBackground(Color.WHITE);
        rbLaki.setForeground(new Color(60, 52, 137));
        rbPerempuan.setForeground(new Color(60, 52, 137));
        rbLaki.setFont(new Font("Arial", Font.PLAIN, 14));
        rbPerempuan.setFont(new Font("Arial", Font.PLAIN, 14));
        bgJK = new ButtonGroup();
        bgJK.add(rbLaki);
        bgJK.add(rbPerempuan);
        JPanel pnlJK = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlJK.setBackground(Color.WHITE);
        pnlJK.add(rbLaki);
        pnlJK.add(Box.createHorizontalStrut(16));
        pnlJK.add(rbPerempuan);
        tambahForm(form, gbc, "Jenis Kelamin :", pnlJK, null, 1);

        txtUsia = buatTextField();
        tambahForm(form, gbc, "Usia :", txtUsia, "tahun", 2);

        txtBerat = buatTextField();
        tambahForm(form, gbc, "Berat Badan :", txtBerat, "kg", 3);

        txtTinggi = buatTextField();
        tambahForm(form, gbc, "Tinggi Badan :", txtTinggi, "cm", 4);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnPanel.setBackground(Color.WHITE);

        JButton btnHitung = buatTombol("  Hitung BMI  ", new Color(29, 158, 117));
        JButton btnReset  = buatTombol("  Reset  ", new Color(216, 90, 48));

        btnHitung.addActionListener(e -> hitungBMI());
        btnReset.addActionListener(e -> resetForm());

        btnPanel.add(btnHitung);
        btnPanel.add(btnReset);

        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(18, 5, 0, 5);
        form.add(btnPanel, gbc);

        panel.add(form, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatPanelHasil() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(93, 202, 165), 2, true),
            new EmptyBorder(0, 0, 16, 0)
        ));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        header.setBackground(new Color(29, 158, 117));
        JLabel lblHeader = new JLabel("  Hasil Perhitungan BMI");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(lblHeader);
        panel.add(header, BorderLayout.NORTH);

        JPanel isi = new JPanel();
        isi.setLayout(new BoxLayout(isi, BoxLayout.Y_AXIS));
        isi.setBackground(Color.WHITE);
        isi.setBorder(new EmptyBorder(16, 20, 8, 20));

        JPanel rbox = new JPanel(new GridLayout(4, 2, 8, 10));
        rbox.setBackground(new Color(225, 245, 238));
        rbox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(93, 202, 165), 1, true),
            new EmptyBorder(14, 16, 14, 16)
        ));

        rbox.add(buatLabelHasil("Nilai BMI :"));
        lblHasilBMI = new JLabel("-");
        lblHasilBMI.setFont(new Font("Arial", Font.BOLD, 22));
        lblHasilBMI.setForeground(new Color(15, 110, 86));
        rbox.add(lblHasilBMI);

        rbox.add(buatLabelHasil("Status :"));
        lblStatus = new JLabel("-");
        lblStatus.setFont(new Font("Arial", Font.BOLD, 15));
        lblStatus.setForeground(new Color(4, 52, 44));
        rbox.add(lblStatus);

        rbox.add(buatLabelHasil("Berat Ideal :"));
        lblBeratIdeal = new JLabel("-");
        lblBeratIdeal.setFont(new Font("Arial", Font.PLAIN, 15));
        lblBeratIdeal.setForeground(new Color(4, 52, 44));
        rbox.add(lblBeratIdeal);

        rbox.add(buatLabelHasil("Keterangan :"));
        lblSaran = new JLabel("-");
        lblSaran.setFont(new Font("Arial", Font.ITALIC, 14));
        lblSaran.setForeground(new Color(15, 110, 86));
        rbox.add(lblSaran);

        isi.add(rbox);
        isi.add(Box.createVerticalStrut(14));

        JLabel lblProgress = new JLabel("Posisi BMI kamu:");
        lblProgress.setFont(new Font("Arial", Font.BOLD, 13));
        lblProgress.setForeground(new Color(8, 80, 65));
        lblProgress.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(lblProgress);
        isi.add(Box.createVerticalStrut(6));

        progressBMI = new JProgressBar(0, 40);
        progressBMI.setValue(0);
        progressBMI.setStringPainted(true);
        progressBMI.setString("BMI: -");
        progressBMI.setForeground(new Color(29, 158, 117));
        progressBMI.setBackground(new Color(225, 245, 238));
        progressBMI.setPreferredSize(new Dimension(0, 28));
        progressBMI.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        progressBMI.setFont(new Font("Arial", Font.BOLD, 12));
        progressBMI.setAlignmentX(Component.LEFT_ALIGNMENT);
        isi.add(progressBMI);

        panel.add(isi, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buatPanelRiwayat() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(55, 138, 221), 2, true),
            new EmptyBorder(0, 0, 12, 0)
        ));

        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 10));
        header.setBackground(new Color(55, 138, 221));
        JLabel lblHeader = new JLabel("  Riwayat Pengukuran");
        lblHeader.setForeground(Color.WHITE);
        lblHeader.setFont(new Font("Arial", Font.BOLD, 16));
        header.add(lblHeader);
        panel.add(header, BorderLayout.NORTH);

        String[] kolom = {"Nama", "JK", "Usia", "Berat", "Tinggi", "BMI", "Status", "Tanggal"};
        tableModel = new DefaultTableModel(kolom, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tabelRiwayat = new JTable(tableModel);
        tabelRiwayat.setFont(new Font("Arial", Font.PLAIN, 13));
        tabelRiwayat.setRowHeight(28);
        tabelRiwayat.getTableHeader().setBackground(new Color(55, 138, 221));
        tabelRiwayat.getTableHeader().setForeground(Color.WHITE);
        tabelRiwayat.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        tabelRiwayat.setSelectionBackground(new Color(225, 245, 238));
        tabelRiwayat.setGridColor(new Color(220, 220, 235));

        // Lebar kolom
        tabelRiwayat.getColumnModel().getColumn(0).setPreferredWidth(110); // Nama
        tabelRiwayat.getColumnModel().getColumn(1).setPreferredWidth(70);  // JK
        tabelRiwayat.getColumnModel().getColumn(2).setPreferredWidth(50);  // Usia
        tabelRiwayat.getColumnModel().getColumn(3).setPreferredWidth(70);  // Berat
        tabelRiwayat.getColumnModel().getColumn(4).setPreferredWidth(70);  // Tinggi
        tabelRiwayat.getColumnModel().getColumn(5).setPreferredWidth(55);  // BMI
        tabelRiwayat.getColumnModel().getColumn(6).setPreferredWidth(140); // Status
        tabelRiwayat.getColumnModel().getColumn(7).setPreferredWidth(140); // Tanggal

        // Warna baris sesuai status
        tabelRiwayat.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                if (!sel) {
                    String status = String.valueOf(t.getValueAt(row, 6));
                    switch (status) {
                        case "Normal"              -> setBackground(new Color(234, 243, 222));
                        case "Kurus (Underweight)" -> setBackground(new Color(250, 238, 218));
                        case "Gemuk (Overweight)"  -> setBackground(new Color(250, 236, 231));
                        case "Obesitas"            -> setBackground(new Color(252, 235, 235));
                        default                    -> setBackground(Color.WHITE);
                    }
                    setForeground(new Color(30, 30, 30));
                }
                return this;
            }
        });

        JScrollPane scroll = new JScrollPane(tabelRiwayat);
        scroll.setPreferredSize(new Dimension(0, 200));
        scroll.setBorder(new EmptyBorder(10, 12, 0, 12));
        panel.add(scroll, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        btnPanel.setBackground(Color.WHITE);
        JButton btnHapus = buatTombol("  Hapus Semua  ", new Color(226, 75, 74));
        btnHapus.addActionListener(e -> {
            int ok = JOptionPane.showConfirmDialog(this,
                "Yakin hapus semua riwayat?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
            if (ok == JOptionPane.YES_OPTION) {
                try (Connection c = connect(); Statement st = c.createStatement()) {
                    st.execute("DELETE FROM riwayat_bmi");
                    muatRiwayat();
                } catch (SQLException ex) {
                    System.out.println("Error hapus: " + ex.getMessage());
                }
            }
        });
        btnPanel.add(btnHapus);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void tambahForm(JPanel form, GridBagConstraints gbc,
                            String label, JComponent field, String satuan, int row) {
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;
        gbc.gridx = 0; gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(8, 8, 8, 8);
        JLabel lbl = new JLabel(label);
        lbl.setForeground(new Color(60, 52, 137));
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        form.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        form.add(field, gbc);

        if (satuan != null) {
            gbc.gridx = 2;
            gbc.fill = GridBagConstraints.NONE;
            gbc.weightx = 0;
            JLabel lSatuan = new JLabel(satuan);
            lSatuan.setForeground(new Color(83, 74, 183));
            lSatuan.setFont(new Font("Arial", Font.PLAIN, 14));
            form.add(lSatuan, gbc);
        }
    }

    private JTextField buatTextField() {
        JTextField tf = new JTextField(15);
        tf.setBackground(new Color(238, 237, 254));
        tf.setForeground(new Color(38, 33, 92));
        tf.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(175, 169, 236), 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        tf.setFont(new Font("Arial", Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(200, 32));
        return tf;
    }

    private JButton buatTombol(String teks, Color warna) {
        JButton btn = new JButton(teks);
        btn.setBackground(warna);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JLabel buatLabelHasil(String teks) {
        JLabel lbl = new JLabel(teks);
        lbl.setForeground(new Color(8, 80, 65));
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        return lbl;
    }

    private void hitungBMI() {
        try {
            String nama     = txtNama.getText().trim();
            String jk       = rbLaki.isSelected() ? "Laki-laki" : "Perempuan";
            int usia        = Integer.parseInt(txtUsia.getText().trim());
            double berat    = Double.parseDouble(txtBerat.getText().trim());
            double tinggiCm = Double.parseDouble(txtTinggi.getText().trim());
            double tinggi   = tinggiCm / 100.0;

            if (nama.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Nama tidak boleh kosong!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (berat <= 0 || tinggi <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Berat dan tinggi harus lebih dari 0!", "Peringatan",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            double bmi = berat / (tinggi * tinggi);
            String status, saran;
            Color warnaStatus;
            double batasBawah = 18.5;
            double batasAtas = 25.0;
             if(jk.equalsIgnoreCase("Wanita")){
            batasBawah = 18.0;
            batasAtas = 24.0;
        }
     
        if(usia < 18){
            batasBawah -= 1.0;
            batasAtas -= 1.0;
        } else if (usia > 50){
            batasBawah += 1.0;
            batasAtas += 2.0;
        }
        
        
            if (bmi < 18.5) {
                status = "Kurus (Underweight)";
                saran  = "Tambah porsi makan bergizi & konsultasi dokter.";
                warnaStatus = new Color(186, 117, 23);
                progressBMI.setForeground(new Color(186, 117, 23));
            } else if (bmi < 25.0) {
                status = "Normal";
                saran  = "Pertahankan! Olahraga rutin & makan seimbang.";
                warnaStatus = new Color(29, 158, 117);
                progressBMI.setForeground(new Color(29, 158, 117));
            } else if (bmi < 30.0) {
                status = "Gemuk (Overweight)";
                saran  = "Kurangi kalori & perbanyak aktivitas fisik.";
                warnaStatus = new Color(216, 90, 48);
                progressBMI.setForeground(new Color(216, 90, 48));
            } else {
                status = "Obesitas";
                saran  = "Segera konsultasi dengan dokter atau ahli gizi.";
                warnaStatus = new Color(226, 75, 74);
                progressBMI.setForeground(new Color(226, 75, 74));
            }

            double idealMin = 18.5 * tinggi * tinggi;
            double idealMax = 24.9 * tinggi * tinggi;

            lblHasilBMI.setText(String.format("%.1f", bmi));
            lblStatus.setText(status);
            lblStatus.setForeground(warnaStatus);
            lblBeratIdeal.setText(String.format("%.1f kg – %.1f kg", idealMin, idealMax));
            lblSaran.setText(saran);

            progressBMI.setValue((int) Math.min(bmi, 40));
            progressBMI.setString(String.format("BMI: %.1f", bmi));

            simpanRiwayat(nama, jk, usia, berat, tinggiCm, bmi, status);
            muatRiwayat();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Pastikan usia, berat, dan tinggi diisi dengan angka!",
                "Input Tidak Valid", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void resetForm() {
        txtNama.setText("");
        txtUsia.setText("");
        txtBerat.setText("");
        txtTinggi.setText("");
        rbLaki.setSelected(true);
        lblHasilBMI.setText("-");
        lblStatus.setText("-");
        lblStatus.setForeground(new Color(4, 52, 44));
        lblBeratIdeal.setText("-");
        lblSaran.setText("-");
        progressBMI.setValue(0);
        progressBMI.setString("BMI: -");
        progressBMI.setForeground(new Color(29, 158, 117));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BMIFrame().setVisible(true));
    }
}