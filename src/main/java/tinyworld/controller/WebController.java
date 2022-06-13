package tinyworld.controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

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
	public List<String> getAllItems() {

		List<String> newList = new ArrayList<>();

		Connection conn = this.getConnection(newList);
		try {
			newList=executeSomething(conn);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return newList;
	}

	private List<String> executeSomething(Connection conn) throws SQLException {
		// String currentSchema = "";
//	    PreparedStatement prepareStatement = conn.prepareStatement("INSERT INTO \"FB578CC3DC744AF78AF7311B0F52DDB3\".\"TEST\" VALUES('1')");

		List<String> newList = new ArrayList<>();
		Statement stmt = conn.createStatement();
		ResultSet resultSet = stmt.executeQuery("SELECT NAME from FB578CC3DC744AF78AF7311B0F52DDB3.TEST;");
		System.out.print(resultSet);
		while (resultSet.next()) {
			String title = resultSet.getString(1);
			newList.add(title);
			System.out.println(title);
		}

		return newList;
	}

	private Connection getConnection(List<String> newList) {

		Connection conn = null;
		String DB_USERNAME = "";
		String DB_PASSWORD = "";
		String DB_HOST = "";
		String DB_PORT = "";

		try {
			JSONObject obj = new JSONObject(System.getenv("VCAP_SERVICES"));
			JSONArray arr = obj.getJSONArray("hana");
			System.out.println(arr);
			DB_USERNAME = arr.getJSONObject(0).getJSONObject("credentials").getString("user");
			DB_PASSWORD = arr.getJSONObject(0).getJSONObject("credentials").getString("password");
			DB_HOST = arr.getJSONObject(0).getJSONObject("credentials").getString("host").split(",")[0];
			DB_PORT = arr.getJSONObject(0).getJSONObject("credentials").getString("port");

			newList.add("user:" + DB_USERNAME);
			newList.add("Password:" + DB_PASSWORD);
			newList.add("host:" + DB_HOST);
			newList.add("port:" + DB_PORT);

			String DB_READ_CONNECTION_URL = "jdbc:sap://" + DB_HOST + ":" + DB_PORT;
//		        Class.forName("com.sap.cloud.db.jdbc");

			conn = (Connection) DriverManager.getConnection(DB_READ_CONNECTION_URL, DB_USERNAME, DB_PASSWORD);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Connection Error");
		}

		return conn;
	}
}
