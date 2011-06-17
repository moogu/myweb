<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<%@ page import="javax.servlet.http.*" %> 


<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <meta http-equiv="X-UA-Compatible" content="chrome=0">

    <!--                                                               -->
    <!-- Consider inlining CSS to reduce the number of requested files -->
    <!--                                                               -->
    <link rel="stylesheet" type="text/css" href="res/css/gxt-all.css" />
    <link rel="stylesheet" type="text/css" href="res/css/gxt-gray.css" />
    <link rel="stylesheet" type="text/css" href="res/themes/slate/css/xtheme-slate.css" />  
    
    <%
        // The session is just needed to make works the sticky session mechanism on load-balancing
        // Absolutly no data are set on the the session. 
        HttpSession newSession = request.getSession(true);
        newSession.setMaxInactiveInterval(-1); // never expire
    %>    	


    <!--                                           -->
    <!-- Any title is fine                         -->
    <!--                                           -->
    <title>MyWeb</title>
    
    <!--                                           -->
    <!-- This script loads your compiled module.   -->
    <!-- If you add any GWT meta tags, they must   -->
    <!-- be added before this line.                -->
    <!--                                           -->
    <script type="text/javascript" language="javascript" src="mywebapp/mywebapp.nocache.js"></script>
    <script type="text/javascript" language="javascript" src="res/flash/swfobject.js"></script>
  </head>

  <!--                                           -->
  <!-- The body can have arbitrary html, or      -->
  <!-- you can leave the body empty if you want  -->
  <!-- to create a completely dynamic UI.        -->
  <!--                                           -->
  <body style="margin:0px auto; padding:0px; text-align:center; ">

    <!-- OPTIONAL: include this if you want history support -->
    <iframe src="javascript:''" id="__gwt_historyFrame" tabIndex='-1' style="position:absolute;width:0;height:0;border:0"></iframe>
    
    <!-- RECOMMENDED if your web app will not function without JavaScript enabled -->
    <noscript>
      <div style="width: 22em; position: absolute; left: 50%; margin-left: -11em; color: red; background-color: white; border: 1px solid red; padding: 4px; font-family: sans-serif">
        Your web browser must have JavaScript enabled
        in order for this application to display correctly.
      </div>
    </noscript>

    <table align="center">
      <tr>
        <td colspan="0" style="font-weight:bold;">        	
        	<img src="res/images/MyWeb-Logo.png" >
		</td>        
      </tr>
      <tr>
        <td id="MYWEB_ROOT_PANEL"></td>
      </tr>
    </table>
    
  </body>
</html>