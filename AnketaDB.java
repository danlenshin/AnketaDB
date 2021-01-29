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
import java.awt.Toolkit;
import java.awt.CardLayout;
import java.awt.Rectangle;
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

        JButton mainSurveyCreationButton = new JButton("Создать Анкету");
        mainSurveyCreationButton.setBounds(100, 20, 250, 50);
        main.add(mainSurveyCreationButton);

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

        JLabel listOfSurveysSearchLabel = new JLabel("Поиск");
        listOfSurveysSearchLabel.setBounds(200, 20, 230, 20);
        listOfSurveys.add(listOfSurveysSearchLabel);

        JLabel listOfSurveysSearchNameLabel = new JLabel("Название", SwingConstants.RIGHT);
        listOfSurveysSearchNameLabel.setBounds(0, 50, 190, 20);
        listOfSurveys.add(listOfSurveysSearchNameLabel);

        JTextField listOfSurveysSearchNameTextField = new JTextField();
        listOfSurveysSearchNameTextField.setBounds(200, 50, 330, 20);
        listOfSurveys.add(listOfSurveysSearchNameTextField);

        JLabel listOfSurveysSearchYearLabel = new JLabel("Год");
        listOfSurveysSearchYearLabel.setBounds(540, 50, 50, 20);
        listOfSurveys.add(listOfSurveysSearchYearLabel);

        JTextField listOfSurveysSearchYearTextField = new JTextField();
        listOfSurveysSearchYearTextField.setBounds(570, 50, 80, 20);
        listOfSurveys.add(listOfSurveysSearchYearTextField);

        JButton listOfSurveysSearchButton = new JButton("Искать");
        listOfSurveysSearchButton.setBounds(300, 80, 200, 50);
        listOfSurveys.add(listOfSurveysSearchButton);

        JList<String> listOfSurveysResultsList = new JList();
        listOfSurveysResultsList.setBounds(150, 160, 500, 300);
        listOfSurveys.add(listOfSurveysResultsList);

        JButton listOfSurveysEditButton = new JButton("Редактировать");
        listOfSurveysEditButton.setBounds(30, 480, 170, 50);
        listOfSurveys.add(listOfSurveysEditButton);

        JButton listOfSurveysDeleteButton = new JButton("Удалить");
        listOfSurveysDeleteButton.setBounds(220, 480, 170, 50);
        listOfSurveys.add(listOfSurveysDeleteButton);

        JButton listOfSurveysFillInButton = new JButton("Выполнить");
        listOfSurveysFillInButton.setBounds(410, 480, 170, 50);
        listOfSurveys.add(listOfSurveysFillInButton);

        JButton listOfSurveysBackButton = new JButton("Назад");
        listOfSurveysBackButton.setBounds(600, 480, 170, 50);
        listOfSurveys.add(listOfSurveysBackButton);

        container.add("listOfSurveys", listOfSurveys);

        /*
        Creating the survey creation window and adding it to cards
        */
        JPanel surveyCreation = new JPanel();
        surveyCreation.setLayout(null);

        JLabel surveyCreationNameLabel = new JLabel("Название");
        surveyCreationNameLabel.setBounds(150, 20, 100, 20);
        surveyCreation.add(surveyCreationNameLabel);

        JTextField surveyCreationNameTextField = new JTextField();
        surveyCreationNameTextField.setBounds(210, 20, 440, 20);
        surveyCreation.add(surveyCreationNameTextField);

        JLabel surveyCreationYearLabel = new JLabel("Год");
        surveyCreationYearLabel.setBounds(180, 50, 100, 20);
        surveyCreation.add(surveyCreationYearLabel);

        JTextField surveyCreationYearTextField = new JTextField();
        surveyCreationYearTextField.setBounds(210, 50, 100, 20);
        surveyCreation.add(surveyCreationYearTextField);

        JLabel surveyCreationLastNameQuestionLabel = new JLabel("Фамилия");
        surveyCreationLastNameQuestionLabel.setBounds(150, 100, 100, 20);
        surveyCreation.add(surveyCreationLastNameQuestionLabel);

        JTextField surveyCreationLastNameQuestionTextField = new JTextField();
        surveyCreationLastNameQuestionTextField.setBounds(210, 100, 440, 20);
        surveyCreationLastNameQuestionTextField.setEnabled(false);
        surveyCreation.add(surveyCreationLastNameQuestionTextField);

        JLabel surveyCreationFirstNameQuestionLabel = new JLabel("Имя", SwingConstants.RIGHT);
        surveyCreationFirstNameQuestionLabel.setBounds(105, 125, 100, 20);
        surveyCreation.add(surveyCreationFirstNameQuestionLabel);

        JTextField surveyCreationFirstNameQuestionTextField = new JTextField();
        surveyCreationFirstNameQuestionTextField.setBounds(210, 125, 440, 20);
        surveyCreationFirstNameQuestionTextField.setEnabled(false);
        surveyCreation.add(surveyCreationFirstNameQuestionTextField);

        JButton surveyCreationCreateButton = new JButton("Сделать");
        surveyCreationCreateButton.setBounds(240, getBounds().height - 80, 150, 30);
        surveyCreation.add(surveyCreationCreateButton);

        JButton surveyCreationCancelButton = new JButton("Отменить");
        surveyCreationCancelButton.setBounds(410, getBounds().height - 80, 150, 30);
        surveyCreation.add(surveyCreationCancelButton);

        JButton surveyCreationAddLongQuestionButton = new JButton("Добавь Длинный Вопрос");
        surveyCreationAddLongQuestionButton.setBounds(240, getBounds().height - 120, 320, 30);
        surveyCreation.add(surveyCreationAddLongQuestionButton);

        JButton surveyCreationAddShortQuestionButton = new JButton("Добавь Короткий Вопрос");
        surveyCreationAddShortQuestionButton.setBounds(240, getBounds().height - 160, 320, 30);
        surveyCreation.add(surveyCreationAddShortQuestionButton);

        container.add("surveyCreation", surveyCreation);
        
        /*
        Navigational ActionListeners
        These ActionListeners are added to buttons which move between panels
        */
        causeToShowCard(mainListOfSurveysButton, "listOfSurveys", this, "Список Анкет");
        causeToShowCard(mainSurveyCreationButton, "surveyCreation", this, "Создание Анкеты");
        causeToShowCard(listOfSurveysBackButton, "main", this, "AnketaDB by Daniel Lenshin");
        causeToShowCard(surveyCreationCancelButton, "main", this, "AnketaDB by Daniel Lenshin");

        /*
        Main Panel ActionListeners
        */
        mainSearchButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                String query = "SELECT id, surveyid, firstname, lastname FROM responses WHERE (firstname LIKE '%" + mainSearchNameTextField.getText() +
                               "%'' AND year = " + mainSearchYearTextField.getText() +
                               "AND surveyid = (SELECT id FROM surveys WHERE name LIKE '%" + mainSearchSurveyTextField.getText() +
                               "%')) OR (lastname LIKE '%" + mainSearchNameTextField.getText() + "AND year = " + mainSearchYearTextField.getText() +
                               "AND surveyid = (SELECT id FROM surveys WHERE name LIKE '%" + mainSearchSurveyTextField.getText() + "%'));";
                
                try
                {
                    results = statement.executeQuery(query); //Executes a query which returns all the rows matching the parameters of the search box
                    String listElement = "";
                    String[] listElements = new String[0];

                    while(results.next())
                    {
                        listElement = results.getString("firstname") + " " + results.getString("lastname") + " | " + 
                    }
                }
                catch(SQLException exception)
                {
                    System.out.println("mainSearchButton query failed");
                }
            }
        });

        //!TEST CODE
        cards.show(container, "main");
        setVisible(true);
        setSize(800, 600);
        surveyCreationCreateButton.setBounds(240, getBounds().height - 80, 150, 30);
        surveyCreationCancelButton.setBounds(410, getBounds().height - 80, 150, 30);
        surveyCreationAddLongQuestionButton.setBounds(240, getBounds().height - 120, 320, 30);
        surveyCreationAddShortQuestionButton.setBounds(240, getBounds().height - 160, 320, 30);
        //!TEST CODE
    }

    //Method which adds an ActionListener to a button which causes it to show a certain card and change the name of the frame
    public void causeToShowCard(JButton button, String cardName, JFrame frame, String newTitle)
    {
        button.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                setSize(800, 600); //In order to reset size if it was changed by survey creation panel
                frame.setTitle(newTitle); //Changes the title of the frame to the new title
                cards.show(container, cardName); //Shows the card with the name cardName
            }
        });
    }

    //Method which adds an object to an array of the same kind of object
    public Object[] pushElementToArray(Object[] array, Object element)
    {
        Object[] newArray = new Object[array.length + 1];

        for(int i = 0; i < array.length; i++)
        {
            newArray[i] = array[i];
        }

        newArray[newArray.length - 1] = element;

        return newArray;
    }

    public static void main(String[] args) throws IOException, SQLException
    {
        //!TEST CODE
        JFrame frame = new AnketaDB();
        //!TEST CODE
    }
}
