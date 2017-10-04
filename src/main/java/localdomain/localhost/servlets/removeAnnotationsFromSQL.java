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
public class removeAnnotationsFromSQL extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		//get parameters that were sent with the httpRequest
			String url = request.getParameter("url");
			String firstname = request.getParameter("firstname");
			String lastname = request.getParameter("lastname");		
			String class_model = request.getParameter("class");	
			String x = request.getParameter("x");	
			String y = request.getParameter("y");	
			String height = request.getParameter("height");	
			String width = request.getParameter("width");	
			String action = request.getParameter("action");	
		
		//STORE THE ANNOTATIONS AS TEXTFILES ON SERVER & ON SQL DATABASE FOR RETRIEVAL/////////////////////////
		Connection con = null;
				
		try {
			//Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/Cultural_heritage_collection", "root", "Hie5uiphei");
			
    		String insert = "DELETE FROM OutputEvaluation1 WHERE url='"+url+"' and firstname='"+firstname+"' and lastname='"+lastname+"' and class_model='"+class_model+"' and x='"+x+"' and y='"+y+"' and height='"+height+"' and width='"+width+"' and action='"+action+"'" ;

			System.out.println(insert);
			
            Statement statement = con.createStatement();
    		statement.executeUpdate(insert);
    		
    		con.close();
    		
		} catch (Exception e) { 
            System.err.println("Got an exception! "); 
            System.err.println(e.getMessage()); 
        } 
		
		System.out.println("Successfully Deleted Annotation from SQL database...");
		
	}
}
