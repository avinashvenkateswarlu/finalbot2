<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>AI-Bot</title>
        <script type="text/javascript">
            window.onload=function(){
                document.getElementById("bd").style.height=(window.innerHeight-25)+"px";
            };
            
            function update()
            {
                document.getElementById("console").innerHTML="Processing...";
                                  if (window.XMLHttpRequest)
				  {// code for IE7+, Firefox, Chrome, Opera, Safari
                                    peopleonlinexmlhttp=new XMLHttpRequest();
				  }
				else
				  {// code for IE6, IE5
                                     peopleonlinexmlhttp=new ActiveXObject("Microsoft.XMLHTTP");
				  }
				peopleonlinexmlhttp.onreadystatechange=function()
				  {
					if (peopleonlinexmlhttp.readyState==4 && peopleonlinexmlhttp.status==200)
					{
                                            document.getElementById("console").innerHTML=peopleonlinexmlhttp.responseText;
					}
				  }
                                  peopleonlinexmlhttp.open("GET","secured/index.jsp",true);
                                  setTimeout(function(){if(!peopletemp){peopleonlinexmlhttpreq.abort();}}, 5000);
                                  peopleonlinexmlhttp.send();
            }
        </script>
    </head>
    <body id="bd">
        <div style="height: 20%;">
        <h1>DashBoard</h1>
        <button style="font-size: 20px;" onclick="update()">Update Bot</button>
          <span class="skype-button rounded" data-bot-id="ee12e421-b1a9-46c4-85cb-07af1a80bbf1" data-text="Test Bot" style="margin-bottom: 15px;background-color: transparent"></span>
          <script src="https://latest-swc.cdn.skype.com/sdk/v1/sdk.js"></script>
          </div>
        <h2>CONSOLE:</h2>
        <div id="console" style="font-size: 15px;padding-left:10px;background-color: #333333;color: #eee;width:50%;height: 65%;overflow-wrap: break-word;word-wrap: break-word;overflow-y: auto;" contenteditable>
              Click on Update to Update bot intents.
          </div>
    </body>
</html>
