FROM eclipse/rdf4j-workbench:amd64-latest

USER root
COPY tomcat/conf/tomcat-users.xml /usr/local/tomcat/conf/
COPY target/semanticAnnotator.war /usr/local/tomcat/webapps
RUN chown tomcat.root /usr/local/tomcat/conf/tomcat-users.xml
