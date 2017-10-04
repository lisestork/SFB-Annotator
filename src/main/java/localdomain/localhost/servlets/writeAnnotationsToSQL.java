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
public class writeAnnotationsToSQL extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		//get parameters that were sent with the httpRequest
		String url = request.getParameter("url");
		String class_model = request.getParameter("class");	
		String x = request.getParameter("x");
		String y = request.getParameter("y");
		String height = request.getParameter("height");
		String width = request.getParameter("width");
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");
		String organismID = request.getParameter("organismID");		
		
		//STORE THE ANNOTATIONS IN SQL DATABASE FOR RETRIEVAL/////////////////////////
		Connection con = null;
				
		try {
			//Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/INSERT_DATABASE_NAME", "INSERT_USERNAME", "INSERT_PASSWORD");
			
    		String insert = "INSERT INTO Annotations (url, firstname, lastname, class_model, organismID, x, y, height, width) VALUES ('"+url+"','"+firstname+"','"+lastname+"','"+class_model+"','"+organismID+"','"+x+"','"+y+"','"+height+"','"+width+"');";

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
