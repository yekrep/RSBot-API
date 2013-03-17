@SET docs=docs
@IF EXIST "%docs%" @RMDIR /S /Q "%docs%" > NUL
@MKDIR "%docs%"
javadoc -d "%docs%" -version -author -windowtitle "RSBot API Documentation" -header "RSBot&trade; API" -footer "Copyright &copy; 2011-2013 J.P. Holdings Int'l Ltd" -bottom "<script type='text/javascript' src='//powerbot-gold4rs.netdna-ssl.com/community/public/min/?s=1&amp;f=ga.js'></script>" -charset "utf-8" -docencoding "utf-8" -classpath src org.powerbot.script org.powerbot.script.task org.powerbot.script.event
