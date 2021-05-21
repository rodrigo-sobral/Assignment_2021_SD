# Assignment_2021_SD

## Getting Started

:warning: **VERY IMPORTANTE NOTE:** 
    
    Keep the jar files among source files, otherwise it may not run!

- Step :one:

    - Run `rmiserver.jar` with `java -jar rmiserver.jar`. Then server will ask you for an ip address. 
        - If you want to that be the main server, then you must click **enter**
        - Otherwise you shall input the **IPv4** address of the remote machine you want to connect with.
        - ***NOTE!** This process is also applied to Administrator Console and Voting Table*
    - If you want to run a **Secundary Server** you just need to run it one more time.

- Step :two:

    - Run `server.jar` or `console.jar`, however we strongly recommend you to run **Administrator Console** first since you must have some Users, Departments, Voting Tables, Elections and Candidatures registed previously to make **Multicast Server** work fine.
        - **MultiCast Server**: `java -jar server.jar`
        - **Administrator Console**: `java -jar console.jar`
        - ***NOTE!** This process of running is similar to **RMI Server**, so you must input the **IPv4** address of the Main Server you want to connect*
    
    - When you run `server.jar` you also must input another **IPV4** (and a port), wich is the one where the MultiCast group will be allocated
    

- Step :three:

    - With some data registed, you can finnally run `terminal.jar` with the following command `java -jar terminal.jar` and, once again, input the **IPV4** (and the port), where MultiCast Server is running

- Step :four:
    
    - And that's it, you might have you entire system working fine now. 



## Contributors

Filipa Capela - 2018297335
Rodrigo Sobral - 2018298209
