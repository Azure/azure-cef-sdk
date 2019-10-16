package com.bluecloud.ccs;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;


import com.bluecloud.model.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        JsonObject json = new JsonObject();


        JsonObject templateParams = new JsonObject();
        templateParams.addProperty("code", "12344");
        
        JsonObject templateBody = new JsonObject();
        templateBody.addProperty("templateName", "notifyOTP02");
        templateBody.add("templateParam", templateParams);

        JsonArray telNums = new JsonArray();

        telNums.add("18321676517");

        json.add("phoneNumber", telNums);

        json.addProperty("extend", "09");

        json.add("messageBody", templateBody);

       System.out.println(json.toString());

        try{
            HttpClient.createSMSHttpPostClient(Config.cef_accountname, json);
        }catch (Exception e){
			// e.getStackTrace().
		}

        
        

       // json.messageBody("extend", "09");
        

      // json.add(property, value);

       // String json = "{ \"name\": \"Baeldung\", \"java\": true }";
       // JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        



        System.out.println( "Hello World!" );
    }
}
