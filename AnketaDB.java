//Settings.json imports
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.Console;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONException;

//SQL imports
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.SQLException;

//GUI imports
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JList;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AnketaDB extends JPanel
{
    String connectionAddress = "localhost"; //IP Address of the database
    String username = "";
}
