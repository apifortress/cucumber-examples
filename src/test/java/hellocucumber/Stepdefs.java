package hellocucumber;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.Assert.*;

public class Stepdefs {
    private String project;
    private String url;
    private JSONArray output = new JSONArray();

    @Given("^the following API Fortress Project name \"(.*?)\"$")
    public void theFollowingURL(String project) {
        //store input url to global var
        this.project = project;
        getProjInfo(this.project);
        //throw new cucumber.api.PendingException();
    }

    @When("^run all tests$")
    public void callApi() {
        //call method and pass in url
        callAPI(url);
        //throw new cucumber.api.PendingException();
    }

    @Then("^all test should pass$")
    public void foundResult() {
        //get report link from output json
        String[] report = new String[output.length()];
        String[] testname = new String[output.length()];

        int failures = 0;

        for(int x = 0; x<output.length(); x++){
            JSONObject item = output.getJSONObject(x);
            report[x] = item.getJSONObject("links").get("private").toString();
            testname[x] = item.getJSONObject("test").get("name").toString();

            if (item.getInt("failuresCount") > 0) {
                failures++;
                System.out.println("\n" + "Review " + "\u001B[31m" + "Failed " + "\u001B[0m" + "API Fortress Report For " + testname[x] + " Here: " + report[x]);
            } else {
                System.out.println("\n" + "Review " + "\u001B[32m" + "Passed " + "\u001B[0m" + "API Fortress Report For " + testname[x] + " Here: " + report[x]);
            }
        }
        System.out.println("\n");
        assertEquals(0,failures);
    }


    private void getProjInfo(String project) {
        String result;

        try {
            //read in config file
            BufferedReader br = new BufferedReader(new FileReader("config2.json"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            result = sb.toString();

            //parse through config to match project name to hook
            JSONObject config = new JSONObject(result);
            JSONArray projects = config.getJSONArray("projects");
            for(int x = 0; x<projects.length(); x++) {
                if(projects.getJSONObject(x).get("projectname").equals(this.project)) {
                    url = projects.getJSONObject(x).get("url").toString();
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void callAPI(String url) {

        try{
            //setup url
            url = this.url +"/tests/run-all?sync=true";

            //setup http call
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();

            //set method
            con.setRequestMethod("GET");

            int responseCode = con.getResponseCode();

            System.out.println("\nSending 'GET' request to url : " + url);
            System.out.println("Response Code : " + responseCode + "\n");

            //read in output
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while((inputLine=in.readLine()) != null){
            response.append(inputLine);
            }
            in.close();

            //print result
            output = new JSONArray(response.toString());
            //System.out.println(output);

            //throw new cucumber.api.PendingException();

            }catch(Exception e){
                System.out.println("API Connection Error");
            }
    }
}