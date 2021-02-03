import java.sql.ResultSet; //To allow creation of constructor based on ResultSet
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
        this.name = results.getString("surveyname");
        this.year = results.getInt("surveyyear");
        this.questions = new Question[0];

        Question newQuestion = new Question();

        for(int i = 1; i < 31; i++) //Goes through questions and adds them to the questions array
        {
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

    //Creates an INSERT INTO statement which inserts the survey into the surveys table
    public String toInsertStatement()
    {
        String statement = "INSERT INTO surveys ("; //Creates statement string

        //Adds columns to statement string
        statement += "surveyname, surveyyear, ";
        for(int i = 0; i < questions.length; i++)
        {
            statement += "q" + (i+1) + ", q" + (i+1) + "length, ";
        }
        statement += ") VALUES (";

        //Adds values to statement string
        statement += "\'" + name + "\', " + year + ", ";
        for(int i = 0; i < questions.length-1; i++)
        {
            statement += "\'" + questions[i].getText() + "\', " + (questions[i].getIsLong() ? 1 : 0) + ", ";
        }
        statement += "\'" + questions[questions.length - 1].getText() + "\', " + (questions[questions.length - 1].getIsLong() ? 1 : 0) + ");";

        return statement;
    }
}
