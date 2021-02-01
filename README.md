# L20 Polling System
Una live demo dell'applicativo è disponibile all'indirizzo: https://l20.fresqo.net
## Istruzioni per l'utilizzo
Il progetto è stato realizzato con IntelliJ Idea, il framework Spring e il template engine Thymeleaf.\
Per poter utilizzare l'applicativo è necessario disporre di una connessione con un database MySQL in locale o in remoto e un indirizzo email che servirà per le notifiche della piattaforma.\
\
Una volta clonata la repository direttamente da IntelliJ Idea o con
```
git clone https://github.com/IngSW-unipv/Progetto-L20.git
```
sarà necessario accedere al server MySQL, creare uno *SCHEMA* dal nome '*l20pollingsystem*' ed eseguire il file `database/db-creation.sql`. Successivamente si dovrà aprire il progetto in IntelliJ, caricare le dipendenze Maven elencate nel file  `pom.xml` e in seguito modificare il file `src/main/resources/application.properties` inserendo l'indirizzo a cui si trova il database e le credenziali per accedervi, oltre alle credenziali per l'indirizzo mail di notifica.\
A questo punto sarà sufficiente fare la build del progetto ed eseguire il metodo `main` della classe `L20PollingSystemApplication.java`, aprire il browser e puntare all'indirizzo `http://localhost:9000`.\
Le dipendenze possono essere esplorate nel dettaglio [qui](https://github.com/IngSW-unipv/Progetto-L20/network/dependencies).
