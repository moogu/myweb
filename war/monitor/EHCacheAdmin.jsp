<%--
  By Fabio Furtado (July/2009)
  
  This JSP was created with the intention to see what is going on with our
  EHCache and how it behaves regarding the memory consumption.
--%>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.io.UnsupportedEncodingException" %>
<%@ page import="java.net.URLDecoder" %>
<%@ page import="java.net.URLEncoder" %>


<%@ page import="net.sf.ehcache.Cache" %>
<%@ page import="net.sf.ehcache.CacheManager" %>
<%@ page import="net.sf.ehcache.Element" %>
<%@ page import="net.sf.ehcache.Statistics" %>

<html>

<head>
<title> EH Cache Admin Servlet </title>
</head>

<script type="text/javascript">
	function clearCacheAllServers()	{
		var xmlhttp;
		if (window.XMLHttpRequest) {
		  // code for IE7+, Firefox, Chrome, Opera, Safari
		  xmlhttp=new XMLHttpRequest();
		} else if (window.ActiveXObject) {
		  // code for IE6, IE5
		  xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
		} else {
		  alert("Your browser does not support XMLHTTP!");
		}
		
		xmlhttp.onreadystatechange=function() {
			if (xmlhttp.readyState == 4) {
			  message = document.getElementById("message");
			  message.innerHTML = "Cache will be cleaned in all CMLI servers in the next time it is used !"
			}
		}

		xmlhttp.open("GET","clearCache.html",true);
		xmlhttp.send(null);
    }
</script>

<body>

<%
  String message = "";
  CacheManager cacheManager = CacheManager.getInstance();
  String clearCache = request.getParameter("clearCache");
  if ("all".equals(clearCache)) {
	  CacheManager.getInstance().clearAll();
	  message = "The whole cache has been cleaned up !";
  } else {
	  Cache cache = cacheManager.getCache(clearCache);
	  if (cache !=  null) {
		  cache.removeAll();
		  cache.clearStatistics();
		  Runtime.getRuntime().gc();
		  message = "The cache '" +clearCache+ "' has been cleaned up !";
	  }		  
  }
  
  int cacheHits = 0;
  int cacheMisses = 0;
  int inMemoryHits = 0;
  int objectCount = 0;
  int onDiskHits = 0;
  
  String showCache = request.getParameter("showCache");
  if (showCache!=null) {
  // display whats in the cache here
 %>
 <h3>Cache Name=<%=showCache%>
 <table border=1 padding=0>
    <tr>
        <th>Key</th>
        <th>Created</th>
        <th>Last Access</th>
        <th>Hit Count</th>
    </tr>
<%
Cache cache = cacheManager.getCache(showCache);
SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm:ss");

Object elementKey = request.getParameter("key");
if (elementKey!=null) {
    out.write("<tr><td>" + elementKey.toString() + "</td>");
    Element e = cache.getQuiet(elementKey);
    if (e!=null) {
	    Object o = e.getObjectValue();
	    out.write("<td>" + sdf.format(new Date(e.getCreationTime())) + "</td>");
	    out.write("<td>" + sdf.format(new Date(e.getLastAccessTime())) + "</td>");
	    out.write("<td>" + e.getHitCount() + "</td>");
	    // This time do a toString on the object ...
	    out.write("</tr>");
	    out.write("</table><table><tr><td>");	    
	    out.write(o.toString());
	    out.write("</td></tr></table>");
    }
} else {
//for (Object key : cache.getKeys()) {
for (Iterator it = cache.getKeys().iterator(); it.hasNext();) {
    Object key = it.next();
    out.write("<tr><td>" + key.toString() + "</td>");
    Element e = cache.getQuiet(key);
    if (e!=null) {
	    Object o = e.getObjectValue();
	    out.write("<td>" + sdf.format(new Date(e.getCreationTime())) + "</td>");
	    out.write("<td>" + sdf.format(new Date(e.getLastAccessTime())) + "</td>");
	    out.write("<td>" + e.getHitCount() + "</td>");
	    //out.write("<td>" + o.toString() + "</td>");
	    out.write("<td><A href=EHCacheAdmin.jsp?showCache=" + showCache + "&key=" + URLEncoder.encode(key.toString(),"UTF-8") + ">Object.toString()</A></td>");
	    out.write("</tr>");
    }
}
out.write("</table>");
}
} else {      
%>  
<h1> Cache Names Defined </h1>
<form method="post" name="clearCacheForm" action="EHCacheAdmin.jsp">
 <input type="hidden" name="clearCache" id="clearCache" value="all" />
 
 <table border="1" cellpadding="5" >
    <tr>
        <th> Associated Cache Name </th>
        <th> Object Count </th>
        <th> Cache Hits </th>
        <th> In memory hits </th>
        <th> On disk hits </th>
        <th> Cache Misses </th>
        <th> Clear </th>
    </tr>

<%
  String[] cacheNames = cacheManager.getCacheNames();
  for (int i = 0; i < cacheNames.length; i++) {
	 String name = cacheNames[i];
 	 Cache cache = cacheManager.getCache(name);
 	 if ("all".equals(clearCache)) {
 		 cache.clearStatistics();
 	 }
	 Statistics statistics = cache.getStatistics();
	 cacheHits += statistics.getCacheHits();
	 cacheMisses += statistics.getCacheMisses();
	 inMemoryHits += statistics.getInMemoryHits();
	 objectCount += statistics.getObjectCount();
	 onDiskHits += statistics.getOnDiskHits();
%>
        <tr>
	        <td> <A href=EHCacheAdmin.jsp?showCache=<%=statistics.getAssociatedCacheName() %>> <%=statistics.getAssociatedCacheName() %></A></td>
	        <td> <%=statistics.getObjectCount() %> </td>
	        <td> <%=statistics.getCacheHits() %> </td>
	        <td> <%=statistics.getInMemoryHits() %> </td>
	        <td> <%=statistics.getOnDiskHits() %> </td>
	        <td> <%=statistics.getCacheMisses() %> </td>
	        <td>
	          <input type="button" value="Clear" onClick="clearCache.value='<%=name%>';clearCacheForm.submit();"/>
	        </td>
	    </tr>
<%
  }
%>

        <tr>
	        <td> <b>TOTAL</b> </td>
	        <td> <b><%=objectCount %></b> </td>
	        <td> <b><%=cacheHits %></b> </td>
	        <td> <b><%=inMemoryHits %></b> </td>
	        <td> <b><%=onDiskHits %></b> </td>
	        <td> <b><%=cacheMisses %></b> </td>
	        <td>
	          <input type="button" value="Clear ALL" onClick="clearCacheForm.submit();"/>
	        </td>
	    </tr>

 </table>
 <br/>

<input type="button" value="Clear ALL in ALL Servers" onClick="clearCacheAllServers();"/>

<h1> Memory Status </h1>

Maximum memory: <%= NumberFormat.getInstance().format(Runtime.getRuntime().maxMemory() / 1024) %> kbytes<br/>
Total memory: <%= NumberFormat.getInstance().format(Runtime.getRuntime().totalMemory() / 1024) %> kbytes<br/>
Free  memory: <%= NumberFormat.getInstance().format(Runtime.getRuntime().freeMemory() / 1024) %> kbytes<br/>

<br/>
<input type="button" value="Refresh" onClick="clearCache.value=null;clearCacheForm.submit();"/>
<br/>
<br/>

<div id="message">
<%=message %>
</div>

<%}%>

</form>

</body>

</html>
