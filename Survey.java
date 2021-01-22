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
        questions = new Question[0];
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

        for(int i = 0; i < this.questions.length; i++)
        {
            newQuestions[i] = this.questions[i];
        }

        newQuestions[newSize - 1] = this.questions[newSize - 2];

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
