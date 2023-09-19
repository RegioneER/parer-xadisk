# Xadisk

Fonte template redazione documento:  https://www.makeareadme.com/.


# Descrizione

Fork del progetto [GitHub](https://github.com/nitin-verma-github/xadisk).
Il seguente progetto è utilizzato come **dipendenza** interna ed utilizzato per implementare processi di gestione lettura/scrittura di dati su file system, con il supporto di un modello "transazionale", ossia, qualunque operazione venga effettuata viene gestita con la classica modalità begin-commit-rollback, notoriamente utilizzata per operazioni CRUD (create, read, update, e delete) verso una qualunque base dati.


>This project was originally hosted on xadisk.java.net, and is now migrated here.
XADisk (pronounced 'x-a-disk') enables transactional access to existing file systems by providing APIs to perform file/directory operations. With simple steps, it can be deployed over any JVM and can then start serving all kinds of Java/JavaEE application running anywhere.
XADisk supports both normal and distributed (XA) transactions, can recover from application crashes, resolve deadlocks and provides many other useful features. It can be used in a variety of Java environments:
> - XADisk Jar  Deploys over any JVM, including standalone Java applications, Web Servers, JavaEE Servers.
> -  XADisk Rar - Fully JCA compliant and deploys over any JavaEE Server. 
    
# Installazione

Come già specificato nel paragrafo precedente [Descrizione](# Descrizione) si tratta di un progetto di tipo "libreria", quindi un modulo applicativo utilizzato attraverso la definzione della dipendenza Maven secondo lo standard previsto (https://maven.apache.org/): 

```xml
<dependency>
    <groupId>net.java.xadisk</groupId>
    <artifactId>xadisk</artifactId>
    <version>$VERSIONE</version>
</dependency>  
```

Trattandosi di un fork del progetto originale in quanto sono state create delle versioni "custom" con alcune correttive non previste dal mantainer, la suddetta libreria viene direttamente risolta attraverso meccanismi di repository maven interni (e.g. Nexus https://oss.sonatype.org/).

# Utilizzo

La libreria Xadisk permette la simulazione transazionata di begin-commit-rollback e massiva in fase di manipolazione di file e directory su file system, per ulteriori dettagli consultare la seguente [documentazione](https://github.com/nitin-verma-github/xadisk/tree/master/documentation).

# Supporto

Progetto a cura di [Engineering Ingegneria Informatica S.p.A.](https://www.eng.it/).

# Contributi

Se interessati a crontribuire alla crescita del progetto potete scrivere all'indirizzo email <a href="mailto:areasviluppoparer@regione.emilia-romagna.it">areasviluppoparer@regione.emilia-romagna.it</a>.

# Autori

Proprietà intellettuale del progetto di [Regione Emilia-Romagna](https://www.regione.emilia-romagna.it/) e [Polo Archivisitico](https://poloarchivistico.regione.emilia-romagna.it/).

# Licenza

Questo progetto è rilasciato sotto licenza GNU Affero General Public License v3.0 or later ([LICENSE.txt](LICENSE.txt)).
