package main.java;

import java.sql.ResultSet; //To allow creation of constructor based on ResultSet
import java.sql.Statement; //To allow execution of queries and updates
import java.sql.Connection; //To allow class to connect to an SQL database
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
        this.survey = new Survey(results);

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
    }

    public Survey getSurvey() //Returns the survey the response is based on
    {
        return survey;
    }

    public String[] getResponses() //Returns the responses in the response
    {
        return responses;
    }

    public void setResponses(String[] responses)
    {
        this.responses = responses;
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

    //returns the ID of the response in the responses table as an integer
    public int getSQLId(Connection connection) throws SQLException
    {
        Statement statement = connection.createStatement();
        String query = "SELECT responses.id FROM responses"
                     + " WHERE (responses.lastname = \"" + filterString(this.responses[0])
                     + "\" AND responses.firstname = \"" + filterString(this.responses[1])
                     + "\") AND responses.surveyid = " + survey.getSQLId(connection) + ";";

        ResultSet results = statement.executeQuery(query);
        results.next();
        return results.getInt("id");
    }

    public String toString()
    {
        return responses[1] + " " + responses[0] + " | " + survey.getYear() + " | " + survey.getName();
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
