package com.roshka.bootcamp;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.PrintWriter;
import java.sql.*;
import java.util.Enumeration;

@WebServlet("/consulta2")
public class Consulta2 extends HttpServlet {
    Connection connection;

    public void init(ServletConfig config) {
        ServletContext context = config.getServletContext();

        //mostramos los parametros del contexto
        Enumeration<String> attributeNames = context.getInitParameterNames();

        while(attributeNames.hasMoreElements()) {
            String eachName = attributeNames.nextElement();
            System.out.println("Context Param name: " + eachName);
        }

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection(context.getInitParameter("dbUrl"), context.getInitParameter("dbUser"), context.getInitParameter("dbPassword"));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }


    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res) {
        System.out.println("pasa por aca uwuwuwu");
        try {
            Statement stmt = connection.createStatement();
            res.setContentType("text/html");
            PrintWriter out = res.getWriter();
            ResultSet rs = stmt
                    .executeQuery("  select cliente_id, CAST(sum(p.precio*f.cantidad) AS DECIMAL(12, 2)) as gasto\n" +
                            "  from factura as a\n" +
                            "  join factura_detalle as f\n" +
                            "  on a.id = f.factura_id\n" +
                            "  join producto as p\n" +
                            "  on f.producto_id = p.id\n" +
                            "  group by cliente_id \n" +
                            "  order by gasto desc; ");
            out.println("<!doctype html>\n" +
                    "<html lang=\"en\">\n" +
                    "  <head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "    <title>Bootstrap demo</title>\n" +
                    "\n" +
                    "    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.2.0-beta1/dist/css/bootstrap.min.css\" rel=\"stylesheet\" integrity=\"sha384-0evHe/X+R7YkIZDRvuzKMRqM+OrBnVFBL6DOitfPri4tjfHxaWutUpFmBp4vmVor\" crossorigin=\"anonymous\">\n" +
                    "  </head>\n" +
                    "  <body class=\"p-5 mb-5 bg-info text-dark\" >" +
                    "<h1 class=\"h1\">CONSULTA 2</h1>");
            out.println("<div class=\"col\">\n" +
                    "            <div class=\"d-grid gap-2\">\n" +
                    "              <a href='index.jsp'><button class=\"btn btn-primary\" type = \"button\">Menu Principal</button></a>\n" +
                    "            </div> <br>");
        out.println("<table class=\"table table-dark\">\n" +
                "  <thead>\n" +
                "    <tr class=\"table-active\">\n" +
                "      <th scope=\"col\">ID</th>\n" +
                "      <th scope=\"col\">GASTO</th>\n" +
                "    </tr>\n" +
                "  </thead>\n" +
                "  <tbody>\n");

            while (rs.next()) {
                String nombre = rs.getString("cliente_id");
                String count = rs.getString("gasto");

                out.println(" <tr class=\"table-active\"><td class=\"table-active\">" + nombre + "</td>");
                out.println("<td class=\"table-active\">" + count + "</td></tr>");
            }
            out.println(" </tbody> </table></body>");
            out.println("</html>");
            rs.close();
            stmt.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }

    }

    public void destroy() {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
