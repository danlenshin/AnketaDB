public class Anketa 
{
    public String name; //name of Anketa
    public AnketaQuestion[] questions; //Questions

    //Constructor
    public Anketa(String name)
    {
        this.name = name;
        questions = new AnketaQuestion[0];
    }

    //Getter and setter methods
    public String getName()
    {
        return name;
    }

    public AnketaQuestion[] getQuestions()
    {
        return questions;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    //Adds a question to the questions array at the desired index
    public void addQuestion(AnketaQuestion question, int index)
    {
        //Create new questions array
        int newSize = this.questions.length + 1;
        AnketaQuestion[] newQuestions = new AnketaQuestion[newSize];

        //Add questions in original questions before index
        for(int i = 0; i < index; i++)
        {
            newQuestions[i] = this.questions[i];
        }

        //Add new question at desired index
        newQuestions[index] = question;

        //Add questions in original questions before index
        for(int i = index + 1; i < newQuestions.length; i++)
        {
            newQuestions[i] = this.questions[i - 1];
        }

        //Set questions to the new questions array
        this.questions = newQuestions;
    }
}
