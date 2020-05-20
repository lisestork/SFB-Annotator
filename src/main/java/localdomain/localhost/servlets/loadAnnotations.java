package localdomain.localhost.servlets;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
public class loadAnnotations extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		// get parameters that were sent with the httpRequest
		String firstname = request.getParameter("firstname");
		String lastname = request.getParameter("lastname");

		// initialise variables
		Connection con = null;
		String anno = new String();
		ResultSet result = null;
		String output = new String();
		int nr = 0;

		try {
			// Register JDBC driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/INSERT_DATABASE_NAME", "INSERT_USERNAME",
					"INSERT_PASSWORD");

			String query = "SELECT url, x, y, height, width, class_model, organismID FROM Annotations WHERE firstname = '"
					+ firstname + "' and lastname='" + lastname + "'";
			String countQuery = "SELECT count(*) FROM (SELECT url, x, y, height, width, class_model, organismID FROM Annotations WHERE firstname = '"
					+ firstname + "' and lastname='" + lastname + ") class_model";

			// create statement
			Statement statement = con.createStatement();

			// count solutions to query
			result = statement.executeQuery(countQuery);

			while (result.next()) {
				output = result.getString("count(*)");
			}
			nr = Integer.parseInt(output);
			System.out.println(output);
			// if there are solutions to the query, store the solutions as a string.
			if (nr != 0) {
				result = statement.executeQuery(query);

				while (result.next()) {
					anno = anno + result.getString("url") + "," + result.getString("class_model") + ","
							+ result.getString("x") + "," + result.getString("y") + "," + result.getString("width")
							+ "," + result.getString("height") + "," + result.getString("organismID") + "\n";
				}
				anno.toString();
			} else {
				anno = "no annotations";
			}

			con.close();

		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
		}

		System.out.println("the annotation for this image was retrieved");
		System.out.println("containing" + nr + "lines");
		System.out.println(anno);

		// encode result to send via URL, send response
		URLEncoder.encode(anno, "UTF-8");
		response.setContentType("text/html");

		response.getWriter().write(nr + ";" + anno);

	}
}
