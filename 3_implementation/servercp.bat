@echo off

call .\env.bat
%JAVA_CMD% net.hudup.server.ext.ExtendedServerCP

@echo on
