public class Question 
{
    public String text; //Text of question
    public boolean isLong;

    //Constructor
    public Question(String text, boolean isLong)
    {
        this.text = text;
        this.isLong = isLong;
    }

    //Blank constructor
    public Question()
    {
        this.text = null;
        this.isLong = false;
    }

    //Getter and setter methods
    public String getText()
    {
        return text;
    }

    public boolean getIsLong()
    {
        return isLong;
    }

    public void setText(String text)
    {
        this.text = text;
    }

    public void setIsLong(boolean isLong)
    {
        this.isLong = isLong;
    }
}
