call .\env.bat

@echo off

if "%1" == "noconsole" goto noconsole
goto console

:noconsole
%JAVAW_CMD% net.hudup.Balancer
goto end

:console
%JAVA_CMD% net.hudup.Balancer
goto end

:end
@echo on

