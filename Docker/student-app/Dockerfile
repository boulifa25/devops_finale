# Image de base : JRE 17 léger
FROM eclipse-temurin:17-jre-alpine

# Dossier de travail dans le conteneur
WORKDIR /app

# Argument pour le JAR à copier (buildé par Maven)
ARG JAR_FILE=target/*.jar

# Copier le JAR généré par Maven dans l'image
COPY ${JAR_FILE} app.jar

# Commande de lancement
ENTRYPOINT ["java", "-jar", "/app/app.jar"]