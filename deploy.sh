SNACK=snacktory-1.0-SNAPSHOT.jar
scp target/$SNACK root@pannous.info:/root/install/tomcat/webapps/jetwick/WEB-INF/lib/

scp target/$SNACK pkarich@217.92.216.224:/home/pkarich/
ssh pkarich@217.92.216.224 "sudo cp /home/pkarich/$SNACK /root/install/tomcat/webapps/jetwick/WEB-INF/lib/"