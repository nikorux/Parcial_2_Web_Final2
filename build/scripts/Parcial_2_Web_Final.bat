@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Parcial_2_Web_Final startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and PARCIAL_2_WEB_FINAL_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=

@rem Find java.exe
if defined JAVA_HOME goto findJavaFromJavaHome

set JAVA_EXE=java.exe
%JAVA_EXE% -version >NUL 2>&1
if "%ERRORLEVEL%" == "0" goto init

echo.
echo ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:findJavaFromJavaHome
set JAVA_HOME=%JAVA_HOME:"=%
set JAVA_EXE=%JAVA_HOME%/bin/java.exe

if exist "%JAVA_EXE%" goto init

echo.
echo ERROR: JAVA_HOME is set to an invalid directory: %JAVA_HOME%
echo.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation.

goto fail

:init
@rem Get command-line arguments, handling Windows variants

if not "%OS%" == "Windows_NT" goto win9xME_args

:win9xME_args
@rem Slurp the command line arguments.
set CMD_LINE_ARGS=
set _SKIP=2

:win9xME_args_slurp
if "x%~1" == "x" goto execute

set CMD_LINE_ARGS=%*

:execute
@rem Setup the command line

set CLASSPATH=%APP_HOME%\lib\Parcial_2_Web_Final-1.0-SNAPSHOT.jar;%APP_HOME%\lib\hibernate-entitymanager-5.4.1.Final.jar;%APP_HOME%\lib\hibernate-core-5.4.1.Final.jar;%APP_HOME%\lib\h2-1.4.197.jar;%APP_HOME%\lib\spark-template-freemarker-2.7.1.jar;%APP_HOME%\lib\spark-core-2.7.2.jar;%APP_HOME%\lib\freemarker-2.3.26-incubating.jar;%APP_HOME%\lib\spark-debug-tools-0.5.jar;%APP_HOME%\lib\gson-2.6.2.jar;%APP_HOME%\lib\slf4j-simple-1.7.21.jar;%APP_HOME%\lib\jetty-http-spi-9.4.4.v20170414.jar;%APP_HOME%\lib\jsr311-api-1.1.1.jar;%APP_HOME%\lib\hibernate-commons-annotations-5.1.0.Final.jar;%APP_HOME%\lib\jboss-logging-3.3.2.Final.jar;%APP_HOME%\lib\javax.persistence-api-2.2.jar;%APP_HOME%\lib\javassist-3.24.0-GA.jar;%APP_HOME%\lib\byte-buddy-1.9.5.jar;%APP_HOME%\lib\antlr-2.7.7.jar;%APP_HOME%\lib\jboss-transaction-api_1.2_spec-1.1.1.Final.jar;%APP_HOME%\lib\jandex-2.0.5.Final.jar;%APP_HOME%\lib\classmate-1.3.4.jar;%APP_HOME%\lib\jaxb-runtime-2.3.1.jar;%APP_HOME%\lib\jaxb-api-2.3.1.jar;%APP_HOME%\lib\javax.activation-api-1.2.0.jar;%APP_HOME%\lib\dom4j-2.1.1.jar;%APP_HOME%\lib\slf4j-api-1.7.21.jar;%APP_HOME%\lib\jetty-webapp-9.4.8.v20171121.jar;%APP_HOME%\lib\websocket-server-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-servlet-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-security-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-server-9.4.8.v20171121.jar;%APP_HOME%\lib\websocket-servlet-9.4.8.v20171121.jar;%APP_HOME%\lib\txw2-2.3.1.jar;%APP_HOME%\lib\istack-commons-runtime-3.0.7.jar;%APP_HOME%\lib\stax-ex-1.8.jar;%APP_HOME%\lib\FastInfoset-1.2.15.jar;%APP_HOME%\lib\javax.servlet-api-3.1.0.jar;%APP_HOME%\lib\websocket-client-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-client-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-http-9.4.8.v20171121.jar;%APP_HOME%\lib\websocket-common-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-io-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-xml-9.4.8.v20171121.jar;%APP_HOME%\lib\websocket-api-9.4.8.v20171121.jar;%APP_HOME%\lib\jetty-util-9.4.8.v20171121.jar

@rem Execute Parcial_2_Web_Final
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %PARCIAL_2_WEB_FINAL_OPTS%  -classpath "%CLASSPATH%" edu.pucmm.parcial2.Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable PARCIAL_2_WEB_FINAL_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%PARCIAL_2_WEB_FINAL_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
