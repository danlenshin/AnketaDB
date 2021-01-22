public class Question 
{
    public String question; //Text of question
    public String type; //Type of question (short answer or long answer)

    //Constructor
    public Question(String question, String type)
    {
        this.question = question;

        if(type.equals("Short") || type.equals("Long"))
        {
            this.type = type;
        }
        else
        {
            this.type = "Short";
        }
    }

    //Getter and setter methods
    public String getQuestion()
    {
        return question;
    }

    public String getType()
    {
        return type;
    }

    public void setQuestion(String question)
    {
        this.question = question;
    }

    public void setType(String type)
    {
        this.type = type;
    }
}
