set MAVEN_OPTS=-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000
rem mvn -Dmaven.test.skip=true tomcat7:run-war
mvn tomcat7:run