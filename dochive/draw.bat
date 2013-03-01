@echo off

SETLOCAL EnableDelayedExpansion
SET IMCONV="%PROGRAMFILES%\ImageMagick-6.7.8-Q16\Convert"

echo %* >> woot.txt
%IMCONV% %*