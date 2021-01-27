//Settings.json imports
import java.io.File;
import java.io.FileReader;
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
import javax.swing.SwingConstants;
import java.awt.Container;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class AnketaDB extends JFrame
{
    File settingsFile; //File object for settings.json
    FileReader settingsReader; //FileReader object to read settingsFile
    String settingsFileString; //String which contains the contents of settingsFile
    JSONObject settings; //JSONObject which contains a JSONObject of the data in settingsFile

    String username; //String which contains SQL server user username
    String password; //String which contains SQL server user password
    String address; //String which contains SQL server address
    
    Connection databaseConnection; //Connection object which stores the connection to the SQL server
    Statement statement; //Statement object which stores statements to the SQL server
    ResultSet results; //ResultSet object which stores the results of SQL queries

    CardLayout cards; //CardLayout object for creating a card layout
    Container container; //Container object which the card layout will be on

    public AnketaDB() throws IOException, SQLException
    {
        settingsFile = new File("settings.json"); //Sets settingsFile as settings.json
        settingsReader = new FileReader(settingsFile); //Set settingsReader to read from settingsFile
        while(settingsReader.ready()) //Sets settingsFileString as the content in settingsFile (formatted to remove tabs, spaces, and newlines)
        {
            settingsFileString += Character.toString(settingsReader.read());
        }
        settingsFileString = settingsFileString.replaceAll("null", ""); //Removes the initial null character read from settings.json, which is "null" in the settingsFileString
        settings = new JSONObject(settingsFileString.replaceAll("[^\\S]", "")); //Sets settings to a JSONObject with the contents of settingsFileString

        //Sets username, password, and address equal to their counterparts in settings JSONobject
        username = settings.getString("username");
        password = settings.getString("password");
        address = settings.getString("address");

        databaseConnection = DriverManager.getConnection(("jdbc:mysql://" + address), username, password); //Sets databaseConnection as the connection to the SQL server

        cards = new CardLayout(); //Set cards to a new CardLayout object
        container = getContentPane(); //Sets the container to the content pane
        container.setLayout(cards); //Sets the layout of the container to cards
        setResizable(false); //Disables the user from resizing the JFrame
        setSize(800, 600); //Sets the size of the JFrame to 800 x 600 pixels
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Sets it so that the program will close every time the frame is closed
        setTitle("AnketaDB by Daniel Lenshin");

        /*
        Creating the main panel and adding it to cards
        */
        JPanel main = new JPanel(); //Creates new JPanel for the main window
        main.setLayout(null); //Sets the layout of the main panel as null (since no layout manager is being used)

        JButton mainCreateSurveyButton = new JButton("Создать Анкету");
        mainCreateSurveyButton.setBounds(100, 20, 250, 50);
        main.add(mainCreateSurveyButton);

        JButton mainListOfSurveysButton = new JButton("Список Анкет");
        mainListOfSurveysButton.setBounds(400, 20, 250, 50);
        main.add(mainListOfSurveysButton);

        JLabel mainSearchLabel = new JLabel("Поиск", SwingConstants.RIGHT);
        mainSearchLabel.setBounds(100, 70, 230, 50);
        main.add(mainSearchLabel);

        JLabel mainSearchNameLabel = new JLabel("Имя");
        mainSearchNameLabel.setBounds(150, 120, 60, 20);
        main.add(mainSearchNameLabel);

        JTextField mainSearchNameTextField = new JTextField();
        mainSearchNameTextField.setBounds(180, 120, 250, 20);
        main.add(mainSearchNameTextField);

        JLabel mainSearchYearLabel = new JLabel("Год");
        mainSearchYearLabel.setBounds(450, 120, 60, 20);
        main.add(mainSearchYearLabel);

        JTextField mainSearchYearTextField = new JTextField();
        mainSearchYearTextField.setBounds(480, 120, 150, 20);
        main.add(mainSearchYearTextField);

        JLabel mainSearchSurveyLabel = new JLabel("Анкета", SwingConstants.RIGHT);
        mainSearchSurveyLabel.setBounds(95, 150, 80, 20);
        main.add(mainSearchSurveyLabel);

        JTextField mainSearchSurveyTextField = new JTextField();
        mainSearchSurveyTextField.setBounds(180, 150, 450, 20);
        main.add(mainSearchSurveyTextField);

        JButton mainSearchButton = new JButton("Искать");
        mainSearchButton.setBounds(300, 200, 200, 50);
        main.add(mainSearchButton);

        JLabel mainResultsLabel = new JLabel("Результаты", SwingConstants.CENTER);
        mainResultsLabel.setBounds(300, 280, 200, 20);
        main.add(mainResultsLabel);

        JList<String> mainResultsList = new JList();
        mainResultsList.setBounds(150, 300, 500, 175);
        main.add(mainResultsList);

        JButton mainSelectButton = new JButton("Выбрать");
        mainSelectButton.setBounds(300, 490, 200, 50);
        main.add(mainSelectButton);

        container.add("main", main); //Adds main panel to the container

        /*
        Creating the list of surveys panel and adding it to cards
        */
        JPanel listOfSurveys = new JPanel();
        listOfSurveys.setLayout(null);

        

        cards.show(container, "main"); //Displays main panel on startup
        setVisible(true); //Makes the JFrame visible on the screen
    }

    public static void main(String[] args) throws IOException, SQLException
    {
        //!TEST PORTION
        JFrame frame = new AnketaDB();
        //!TEST PORTION
    }
}
