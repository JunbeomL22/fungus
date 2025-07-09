@echo off
echo Creating local repository structure...
mkdir local-repo\org\apache\maven\plugins\maven-clean-plugin\3.2.0 2>nul
mkdir local-repo\org\apache\maven\plugins\maven-compiler-plugin\3.11.0 2>nul
mkdir local-repo\org\apache\maven\plugins\maven-surefire-plugin\3.1.2 2>nul
mkdir local-repo\org\codehaus\mojo\exec-maven-plugin\3.0.0 2>nul
mkdir local-repo\junit\junit\4.13.2 2>nul
mkdir local-repo\org\hamcrest\hamcrest-core\2.2 2>nul
mkdir local-repo\net\openhft\chronicle-core\2.20.1 2>nul
mkdir local-repo\org\openjdk\jmh\jmh-core\1.11 2>nul
mkdir local-repo\org\openjdk\jmh\jmh-generator-annprocess\1.11 2>nul

echo.
echo Downloading dependencies using curl...
echo.

echo Downloading maven-clean-plugin...
curl -k -L -o local-repo\org\apache\maven\plugins\maven-clean-plugin\3.2.0\maven-clean-plugin-3.2.0.jar https://repo1.maven.org/maven2/org/apache/maven/plugins/maven-clean-plugin/3.2.0/maven-clean-plugin-3.2.0.jar
curl -k -L -o local-repo\org\apache\maven\plugins\maven-clean-plugin\3.2.0\maven-clean-plugin-3.2.0.pom https://repo1.maven.org/maven2/org/apache/maven/plugins/maven-clean-plugin/3.2.0/maven-clean-plugin-3.2.0.pom

echo.
echo Downloading maven-compiler-plugin...
curl -k -L -o local-repo\org\apache\maven\plugins\maven-compiler-plugin\3.11.0\maven-compiler-plugin-3.11.0.jar https://repo1.maven.org/maven2/org/apache/maven/plugins/maven-compiler-plugin/3.11.0/maven-compiler-plugin-3.11.0.jar
curl -k -L -o local-repo\org\apache\maven\plugins\maven-compiler-plugin\3.11.0\maven-compiler-plugin-3.11.0.pom https://repo1.maven.org/maven2/org/apache/maven/plugins/maven-compiler-plugin/3.11.0/maven-compiler-plugin-3.11.0.pom

echo.
echo Downloading maven-surefire-plugin...
curl -k -L -o local-repo\org\apache\maven\plugins\maven-surefire-plugin\3.1.2\maven-surefire-plugin-3.1.2.jar https://repo1.maven.org/maven2/org/apache/maven/plugins/maven-surefire-plugin/3.1.2/maven-surefire-plugin-3.1.2.jar
curl -k -L -o local-repo\org\apache\maven\plugins\maven-surefire-plugin\3.1.2\maven-surefire-plugin-3.1.2.pom https://repo1.maven.org/maven2/org/apache/maven/plugins/maven-surefire-plugin/3.1.2/maven-surefire-plugin-3.1.2.pom

echo.
echo Downloading exec-maven-plugin...
curl -k -L -o local-repo\org\codehaus\mojo\exec-maven-plugin\3.0.0\exec-maven-plugin-3.0.0.jar https://repo1.maven.org/maven2/org/codehaus/mojo/exec-maven-plugin/3.0.0/exec-maven-plugin-3.0.0.jar
curl -k -L -o local-repo\org\codehaus\mojo\exec-maven-plugin\3.0.0\exec-maven-plugin-3.0.0.pom https://repo1.maven.org/maven2/org/codehaus/mojo/exec-maven-plugin/3.0.0/exec-maven-plugin-3.0.0.pom

echo.
echo Downloading junit...
curl -k -L -o local-repo\junit\junit\4.13.2\junit-4.13.2.jar https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.jar
curl -k -L -o local-repo\junit\junit\4.13.2\junit-4.13.2.pom https://repo1.maven.org/maven2/junit/junit/4.13.2/junit-4.13.2.pom

echo.
echo Downloading hamcrest-core...
curl -k -L -o local-repo\org\hamcrest\hamcrest-core\2.2\hamcrest-core-2.2.jar https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/2.2/hamcrest-core-2.2.jar
curl -k -L -o local-repo\org\hamcrest\hamcrest-core\2.2\hamcrest-core-2.2.pom https://repo1.maven.org/maven2/org/hamcrest/hamcrest-core/2.2/hamcrest-core-2.2.pom

echo.
echo Downloading chronicle-core...
curl -k -L -o local-repo\net\openhft\chronicle-core\2.20.1\chronicle-core-2.20.1.jar https://repo1.maven.org/maven2/net/openhft/chronicle-core/2.20.1/chronicle-core-2.20.1.jar
curl -k -L -o local-repo\net\openhft\chronicle-core\2.20.1\chronicle-core-2.20.1.pom https://repo1.maven.org/maven2/net/openhft/chronicle-core/2.20.1/chronicle-core-2.20.1.pom

echo.
echo Downloading jmh-core...
curl -k -L -o local-repo\org\openjdk\jmh\jmh-core\1.11\jmh-core-1.11.jar https://repo1.maven.org/maven2/org/openjdk/jmh/jmh-core/1.11/jmh-core-1.11.jar
curl -k -L -o local-repo\org\openjdk\jmh\jmh-core\1.11\jmh-core-1.11.pom https://repo1.maven.org/maven2/org/openjdk/jmh/jmh-core/1.11/jmh-core-1.11.pom

echo.
echo Downloading jmh-generator-annprocess...
curl -k -L -o local-repo\org\openjdk\jmh\jmh-generator-annprocess\1.11\jmh-generator-annprocess-1.11.jar https://repo1.maven.org/maven2/org/openjdk/jmh/jmh-generator-annprocess/1.11/jmh-generator-annprocess-1.11.jar
curl -k -L -o local-repo\org\openjdk\jmh\jmh-generator-annprocess\1.11\jmh-generator-annprocess-1.11.pom https://repo1.maven.org/maven2/org/openjdk/jmh/jmh-generator-annprocess/1.11/jmh-generator-annprocess-1.11.pom

echo.
echo Dependencies downloaded to local-repo directory.
