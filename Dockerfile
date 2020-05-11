FROM eclipse/rdf4j-workbench:amd64-latest

USER root
COPY ./ /usr/local/tomcat/
RUN apk add maven openjdk8 && \
    mvn clean install && \
    cp ./target/semanticAnnotator.war /usr/local/tomcat/webapps && \
    chown -R tomcat.root /usr/local/tomcat/
