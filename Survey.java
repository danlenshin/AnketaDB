import java.sql.ResultSet; //To allow creation of constructor based on ResultSet
import java.sql.Statement; //To allow for the execution of queries and updates
import java.sql.Connection; //To allow class to connect to an SQL database
import java.sql.SQLException;

public class Survey 
{
    public String name; //name of Survey
    public int year;
    public Question[] questions; //Questions

    //Constructor
    public Survey(String name, int year)
    {
        this.name = name;
        this.year = year;
        this.questions = new Question[0];
    }

    /*
    Creates a survey object using a ResultSet object
    The ResultSet must contain all of the survey table columns
    results must be a single row (representing a single survey)
    */
    public Survey(ResultSet results) throws SQLException
    {
        results.next(); //Moves cursor into results so as not to throw a before start of result set exception

        this.name = results.getString("surveyname");
        this.year = results.getInt("surveyyear");
        this.questions = new Question[0];

        Question newQuestion;

        for(int i = 1; i < 31; i++) //Goes through questions and adds them to the questions array
        {
            newQuestion = new Question();
            
            if(results.getString("q" + i) == null) //Checks if the question is null, breaks if so
            {
                break;
            }
            else
            {
                newQuestion.setText(results.getString("q" + i)); //Sets new question text from results
                newQuestion.setIsLong(results.getInt("q" + i + "length") == 1 ? true : false); //Sets new question isLong from results
                this.addQuestion(newQuestion); //Adds the new question to the questions array
            }
        }
    }

    //Getter and setter methods
    public String getName()
    {
        return name;
    }

    public int getYear()
    {
        return year;
    }

    public Question[] getQuestions()
    {
        return questions;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setYear(int year)
    {
        this.year = year;
    }

    //Adds a question to the questions array
    public void addQuestion(Question question)
    {
        //Create new questions array
        int newSize = this.questions.length + 1;
        Question[] newQuestions = new Question[newSize];

        for(int i = 0; i < this.questions.length; i++) //Adds old questions to newQuestions
        {
            newQuestions[i] = this.questions[i];
        }

        newQuestions[newSize - 1] = question; //Adds new question to the end of newQuestions

        this.questions = newQuestions;
    }

    //toString method
    public String toString()
    {
        return name + " | " + year;
    }

    //Returns the id of the survey in the surveys table
    public int getSQLId(Connection connection) throws SQLException
    {
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("SELECT surveys.id FROM surveys WHERE surveys.surveyname = \"" + filterString(this.name) + "\" AND surveys.surveyyear = " + this.year + ";");
        results.next();
        return results.getInt("id");
    }

    //Method which checks for SQL escape characters and properly formats them (in order to prevent SQL Injection and accidental syntax errors)
    private String filterString(String string)
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
}
