
OS: Ubuntu

Current dir:<>/216201004/2c

Compile:
mkdir bin
javac src/filesystem/*.java -d bin

Run:
Create File:
java  -cp bin filesystem.Filesystem mf <file_name> <content>

List files:
java  -cp bin filesystem.Filesystem ls

List file-content:
java  -cp bin filesystem.Filesystem pf <file_name>

Delete file:

java  -cp bin filesystem.Filesystem df <file_name>


Rename:
java  -cp bin filesystem.Filesystem rf <file_name>


Important points:
1. File name is restricted to 6 characters.
2. Max 16 files can be created
3. Content should have even length strings of maximum 120 characters. It is a technical limitation I could not work on due to paucity of time. 




