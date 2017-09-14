setx M2_HOME "%SourcesPath%\apache-maven-3.5.0"
setx M2 "%SourcesPath%\apache-maven-3.5.0"
setx JAVA_HOME "C:\Program Files\Java\jdk1.8.0_91"
setx PATH "%M2_HOME%\bin\;%JAVA_HOME%\bin\"
mvn -v
mvn install