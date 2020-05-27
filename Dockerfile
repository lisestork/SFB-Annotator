FROM eclipse/rdf4j-workbench:amd64-3.2.0-M2
USER root
ENV PATH=${PATH}:/tmp/eclipse-rdf4j-3.2.0-M2/bin
COPY ./ /usr/local/tomcat/
RUN apk update && apk add maven openjdk8
RUN mvn clean install
RUN ln -s ./target/semanticAnnotator.war /usr/local/tomcat/webapps/
RUN chown -R tomcat.root /usr/local/tomcat
RUN mkdir -p /var/rdf4j/server
RUN cat create_store.txt | console.sh
