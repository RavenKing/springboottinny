package tinyworld.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.cloudfoundry.identity.client.UaaContext;
import org.cloudfoundry.identity.client.UaaContextFactory;
import org.cloudfoundry.identity.client.token.GrantType;
import org.cloudfoundry.identity.client.token.TokenRequest;
import org.cloudfoundry.identity.uaa.oauth.token.CompositeAccessToken;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tinyworld.util.Constants;

@RestController
@RequestMapping(Constants.Root_API)
public class WebController {

	@GetMapping("/all")
	public String getAllItems() {

		List<String> newList = new ArrayList<>();

		String conn = this.getConnection(newList);
//        try {
//			//executeSomething(conn);
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		return conn;
	}

	private void executeSomething(Connection conn) throws SQLException {
		// String currentSchema = "";
		// PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO
		// \"7C9E9162159B42D7B46C5821F7FC081D\".\"SpringWithHanaTest.SpringDBTest::Policy.Testing2\"
		// VALUES('1','2','3');");
		// prepareStatement.executeQuery();
	}

	private String getConnection(List<String> newList) {

		Connection conn = null;
		String DB_USERNAME = "";
		String DB_PASSWORD = "";
		String DB_HOST = "";
		String DB_PORT = "";
		JSONArray arr = new JSONArray();
		String employeedata = null;
		int status = 0;
		try {
			JSONObject obj = new JSONObject(System.getenv("VCAP_SERVICES"));
			arr = obj.getJSONArray("destination");
			String sDestinationURL = arr.getJSONObject(0).getJSONObject("credentials").getString("uri");
			System.out.println(arr);
			// get destination client id and client secrect
			String clientid = arr.getJSONObject(0).getJSONObject("credentials").getString("clientid");
			String clientSecrect = arr.getJSONObject(0).getJSONObject("credentials").getString("clientsecret");

			arr = obj.getJSONArray("xsuaa");
			String XSUAA = arr.getJSONObject(0).getJSONObject("credentials").getString("url");

			String serviceUrl = sDestinationURL + "/destination-configuration/v1/destinations/kevinWired";

			URI xsuaaUrl = new URI(XSUAA);
			// get acccess token
			UaaContextFactory factory = UaaContextFactory.factory(xsuaaUrl).authorizePath("/oauth/authorize")
					.tokenPath("/oauth/token");
			TokenRequest tokenRequest = factory.tokenRequest();
			tokenRequest.setGrantType(GrantType.CLIENT_CREDENTIALS);
			tokenRequest.setClientId(clientid);
			tokenRequest.setClientSecret(clientSecrect);
			UaaContext xsuaaContext = factory.authenticate(tokenRequest);
			CompositeAccessToken accessToken = xsuaaContext.getToken();
			System.out.println("------access-----token");
			System.out.println(accessToken.toString());
			// destination url
			URL sServiceurl = new URL(serviceUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) sServiceurl.openConnection();

			urlConnection.setRequestProperty("Authorization", "Bearer " + accessToken.toString());
			urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			urlConnection.setRequestProperty("Accept", "application/json");

			status = urlConnection.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine;
			StringBuffer content = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				content.append(inputLine);
			}
			System.out.println(content);

			in.close();
			JSONObject destinationJSON = new JSONObject(content.toString());
			String TargetUrl = destinationJSON.getJSONObject("destinationConfiguration").getString("URL");
			if (destinationJSON.getJSONObject("destinationConfiguration").getString("Authentication")
					.equals("NoAuthentication")) {
				URL sTargetURL = new URL(TargetUrl+"/sap/opu/odata/sap/Z_PRODUCTS_ODATA_SRV/ProductsSet?$format=json");
				// set proxy
				JSONObject connectivityCredentials = obj.getJSONArray("connectivity").getJSONObject(0)
						.getJSONObject("credentials");

				String connProxyHost = connectivityCredentials.getString("onpremise_proxy_host");
				int connProxyPort = Integer.parseInt(connectivityCredentials.getString("onpremise_proxy_port"));

				String clientidConnect = connectivityCredentials.getString("clientid");
				String clientsecretConnect = connectivityCredentials.getString("clientsecret");

				UaaContextFactory factoryConnect = UaaContextFactory.factory(xsuaaUrl).authorizePath("/oauth/authorize")
						.tokenPath("/oauth/token");
				TokenRequest tokenRequestConnect = factoryConnect.tokenRequest();
				tokenRequestConnect.setGrantType(GrantType.CLIENT_CREDENTIALS);
				tokenRequestConnect.setClientId(clientidConnect);
				tokenRequestConnect.setClientSecret(clientsecretConnect);
				UaaContext xsuaaContextConnect = factoryConnect.authenticate(tokenRequestConnect);
				CompositeAccessToken accessTokenConnect = xsuaaContextConnect.getToken();
				
				
				
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(connProxyHost, connProxyPort));
				System.out.println(proxy.toString());
				HttpURLConnection urlTargetConnection = (HttpURLConnection) sTargetURL.openConnection(proxy);
				urlTargetConnection.setRequestProperty("x-csrf-token", "fetch");
				urlTargetConnection.setRequestProperty("Proxy-Authorization", "Bearer " + accessTokenConnect);
				urlTargetConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlTargetConnection.setRequestProperty("Accept", "application/json");
				urlTargetConnection.setRequestProperty("SAP-Connectivity-SCC-Location_ID", "Shanghai");
				
				String authorization="";
	            authorization = "TRINHEM:Lemonade99";
				String basicAuth = "Basic " + new String(Base64.getEncoder().encode(authorization.getBytes()));
				urlTargetConnection.setRequestProperty("Authorization", basicAuth);				
				status = urlTargetConnection.getResponseCode();
				
				BufferedReader innew = new BufferedReader(new InputStreamReader(urlTargetConnection.getInputStream()));
				StringBuffer employeeContent = new StringBuffer();

				while ((inputLine = innew.readLine()) != null) {
					employeeContent.append(inputLine);
				}
				employeedata = employeeContent.toString();
				innew.close();
			}

//			// set proxy

//			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(connProxyHost, connProxyPort));
//
//			URL url = new URL("https://usphlhanaags07.phl.sap.corp:44311");
//			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection(proxy);
//
//			// get value of "clientid" and "clientsecret" from the environment variables
//			clientid = connectivityCredentials.getString("clientid");
//			String clientsecret = connectivityCredentials.getString("clientsecret");
//
//			// get the URL to xsuaa from the environment variables
//			URI xsuaaUrl = new URI(XSUAA);
//
//			// make request to UAA to retrieve access token
//			UaaContextFactory factory = UaaContextFactory.factory(xsuaaUrl).authorizePath("/oauth/authorize")
//					.tokenPath("/oauth/token");
//			TokenRequest tokenRequest = factory.tokenRequest();
//			tokenRequest.setGrantType(GrantType.CLIENT_CREDENTIALS);
//			tokenRequest.setClientId(clientid);
//			tokenRequest.setClientSecret(clientsecret);
//			UaaContext xsuaaContext = factory.authenticate(tokenRequest);
//			CompositeAccessToken accessToken = xsuaaContext.getToken();
//
//			// set access token as Proxy-Authorization header in the URL connection
//			urlConnection.setRequestProperty("Proxy-Authorization", "Bearer " + accessToken);
//
//			status = urlConnection.getResponseCode();
//
//			BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
//			String inputLine;
//			StringBuffer content = new StringBuffer();
//			while ((inputLine = in.readLine()) != null) {
//				content.append(inputLine);
//			}
//			System.out.println(content);
//
//			in.close();

//		        DB_USERNAME = arr.getJSONObject(0).getJSONObject("credentials").getString("user");
//		        DB_PASSWORD = arr.getJSONObject(0).getJSONObject("credentials").getString("password");
//		        DB_HOST = arr.getJSONObject(0).getJSONObject("credentials").getString("host").split(",")[0];
//		        DB_PORT = arr.getJSONObject(0).getJSONObject("credentials").getString("port");
//		        
//		        newList.add("user:"+DB_USERNAME);
//		        newList.add("Password:"+DB_PASSWORD);
//		        newList.add("host:"+DB_HOST);
//		        newList.add("port:"+DB_PORT);
//		        
//		        String DB_READ_CONNECTION_URL = "jdbc:sap://" + DB_HOST + ":" + DB_PORT ;
//
//		        conn = (Connection) DriverManager.getConnection(DB_READ_CONNECTION_URL, DB_USERNAME, DB_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
//		        System.out.println("Connection Error");
		}

		return employeedata.toString();
	}

}
