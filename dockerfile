FROM amazoncorretto:23-alpine-jdk

COPY target/snake-game-1.0.0.jar snake-game.jar

ENTRYPOINT [ "java", "-jar", "snake-game.jar" ]

