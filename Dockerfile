FROM eclipse/rdf4j-workbench:amd64-3.2.0-M2
USER root
ENV PATH=${PATH}:/tmp/eclipse-rdf4j-3.2.0-M2/bin
WORKDIR /usr/local/tomcat/
COPY ./ ./
RUN apk update && apk add maven openjdk8 \
    && mvn clean install \
    && mv ./target/semanticAnnotator.war ./webapps/ \
    && chown -R tomcat.root ./
