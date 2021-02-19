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
    Survey selectedSurvey; //Stores any one response that the program is dealing with
    Response selectedResponse; //Stores any one survey that the program is dealing with

    public AnketaDB() throws IOException, SQLException, JSONException
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
        schema = settings.getString("schema");

        databaseConnection = DriverManager.getConnection(("jdbc:mysql://" + address), username, password); //Sets databaseConnection as the connection to the SQL server
        statement = databaseConnection.createStatement(); //Sets statement to a statement on the connected database
        statement.executeUpdate("USE " + schema + ";"); //Sets it so that all future queries query the schema specified in settings.json

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
        mainSurveyCreationButton.setBounds(120, 20, 250, 50);
        main.add(mainSurveyCreationButton);

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

        JList<Survey> listOfSurveysResultsList = new JList<Survey>(listOfSurveysResultsListElements);
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
        Creating the survey edit window and adding it to cards
        */
        JPanel surveyEdit = new JPanel();
        surveyEdit.setLayout(null);

        Container surveyEditQuestionsContainer = new Container();

        JButton surveyEditSaveButton = new JButton("Сохранить");
        surveyEditSaveButton.setBounds(200, 500, 150, 50);
        surveyEdit.add(surveyEditSaveButton);

        JButton surveyEditCancelButton = new JButton("Отменить");
        surveyEditCancelButton.setBounds(450, 500, 150, 50);
        surveyEdit.add(surveyEditCancelButton);

        JPanel surveyEditQuestionsPanel = new JPanel();
        surveyEditQuestionsPanel.setLayout(new BoxLayout(surveyEditQuestionsPanel, BoxLayout.Y_AXIS));
        surveyEditQuestionsContainer.add(surveyEditQuestionsPanel);

        JScrollPane surveyEditScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        surveyEditScrollPane.setBounds(50, 20, 700, 450);
        surveyEditScrollPane.setViewportView(surveyEditQuestionsPanel);
        surveyEdit.add(surveyEditScrollPane);

        container.add("surveyEdit", surveyEdit);

        /*
        Creating the response view window and adding it to cards
        */
        JPanel responseView = new JPanel();
        responseView.setLayout(null);

        Container responseViewResponsesContainer = new Container();

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
        responseViewResponsesContainer.add(responseViewResponsesPanel);

        JScrollPane responseViewScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        responseViewScrollPane.setBounds(50, 20, 700, 450);
        responseViewScrollPane.setViewportView(responseViewResponsesPanel);
        responseView.add(responseViewScrollPane);

        container.add("responseView", responseView);

        /*
        Creating the response edit window and adding it to cards
        */
        JPanel responseEdit = new JPanel();
        responseEdit.setLayout(null);

        Container responseEditResponsesContainer = new Container();

        JButton responseEditSaveButton = new JButton("Сохранить");
        responseEditSaveButton.setBounds(200, 500, 150, 50);
        responseEdit.add(responseEditSaveButton);

        JButton responseEditCancelButton = new JButton("Отменить");
        responseEditCancelButton.setBounds(450, 500, 150, 50);
        responseEdit.add(responseEditCancelButton);

        JPanel responseEditResponsesPanel = new JPanel();
        responseEditResponsesPanel.setLayout(new BoxLayout(responseEditResponsesPanel, BoxLayout.Y_AXIS));
        responseEditResponsesContainer.add(responseEditResponsesPanel);

        JScrollPane responseEditScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        responseEditScrollPane.setBounds(50, 20, 700, 450);
        responseEditScrollPane.setViewportView(responseEditResponsesPanel);
        responseEdit.add(responseEditScrollPane);

        container.add("responseEdit", responseEdit);

        /*
        Navigational ActionListeners
        These ActionListeners are added to buttons which switch to a different window without manipulating any data
        */
        causeToShowCard(mainListOfSurveysButton, "listOfSurveys", this, "Список Анкет");
        causeToShowCard(mainSurveyCreationButton, "surveyCreation", this, "Создание Анкеты");
        causeToShowCard(listOfSurveysBackButton, "main", this, "AnketaDB by Daniel Lenshin");
        causeToShowCard(surveyCreationCancelButton, "main", this, "AnketaDB by Daniel Lenshin");
        causeToShowCard(responseViewBackButton, "main", this, "AnketaDB by Daniel Lenshin");
        causeToShowCard(surveyEditCancelButton, "listOfSurveys", this, "Список Анкет");

        //Adds action listener to main screen search button
        mainSearchButton.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                mainResultsListElements = new Response[0]; //Clears mainResultsListElements array

                //SQL Query which searches for responses which match parameters
                String query = "SELECT responses.id"
                        +" FROM responses, surveys WHERE responses.surveyid = surveys.id"
                        +" AND (responses.firstname LIKE '%" + filterString(mainSearchNameTextField.getText()) + "%'"
                        +" OR responses.lastname LIKE '%" + filterString(mainSearchNameTextField.getText()) + "%')"
                        +" AND surveys.surveyname LIKE '%" + filterString(mainSearchSurveyTextField.getText()) + "%'";
                
                //Checks if the year input is a valid int, either finishes query construction or displays error message
                if(isInt(mainSearchYearTextField.getText()))
                {
                    query += " AND surveys.surveyyear = " + Integer.parseInt(mainSearchYearTextField.getText()) + ";"; //Search for responses equal to year if year is an int
                }
                else if(mainSearchYearTextField.getText().isEmpty())
                {
                    query += ";"; //Ignores year if year text box is empty
                }
                else
                {
                    //Displays warning message and does not execute query if year is not empty but invalid
                    JOptionPane.showMessageDialog(AnketaDB.this, "Ввод \"" + mainSearchYearTextField.getText() + "\" недействительный год.", "Внимание", JOptionPane.WARNING_MESSAGE);
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
                        JOptionPane.showMessageDialog(AnketaDB.this, "Не мог найти ответы с данным вводам.", "Внимание", JOptionPane.INFORMATION_MESSAGE);
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
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальную Ошибку", JOptionPane.ERROR_MESSAGE);
                    return;
                }
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
                    newResponseEditLabels[newResponseEditLabels.length - 1] = new JLabel("<html><body><p style='width: 500px;'><u>" + responseEditQuestions[i].getText() + "</u></p></body></html>");
                    
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
                int deleteResponse = JOptionPane.showOptionDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + "Вы уверен что вы хотите удалить ответ \"" + selectedResponse.toString() + "\"? (Удалёных данных будет потеренно навсегда!)", "Внимание", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

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
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальную Ошибку", JOptionPane.ERROR_MESSAGE);
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

                //TODO: add check that a last and first name have been edited

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

                String[] newResponses = selectedResponse.getResponses(); //Array which will store the new responses to the survey
                int selectedResponseId = -1; //Stores the id of the response in the database

                try
                {
                    selectedResponseId = selectedResponse.getSQLId(databaseConnection); //Stores the id of the selected response
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальную Ошибку", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                for(int i = 0; i < newResponses.length; i++) //Pushes the responses in the text components to newResponses
                {
                    newResponses[i] = responseEditResponsesPanelTextComponents[i].getText();
                }

                //Checks if the new responses have valid last and first names (not empty), aborts if not
                if(newResponses[0].isEmpty())
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Ввод для фамилия пустой.", "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                else if(newResponses[1].isEmpty())
                {
                    JOptionPane.showMessageDialog(AnketaDB.this, "Ввод для имя пустой.", "Внимание", JOptionPane.WARNING_MESSAGE);
                    return;
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

                    JOptionPane.showMessageDialog(AnketaDB.this, "Ответ \"" + selectedResponse.toString() + "\" успешно редактированно.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    cards.show(container, "responseView");
                }
                catch(SQLException exception)
                {
                    //Displays error message containing SQLException in case of fatal error (this should not be triggerable by the user)
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальную Ошибку", JOptionPane.ERROR_MESSAGE);
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
                String query = "SELECT surveys.id FROM surveys WHERE surveys.surveyname LIKE '%" + filterString(listOfSurveysSearchNameTextField.getText()) + "%'";

                //Checks if the year input is a valid int, either finishes query construction or displays error message
                if(isInt(listOfSurveysSearchYearTextField.getText()))
                {
                    query += " AND surveys.surveyyear = " + Integer.parseInt(listOfSurveysSearchYearTextField.getText()) + ";"; //Search for surveys with the year if year is an int
                }
                else if(listOfSurveysSearchYearTextField.getText().isEmpty())
                {
                    query += ";"; //Ignores year if year text box is empty
                }
                else
                {
                    //Displays warning message and does not execute query if year is not empty but invalid
                    JOptionPane.showMessageDialog(AnketaDB.this, "Ввод \"" + listOfSurveysSearchYearTextField.getText() + "\" недействительный год.", "Внимание", JOptionPane.WARNING_MESSAGE);
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
                        JOptionPane.showMessageDialog(AnketaDB.this, "Не мог найти анкеты с данным вводам.", "Внимание", JOptionPane.INFORMATION_MESSAGE);
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
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальную Ошибку", JOptionPane.ERROR_MESSAGE);
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

                AnketaDB.this.setTitle("Редакторование: " + selectedSurvey.toString());

                JTextField[] surveyEditTextFields = new JTextField[0];
                JTextField[] surveyEditResponseTextFields = new JTextField[0];
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

                    //Adds a blank, uneditable TextField representing the answer space
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
                int deleteSurvey = JOptionPane.showOptionDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + "Вы уверен что вы хотите удалить анкета \"" + selectedSurvey.getName() + "\" и все ответы к нему? (Удалёных данных будет потеренно навсегда!)", "Внимание", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);

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
                    JOptionPane.showMessageDialog(AnketaDB.this, "<html><body><p style='width:300px;'>" + exception + "</p></body></html>", "Фатальную Ошибку", JOptionPane.ERROR_MESSAGE);
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
        //!TEST CODE
        JFrame frame = new AnketaDB();
        frame.setVisible(true);
        //!TEST CODE
    }
}