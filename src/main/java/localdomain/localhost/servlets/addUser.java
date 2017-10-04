package localdomain.localhost.servlets;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Servlet implementation class WriteAnnotations
 */
public class addUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		//get parameters that were sent with the httpRequest
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String institution = request.getParameter("institution");
		
		//STORE THE ANNOTATIONS AS TEXTFILES ON SERVER & ON SQL DATABASE FOR RETRIEVAL/////////////////////////
		Connection con = null;
		
		try {
			//Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/INSERT_DATABASE_NAME", "INSERT_USERNAME", "INSERT_PASSWORD");
    		String insert = "INSERT INTO users (firstname, lastname, institution) SELECT '"+firstname+"','"+lastname+"','"+institution+"' FROM dual WHERE NOT EXISTS (SELECT 1 FROM users WHERE firstname = '"+firstname+"' AND lastname = '"+lastname+"' AND institution = '"+institution+"');";

			System.out.println(insert);
			
            Statement statement = con.createStatement();
    		statement.executeUpdate(insert);
    		
    		con.close();
    		
		} catch (Exception e) { 
            System.err.println("Got an exception! "); 
            System.err.println(e.getMessage()); 
        } 
		
		System.out.println("Successfully Copied JSON Object to SQL database...");
		
	}
}
