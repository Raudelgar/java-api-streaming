import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Date;

import org.apache.http.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.HttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class JavaApiStreaming {
    @SuppressWarnings("deprecation")
	public static void main (String[]args) throws IOException {

        HttpClient httpClient = HttpClientBuilder.create().build();

        try {

            // Set these variables to whatever personal ones are preferred
            String domain = "https://api-fxpractice.oanda.com";
            String domainv20 = "/v3/accounts/";
            String endPointPrice = "/pricing?instruments=";
            String access_token = "aafefcd10c35cc034f8890f2aa8c02d0-cc731cea4a7467697e343cb9fb36724b";
            String account_id = "101-001-9464198-001";
            String instruments = "EUR_USD,USD_JPY,EUR_JPY";
            String eur_usd = "EUR_USD";
//https://api-fxpractice.oanda.com/v3/accounts/101-001-9464198-001/pricing?instruments=USD_CAD

            HttpUriRequest httpGet = new HttpGet(domain+domainv20+account_id+endPointPrice+instruments);
            httpGet.setHeader(new BasicHeader("Authorization", "Bearer " + access_token));

            System.out.println("Executing request: " + httpGet.getRequestLine());

            HttpResponse resp = httpClient.execute(httpGet);
            HttpEntity entity = resp.getEntity();

            if (resp.getStatusLine().getStatusCode() == 200 && entity != null) {
                InputStream stream = entity.getContent();
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(stream));

                while ((line = br.readLine()) != null) {

                    Object obj = JSONValue.parse(line);
                    JSONObject quotes = (JSONObject) obj;

                    // unwrap if necessary
                    if (quotes.containsKey("prices")) {
                    	JSONArray pricesArray = (JSONArray)quotes.get("prices");
                    	int count = pricesArray.size();
                    	int i = 0;
                    	while(count > 0) {
                    		JSONObject instrumentObj = (JSONObject)pricesArray.get(i);
                    		JSONArray askArray = (JSONArray)instrumentObj.get("asks");
                    		JSONObject askObj = (JSONObject)askArray.get(0);
                    		JSONArray bidArray = (JSONArray)instrumentObj.get("bids");
                    		JSONObject bidObj = (JSONObject)bidArray.get(0);
                        	String instrument = instrumentObj.get("instrument").toString();
                        	String time = instrumentObj.get("time").toString();
                        	double bid = Double.parseDouble(bidObj.get("price").toString());
                        	double ask = Double.parseDouble(askObj.get("price").toString());
                        	double closeoutBid = Double.parseDouble(instrumentObj.get("closeoutBid").toString());
                        	double closeoutAsk = Double.parseDouble(instrumentObj.get("closeoutAsk").toString());
                        	
                        	System.out.println("-------");
                        	System.out.println("Instrument: "+instrument);
                            System.out.println("Time: "+time);
                            System.out.println("Bid: "+bid);
                            System.out.println("Ask: "+ask);
                            System.out.println("CloseOutBid: "+closeoutBid);
                            System.out.println("CloseOutAsk: "+closeoutAsk);
                     
                            i++;
                            count--;
                    	}
                    }
                }
            } else {
                // print error message
                String responseString = EntityUtils.toString(entity, "UTF-8");
                System.out.println(responseString);
            }

        } finally {
            httpClient.getConnectionManager().shutdown();
        }
    }
}
