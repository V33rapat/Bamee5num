Set-Location "c:\Software_Design\Bamee5num\Project Principle\demo"
$env:JAVA_HOME = "C:\Users\NBODT\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.8.9-hotspot"
.\mvnw.cmd spring-boot:run *>&1 | Tee-Object -FilePath "c:\Software_Design\Bamee5num\Project Principle\demo\spring-run.log"