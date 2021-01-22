public class Survey 
{
    public String name; //name of Survey
    public Question[] questions; //Questions

    //Constructor
    public Survey(String name)
    {
        this.name = name;
        questions = new Question[0];
    }

    //Getter and setter methods
    public String getName()
    {
        return name;
    }

    public Question[] getQuestions()
    {
        return questions;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    //Adds a question to the questions array at the desired index
    public void addQuestion(Question question, int index)
    {
        //Create new questions array
        int newSize = this.questions.length + 1;
        Question[] newQuestions = new Question[newSize];

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

    //Deletes the question at a certain index
    public void deleteQuestion(int index)
    {
        //Create new questions array
        int newSize = this.questions.length + 1;
        Question[] newQuestions = new Question[newSize];

        //Add questions in original questions, skipping the question at the specified index
        boolean questionSkipped = false;
        for(int i = 0; i < this.questions.length; i++)
        {
            if(i == index)
            {
                questionSkipped = true;
            }
            else if(i != index && !questionSkipped)
            {
                newQuestions[i] = this.questions[i];
            }
            else if(i != index && questionSkipped)
            {
                newQuestions[i] = this.questions[i - 1];
            }
        }

        //Set questions to the new questions array
        this.questions = newQuestions;
    }

    //Replaces the question at a certain index
    public void replaceQuestion(Question newQuestion, int index)
    {
        this.questions[index] = newQuestion;
    }
}
