public class Response 
{
    public Survey survey;
    public String[] responses;

    public Response(Survey survey)
    {
        this.survey = survey;
        responses = new String[0];
    }

    public Survey getSurvey()
    {
        return survey;
    }

    public String[] getResponses()
    {
        return responses;
    }

    public void setSurvey(Survey survey)
    {
        this.survey = survey;
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
}
