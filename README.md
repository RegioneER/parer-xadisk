# PARER Xadisk

Fork dell'omonimo progetto con applicate alcuni bugfix (segnalati anche allo sviluppatore di riferimento).

## Note

L'artefatto di riferimento è stato pubblicato nel progetto "principale" ossia [parer.sacer.parent](url).
Deploy eseguito attraverso opportuno comando maven:

```
mvn deploy:deploy-file -DpomFile=<pom file> -DrepositoryId=github -Durl=https://maven.pkg.github.com/RegioneER/parer.sacer.parent -Dfile=<jar file> -Dfiles=<rar file> -Dclassifiers='' -Dtypes=ra
```

questo comando permette l'upload sia del file jar che di quello rar previsti dal goal "package" maven del progetto.

# xadisk

This project was originally hosted on xadisk.java.net, and is now migrated here.

XADisk (pronounced 'x-a-disk') enables transactional access to existing file systems by providing APIs to perform file/directory operations. With simple steps, it can be deployed over any JVM and can then start serving all kinds of Java/JavaEE application running anywhere.

XADisk supports both normal and distributed (XA) transactions, can recover from application crashes, resolve deadlocks and provides many other useful features. It can be used in a variety of Java environments:

    XADisk Jar - Deploys over any JVM, including standalone Java applications, Web Servers, JavaEE Servers.
    XADisk Rar - Fully JCA compliant and deploys over any JavaEE Server. 
    
   
Discussion Forum:
https://groups.google.com/group/xadisk



