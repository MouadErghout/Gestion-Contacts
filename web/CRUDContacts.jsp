<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Liste des contacts</title>
</head>
<body>
<center>
    <h1>Liste des contacts</h1>
    <form action="ContactController" method="post">
        <input type="text" name="critere">
        
        <input type="submit" name="method" value="Rechercher">
    </form>
    
    <!-- Affichage de la liste des contacts -->
    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Nom</th>
                <th>Prénom</th>
                <th>Email</th>
                <th colspan="2">Actions</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="contact" items="${contacts}">
                <tr>
                    <form action="ContactController" method="post">
                        <td><input type="texte" name="id" value="${contact.id}"></td>
                        <td><input type="texte" name="nom" value="${contact.nom}"></td>
                        <td><input type="texte" name="prenom" value="${contact.prenom}"></td>
                        <td><input type="text" name="email" value="${contact.email}"></td>
                        <td>
                            <input type="submit" name="method" value="Modifier">
                        </td>
                        <td>
                            <input type="submit" name="method" value="Supprimer">
                        </td>
                    </form>
                </tr>
            </c:forEach>
        </tbody>
</table><br>
    <form action="ContactController">
        <input type="submit" name="lister" value="Lister les contacts">
    </form>
    <!-- Formulaire d'ajout d'un contact -->
    <h2>Ajouter un nouveau contact</h2>
    <table>
        <form action="ContactController" method="post">
            <tr>
                <th>
                    <label for="nom">Nom</label>
                </th>
                <td>
                    <input type="text" id="nom" name="nom" value="" required> 
                </td>
            </tr>
            <tr>            
                <th>
                    <label for="prenom">Prénom</label>
                </th>
                <td>
                    <input type="text" id="prenom" name="prenom" value="" required>
                </td>
            </tr>
            <tr>            
                <th>
                    <label for="email">Email</label>
                </th>
                <td>
                    <input type="text" id="email" name="email" value="" required>
                </td>
            </tr>
            <tr>            
                <th colspan="2" ><input type="submit" value="Ajouter"></th>
            </tr>
        </form>
        </table>
        <c:if test="${not empty invalidatemail}">
                 <p style="color: red">${invalidatemail.message}</p>
            </c:if>
</center>
</body>
</html>
