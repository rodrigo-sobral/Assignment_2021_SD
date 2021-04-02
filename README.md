# Assignment_2021_SD

## CHECKLIST

Sistemas Distribuídos 2020/21 - Meta 1 | -- 	
-- | --
Nome:	| Aluno de SD
Número de Aluno:	| 2017987654
Nota Final:	| 100
-- | 
**Requisitos Funcionais** | 44
Registar novo utilizador (estudante, docente, ou funcionário) | 1 (X)
Criar eleição | 3 (X)
Gerir listas de candidatos a uma eleição | 3 (X)
Criar mesas de voto | 3 (X)
Gestão automática de terminais de voto, por Multicast | 3
Identificar eleitor na mesa de voto e desbloquear um terminal de voto | 3 (X)
Login de eleitor no terminal de voto | 3 (X)
Votar (escolher, uma só vez, uma lista no terminal de voto) | 3
Editar propriedades de uma eleição | 3 (X)
Saber em que local votou cada eleitor | 3
Consola de administração mostra mesas de voto on/off e votantes | 3
Consola de administração atualizada em tempo real nas eleições | 3 (X)
Eleição termina corretamente na data, hora e minuto marcados | 7 (X)
Consultar resultados detalhados de todas as eleições passadas | 3 (X)
-- | 
**Tratamento de Exceções** | 24
Avaria de um servidor RMI não tem qualquer efeito nos clientes | 4
Não se perde/duplica votos se os servidores RMI falharem | 4
Não se perde/duplica votos se a comunicação Multicast tiver falhas | 4
Avarias temporárias (<30s) dos 2 RMIs são invisíveis para clientes | 4
Terminal de voto bloqueado automaticamente após 60s sem uso | 4 (X)
Crash de terminal de voto é recuperado | 4
-- | 
**Failover** | 24
O servidor RMI secundário testa periodicamente o primário | 4 (X)
Em caso de avaria longa os servidores Multicast ligam ao secundário | 4 (X)
Servidor RMI secundário substitui o primário em caso de avaria | 4 (X)
Os dados são os mesmos em ambos os servidores RMI | 4 
O failover é invisível para clientes/eleitores (não perdem a sessão) | 4
O servidor original, quando recupera, torna-se secundário | 4 (X)
-- | 
**Relatório**	| 8
Detalhes do funcionamento do servidor Multicast (protocolo, etc.) | 2
Detalhes do funcionamento do servidor RMI (API, etc.) | 2
Distribuição de tarefas pelos elementos do grupo | 2
Testes de software (tabela com descrição + pass/fail de cada teste) | 2
-- | 
**Extra (até 5 pontos)** | 0
Security policies para acesso de consolas de administração (3p)	| 
O voto é comunicado garantindo confidencialidade e integridade (3p)	|
STONITH nos servidores RMI (3p)	|
Outros (a propor pelos alunos)	|
-- | 
**Pontos Obrigatórios** | 0 |
Pontualidade (-10)	|
O projeto corre distribuído por várias máquinas (-5)	|
Configuração usa property files / não requer recompilação (-5)	|
A aplicação não apresenta erros/exceções/avarias (-5)	|
Código legível e bem comentado (-5)	|
-- | 
**No dia da defesa** |
Os estudantes chegam 15 minutos antes para prepararem tudo	|
Trazem duas máquinas (e.g., dois portáteis ou um portátil com uma VM)	|
Máquina #1 corre 1 servidor multicast, 2 terminais e 1 consola	|
Máquina #2 corre 1 servidor multicast, 2 servidores RMI e 2 terminais	|
Há dados de teste (5 eleitores, 2 eleições passadas, 3 departamentos, ...)|	

## Getting Started

Welcome to the VS Code Java world. Here is a guideline to help you get started to write Java code in Visual Studio Code.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

## Dependency Management

The `JAVA DEPENDENCIES` view allows you to manage your dependencies. More details can be found [here](https://github.com/microsoft/vscode-java-pack/blob/master/release-notes/v0.9.0.md#work-with-jar-files-directly).

___

