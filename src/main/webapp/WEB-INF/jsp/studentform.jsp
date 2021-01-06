<%@ taglib prefix = "form" uri = "http://www.springframework.org/tags/form" %>
 <html>
 <head>
 <title>Student Registration Form</title>
 </head>
 <body>
 <form:form action = "studentregis" modelAttribute = "student" >
 FIRST NAME: <form:input path = "fname" />
 <br></br>
 LAST NAME: <form:input path = "lname" />
 <br></br>
 <input type = "submit" value = "Submit"/>
 </form:form>
 </body>
</html>