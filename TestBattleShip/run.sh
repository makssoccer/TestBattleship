#!/bin/bash

# Компилируем Java файлы из директории src/ в текущей директории
javac -d . src/*.java

# Создаем JAR файл с указанием главного класса BattleshipClient
jar cfe Battleship.jar BattleshipClient*.class

# Запускаем приложение, указывая на созданный JAR файл
java -jar Battleship.jar