# Этап сборки
FROM gradle:8.9-jdk21 AS build
WORKDIR /app

# Копируем только файлы сборки для кеша зависимостей
COPY build.gradle settings.gradle gradlew* ./
COPY gradle gradle
RUN ./gradlew dependencies --no-daemon || return 0

# Копируем весь проект и собираем
COPY . .
RUN ./gradlew bootJar -x test --no-daemon

# Этап запуска
FROM eclipse-temurin:21-jre
WORKDIR /app

# Копируем JAR из сборки
COPY --from=build /app/build/libs/*.jar app.jar

ENTRYPOINT ["java","-jar","app.jar"]
