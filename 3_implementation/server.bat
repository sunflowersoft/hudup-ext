call .\env.bat

@echo off

if "%1" == "noconsole" goto noconsole
goto console

:noconsole
%JAVAW_CMD% -Xmx1g net.hudup.Server %1 %2 %3 %4
goto end

:console
%JAVA_CMD% -Xmx1g net.hudup.Server %1 %2 %3 %4
goto end

:end
@echo on

