mkdir ./patches
unzip ./lib/spigot.jar -d patches/
rm ./lib/spigot.jar
rm ./patches/org/apache/commons/lang3/StringUtils.class
cd patches
jar cvf ./a.jar *
mv ./a.jar ../lib/spigot.jar
