# drools-linking

This is example of using [Drools](http://www.drools.org/) for linking bank transaction and application object.

### How to run?
1. Run Spring Boot Application `gradle run`
2. Run a test `gradlew cleanTest test -i --tests "com.bma.linking.LinkingServiceSpec.performance against application"`

### Linking Rules
Core part is [linking rules](/src/main/resources/com/bma/linking/rules/Linking.drl)
