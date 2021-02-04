import java.sql.ResultSet; //To allow creation of constructor based on ResultSet
import java.sql.SQLException;

public class Response 
{
    public Survey survey;
    public String[] responses;

    public Response(Survey survey) //Creates a response object using a survey
    {
        this.survey = survey;
        responses = new String[2];
    }

    /*
    Creates a response object using a ResultSet object
    The ResultSet must contain all of the response table columns as well as all the survey columns INNER JOINed on responses.surveyid = surveys.id
    results must be a single row (representing a single response)
    */
    public Response(ResultSet results) throws SQLException
    {
        results.next();

        //Constructs the responses object
        responses = new String[0];

        //Adds responses to response array
        addResponse(results.getString("lastname"));
        addResponse(results.getString("firstname"));
        for(int i = 3; i < 31; i++)
        {
            if(results.getString("r" + i) == null)
            {
                break;
            }
            else
            {
                addResponse(results.getString("r" + i));
            }
        }

        this.survey = new Survey(results);
    }

    public Survey getSurvey() //Returns the survey the response is based on
    {
        return survey;
    }

    public String[] getResponses() //Returns the responses in the response
    {
        return responses;
    }

    public void addResponse(String response) //Adds a string to the end of the responses array
    {
        int newSize = this.responses.length + 1;
        String[] newResponses = new String[newSize];
        
        for(int i = 0; i < this.responses.length; i++)
        {
            newResponses[i] = this.responses[i];
        }

        newResponses[newResponses.length - 1] = response;

        this.responses = newResponses;
    }

    public String toInsertStatement()
    {
        String query = "";
        
        return query;
    }

    public String toString()
    {
        return responses[1] + " " + responses[0] + " | " + survey.getYear() + " | " + survey.getName();
    }
}
