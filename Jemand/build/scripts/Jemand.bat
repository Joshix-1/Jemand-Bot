@if "%DEBUG%" == "" @echo off
@rem ##########################################################################
@rem
@rem  Jemand startup script for Windows
@rem
@rem ##########################################################################

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

set DIRNAME=%~dp0
if "%DIRNAME%" == "" set DIRNAME=.
set APP_BASE_NAME=%~n0
set APP_HOME=%DIRNAME%..

@rem Add default JVM options here. You can also use JAVA_OPTS and JEMAND_OPTS to pass JVM options to this script.
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

set CLASSPATH=%APP_HOME%\lib\Jemand-1.0-SNAPSHOT.jar;%APP_HOME%\lib\github-api-1.108.jar;%APP_HOME%\lib\apfloat-1.9.1.jar;%APP_HOME%\lib\commons-io-2.4.jar;%APP_HOME%\lib\httpclient-4.5.10.jar;%APP_HOME%\lib\owo-java-2.2.jar;%APP_HOME%\lib\log4j-core-2.11.1.jar;%APP_HOME%\lib\javacord-core-3.0.5.jar;%APP_HOME%\lib\log4j-api-2.11.1.jar;%APP_HOME%\lib\json-simple-1.1.jar;%APP_HOME%\lib\im4java-1.4.0.jar;%APP_HOME%\lib\emoji-java-5.0.0.jar;%APP_HOME%\lib\zt-zip-1.13.jar;%APP_HOME%\lib\discord-webhooks-0.1.7.jar;%APP_HOME%\lib\slf4j-api-1.7.25.jar;%APP_HOME%\lib\commons-lang3-3.9.jar;%APP_HOME%\lib\jackson-databind-2.10.2.jar;%APP_HOME%\lib\httpcore-4.4.12.jar;%APP_HOME%\lib\commons-logging-1.2.jar;%APP_HOME%\lib\commons-codec-1.11.jar;%APP_HOME%\lib\converter-scalars-2.2.0.jar;%APP_HOME%\lib\converter-gson-2.2.0.jar;%APP_HOME%\lib\retrofit-2.2.0.jar;%APP_HOME%\lib\gson-2.8.0.jar;%APP_HOME%\lib\javacord-api-3.0.5.jar;%APP_HOME%\lib\json-20180813.jar;%APP_HOME%\lib\logging-interceptor-3.9.1.jar;%APP_HOME%\lib\okhttp-3.12.0.jar;%APP_HOME%\lib\annotations-16.0.1.jar;%APP_HOME%\lib\jackson-annotations-2.10.2.jar;%APP_HOME%\lib\jackson-core-2.10.2.jar;%APP_HOME%\lib\nv-websocket-client-2.6.jar;%APP_HOME%\lib\okio-1.15.0.jar

@rem Execute Jemand
"%JAVA_EXE%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %JEMAND_OPTS%  -classpath "%CLASSPATH%" Jemand.Main %CMD_LINE_ARGS%

:end
@rem End local scope for the variables with windows NT shell
if "%ERRORLEVEL%"=="0" goto mainEnd

:fail
rem Set variable JEMAND_EXIT_CONSOLE if you need the _script_ return code instead of
rem the _cmd.exe /c_ return code!
if  not "" == "%JEMAND_EXIT_CONSOLE%" exit 1
exit /b 1

:mainEnd
if "%OS%"=="Windows_NT" endlocal

:omega
