@echo off
echo Starting Maven build with SSL bypass...

:: SSL 우회 설정
set MAVEN_OPTS=-Dmaven.wagon.http.ssl.insecure=true ^
-Dmaven.wagon.http.ssl.allowall=true ^
-Dmaven.wagon.http.ssl.ignore.validity.dates=true ^
-Djavax.net.ssl.trustStore=NUL ^
-Djavax.net.ssl.trustStoreType=Windows-ROOT ^
-Djdk.tls.client.protocols=TLSv1.2

:: Maven 빌드 실행
mvn -Dmaven.repo.local=local-repo ^
-s settings.xml ^
clean compile ^
-DskipTests ^
-Dmaven.test.skip=true ^
-Dcheckstyle.skip=true ^
-Dmaven.javadoc.skip=true ^
-Dmaven.wagon.http.ssl.insecure=true ^
-Dmaven.wagon.http.ssl.allowall=true ^
-U