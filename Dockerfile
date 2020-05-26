FROM eclipse/rdf4j-workbench:amd64-3.2.0-M2
USER root
COPY ./ /usr/local/tomcat/
RUN apk add maven openjdk8 && \
    mvn clean install && \
    cp ./target/semanticAnnotator.war /usr/local/tomcat/webapps && \
    chown -R tomcat.root /usr/local/tomcat/
