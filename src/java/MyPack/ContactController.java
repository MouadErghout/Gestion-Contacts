package MyPack;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */


import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 *
 * @author mouad
 */

public class ContactController extends HttpServlet {

    private DataSource ds;
    private Connection conn;

    @Override
    public void init() throws ServletException
    {
        super.init();
        
        Context initContext;
        try {
            initContext = new InitialContext();
         
            ds = (DataSource)initContext.lookup("java:/comp/env/jdbc/cc") ;
            // demande d'une connexion à cette datasource
        } catch (NamingException ex) {
            Logger.getLogger(ContactController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
        conn = ds.getConnection();
        }catch (SQLException e) {
           
            return;
        }
    }
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MyServlet</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MyServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        
        List<Contact> contacts = new ArrayList<>();
        String sql="";
        if("Rechercher".equals(request.getParameter("method"))){
            String critere=request.getParameter("critere");
            sql = "SELECT * FROM TUSER WHERE nom= '"+critere+"'or id='"+critere+"'";
        }else
        sql = "SELECT * FROM tuser";
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String email = resultSet.getString("email");

                Contact contact = new Contact(id, nom, prenom, email);
                contacts.add(contact);
                request.setAttribute("contacts", contacts); 
                System.out.println("contacts");
            }

        } catch (SQLException e) {
            response.getWriter().println("Erreur : " + e.getMessage());
            return;
        }
        request.setAttribute("contacts", contacts);
        request.getRequestDispatcher("CRUDContacts.jsp").forward(request, response);
    
}


    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
   protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        if("Modifier".equals(request.getParameter("method")))
        this.doPut(request, response);
    else if("Supprimer".equals(request.getParameter("method")))
        this.doDelete(request, response);
    else if("Rechercher".equals(request.getParameter("method")))
    {
        this.doGet(request, response);
    }else{
    // Récupérer les données du nouveau contact depuis la requête
    String id = request.getParameter("id");
    String nom = request.getParameter("nom");
    String prenom = request.getParameter("prenom");
    String email = request.getParameter("email");

    try {
        conn = ds.getConnection();
        validateEmail(email);
        // Créer la requête d'insertion
        String sql = "INSERT INTO tuser (nom, prenom, email) VALUES (?, ?, ?)";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, nom);
        statement.setString(2, prenom);
        statement.setString(3, email);
        
        // Exécuter la requête d'insertion
        int rowsInserted = statement.executeUpdate();
        if (rowsInserted > 0) {
            response.getWriter().println("Le nouveau contact a été ajouté avec succès.");
            this.doGet(request, response);
        } else {
            response.getWriter().println("Erreur lors de l'ajout du contact.");
        }
    } catch (Exception e) {
        request.setAttribute("invalidatemail", e);
        this.doGet(request, response);
    } 
    }
}
    @Override
   protected void doPut(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    // Récupérer les données mises à jour du contact depuis la requête
    String id = request.getParameter("id");
    String nom = request.getParameter("nom");
    String prenom = request.getParameter("prenom");
    String email = request.getParameter("email");

    try {
        conn = ds.getConnection();
        validateEmail(email);

        // Créer la requête de mise à jour
        String sql = "UPDATE tuser SET nom = ?, prenom = ?, email = ? WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, nom);
        statement.setString(2, prenom);
        statement.setString(3, email);
        statement.setString(4, id);


        // Exécuter la requête de mise à jour
        int rowsUpdated = statement.executeUpdate();
        if (rowsUpdated > 0) {
            this.doGet(request, response);
        } else {
            response.getWriter().println("Erreur lors de la mise à jour du contact.");
        }
    } catch (Exception e) {
        request.setAttribute("invalidatemail", e);
        this.doGet(request, response);
    } 
}

    @Override
   protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    // Récupérer l'ID du contact à supprimer depuis la requête
    String id = request.getParameter("id");

    try {
        conn = ds.getConnection();

        // Créer la requête de suppression
        String sql = "DELETE FROM tuser WHERE id = ?";
        PreparedStatement statement = conn.prepareStatement(sql);
        statement.setString(1, id);

        // Exécuter la requête de suppression
        int rowsDeleted = statement.executeUpdate();
        if (rowsDeleted > 0) {
            response.getWriter().println("Le contact a été supprimé avec succès.");
            this.doGet(request, response);
        } else {
            response.getWriter().println("Erreur lors de la suppression du contact.");
        }
    } catch (SQLException e) {
        response.getWriter().println("Erreur : " + e.getMessage());
    }
}



    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    public void validateEmail(String value) throws Exception{
        
        
        if (!value.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])")){
            String message ="Adresse Email invalide !";
            throw new Exception(message);
        }
    }

}
