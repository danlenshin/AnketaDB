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
    The ResultSet must contain all of the response table columns as well as the surveyname and surveyyear columns INNER JOINed using surveyid
    results must be a single row (representing a single response)
    */
    public Response(ResultSet results) throws SQLException
    {
        while(results.next())
        {
            survey.setName(results.getString("surveys.surveyname"));
            survey.setYear(results.getInt("surveys.surveyyear"));

            for(int i = 3; i < 31; i++)
            {
                if(results.getString("r" + i) == null)
                {
                    break;
                }
                else
                {
                    responses[i - 3] = results.getString("r" + i);
                }
            }
        }
    }

    public Survey getSurvey() //Returns the survey the response is based on
    {
        return survey;
    }

    public String[] getResponses() //Returns the responses in the response
    {
        return responses;
    }

    public void addResponse(String response)
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

    @Override
    public String toString()
    {
        return responses[0] + " " + responses[1] + " | " + survey.getYear() + " | " + survey.getName();
    }
}
