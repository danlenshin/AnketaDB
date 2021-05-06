/*
AnketaDB
Russian Language Survey Database using MySQL
By Daniel Lenshin
*/

package main.java;

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
import javax.swing.text.JTextComponent;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.Box;
import javax.swing.BoxLayout;
import java.awt.Container;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Dimension;
import java.awt.Color;

public class AnketaDB extends JFrame
{
    //Settings file objects
    File settingsFile; //File object for settings.json
    FileReader settingsReader; //FileReader object to read settingsFile
    String settingsFileString; //String which contains the contents of settingsFile
    JSONObject settings; //JSONObject which contains a JSONObject of the data in settingsFile

    //Settings objects
    String username; //String which contains SQL server user username
    String password; //String which contains SQL server user password
    String address; //String which contains SQL server address
    String schema; //String which contains schema on which the tables are located
    
    //SQL Objects
    Connection databaseConnection; //Connection object which stores the connection to the SQL server
    Statement statement; //Statement object which stores statements to the SQL server
    ResultSet results; //ResultSet object which stores the results of SQL queries

    //GUI Layout Objects
    CardLayout cards; //CardLayout object for creating a card layout
    Container container; //Container object which the card layout will be on

    //GUI List Objects
    Response[] mainResultsListElements = new Response[0]; //Stores the elements of the response search list on the main screen
    Survey[] listOfSurveysResultsListElements = new Survey[0]; //Stores the elements of the list of surveys list on the list of surveys screen

    //Selected objects
    Survey selectedSurvey; //Stores any one survey that the program is dealing with
    Response selectedResponse; //Stores any one response that the program is dealing with

    public AnketaDB() throws IOException, SQLException, JSONException
    {
        try
        {
            settingsFile = new File(System.getProperty("user.dir") + "\\settings.json"); //Sets settingsFile as settings.json
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
            address = settings.getString("host");
            schema = settings.getString("schema");
        }
        catch(IOException | JSONException exception) //Catches errors when retrieving settings
        {
            JOptionPane.showMessageDialog(AnketaDB.this, "Пожалуйста проверьте файл settings.json\n\n<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        try
        {
            databaseConnection = DriverManager.getConnection(("jdbc:mysql://" + address), username, password); //Sets databaseConnection as the connection to the SQL server
            statement = databaseConnection.createStatement(); //Sets statement to a statement on the connected database
            statement.executeUpdate("USE " + schema + ";"); //Sets it so that all future queries query the schema specified in settings.json
        }
        catch(SQLException exception)
        {
            JOptionPane.showMessageDialog(AnketaDB.this, "Пожалуйста проверьте конфигураций о база данных.\n\n<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

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

        JButton mainSurveyCreationButton = new JButton("Создать Анкету"); //Creates a JButton with the specified text
        mainSurveyCreationButton.setBounds(120, 20, 250, 50); //Sets the bounds of the JButton on the panel
        main.add(mainSurveyCreationButton); //Adds the JButton to the panel

        JButton mainListOfSurveysButton = new JButton("Список Анкет");
        mainListOfSurveysButton.setBounds(430, 20, 250, 50);
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

        JList<Response> mainResultsList = new JList<Response>(mainResultsListElements);

        JScrollPane mainResultsListScrollPane = new JScrollPane();
        mainResultsListScrollPane.setBounds(150, 300, 500, 175);
        mainResultsListScrollPane.setViewportView(mainResultsList);
        main.add(mainResultsListScrollPane);

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

        JList<Survey> listOfSurveysResultsList = new JList<Survey>(listOfSurveysResultsListElements);

        JScrollPane listOfSurveysResultsListScrollPane = new JScrollPane();
        listOfSurveysResultsListScrollPane.setBounds(150, 160, 500, 300);
        listOfSurveysResultsListScrollPane.setViewportView(listOfSurveysResultsList);
        listOfSurveys.add(listOfSurveysResultsListScrollPane);

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

        JPanel surveyCreationQuestionsPanel = new JPanel();
        surveyCreationQuestionsPanel.setLayout(new BoxLayout(surveyCreationQuestionsPanel, BoxLayout.Y_AXIS));
        
        JScrollPane surveyCreationScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        surveyCreationScrollPane.setBounds(50, 90, 700, 330);
        surveyCreationScrollPane.setViewportView(surveyCreationQuestionsPanel);
        surveyCreationScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        surveyCreation.add(surveyCreationScrollPane);

        container.add("surveyCreation", surveyCreation);

        /*
        Creating the survey edit window and adding it to cards
        */
        JPanel surveyEdit = new JPanel();
        surveyEdit.setLayout(null);

        JLabel surveyEditNameLabel = new JLabel("Название");
        surveyEditNameLabel.setBounds(150, 20, 100, 20);
        surveyEdit.add(surveyEditNameLabel);

        JTextField surveyEditNameTextField = new JTextField();
        surveyEditNameTextField.setBounds(210, 20, 440, 20);
        surveyEdit.add(surveyEditNameTextField);

        JLabel surveyEditYearLabel = new JLabel("Год");
        surveyEditYearLabel.setBounds(180, 50, 100, 20);
        surveyEdit.add(surveyEditYearLabel);

        JTextField surveyEditYearTextField = new JTextField();
        surveyEditYearTextField.setBounds(210, 50, 100, 20);
        surveyEdit.add(surveyEditYearTextField);

        JButton surveyEditSaveButton = new JButton("Сохранить");
        surveyEditSaveButton.setBounds(200, 500, 150, 50);
        surveyEdit.add(surveyEditSaveButton);

        JButton surveyEditCancelButton = new JButton("Отменить");
        surveyEditCancelButton.setBounds(450, 500, 150, 50);
        surveyEdit.add(surveyEditCancelButton);

        JPanel surveyEditQuestionsPanel = new JPanel();
        surveyEditQuestionsPanel.setLayout(new BoxLayout(surveyEditQuestionsPanel, BoxLayout.Y_AXIS));

        JScrollPane surveyEditScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        surveyEditScrollPane.setBounds(50, 90, 700, 380);
        surveyEditScrollPane.setViewportView(surveyEditQuestionsPanel);
        surveyEditScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        surveyEdit.add(surveyEditScrollPane);

        container.add("surveyEdit", surveyEdit);

        /*
        Creating the survey answer window and adding it to cards
        */
        JPanel surveyAnswer = new JPanel();
        surveyAnswer.setLayout(null);

        JButton surveyAnswerSaveButton = new JButton("Сохранить");
        surveyAnswerSaveButton.setBounds(200, 500, 150, 50);
        surveyAnswer.add(surveyAnswerSaveButton);

        JButton surveyAnswerCancelButton = new JButton("Отменить");
        surveyAnswerCancelButton.setBounds(450, 500, 150, 50);
        surveyAnswer.add(surveyAnswerCancelButton);

        JPanel surveyAnswerQuestionsPanel = new JPanel();
        surveyAnswerQuestionsPanel.setLayout(new BoxLayout(surveyAnswerQuestionsPanel, BoxLayout.Y_AXIS));

        JScrollPane surveyAnswerScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        surveyAnswerScrollPane.setBounds(50, 20, 700, 450);
        surveyAnswerScrollPane.setViewportView(surveyAnswerQuestionsPanel);
        surveyAnswerScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        surveyAnswer.add(surveyAnswerScrollPane);

        container.add("surveyAnswer", surveyAnswer);

        /*
        Creating the response view window and adding it to cards
        */
        JPanel responseView = new JPanel();
        responseView.setLayout(null);

        JButton responseViewEditButton = new JButton("Редактировать");
        responseViewEditButton.setBounds(80, getBounds().height - 100, 200, 50);
        responseView.add(responseViewEditButton);

        JButton responseViewDeleteButton = new JButton("Удалить");
        responseViewDeleteButton.setBounds(300, getBounds().height - 100, 200, 50);
        responseView.add(responseViewDeleteButton);

        JButton responseViewBackButton = new JButton("Назад");
        responseViewBackButton.setBounds(520, getBounds().height - 100, 200, 50);
        responseView.add(responseViewBackButton);

        JPanel responseViewResponsesPanel = new JPanel(); //This panel is viewed in the JScrollPane
        responseViewResponsesPanel.setLayout(new BoxLayout(responseViewResponsesPanel, BoxLayout.Y_AXIS));

        JScrollPane responseViewScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        responseViewScrollPane.setBounds(50, 20, 700, 450);
        responseViewScrollPane.setViewportView(responseViewResponsesPanel);
        responseViewScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        responseView.add(responseViewScrollPane);

        container.add("responseView", responseView);

        /*
        Creating the response edit window and adding it to cards
        */
        JPanel responseEdit = new JPanel();
        responseEdit.setLayout(null);

        JButton responseEditSaveButton = new JButton("Сохранить");
        responseEditSaveButton.setBounds(200, 500, 150, 50);
        responseEdit.add(responseEditSaveButton);

        JButton responseEditCancelButton = new JButton("Отменить");
        responseEditCancelButton.setBounds(450, 500, 150, 50);
        responseEdit.add(responseEditCancelButton);

        JPanel responseEditResponsesPanel = new JPanel();
        responseEditResponsesPanel.setLayout(new BoxLayout(responseEditResponsesPanel, BoxLayout.Y_AXIS));

        JScrollPane responseEditScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        responseEditScrollPane.setBounds(50, 20, 700, 450);
        responseEditScrollPane.setViewportView(responseEditResponsesPanel);
        responseEditScrollPane.getVerticalScrollBar().setUnitIncrement(10);
        responseEdit.add(responseEditScrollPane);

        container.add("responseEdit", responseEdit);

        /*
        Navigational ActionListeners
        These ActionListeners are added to buttons which switch to a different window without manipulating any data
        */
        causeToShowCard(mainListOfSurveysButton, "listOfSurveys", this, "Список Анкет");
        causeToShowCard(listOfSurveysBackButton, "main", this, "AnketaDB by Daniel Lenshin");
        causeToShowCard(surveyCreationCancelButton, "main", this, "AnketaDB by Daniel Lenshin");
        causeToShowCard(responseViewBackButton, "main", this, "AnketaDB by Daniel Lenshin");
        causeToShowCard(surveyEditCancelButton, "listOfSurveys", this, "Список Анкет");
        causeToShowCard(surveyAnswerCancelButton, "listOfSurveys", this, " Список Анкет");

        //Adds action listener to main screen search button
        mainSearchButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                mainResultsListElements = new Response[0]; //Clears mainResultsListElements array

                //SQL Query which searches for responses which match parameters
                String query = "SELECT responses.id"
                        +" FROM responses, surveys WHERE responses.surveyid = surveys.id"
                        +" AND (responses.firstname LIKE '%" + filterString(mainSearchNameTextField.getText().trim()) + "%'"
                        +" OR responses.lastname LIKE '%" + filterString(mainSearchNameTextField.getText().trim()) + "%')"
                        +" AND surveys.surveyname LIKE '%" + filterString(mainSearchSurveyTextField.getText().trim()) + "%'";
                
                //Checks if the year input is a valid int, either finishes query construction or displays error message
                if(isInt(mainSearchYearTextField.getText().trim()))
                {
                    query += " AND surveys.surveyyear = " + Integer.parseInt(mainSearchYearTextField.getText().trim()) + ";"; //Search for responses equal to year if year is an int
                }
                else if(mainSearchYearTextField.getText().trim().isEmpty())
                {
                    query += ";"; //Ignores year if year text box is empty
                }
                else
                {
                    //Displays warning message and does not execute query if year is not empty but invalid
                    JOptionPane.showMessageDialog(AnketaDB.this, "Неправельный формат поля \"Год\": " + mainSearchYearTextField.getText().trim(), "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }              
                
                //Attempts to execute the query and return the results into the JList
                try
                {
                    results = statement.executeQuery(query); //Executes the query
                    int[] responseids = new int[0]; //Int array containing the id of all the responses which are in the query

                    while(results.next()) //Pushes ids to responseids
                    {
                        responseids = pushElementToIntArray(responseids, results.getInt("id"));
                    }

                    if(responseids.length == 0) //Checks if responseids is empty, shows "no responses found" message
                    {
                        JOptionPane.showMessageDialog(AnketaDB.this, "Ответы на анкету с такими даннами не найденны.", "Внимание", JOptionPane.INFORMATION_MESSAGE);
                    }

                    for(int id : responseids) //Adds response objects to JList
                    {
                        //Executes query which returns the row in the responses table with id equal to int id
                        results = statement.executeQuery("SELECT responses.*, surveys.* FROM responses, surveys WHERE responses.surveyid = surveys.id AND responses.id = " + id);

                        //Creates new response array and new response object
                        Response[] newMainResultsListElements = new Response[mainResultsListElements.length + 1];
                        Response newResponse = new Response(results);

                        //Adds elements already in main array to new array
                        for(int i = 0; i < mainResultsListElements.length; i++)
                        {
                            newMainResultsListElements[i] = mainResultsListElements[i];
                        }
                        newMainResultsListElements[newMainResultsListElements.length - 1] = newResponse; //Adds new element to new array

                        mainResultsListElements = newMainResultsListElements; //Sets main array equal to new array
                    }

                    mainResultsList.setListData(mainResultsListElements); //Resets JList
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        //Adds ActionListener to the main screen create survey button
        mainSurveyCreationButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Creates new survey
                selectedSurvey = new Survey("", 0);

                //Adds last name and first name questions to survey
                selectedSurvey.addQuestion(new Question("Фамилия", false));
                selectedSurvey.addQuestion(new Question("Имя", false));
                
                //Clears questions panel
                surveyCreationQuestionsPanel.removeAll();

                //Adds last name and first name textfields
                JTextField lastNameField = new JTextField();
                lastNameField.setText("Фамилия");
                lastNameField.setEditable(false);
                lastNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
                lastNameField.setMaximumSize(new Dimension(1000, 25));
                surveyCreationQuestionsPanel.add(lastNameField);
                surveyCreationQuestionsPanel.add(Box.createRigidArea(new Dimension(0, 25)));

                JTextField firstNameField = new JTextField();
                firstNameField.setText("Имя");
                firstNameField.setEditable(false);
                firstNameField.setAlignmentX(Component.LEFT_ALIGNMENT);
                firstNameField.setMaximumSize(new Dimension(1000, 25));
                surveyCreationQuestionsPanel.add(firstNameField);
                surveyCreationQuestionsPanel.add(Box.createRigidArea(new Dimension(0, 25)));

                //Shows survey creation window
                cards.show(container, "surveyCreation");
            }
        });

        //Adds ActionListener to the main screen select button
        mainSelectButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectedResponse = mainResultsList.getSelectedValue();

                if(selectedResponse == null) //Checks if there is no selected response, displays information message if so
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Что бы выбрать ответ, нажимайте на ответ и потом на кнопка \"Выбрать\".", "Выбор Нет", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                AnketaDB.this.setTitle(selectedResponse.toString()); //Sets window title to response toString

                Question[] responseViewQuestions = selectedResponse.getSurvey().getQuestions(); //Sets responseViewQuestions to the selection questions
                String[] responseViewResponses = selectedResponse.getResponses(); //Sets responseViewResponses to the selection responses
                JLabel[] responseViewLabels = new JLabel[0]; //Stores the labels to be displayed on the response view window

                for(int i = 0; i < responseViewQuestions.length; i++) //Pushes questions and responses to responseViewLabels
                {
                    JLabel[] newResponseViewLabels = new JLabel[responseViewLabels.length + 2];

                    for(int j = 0; j < responseViewLabels.length; j++)
                    {
                        newResponseViewLabels[j] = responseViewLabels[j];
                    }

                    newResponseViewLabels[newResponseViewLabels.length - 2] = new JLabel("<html><body><p style='width: 500px;'><u>" + responseViewQuestions[i].getText() + "</u></p></body></html>");
                    newResponseViewLabels[newResponseViewLabels.length - 1] = new JLabel("<html><body><p style='width: 500px;'>" + responseViewResponses[i] + "</p></body></html>");

                    responseViewLabels = newResponseViewLabels;
                }

                responseViewResponsesPanel.removeAll(); //Cleans responseViewResponsesPanel of any previous labels

                for(int i = 0; i < responseViewLabels.length; i+=2) //Adds labels to response view
                {
                    responseViewResponsesPanel.add(responseViewLabels[i]);
                    responseViewResponsesPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                    responseViewResponsesPanel.add(responseViewLabels[i + 1]);
                    responseViewResponsesPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                }

                //Cleans main screen search list
                mainResultsListElements = new Response[0];
                mainResultsList.setListData(mainResultsListElements);

                cards.show(container, "responseView"); //Shows response view window with the labels added
            }
        });
        
        //Adds ActionListener to the survey creation add short question button
        surveyCreationAddShortQuestionButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Checks if survey is at 30 question limit
                if(selectedSurvey.getQuestions().length >= 30)
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Количество вопросы в анкете не может быть большее 30.", "Внимание", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                //Adds a short question to the survey being created
                selectedSurvey.addQuestion(new Question("", false));

                //Creates JComponents to be added to the questions panel
                JTextField addedTextField = new JTextField();
                addedTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
                addedTextField.setMaximumSize(new Dimension(1000, 25));

                JButton addedButton = new JButton("Удалить Вопрос");
                addedButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                addedButton.setMaximumSize(new Dimension(200, 30));

                //Adds ActionListener to button that causes it to delete question from the survey
                addedButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        //Obtains components of JPanel with questions
                        Component[] surveyCreationQuestionsPanelComponents = surveyCreationQuestionsPanel.getComponents();

                        //Finds index of addedButton in the components
                        int addedButtonIndex = -1;
                        for(int i = 0; i < surveyCreationQuestionsPanelComponents.length; i++)
                        {
                            if(surveyCreationQuestionsPanelComponents[i] == addedButton)
                            {
                                addedButtonIndex = i;
                                break;
                            }
                        }

                        //Creates array of JButtons in the JPanel
                        JButton[] surveyCreationQuestionsPanelButtons = new JButton[0];
                        for(int i = 0; i < surveyCreationQuestionsPanelComponents.length; i++)
                        {
                            if(surveyCreationQuestionsPanelComponents[i] instanceof JButton)
                            {
                                JButton[] newSurveyCreationQuestionsPanelButtons = new JButton[surveyCreationQuestionsPanelButtons.length + 1];
                                for(int j = 0; j < surveyCreationQuestionsPanelButtons.length; j++)
                                {
                                    newSurveyCreationQuestionsPanelButtons[j] = surveyCreationQuestionsPanelButtons[j];
                                }
                                newSurveyCreationQuestionsPanelButtons[newSurveyCreationQuestionsPanelButtons.length - 1] = (JButton)surveyCreationQuestionsPanelComponents[i];
                                surveyCreationQuestionsPanelButtons = newSurveyCreationQuestionsPanelButtons;
                            }
                        }

                        //Finds the index of addedButton in array of JButtons
                        int addedButtonButtonsIndex = -1;
                        for(int i = 0; i < surveyCreationQuestionsPanelButtons.length; i++)
                        {
                            if(surveyCreationQuestionsPanelButtons[i] == addedButton)
                            {
                                addedButtonButtonsIndex = i;
                                break;
                            }
                        }

                        //Creates new questions array without the question associated with addedButton
                        Question[] selectedSurveyQuestions = selectedSurvey.getQuestions();
                        Question[] newSelectedSurveyQuestions = new Question[selectedSurveyQuestions.length - 1];
                        for(int i = 0; i < addedButtonButtonsIndex; i++)
                        {
                            newSelectedSurveyQuestions[i] = selectedSurveyQuestions[i];
                        }
                        for(int i = addedButtonButtonsIndex; i < newSelectedSurveyQuestions.length; i++)
                        {
                            newSelectedSurveyQuestions[i] = selectedSurveyQuestions[i];
                        }

                        //Sets the new survey questions
                        selectedSurvey.setQuestions(newSelectedSurveyQuestions);

                        //Deletes the button and associated components from the JPanel
                        surveyCreationQuestionsPanel.remove(surveyCreationQuestionsPanelComponents[addedButtonIndex - 2]); //JTextField with question text
                        surveyCreationQuestionsPanel.remove(surveyCreationQuestionsPanelComponents[addedButtonIndex - 1]); //Box which separates JTextField and JButton
                        surveyCreationQuestionsPanel.remove(surveyCreationQuestionsPanelComponents[addedButtonIndex]); //JButton
                        surveyCreationQuestionsPanel.remove(surveyCreationQuestionsPanelComponents[addedButtonIndex + 1]); //Box which separates JButton and next question

                        //Refreshes the panel
                        surveyCreationQuestionsPanel.revalidate();
                        surveyCreationQuestionsPanel.repaint();
                    }
                });

                //Adds JComponents to questions panel
                surveyCreationQuestionsPanel.add(addedTextField);
                surveyCreationQuestionsPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                surveyCreationQuestionsPanel.add(addedButton);
                surveyCreationQuestionsPanel.add(Box.createRigidArea(new Dimension(0, 25)));

                //Refreshes JPanel
                surveyCreationQuestionsPanel.revalidate();
                surveyCreationQuestionsPanel.repaint();
            }
        });

        //Adds ActionListener to the survey creation screen add long question button
        surveyCreationAddLongQuestionButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Checks if survey is at 30 question limit
                if(selectedSurvey.getQuestions().length >= 30)
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Количество вопросы в анкете не может быть большее 30.", "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                //Adds a long question to the survey being created
                selectedSurvey.addQuestion(new Question("", true));

                //Creates JComponents to be added to the questions panel
                JTextField addedTextField = new JTextField();
                addedTextField.setAlignmentX(Component.LEFT_ALIGNMENT);
                addedTextField.setPreferredSize(new Dimension(1000, 20));
                addedTextField.setMaximumSize(addedTextField.getPreferredSize());

                JTextField addedAnswerField = new JTextField();
                addedAnswerField.setAlignmentX(Component.LEFT_ALIGNMENT);
                addedAnswerField.setEnabled(false);
                addedAnswerField.setBackground(new Color(185, 185, 185));
                addedAnswerField.setPreferredSize(new Dimension(1000, 50));
                addedAnswerField.setMaximumSize(addedAnswerField.getPreferredSize());

                JButton addedButton = new JButton("Удалить Вопрос");
                addedButton.setAlignmentX(Component.LEFT_ALIGNMENT);
                addedButton.setPreferredSize(new Dimension(200, 25));
                addedButton.setMaximumSize(addedButton.getPreferredSize());

                //Adds ActionListener to button that causes it to delete question from the survey
                addedButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                    {
                        //Obtains components of JPanel with questions
                        Component[] surveyCreationQuestionsPanelComponents = surveyCreationQuestionsPanel.getComponents();

                        //Finds index of addedButton in the components
                        int addedButtonIndex = -1;
                        for(int i = 0; i < surveyCreationQuestionsPanelComponents.length; i++)
                        {
                            if(surveyCreationQuestionsPanelComponents[i] == addedButton)
                            {
                                addedButtonIndex = i;
                                break;
                            }
                        }

                        //Creates array of JButtons in the JPanel
                        JButton[] surveyCreationQuestionsPanelButtons = new JButton[0];
                        for(int i = 0; i < surveyCreationQuestionsPanelComponents.length; i++)
                        {
                            if(surveyCreationQuestionsPanelComponents[i] instanceof JButton)
                            {
                                JButton[] newSurveyCreationQuestionsPanelButtons = new JButton[surveyCreationQuestionsPanelButtons.length + 1];
                                for(int j = 0; j < surveyCreationQuestionsPanelButtons.length; j++)
                                {
                                    newSurveyCreationQuestionsPanelButtons[j] = surveyCreationQuestionsPanelButtons[j];
                                }
                                newSurveyCreationQuestionsPanelButtons[newSurveyCreationQuestionsPanelButtons.length - 1] = (JButton)surveyCreationQuestionsPanelComponents[i];
                                surveyCreationQuestionsPanelButtons = newSurveyCreationQuestionsPanelButtons;
                            }
                        }

                        //Finds the index of addedButton in array of JButtons
                        int addedButtonButtonsIndex = -1;
                        for(int i = 0; i < surveyCreationQuestionsPanelButtons.length; i++)
                        {
                            if(surveyCreationQuestionsPanelButtons[i] == addedButton)
                            {
                                addedButtonButtonsIndex = i;
                                break;
                            }
                        }

                        //Creates new questions array without the question associated with addedButton
                        Question[] selectedSurveyQuestions = selectedSurvey.getQuestions();
                        Question[] newSelectedSurveyQuestions = new Question[selectedSurveyQuestions.length - 1];
                        for(int i = 0; i < addedButtonButtonsIndex; i++)
                        {
                            newSelectedSurveyQuestions[i] = selectedSurveyQuestions[i];
                        }
                        for(int i = addedButtonButtonsIndex; i < newSelectedSurveyQuestions.length; i++)
                        {
                            newSelectedSurveyQuestions[i] = selectedSurveyQuestions[i];
                        }

                        //Sets the new survey questions
                        selectedSurvey.setQuestions(newSelectedSurveyQuestions);

                        //Deletes the button and associated components from the JPanel
                        surveyCreationQuestionsPanel.remove(surveyCreationQuestionsPanelComponents[addedButtonIndex - 2]); //JTextField with question text
                        surveyCreationQuestionsPanel.remove(surveyCreationQuestionsPanelComponents[addedButtonIndex - 1]); //Box which separates JTextField and JButton
                        surveyCreationQuestionsPanel.remove(surveyCreationQuestionsPanelComponents[addedButtonIndex]); //JButton
                        surveyCreationQuestionsPanel.remove(surveyCreationQuestionsPanelComponents[addedButtonIndex + 1]); //Box which separates JButton and Answer Field
                        surveyCreationQuestionsPanel.remove(surveyCreationQuestionsPanelComponents[addedButtonIndex + 2]); //Answer JTextField
                        surveyCreationQuestionsPanel.remove(surveyCreationQuestionsPanelComponents[addedButtonIndex + 3]); //Box which separates Answer Field and next question

                        //Refreshes the panel
                        surveyCreationQuestionsPanel.revalidate();
                        surveyCreationQuestionsPanel.repaint();
                    }
                });

                //Adds JComponents to questions panel
                surveyCreationQuestionsPanel.add(addedTextField);
                surveyCreationQuestionsPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                surveyCreationQuestionsPanel.add(addedButton);
                surveyCreationQuestionsPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                surveyCreationQuestionsPanel.add(addedAnswerField);
                surveyCreationQuestionsPanel.add(Box.createRigidArea(new Dimension(0, 25)));

                //Refreshes JPanel
                surveyCreationQuestionsPanel.revalidate();
                surveyCreationQuestionsPanel.repaint();
            }
        });

        //Adds ActionListener to the survey creation create button
        surveyCreationCreateButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Checks if a valid name and year is entered
                if(surveyCreationNameTextField.getText().trim().isEmpty())
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Поля \"Название\" не может быть пустым.", "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                try
                {
                    Integer.parseInt(surveyCreationYearTextField.getText().trim());
                }
                catch(NumberFormatException exception)
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Неправельный формат поля \"Год\": " + surveyCreationYearTextField.getText().trim(), "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                //Creates array of textfields in the JPanel
                Component[] surveyCreationQuestionsPanelComponents = surveyCreationQuestionsPanel.getComponents();
                JTextField[] surveyCreationQuestionsPanelTextFields = new JTextField[0];
                for(Component component: surveyCreationQuestionsPanelComponents)
                {
                    if(component instanceof JTextField)
                    {
                        JTextField[] newSurveyCreationQuestionsPanelTextFields = new JTextField[surveyCreationQuestionsPanelTextFields.length + 1];
                        for(int i = 0; i < surveyCreationQuestionsPanelTextFields.length; i++)
                        {
                            newSurveyCreationQuestionsPanelTextFields[i] = surveyCreationQuestionsPanelTextFields[i];
                        }
                        newSurveyCreationQuestionsPanelTextFields[newSurveyCreationQuestionsPanelTextFields.length - 1] = (JTextField)component;
                        surveyCreationQuestionsPanelTextFields = newSurveyCreationQuestionsPanelTextFields;
                    }
                }

                //Sets the questions text to the contents of the JPanels
                Question[] createdSurveyQuestions = selectedSurvey.getQuestions();
                for(int i = 0; i < createdSurveyQuestions.length; i++)
                {
                    createdSurveyQuestions[i].setText(surveyCreationQuestionsPanelTextFields[i].getText());
                }
                selectedSurvey.setQuestions(createdSurveyQuestions);

                //Generates SQL statement which inserts the survey into the surveys table
                String update = "INSERT INTO surveys (surveyname, surveyyear, ";

                for(int i = 0; i < selectedSurvey.getQuestions().length -1; i++)
                {
                    update += "q" + (i + 1) + ", q" + (i + 1) + "length, ";
                }
                update += "q" + (selectedSurvey.getQuestions().length) + ", q" + (selectedSurvey.getQuestions().length) + "length) VALUES (\"" + filterString(surveyCreationNameTextField.getText().trim()) + "\", " + filterString(surveyCreationYearTextField.getText().trim()) + ", ";

                for(int i = 0; i < selectedSurvey.getQuestions().length - 1; i++)
                {
                    update += "\"" + filterString(selectedSurvey.getQuestions()[i].getText().trim()) + "\", ";

                    if(selectedSurvey.getQuestions()[i].getIsLong())
                    {
                        update += 1 + ", ";
                    }
                    else
                    {
                        update += 0 + ", ";
                    }
                }
                update += "\"" + filterString(selectedSurvey.getQuestions()[selectedSurvey.getQuestions().length - 1].getText().trim()) + "\", ";
                if(selectedSurvey.getQuestions()[selectedSurvey.getQuestions().length - 1].getIsLong())
                {
                    update += 1 + ");";
                }
                else
                {
                    update += 0 + ");";
                }

                //Inserts the survey into the surveys table
                try
                {
                    //Executes the update
                    statement.executeUpdate(update);

                    //Shows successful survey creation message and goes to main screen
                    JOptionPane.showMessageDialog(AnketaDB.this, "Анкета " + surveyCreationNameTextField.getText() + " успешно сделанно", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    cards.show(container, "main");
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        //Adds ActionListener to the response view edit button
        responseViewEditButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                AnketaDB.this.setTitle("Редактирование: " + selectedResponse.toString()); //Sets the window title to "Editing: [RESPONSE]"

                JLabel[] responseEditLabels = new JLabel[0];
                JTextComponent[] responseEditTextComponents = new JTextComponent[0];
                Question[] responseEditQuestions = selectedResponse.getSurvey().getQuestions();

                //Pushes labels to responseEditLabels and textfields to responseEditTextFields
                for(int i = 0; i < responseEditQuestions.length; i++)
                {
                    JLabel[] newResponseEditLabels = new JLabel[responseEditLabels.length + 1];
                    JTextComponent[] newResponseEditTextComponents = new JTextComponent[responseEditTextComponents.length + 1];

                    for(int j = 0; j < responseEditTextComponents.length; j++)
                    {
                        newResponseEditLabels[j] = responseEditLabels[j];
                        newResponseEditTextComponents[j] = responseEditTextComponents[j];
                    }

                    //Adds label to newResponseEditComponents
                    newResponseEditLabels[newResponseEditLabels.length - 1] = new JLabel("<html><body><p style='width: 500px;'><u>" + responseEditQuestions[i].getText().trim() + "</u></p></body></html>");
                    
                    //Adds either a JTextField or JTextArea to newResponseEditTextComponents depending on question length
                    if(responseEditQuestions[i].getIsLong())
                    {
                        JTextArea addedTextComponent = new JTextArea();
                        addedTextComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
                        addedTextComponent.setLineWrap(true);
                        addedTextComponent.setWrapStyleWord(true);
                        addedTextComponent.setMaximumSize(new Dimension(650, 175));
                        addedTextComponent.setText(selectedResponse.getResponses()[i]);
                        newResponseEditTextComponents[newResponseEditTextComponents.length - 1] = addedTextComponent;
                    }
                    else
                    {
                        JTextField addedTextComponent = new JTextField();
                        addedTextComponent.setMaximumSize(new Dimension(1000, 25));
                        addedTextComponent.setText(selectedResponse.getResponses()[i]);
                        newResponseEditTextComponents[newResponseEditTextComponents.length - 1] = addedTextComponent;
                    }

                    responseEditLabels = newResponseEditLabels;
                    responseEditTextComponents = newResponseEditTextComponents;
                }

                responseEditResponsesPanel.removeAll(); //Clears responseEditResponsesPanel of any previous components

                for(int i = 0; i < responseEditTextComponents.length; i++) //Adds components to responseEditResponsesPanel
                {
                    responseEditResponsesPanel.add(responseEditLabels[i]);
                    responseEditResponsesPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                    responseEditResponsesPanel.add(responseEditTextComponents[i]);
                    responseEditResponsesPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                }

                cards.show(container, "responseEdit"); //Shows response edit window with elements added
            }
        });

        //Adds ActionListener to the response view delete button
        responseViewDeleteButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Asks user if they want to delete the response
                Object[] options = {"Да", "Нет"}; //Change dialog buttons to display in Russian
                int deleteResponse = JOptionPane.showOptionDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + "Вы уверен что вы хотите удалить ответ \"" + selectedResponse.toString() + "\"? (Удалёные данны не будут сохраннены!)", "Внимание", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                //Stops execution if user selects "no" or the X button
                if(deleteResponse == JOptionPane.NO_OPTION || deleteResponse == JOptionPane.CLOSED_OPTION)
                {
                    return;
                }

                //Attempts to delete the selected response from the responses table
                try
                {
                    //Gets the id of the response to be deleted
                    int deletedid = selectedResponse.getSQLId(databaseConnection);

                    //Deletes the response from the responses table
                    statement.executeUpdate("DELETE FROM responses WHERE responses.id = " + deletedid + ";");

                    JOptionPane.showMessageDialog(AnketaDB.this, "Ответ \"" + selectedResponse.toString() + "\" удалён.", "Удаление завершено", JOptionPane.INFORMATION_MESSAGE);

                    cards.show(container, "main"); //Shows main window after deletion
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        //Adds ActionListener to the response edit save button
        responseEditSaveButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Component[] responseEditResponsesPanelComponents = responseEditResponsesPanel.getComponents(); //Gets all the components in the response edit responses panel

                //Filters components array into new array with only the JTextComponents
                JTextComponent[] responseEditResponsesPanelTextComponents = new JTextComponent[0];
                for(int i = 0; i < responseEditResponsesPanelComponents.length; i++)
                {
                    if(responseEditResponsesPanelComponents[i] instanceof JTextComponent) //Pushes element to text components array if it is a text component
                    {
                        JTextComponent[] newResponseEditResponsesPanelTextComponents = new JTextComponent[responseEditResponsesPanelTextComponents.length + 1];

                        for(int j = 0; j < responseEditResponsesPanelTextComponents.length; j++)
                        {
                            newResponseEditResponsesPanelTextComponents[j] = responseEditResponsesPanelTextComponents[j];
                        }
                        newResponseEditResponsesPanelTextComponents[newResponseEditResponsesPanelTextComponents.length - 1] = (JTextComponent)responseEditResponsesPanelComponents[i];

                        responseEditResponsesPanelTextComponents = newResponseEditResponsesPanelTextComponents;
                    }
                }

                //Checks if a valid first and last name has been inputted, aborts if not
                if(responseEditResponsesPanelTextComponents[0].getText().trim().isEmpty())
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Поле \"Фамилия\" не может быть пустым.", "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                else if(responseEditResponsesPanelTextComponents[1].getText().trim().isEmpty())
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Поле \"Имя\" не может быть пустым.", "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String[] newResponses = selectedResponse.getResponses(); //Array which will store the new responses to the survey
                int selectedResponseId = -1; //Stores the id of the response in the database

                try
                {
                    selectedResponseId = selectedResponse.getSQLId(databaseConnection); //Stores the id of the selected response
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                for(int i = 0; i < newResponses.length; i++) //Pushes the responses in the text components to newResponses
                {
                    newResponses[i] = responseEditResponsesPanelTextComponents[i].getText().trim();
                }

                //Generates SQL update which edits the response in the database
                String update = "UPDATE responses SET lastname = \"" + filterString(newResponses[0]) + "\", firstname = \"" + filterString(newResponses[1]) + "\", ";
                for(int i = 2; i < newResponses.length - 1; i++)
                {
                    update += "r" + (i + 1) + " = \"" + filterString(newResponses[i]) + "\", ";
                }
                update += "r" + newResponses.length + " = \"" + filterString(newResponses[newResponses.length - 1]) + "\" WHERE id = " + selectedResponseId + ";";

                //Attempts to execute the update
                try
                {
                    statement.executeUpdate(update);

                    selectedResponse.setResponses(newResponses); //Sets the responses of the selected response equal to newResponses (only upon successful update in the database)

                    AnketaDB.this.setTitle(selectedResponse.toString()); //Sets window title to response toString

                    Question[] responseViewQuestions = selectedResponse.getSurvey().getQuestions(); //Sets responseViewQuestions to the selection questions
                    String[] responseViewResponses = selectedResponse.getResponses(); //Sets responseViewResponses to the selection responses
                    JLabel[] responseViewLabels = new JLabel[0]; //Stores the labels to be displayed on the response view window

                    for(int i = 0; i < responseViewQuestions.length; i++) //Pushes questions and responses to responseViewLabels
                    {
                        JLabel[] newResponseViewLabels = new JLabel[responseViewLabels.length + 2];

                        for(int j = 0; j < responseViewLabels.length; j++)
                        {
                            newResponseViewLabels[j] = responseViewLabels[j];
                        }

                        newResponseViewLabels[newResponseViewLabels.length - 2] = new JLabel("<html><body><p style='width: 500px;'><u>" + responseViewQuestions[i].getText() + "</u></p></body></html>");
                        newResponseViewLabels[newResponseViewLabels.length - 1] = new JLabel("<html><body><p style='width: 500px;'>" + responseViewResponses[i].trim() + "</p></body></html>");

                        responseViewLabels = newResponseViewLabels;
                    }

                    responseViewResponsesPanel.removeAll(); //Cleans responseViewResponsesPanel of any previous labels

                    for(int i = 0; i < responseViewLabels.length; i+=2) //Adds labels to response view
                    {
                        responseViewResponsesPanel.add(responseViewLabels[i]);
                        responseViewResponsesPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                        responseViewResponsesPanel.add(responseViewLabels[i + 1]);
                        responseViewResponsesPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                    }

                    JOptionPane.showMessageDialog(AnketaDB.this, "Ответ \"" + selectedResponse.toString() + "\" успешно редактированно.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    cards.show(container, "responseView");
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        //Adds ActionListener to the response edit cancel button
        responseEditCancelButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Show the response view panel with the appropriate title
                AnketaDB.this.setTitle(selectedResponse.toString());
                cards.show(container, "responseView");
            }
        });

        //Adds ActionListener to the list of surveys search button
        listOfSurveysSearchButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                listOfSurveysResultsListElements = new Survey[0]; //Clears list of surveys list elements

                //SQL query which searches for surveys which match parameters
                String query = "SELECT surveys.id FROM surveys WHERE surveys.surveyname LIKE '%" + filterString(listOfSurveysSearchNameTextField.getText().trim()) + "%'";

                //Checks if the year input is a valid int, either finishes query construction or displays error message
                if(isInt(listOfSurveysSearchYearTextField.getText().trim()))
                {
                    query += " AND surveys.surveyyear = " + Integer.parseInt(listOfSurveysSearchYearTextField.getText().trim()) + ";"; //Search for surveys with the year if year is an int
                }
                else if(listOfSurveysSearchYearTextField.getText().trim().isEmpty())
                {
                    query += ";"; //Ignores year if year text box is empty
                }
                else
                {
                    //Displays warning message and does not execute query if year is not empty but invalid
                    JOptionPane.showMessageDialog(AnketaDB.this, "Неправельный формат поля \"Год\": " + listOfSurveysSearchYearTextField.getText().trim(), "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                //Attempts to execute the query and return the results into the JList
                try
                {
                    results = statement.executeQuery(query);
                    int[] surveyids = new int[0];

                    while(results.next()) //Pushes ids to surveyids
                    {
                        surveyids = pushElementToIntArray(surveyids, results.getInt("id"));
                    }

                    if(surveyids.length == 0) //Checks if surveyids is empty, shows "no surveys found" message
                    {
                        JOptionPane.showMessageDialog(AnketaDB.this, "Анкеты с такими даннами не найденны.", "Внимание", JOptionPane.INFORMATION_MESSAGE);
                    }

                    for(int id : surveyids) //Adds survey objects to JList
                    {
                        //Executes query which returns the row in the surveys table with id equal to int id
                        results = statement.executeQuery("SELECT surveys.* FROM surveys WHERE id = " + id);

                        //Creates new survey array and survey object
                        Survey[] newListOfSurveysResultsListElements = new Survey[listOfSurveysResultsListElements.length + 1];
                        Survey newSurvey = new Survey(results);

                        //Adds elements already in main array to new array
                        for(int i = 0; i < listOfSurveysResultsListElements.length; i++)
                        {
                            newListOfSurveysResultsListElements[i] = listOfSurveysResultsListElements[i];
                        }
                        newListOfSurveysResultsListElements[newListOfSurveysResultsListElements.length - 1] = newSurvey; //Adds new element to new array

                        listOfSurveysResultsListElements = newListOfSurveysResultsListElements; //Sets main array equal to new array
                    }

                    listOfSurveysResultsList.setListData(listOfSurveysResultsListElements); //Resets JList
                }
                catch (SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        //Adds ActionListener to the list of surveys edit button
        listOfSurveysEditButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectedSurvey = listOfSurveysResultsList.getSelectedValue();

                if(selectedSurvey == null) //Checks if there is no selected survey, displays information message if so
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Что бы редактировать анкету, нажимайте на анкета и потом на кнопка \"Редактировать\".", "Выбор Нет", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                AnketaDB.this.setTitle("Редакторование: " + selectedSurvey.toString());

                //Sets the survey name and survey year to the selected survey values in the text fields
                surveyEditNameTextField.setText(selectedSurvey.getName());
                surveyEditYearTextField.setText("" + selectedSurvey.getYear());

                JTextField[] surveyEditTextFields = new JTextField[0]; //Stores text fields to be added to the survey edit panel
                JTextField[] surveyEditResponseTextFields = new JTextField[0]; //Stores disabled text fields representing answer space to be added to the survey edit panel
                Question[] surveyEditQuestions = selectedSurvey.getQuestions();

                //Pushes TextFields to array
                for(int i = 0; i < surveyEditQuestions.length; i++)
                {
                    JTextField[] newSurveyEditTextFields = new JTextField[surveyEditTextFields.length + 1];
                    JTextField[] newSurveyEditResponseTextFields = new JTextField[surveyEditResponseTextFields.length + 1];

                    for(int j = 0; j < surveyEditTextFields.length; j++)
                    {
                        newSurveyEditTextFields[j] = surveyEditTextFields[j];
                        newSurveyEditResponseTextFields[j] = surveyEditResponseTextFields[j];
                    }

                    //Adds editable TextField with the question
                    JTextField addedTextField = new JTextField();
                    addedTextField.setText(surveyEditQuestions[i].getText());
                    addedTextField.setMaximumSize(new Dimension(800, 25));
                    newSurveyEditTextFields[newSurveyEditTextFields.length - 1] = addedTextField;

                    //Adds a blank, disabled TextField representing the answer space
                    addedTextField = new JTextField();
                    addedTextField.setEnabled(false);
                    addedTextField.setBackground(new Color(185, 185, 185));
                    addedTextField.setText("");
                    if(surveyEditQuestions[i].getIsLong())
                    {
                        addedTextField.setMaximumSize(new Dimension(800, 100));
                    }
                    else
                    {
                        addedTextField.setMaximumSize(new Dimension(800, 25));
                    }
                    newSurveyEditResponseTextFields[newSurveyEditResponseTextFields.length - 1] = addedTextField;

                    surveyEditTextFields = newSurveyEditTextFields;
                    surveyEditResponseTextFields = newSurveyEditResponseTextFields;
                }

                surveyEditQuestionsPanel.removeAll(); //Clears panel of any previous elements

                //Adds components to panel
                for(int i = 0; i < surveyEditTextFields.length; i++)
                {
                    surveyEditQuestionsPanel.add(surveyEditTextFields[i]);
                    surveyEditQuestionsPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                    surveyEditQuestionsPanel.add(surveyEditResponseTextFields[i]);
                    surveyEditQuestionsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                }

                //Disables editing on first two text fields (last and first name questions)
                surveyEditTextFields[0].setEditable(false);
                surveyEditTextFields[1].setEditable(false);

                //Clears list of surveys JList
                listOfSurveysResultsListElements = new Survey[0];
                listOfSurveysResultsList.setListData(listOfSurveysResultsListElements);

                cards.show(container, "surveyEdit"); //Shows survey edit window with elements added
            }
        });

        //Adds ActionListener to the list of surveys delete button
        listOfSurveysDeleteButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectedSurvey = listOfSurveysResultsList.getSelectedValue();

                if(selectedSurvey == null) //Checks if there is no selected survey, displays information message if so
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Что бы удалить анкету, нажимайте на анкета и потом на кнопка \"Удалить\".", "Выбор Нет", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                //Asks user if they want to delete the survey and all responses
                Object[] options = {"Да", "Нет"}; //Change dialog buttons to display in Russian
                int deleteSurvey = JOptionPane.showOptionDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + "Вы уверен что вы хотите удалить анкету \"" + selectedSurvey.getName() + "\" и все ответы на неё? (Удалёные данны не будут сохраннены!)", "Внимание", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

                if(deleteSurvey == JOptionPane.NO_OPTION || deleteSurvey == JOptionPane.CLOSED_OPTION) //Returns if user presses "no" or the X button
                {
                    return;
                }

                //Attempts to delete the survey and all responses to it
                try
                {
                    //Gets the id of the survey to be deleted
                    int deletedid = selectedSurvey.getSQLId(databaseConnection);

                    //Deletes the survey and all responses with the surveyid of that survey
                    statement.executeUpdate("DELETE FROM surveys WHERE id = " + deletedid + ";");
                    statement.executeUpdate("DELETE FROM responses WHERE surveyid = " + deletedid + ";");

                    JOptionPane.showMessageDialog(AnketaDB.this, "Анкета " + selectedSurvey.getName() + " и все ответы удалён.", "Удаление завершено", JOptionPane.INFORMATION_MESSAGE);

                    cards.show(container, "main"); //Shows main window after deletion
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        //Adds ActionListener to the list of surveys fill in button
        listOfSurveysFillInButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                Survey selectedSurvey = listOfSurveysResultsList.getSelectedValue();

                if(selectedSurvey == null) //Checks if there is no selected survey, displays information message if so
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Что бы выполнить анкету, нажимайте на анкета и потом на кнопка \"Выполнить\".", "Выбор Нет", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                AnketaDB.this.setTitle("Выполнение: " + selectedSurvey.toString()); //Sets the window title to "Editing: [SURVEY]

                JLabel[] surveyAnswerLabels = new JLabel[0];
                JTextComponent[] surveyAnswerTextComponents = new JTextComponent[0];
                Question[] surveyAnswerQuestions = selectedSurvey.getQuestions();

                //Pushes labels to surveyAnswerLabels and text components to surveyAnswerTextComponents
                for(int i = 0; i < surveyAnswerQuestions.length; i++)
                {
                    JLabel[] newsurveyAnswerLabels = new JLabel[surveyAnswerLabels.length + 1];
                    JTextComponent[] newsurveyAnswerTextComponents = new JTextComponent[surveyAnswerTextComponents.length + 1];

                    for(int j = 0; j < surveyAnswerTextComponents.length; j++)
                    {
                        newsurveyAnswerLabels[j] = surveyAnswerLabels[j];
                        newsurveyAnswerTextComponents[j] = surveyAnswerTextComponents[j];
                    }

                    //Adds label to newsurveyAnswerComponents
                    newsurveyAnswerLabels[newsurveyAnswerLabels.length - 1] = new JLabel("<html><body><p style='width: 500px;'><u>" + surveyAnswerQuestions[i].getText().trim() + "</u></p></body></html>");
                    
                    //Adds either a JTextField or JTextArea to newsurveyAnswerTextComponents depending on question length
                    if(surveyAnswerQuestions[i].getIsLong())
                    {
                        JTextArea addedTextComponent = new JTextArea();
                        addedTextComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
                        addedTextComponent.setLineWrap(true);
                        addedTextComponent.setWrapStyleWord(true);
                        addedTextComponent.setMaximumSize(new Dimension(650, 175));
                        newsurveyAnswerTextComponents[newsurveyAnswerTextComponents.length - 1] = addedTextComponent;
                    }
                    else
                    {
                        JTextField addedTextComponent = new JTextField();
                        addedTextComponent.setMaximumSize(new Dimension(1000, 25));
                        newsurveyAnswerTextComponents[newsurveyAnswerTextComponents.length - 1] = addedTextComponent;
                    }

                    surveyAnswerLabels = newsurveyAnswerLabels;
                    surveyAnswerTextComponents = newsurveyAnswerTextComponents;
                }

                surveyAnswerQuestionsPanel.removeAll(); //Clears surveyAnswerQuestionsPanel of any previous components

                for(int i = 0; i < surveyAnswerTextComponents.length; i++) //Adds components to surveyAnswerQuestionsPanel
                {
                    surveyAnswerQuestionsPanel.add(surveyAnswerLabels[i]);
                    surveyAnswerQuestionsPanel.add(Box.createRigidArea(new Dimension(0, 2)));
                    surveyAnswerQuestionsPanel.add(surveyAnswerTextComponents[i]);
                    surveyAnswerQuestionsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
                }

                cards.show(container, "surveyAnswer");
            }
        });

        //Adds ActionListener to the survey edit save button
        surveyEditSaveButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                //Checks if there is a valid survey name, aborts if it is empty
                if(surveyEditNameTextField.getText().trim().isEmpty())
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Поле \"Название\" не может быть пустым.", "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                //Checks if there is a valid survey year, aborts if it is empty or invalid
                if(!isInt(surveyEditYearTextField.getText().trim()))
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Неправельный формат поля \"Год\": " + surveyEditYearTextField.getText().trim(), "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                Component[] surveyEditQuestionsPanelComponents = surveyEditQuestionsPanel.getComponents(); //Gets the components in the survey edit questions panel

                //Filters components array into new array with only the enabled JTextFields
                JTextField[] surveyEditQuestionsPanelTextFields = new JTextField[0];
                for(int i = 0; i < surveyEditQuestionsPanelComponents.length; i++)
                {
                    if(surveyEditQuestionsPanelComponents[i] instanceof JTextField) //Checks that component is JTextField
                    {
                        if(((JTextField)surveyEditQuestionsPanelComponents[i]).isEnabled()) //Checks that JTextField is enabled
                        {
                            JTextField[] newSurveyEditQuestionsPanelTextFields = new JTextField[surveyEditQuestionsPanelTextFields.length + 1];

                            for(int j = 0; j < surveyEditQuestionsPanelTextFields.length; j++)
                            {
                                newSurveyEditQuestionsPanelTextFields[j] = surveyEditQuestionsPanelTextFields[j];
                            }
                            newSurveyEditQuestionsPanelTextFields[newSurveyEditQuestionsPanelTextFields.length - 1] = (JTextField)surveyEditQuestionsPanelComponents[i];

                            surveyEditQuestionsPanelTextFields = newSurveyEditQuestionsPanelTextFields;
                        }
                    }
                }

                //Checks that each text field has text, aborts if it finds an empty text field
                for(JTextField textfield : surveyEditQuestionsPanelTextFields)
                {
                    if(textfield.getText().trim().isEmpty())
                    {
                        JOptionPane.showMessageDialog(AnketaDB.this, "Есть пустые вопросы. Не смог сохранить анкету.", "Внимание", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }

                Question[] newQuestions = selectedSurvey.getQuestions(); //Array which stores the new questions of the survey
                int selectedSurveyId = -1; //Stores the id of the response in the database

                try
                {
                    selectedSurveyId = selectedSurvey.getSQLId(databaseConnection);
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                //Sets the name and year of the selected survey to the new name and year (for display purposes)
                selectedSurvey.setName(surveyEditNameTextField.getText().trim());
                selectedSurvey.setYear(Integer.parseInt(surveyEditYearTextField.getText().trim()));

                for(int i = 0; i < newQuestions.length; i++) //Pushes the questions in the text fields to newQuestions
                {
                    newQuestions[i].setText(surveyEditQuestionsPanelTextFields[i].getText());
                }

                //Generates SQL update which edits the survey in the database
                String update = "UPDATE surveys SET surveyname = \"" + surveyEditNameTextField.getText().trim() + "\", surveyyear = " + surveyEditYearTextField.getText().trim() + ", ";
                for(int i = 0; i < newQuestions.length - 1; i++)
                {
                    update += "q" + (i + 1) + " = \"" + filterString(newQuestions[i].getText().trim()) + "\", ";
                }
                update += "q" + newQuestions.length + " = \"" + filterString(newQuestions[newQuestions.length - 1].getText().trim()) + "\" WHERE id = " + selectedSurveyId + ";";

                //Attempts to execute the update
                try
                {
                    statement.executeUpdate(update);

                    //Clears list of surveys results list before returning to list of surveys window
                    listOfSurveysResultsListElements = new Survey[0];
                    listOfSurveysResultsList.setListData(listOfSurveysResultsListElements);

                    //Displays success message and changes to list of surveys window
                    JOptionPane.showMessageDialog(AnketaDB.this, "Анкета \"" + selectedSurvey.toString() + "\" успешно редактированно.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    AnketaDB.this.setTitle("Список Анкет");
                    cards.show(container, "listOfSurveys");
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        //Adds ActionListener to the survey answer save button
        surveyAnswerSaveButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                selectedSurvey = listOfSurveysResultsList.getSelectedValue();

                Component[] surveyAnswerQuestionsPanelComponents = surveyAnswerQuestionsPanel.getComponents(); //Gets all the components in the survey answer questions panel

                //Filters components array into new array with only the JTextComponents
                JTextComponent[] surveyAnswerQuestionsPanelTextComponents = new JTextComponent[0];
                for(int i = 0; i < surveyAnswerQuestionsPanelComponents.length; i++)
                {
                    if(surveyAnswerQuestionsPanelComponents[i] instanceof JTextComponent) //Pushes element to text components array if it is a text component
                    {
                        JTextComponent[] newsurveyAnswerQuestionsPanelTextComponents = new JTextComponent[surveyAnswerQuestionsPanelTextComponents.length + 1];

                        for(int j = 0; j < surveyAnswerQuestionsPanelTextComponents.length; j++)
                        {
                            newsurveyAnswerQuestionsPanelTextComponents[j] = surveyAnswerQuestionsPanelTextComponents[j];
                        }
                        newsurveyAnswerQuestionsPanelTextComponents[newsurveyAnswerQuestionsPanelTextComponents.length - 1] = (JTextComponent)surveyAnswerQuestionsPanelComponents[i];

                        surveyAnswerQuestionsPanelTextComponents = newsurveyAnswerQuestionsPanelTextComponents;
                    }
                }

                //Checks if a valid first and last name has been inputted, aborts if not
                if(surveyAnswerQuestionsPanelTextComponents[0].getText().trim().isEmpty())
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Поле \"Фамилия\" не может быть пустым.", "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                else if(surveyAnswerQuestionsPanelTextComponents[1].getText().trim().isEmpty())
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Поле \"Имя\" не может быть пустым.", "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String[] surveyResponses = new String[surveyAnswerQuestionsPanelTextComponents.length]; //Array which stores the responses to the survey being filled in
                int selectedSurveyId = -1; //Integer which stores the id of the survey being responded to in the database

                try
                {
                    selectedSurveyId = selectedSurvey.getSQLId(databaseConnection);
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                for(int i = 0; i < surveyResponses.length; i++) //Pushes responses to responses array
                {
                    surveyResponses[i] = surveyAnswerQuestionsPanelTextComponents[i].getText().trim();
                }

                //Constructs update which inserts the response into the database
                String update = "INSERT INTO responses (surveyid, lastname, firstname, ";
                for(int i = 3; i < surveyResponses.length; i++)
                {
                    update += "r" + i + ", ";
                }
                update += "r" + surveyResponses.length + ") VALUES (" + selectedSurveyId + ", ";
                for(int i = 0; i < surveyResponses.length - 1; i++)
                {
                    update += "\"" + filterString(surveyResponses[i].trim()) + "\", ";
                }
                update += "\"" + filterString(surveyResponses[surveyResponses.length - 1].trim()) + "\");";

                //Attempts to add the response to the database
                try
                {
                    statement.executeUpdate(update);

                    //Shows success message and returns to list of surveys window
                    JOptionPane.showMessageDialog(AnketaDB.this, "Ответ успешно добавленно.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    AnketaDB.this.setTitle("Список Анкет");
                    cards.show(container, "listOfSurveys");
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальная Ошибка", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        });

        //Sets the main card as the card to be displayed and formats the window to the correct resolution
        cards.show(container, "main");
        setSize(800, 600);
        surveyCreationCreateButton.setBounds(240, getBounds().height - 80, 150, 30);
        surveyCreationCancelButton.setBounds(410, getBounds().height - 80, 150, 30);
        surveyCreationAddLongQuestionButton.setBounds(240, getBounds().height - 120, 320, 30);
        surveyCreationAddShortQuestionButton.setBounds(240, getBounds().height - 160, 320, 30);
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

    //Method which adds an int to the end of an int array (for list generation)
    public int[] pushElementToIntArray(int[] array, int element)
    {
        int[] newArray = new int[array.length + 1];

        for(int i = 0; i < array.length; i++)
        {
            newArray[i] = array[i];
        }

        newArray[newArray.length - 1] = element;

        return newArray;
    }

    //Method which checks if a string is an integer
    public Boolean isInt(String string)
    {
        try
        {
            Integer.parseInt(string);
            return true;
        }
        catch(NumberFormatException exception)
        {
            return false;
        }
    }

    //Method which checks for SQL escape characters and properly formats them (in order to prevent SQL Injection and accidental syntax errors)
    public String filterString(String string)
    {
        char[] escapeChars = {'\'', '"'}; //Char array of SQL escape characters
        String filteredString = ""; //String with escape characters filtered
        boolean charAdded; //Boolean which checks if the character has been added to the string

        for(int i = 0; i < string.length(); i++)
        {
            charAdded = false; //Sets charAdded to false before adding new char

            for(char character : escapeChars)
            {
                if(string.charAt(i) == character) //Adds properly formatted char to filtered string if it is an escape character
                {
                    filteredString += "\\" + string.charAt(i);
                    charAdded = true;
                    break;
                }
            }

            if(!charAdded) //Adds the raw character if it has not yet been added
            {
                filteredString += string.charAt(i);
            }
        }

        return filteredString;
    }

    public static void main(String[] args) throws IOException, SQLException
    {
        JFrame frame = new AnketaDB();
        frame.setVisible(true);
    }
}