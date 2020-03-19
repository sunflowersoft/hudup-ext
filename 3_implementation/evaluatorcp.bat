@echo off

call .\env.bat
%JAVA_CMD% net.hudup.evaluate.ui.EvaluatorCP
cd tools\cp

@echo on
