# TDC Sample Search

### Code template

We are using [Google Java Style](https://google.github.io/styleguide/javaguide.html)
- [*IntelliJ plugin*](https://plugins.jetbrains.com/plugin/8527-google-java-format)
- [Source code](https://github.com/google/google-java-format)

### IntelliJ plugin

- [*IntelliJ plugin*](https://plugins.jetbrains.com/plugin/8527-google-java-format)
- [Lombok](https://plugins.jetbrains.com/plugin/6317-lombok-plugin)
- [Spock Framework](https://plugins.jetbrains.com/plugin/7114-spock-framework-enhancements)
- [Gherkin](https://plugins.jetbrains.com/plugin/7211-gherkin)
- [Cucumber](https://plugins.jetbrains.com/plugin/7212-cucumber-for-java)


### Start application
```
cd app
mvn spring-boot:run
```

### Run Unit test
```
cd app
mvn clean verify
```

### Run Component test
```
cd app
mvn spring-boot:run &

cd ../component-test
mvn clean verify
```

### Run Performance test
```
cd app
mvn spring-boot:run &

cd component-test
mvn clean package -DskipTests gatling:execute 
```