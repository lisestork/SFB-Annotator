FROM tomcat:8-jdk8-corretto

RUN cp -R /usr/local/tomcat/webapps.dist/manager /usr/local/tomcat/webapps
COPY tomcat/manager/META-INF/context.xml /usr/local/tomcat/webapps/manager/META-INF/context.xml
COPY tomcat/conf/tomcat-users.xml /usr/local/tomcat/conf
COPY target/semanticAnnotator.war /usr/local/tomcat/webapps
RUN chown -R root.root /usr/local/tomcat