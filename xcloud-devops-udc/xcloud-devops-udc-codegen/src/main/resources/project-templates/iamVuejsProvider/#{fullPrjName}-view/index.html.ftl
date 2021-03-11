<!-- ${watermark} -->
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=1200,initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="ie=edge">
    <title>${organName?cap_first} ${projectName?cap_first}</title>
    <link rel="shortcut icon" href="./static/icon.png" />
    <link rel="stylesheet" href="./static/css/bootstrap-3.3.7.min.css" />
    <link rel="stylesheet" href="./static/css/login.css" />
    <link rel="stylesheet" href="./static/css/lanren.css" />
    <script type="text/javascript" src="./static/js/jquery-3.5.1.min.js"></script>
    <script type="text/javascript" src="./static/js/sysloader-2.0.1.min.js" fileUriPort="${entryAppPort}"
            fileUriDomainSubLevel="${projectName?lower_case}-services" path="/${entryAppName}/iam-jssdk/assets" cache="false" mode="stable"
            refreshLevel="yyMMddhh"></script>
</head>
<body>
    <div id="app"></div>
    <!--<script type="text/javascript" src="./static/js/tech-anima-1.1.1.js"></script>-->
    <script type="text/javascript" src="./static/icon/iconfont.js"></script>
</body>
</html>
