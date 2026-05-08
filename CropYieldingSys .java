package crop;
import javax.swing.*;
import javax.swing.JCheckBox;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.sql.*;
import java.util.*;

public class CropYieldingSyst{
    public static void main(String[] args) {
        new Dashboard();
    }
}
class Dashboard {
    Dashboard() {
        JFrame f = new JFrame("CROP YIELDING PREDICTION SYSTEM");
        f.setSize(700, 450);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setLayout(null);
       // ImageIcon bgIcon = new ImageIcon("F:\\images.jpg"); 
        JLabel bg = new JLabel(bgIcon);
        bg.setBounds(0, 0, f.getWidth(), f.getHeight());
        f.setContentPane(bg);
        bg.setLayout(null);
        f.addComponentListener(new ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                Image img = bgIcon.getImage().getScaledInstance(f.getWidth(), f.getHeight(), Image.SCALE_SMOOTH);
                bg.setIcon(new ImageIcon(img));
                bg.setBounds(0, 0, f.getWidth(), f.getHeight());
            }
        });
        JLabel title = new JLabel("CROP YIELDING PREDICTION SYSTEM", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(Color.BLACK);
        title.setBounds(50, 30, 600, 30);
        f.add(title);

        JButton btnCrop = new JButton("Fertilizer");
        btnCrop.setBounds(200, 90, 300, 40);
        f.add(btnCrop);

        JButton btnGrowth = new JButton("Expected Growth Prediction");
        btnGrowth.setBounds(200, 150, 300, 40);
        f.add(btnGrowth);

        JButton btnSoilRain = new JButton("Crops by Soil & Rainfall");
        btnSoilRain.setBounds(200, 210, 300, 40);
        f.add(btnSoilRain);

        JButton btnAdmin = new JButton("Admin Mode");
        btnAdmin.setBounds(200, 270, 300, 40);
        f.add(btnAdmin);

        JButton btnExit = new JButton("Exit");
        btnExit.setBounds(200, 330, 300, 40);
        f.add(btnExit);

        btnCrop.addActionListener(e -> { 
        	f.dispose();
        	new CropRecommendationFrame(); 
        	});
        btnGrowth.addActionListener(e -> { 
        	f.dispose();
        	new ExpectedGrowthFrame();
        	});
        btnSoilRain.addActionListener(e -> { 
        	f.dispose(); 
        	new SoilRainCropFrame();
        	});
        btnAdmin.addActionListener(e -> { 
        	f.dispose();
        	new AdminLoginFrame();
        	});
        btnExit.addActionListener(e -> System.exit(0));

        f.setVisible(true);
    }
}
class CropRecommendationFrame {
    CropRecommendationFrame() {
        JFrame f = new JFrame("Fertilizer");
        f.setSize(900,600);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel(null);
        p.setBackground(new Color(230, 255, 230));   
        JLabel lType = new JLabel("Crop Type:");
        lType.setBounds(50, 30, 120, 25);
        p.add(lType);
      
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Select","Cereal","Vegetable","Fruit","Pulses","Oilseeds"});
        cbType.setBounds(180, 30, 150, 25);
        p.add(cbType);

        JLabel lCrop = new JLabel("Crop Name:");
        lCrop.setBounds(50, 70, 120, 25);
        p.add(lCrop);
        JComboBox<String> cbCrop = new JComboBox<>(new String[]{"Select"});
        cbCrop.setBounds(180, 70, 150, 25);
        p.add(cbCrop);

        JLabel lSoil = new JLabel("Soil Type:");
        lSoil.setBounds(50, 110, 120, 25);
        p.add(lSoil);
        JComboBox<String> cbSoil = new JComboBox<>(new String[]{"Select","Alluvial","Black","Red","Loamy","Sandy","Clay"});
        cbSoil.setBounds(180, 110, 150, 25);
        p.add(cbSoil);

        JLabel lArea = new JLabel("Area (acres):");
        lArea.setBounds(50, 150, 120, 25);
        p.add(lArea);
        JTextField tfArea = new JTextField();
        tfArea.setBounds(180, 150, 150, 25);
        p.add(tfArea);

        JButton bSearch = new JButton("PROCEED");
        bSearch.setBounds(180, 190, 200, 30);
        p.add(bSearch);

        JTextArea ta = new JTextArea();
        ta.setBounds(50, 240, 700, 250);
        ta.setEditable(false);
        p.add(ta);

        JButton back = new JButton("Back");
        back.setBounds(50, 500, 100, 25);
        p.add(back);
             		
        	
     
        cbType.addActionListener(e -> {
            cbCrop.removeAllItems();
            cbCrop.addItem("Select");
            String type = cbType.getSelectedItem().toString();
            if (type.equals("Select")) return;
            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                Connection con = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:ORCL","System","Pass#123"
                );
                PreparedStatement ps = con.prepareStatement(
                        "SELECT DISTINCT crop_name FROM crop_recommendation WHERE LOWER(crop_type)=LOWER(?)"
                );
                ps.setString(1, type);
                ResultSet rs = ps.executeQuery();
                while(rs.next()) cbCrop.addItem(rs.getString("crop_name"));
                rs.close();
                ps.close();
                con.close();
            } catch(Exception ex){
            	ex.printStackTrace();
            	JOptionPane.showMessageDialog(f,"DB Error"); 
            	}
        });

        bSearch.addActionListener(e -> {
            String type = cbType.getSelectedItem().toString();
            String crop = cbCrop.getSelectedItem().toString();
            String soil = cbSoil.getSelectedItem().toString();
            String areaStr = tfArea.getText().trim();

            if(type.equals("Select")||crop.equals("Select")||soil.equals("Select")||areaStr.isEmpty()){
                JOptionPane.showMessageDialog(f,"Please fill all fields","Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            double area;
            try{ area=Double.parseDouble(areaStr); if(area<=0) throw new NumberFormatException(); }
            catch(NumberFormatException nfe){ JOptionPane.showMessageDialog(f,"Enter valid area"); return; }

            try{
                Class.forName("oracle.jdbc.driver.OracleDriver");
                Connection con = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:ORCL","username","Password"
                );
                PreparedStatement ps = con.prepareStatement(
                        "SELECT * FROM crop_recommendation WHERE LOWER(crop_type)=LOWER(?) AND LOWER(crop_name)=LOWER(?) AND LOWER(soil_type)=LOWER(?)"
                );
                ps.setString(1,type); ps.setString(2,crop); ps.setString(3,soil);
                ResultSet rs = ps.executeQuery();

                if(rs.next()){
                    double fertPerAcre = 50; 
                    double pestPerAcre = 0.5;
                    double fertPricePerKg = 40;
                    double pestPricePerLiter = 300;

                    double totalFert = fertPerAcre * area;
                    double totalPest = pestPerAcre * area;
                    double totalPrice = totalFert*fertPricePerKg + totalPest*pestPricePerLiter;

                    ta.setText(String.format(
                            "CROP: %-10s \nSOIL: %-10s \nAREA: %.2f acres\n", crop, soil, area) +
                            String.format("Fertilizer: %-15s  Quantity: %.2f kg  Price: ₹%.2f\n", rs.getString("fertilizer"), totalFert, totalFert*fertPricePerKg) +
                            String.format("Pesticide: %-15s  Quantity: %.2f liters  Price: ₹%.2f\n", rs.getString("pesticide"), totalPest, totalPest*pestPricePerLiter) +
                            String.format("Soil pH: %s\n", rs.getString("PH")) +
                            String.format("Total Price: ₹%.2f\n", totalPrice)
                    );
                } else ta.setText("No record found!");
                rs.close(); ps.close(); con.close();
            }catch(Exception ex){
                ex.printStackTrace(); 
                ta.setText("Database Error"); 
            }
        });

        back.addActionListener(e -> {
            f.dispose();
            new Dashboard(); });
        f.add(p);
        f.setVisible(true);
    }
}

class GrowthPredictor {
    private double b0 = 0;
    private double bSoil = 0;
    private double bRain = 0;
    private double bArea = 0;
    private double bPrevYield = 1; 
    private double normalizeRainfall(double rainfall) {
        return Math.min(Math.max(rainfall / 2000.0, 0), 1);
    }

    private double normalizePrevYield(double prevYield) {
        return prevYield / 2000.0;
    }
    private double predictRegression(double soilIndex, double rainfall, double area, double prevYield) {

        double R_norm = normalizeRainfall(rainfall);
        double P_norm = normalizePrevYield(prevYield);

        double yieldPerAcre =
                b0
              + bSoil * soilIndex
              + bRain * R_norm
              + bPrevYield * P_norm;

        if (yieldPerAcre < 0) yieldPerAcre = 0;

        return yieldPerAcre * area;
    }
    private double predictFallback(double soilIndex, double rainfall, double area, double prevYield) {

        double R_norm = normalizeRainfall(rainfall);
        double P_norm = normalizePrevYield(prevYield);

        double yieldPerAcre =
                (prevYield * soilIndex * (0.5 + R_norm));

        return yieldPerAcre * area;
    }
    public double predict(double soilIndex, double rainfall, double area, double prevYield) {

        if (hasRegressionData()) {
            return predictRegression(soilIndex, rainfall, area, prevYield);
        } else {
            return predictFallback(soilIndex, rainfall, area, prevYield);
        }
    }


    private boolean hasRegressionData() {
        return (bSoil != 0 || bRain != 0 || bArea != 0);
    }
    public void setRegressionCoefficients(double b0, double bSoil, double bRain, double bArea, double bPrevYield) {
        this.b0 = b0;
        this.bSoil = bSoil;
        this.bRain = bRain;
        this.bArea = bArea;
        this.bPrevYield = bPrevYield;
    }
}
class ExpectedGrowthFrame {
    ExpectedGrowthFrame() {
        JFrame f = new JFrame("Expected Growth Prediction");
        f.setSize(900, 600);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel p = new JPanel(null);
        p.setBackground(new Color(210, 245, 210));
        f.add(p);
        JLabel lType = new JLabel("Crop Type:");
        lType.setBounds(50,30,120,25);
        p.add(lType);

        JComboBox<String> cbType = new JComboBox<>(new String[]{
                "Select","Cereal","Vegetable","Fruit","Pulses","Oilseeds"
        });
        cbType.setBounds(180,30,150,25);
        p.add(cbType);

        JLabel lCrop = new JLabel("Crop Name:");
        lCrop.setBounds(50,70,120,25);
        p.add(lCrop);

        JComboBox<String> cbCrop = new JComboBox<>(new String[]{"Select"});
        cbCrop.setBounds(180,70,150,25);
        p.add(cbCrop);

        JLabel lSoil = new JLabel("Soil Type:");
        lSoil.setBounds(50,110,120,25);
        p.add(lSoil);

        JComboBox<String> cbSoil = new JComboBox<>(new String[]{
                "Select","Alluvial","Black","Red","Loamy","Sandy","Clay"
        });
        cbSoil.setBounds(180,110,150,25);
        p.add(cbSoil);

        JLabel lRain = new JLabel("Rainfall (mm):");
        lRain.setBounds(50,150,120,25);
        p.add(lRain);

        JTextField tfRain = new JTextField();
        tfRain.setBounds(180,150,150,25);
        p.add(tfRain);

        JLabel lArea = new JLabel("Area (acres):");
        lArea.setBounds(50,190,120,25);
        p.add(lArea);

        JTextField tfArea = new JTextField();
        tfArea.setBounds(180,190,150,25);
        p.add(tfArea);

        JButton calc = new JButton("Calculate Expected Growth");
        calc.setBounds(180,230,220,30);
        p.add(calc);

        JTextArea ta = new JTextArea();
        ta.setBounds(50,280,780,200);
        ta.setEditable(false);
        p.add(ta);

        JButton back = new JButton("Back");
        back.setBounds(50,500,100,25);
        p.add(back);

        back.addActionListener(e -> {
            f.dispose();
            new Dashboard();
        });
        cbType.addActionListener(e -> {
            cbCrop.removeAllItems();
            cbCrop.addItem("Select");

            String type = cbType.getSelectedItem().toString();
            if (type.equals("Select")) return;

            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                Connection con = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:ORCL","username","password"
                );

                PreparedStatement ps = con.prepareStatement(
                        "SELECT DISTINCT crop_name FROM crop_recommendation WHERE LOWER(crop_type)=LOWER(?)"
                );
                ps.setString(1, type);
                ResultSet rs = ps.executeQuery();

                while (rs.next())
                    cbCrop.addItem(rs.getString("crop_name"));

                rs.close(); ps.close(); con.close();

            } catch(Exception ex) {
                JOptionPane.showMessageDialog(f,"Database error");
            }
        });

        calc.addActionListener(e -> {

            try {

                String crop = cbCrop.getSelectedItem().toString();
                String soil = cbSoil.getSelectedItem().toString();
                String type = cbType.getSelectedItem().toString();

                if (crop.equals("Select") || soil.equals("Select") || type.equals("Select")) {
                    JOptionPane.showMessageDialog(f,"Select all fields");
                    return;
                }

                double rainfall = Double.parseDouble(tfRain.getText());
                double area = Double.parseDouble(tfArea.getText());
                double prevYield = 0;

                Class.forName("oracle.jdbc.driver.OracleDriver");
                Connection con = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:ORCL", "System", "Pass#123"
                );

                PreparedStatement ps = con.prepareStatement(
                        "SELECT AVG(yield) AS avg_yield FROM crop_history " +
                                "WHERE LOWER(crop_name)=LOWER(?) AND LOWER(soil_type)=LOWER(?)"
                );

                ps.setString(1, crop);
                ps.setString(2, soil);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) prevYield = rs.getDouble("avg_yield");

                rs.close();
                ps.close();
                con.close();

                if (prevYield == 0) {
                    JOptionPane.showMessageDialog(f,
                            "No past yield data found, prediction may be inaccurate.");
                }
                double soilIndex = switch (soil.toLowerCase()) {
                    case "alluvial" -> 0.95;
                    case "black" -> 0.90;
                    case "red" -> 0.75;
                    case "loamy" -> 0.85;
                    case "sandy" -> 0.55;
                    case "clay" -> 0.60;
                    default -> 0.70;
                };
                GrowthPredictor gp = new GrowthPredictor();

                double predictedKg = gp.predict(
                        soilIndex,
                        rainfall,
                        area,
                        prevYield
                );

                ta.setText(
                        "Expected Growth for " + crop + " (" + soil + " soil)\n\n" +
                                "Rainfall: " + rainfall + " mm\n" +
                                "Area: " + area + " acres\n" +
                                "EXPECTED TOTAL YIELD: " + String.format("%.2f", predictedKg) + " kg\n"
                );

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(f,"Enter valid numeric values");
            }
        });

        f.setVisible(true);
    }
}


class SoilRainCropFrame {
    SoilRainCropFrame() {
        JFrame f = new JFrame("Crops by Soil Type");
        f.setSize(900, 600);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel(null);
        p.setBackground(new Color(170, 220, 180));  
        JLabel lSoil = new JLabel("Soil Type:");
        lSoil.setBounds(50,30,120,25);
        p.add(lSoil);

        JComboBox<String> cbSoil = new JComboBox<>(new String[]{"Select","Alluvial","Black","Red","Loamy","Sandy","Clay"});
        cbSoil.setBounds(180,30,150,25);
        p.add(cbSoil);

        JLabel lRain = new JLabel("Rainfall (mm):");
        lRain.setBounds(50,70,120,25);
        p.add(lRain);

        JTextField tfRain = new JTextField();
        tfRain.setBounds(180,70,150,25);
        p.add(tfRain);

        JButton btnSearch = new JButton("Find Crops");
        btnSearch.setBounds(180,110,150,30);
        p.add(btnSearch);

        JTextArea ta = new JTextArea();
        ta.setBounds(50,160,600,200);
        ta.setEditable(false);
        p.add(ta);

        JButton back = new JButton("Back");
        back.setBounds(50,380,100,25);
        p.add(back);

        btnSearch.addActionListener(e -> {
            String soil = cbSoil.getSelectedItem().toString();
            String rainStr = tfRain.getText().trim();

            if(soil.equals("Select") || rainStr.isEmpty()) {
                JOptionPane.showMessageDialog(f,"Please fill all fields");
                return;
            }

            try { Double.parseDouble(rainStr); } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(f,"Enter valid rainfall number");
                return;
            }

            try {
                Class.forName("oracle.jdbc.driver.OracleDriver");
                Connection con = DriverManager.getConnection(
                        "jdbc:oracle:thin:@localhost:1521:ORCL","System","Pass#123"
                );
                PreparedStatement ps = con.prepareStatement(
                        "SELECT crop_name FROM crop_recommendation WHERE LOWER(soil_type)=LOWER(?)"
                );
                ps.setString(1, soil);
                ResultSet rs = ps.executeQuery();

                StringBuilder crops = new StringBuilder("Crops suitable for "+soil+" soil:\n\n");
                while(rs.next()) crops.append(rs.getString("crop_name")).append("\n");

                if(crops.toString().equals("Crops suitable for "+soil+" soil:\n\n"))
                    ta.setText("No crops found for selected soil type.");
                else
                    ta.setText(crops.toString());

                rs.close(); ps.close(); con.close();
            } catch(Exception ex){
            	ex.printStackTrace();
            	ta.setText("Database Error");
            	}
        });

        back.addActionListener(e -> {
        	f.dispose();
        	new Dashboard(); });

        f.add(p);
        f.setVisible(true);
    }
}

class AdminLoginFrame {
    AdminLoginFrame() {
        JFrame f = new JFrame("Admin Login");
        f.setSize(400,250);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel(null);
        p.setBackground(new Color(255, 210, 200));
        JLabel lUser = new JLabel("Username:"); 
        lUser.setBounds(50,30,100,25); 
        p.add(lUser);
        JTextField tfUser = new JTextField(); 
        tfUser.setBounds(150,30,150,25);
        p.add(tfUser);

        JLabel lPass = new JLabel("Password:"); 
        lPass.setBounds(50,70,100,25); 
        p.add(lPass);
        JPasswordField pfPass = new JPasswordField();
        pfPass.setBounds(150,70,150,25);
        p.add(pfPass);

        JButton login = new JButton("Login"); 
        login.setBounds(150,110,100,30); 
        p.add(login);
        JButton back = new JButton("Back");
        back.setBounds(50,110,80,30);
        p.add(back);

        login.addActionListener(e -> {
            String user=tfUser.getText().trim(); 
            String pass=new String(pfPass.getPassword());
            if(user.equals("admin") && pass.equals("admin123")){ f.dispose(); 
            new AdminFrame(); }
            else JOptionPane.showMessageDialog(f,"Invalid Password");
        });

        back.addActionListener(e -> {
        	f.dispose();
        	new Dashboard(); 
        	});
        f.add(p); 
        f.setVisible(true);
    }
}
class AdminFrame {
    AdminFrame() {
        JFrame f = new JFrame("Admin Mode ");
        f.setSize(800,500);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel p = new JPanel(null);

        JLabel lType = new JLabel("Crop Type:"); 
        lType.setBounds(30,30,100,25);
        p.add(lType);
        JComboBox<String> cbType = new JComboBox<>(new String[]{"Cereal","Vegetable","Fruit","Pulses","Oilseeds"});
        cbType.setBounds(140,30,150,25); 
        p.add(cbType);

        JLabel lCrop = new JLabel("Crop Name:"); 
        lCrop.setBounds(30,70,100,25);
        p.add(lCrop);
        JTextField tfCrop = new JTextField(); 
        tfCrop.setBounds(140,70,150,25); 
        p.add(tfCrop);

        JLabel lSoil = new JLabel("Soil Type:"); 
        lSoil.setBounds(30,110,100,25); 
        p.add(lSoil);
        JComboBox<String> cbSoil = new JComboBox<>(new String[]{"Alluvial","Black","Red","Loamy","Sandy","Clay"}); 
        cbSoil.setBounds(140,110,150,25); 
        p.add(cbSoil);

        JLabel lFert = new JLabel("Fertilizer:"); 
        lFert.setBounds(30,150,100,25); 
        p.add(lFert);
        JTextField tfFert = new JTextField();
        tfFert.setBounds(140,150,150,25); 
        p.add(tfFert);

        JLabel lPest = new JLabel("Pesticide:");
        lPest.setBounds(30,190,100,25); 
        p.add(lPest);
        JTextField tfPest = new JTextField(); 
        tfPest.setBounds(140,190,150,25); 
        p.add(tfPest);

        JLabel lPH = new JLabel("Soil PH:");
        lPH.setBounds(30,230,100,25); 
        p.add(lPH);
        JTextField tfPH = new JTextField();
        tfPH.setBounds(140,230,150,25); 
        p.add(tfPH);

        JButton add = new JButton("Add Crop"); 
        add.setBounds(30,280,120,30); 
        p.add(add);
        JButton delete = new JButton("Delete Crop");
        delete.setBounds(160,280,120,30);
        p.add(delete);
        JButton back = new JButton("Back"); 
        back.setBounds(30,330,100,30); 
        p.add(back);

        add.addActionListener(e -> {
            String type=cbType.getSelectedItem().toString();
            String crop=tfCrop.getText().trim();
            String soil=cbSoil.getSelectedItem().toString();
            String fert=tfFert.getText().trim();
            String pest=tfPest.getText().trim();
            String ph=tfPH.getText().trim();
            if(crop.isEmpty()||fert.isEmpty()||pest.isEmpty()||ph.isEmpty()){ 
            	JOptionPane.showMessageDialog(f,"Fill all fields"); 
                return;
                }

            try{
                Class.forName("oracle.jdbc.driver.OracleDriver");
                Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL","username","password");
                PreparedStatement ps = con.prepareStatement("INSERT INTO crop_recommendation(crop_type,crop_name,soil_type,fertilizer,pesticide,PH) VALUES(?,?,?,?,?,?)");
                ps.setString(1,type);
                ps.setString(2,crop);
                ps.setString(3,soil);
                ps.setString(4,fert);
                ps.setString(5,pest); 
                ps.setString(6,ph);
                ps.executeUpdate();
                ps.close(); 
                con.close();
                JOptionPane.showMessageDialog(f,"Crop added successfully!");
            }catch(Exception ex){
            	ex.printStackTrace(); 
            	JOptionPane.showMessageDialog(f,"DB Error"); }
        });

        delete.addActionListener(e -> {
            String crop=tfCrop.getText().trim(); 
            if(crop.isEmpty()){
            	JOptionPane.showMessageDialog(f,"Enter crop name to delete");
            	return; 
            	}
            try{
                Class.forName("oracle.jdbc.driver.OracleDriver");
                Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:ORCL","  username","  password");
                PreparedStatement ps = con.prepareStatement("DELETE FROM crop_recommendation WHERE LOWER(crop_name)=LOWER(?)");
                ps.setString(1,crop);
                int rows = ps.executeUpdate();
                ps.close();
                con.close();
                JOptionPane.showMessageDialog(f, rows>0?"Deleted successfully":"Crop not found");
            }catch(Exception ex){ 
            	ex.printStackTrace(); 
            	JOptionPane.showMessageDialog(f,"DB Error");
            	}
        });

        back.addActionListener(e -> {
        	f.dispose(); 
        	new Dashboard();
        	});

        f.add(p); 
        f.setVisible(true);
    }
}
