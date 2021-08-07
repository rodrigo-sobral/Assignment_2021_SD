# Distributed Systems Assignment

___

## Used Technologies :computer:

1. ***JDK* (v11.0.10.9)**
2. ***Tom Cat*** 
3. ***JSP***
4. ***Struts2***
5. ***HTML & CSS***
6. ***Ngrok***
7. ***Facebook API***
8. ***Visual Studio Code***


## About :clipboard:

In this assignment we had to build a elections' system where administrators can create and manage candidates, electors and votes, in real time.

By other side, we had also to build a system that allows electors to create an account using a common login authentication (email+password) or their Facebook accounts.

After selecting a candidate, an elector can share a link to the voting page of the candidate on his Facebook feed.

## Running it :running:

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

___

## **Contributors** :sparkles:

<html><i><b> Licenciatura em Engenharia Informática - Universidade de Coimbra<br>
COMPILADORES - 2020/2021 <br>
Coimbra, 26 de maio de 2021
</b></i></html>

:mortar_board: ***[Filipa Rodrigues Santos Capela](https://github.com/FilipaCapela)* - :mortar_board: *[Rodrigo Fernando Henriques Sobral](https://github.com/RodrigoSobral2000)***

___

## License :link:
Have a look at the [license file](LICENSE) for details
___