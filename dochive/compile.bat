@echo on

del app\DocHive.jar

javac org\rpr\dh\*.java
jar cfm app\DocHive.jar org\rpr\dh\Manifest.txt org\rpr\dh\*.class
del org\rpr\dh\*.class

pause