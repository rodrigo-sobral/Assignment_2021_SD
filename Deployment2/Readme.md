# Assignment_2021_SD

## Getting Started

:warning: **VERY IMPORTANTE NOTE:** 
    
    Keep all the files and directories as they are, otherwise, is not guaranteed it works.

- Step :one:

    - Run ***RMI Server*** with `java -jar rmiserver.jar`. Then server will ask you for an ip address. 
        - If you want to that be the main server, then you must click **enter**
        - Otherwise you shall input the **IPv4** address of the remote machine you want to connect with.
        - ***NOTE!** This process is also applied to Administrator Console and Voting Table*
    - If you want to run a **Secundary Server** you just need to run it one more time.
    - You can also run *RMI Server* using `make server` (make sure you have *make* syntax on your machine)

- Step :two:

    - Run `webserver.war` with a *Tomcat server* for example, to deploy the website.
    

- Step :three:

    - Do not forget that you must have some *MultiCast Servers* running, so the vote tables turn on.
    - To run them you just have to do `java -jar server.jar` or `make mcserver`
    - ***NOTE!** This process is similar to **RMI Server**, so you must input the **IPv4** address of the Main Server you want to connect*
    - When you connect it you also must input another **IPV4** (and a port), wich is the one where the MultiCast group will be allocated

- Step :four:
    
    - And that's it, you might have you entire system working fine now. 



## Contributors

Filipa Capela - 2018297335
Rodrigo Sobral - 2018298209
