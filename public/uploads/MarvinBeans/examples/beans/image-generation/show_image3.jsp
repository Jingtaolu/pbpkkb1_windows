<html>
<body>
<%
String format = java.net.URLEncoder.encode("png:w200,h200,b32,#ffffff");
String filename = java.net.URLEncoder.encode("C:\\Program Files\\Apache Tomcat 4.0\\webapps\\examples\\jsp\\marvin\\caffeine.smi");
%>
<img src="http://localhost:8080/examples/jsp/marvin/generate_image.jsp?file=<%=filename%>&format=<%= format%>"
width=200 height=200>
</body>
</html>
