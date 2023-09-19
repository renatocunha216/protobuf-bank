# protobuf-bank
[![en](https://github.com/renatocunha216/common/blob/main/images/lang-en.svg?raw=true)](https://github.com/renatocunha216/protobuf-bank/blob/main/README.en.md)
[![pt-br](https://github.com/renatocunha216/common/blob/main/images/lang-pt-br.svg?raw=true)](https://github.com/renatocunha216/protobuf-bank/blob/main/README.md)

Exemplo de uso da biblioteca [Protocol Buffers](https://protobuf.dev/) para criar
um arquivo binário com dados simulados de transações bancárias.

O formato dos dados é definido com uma linguagem de definição (IDL - Interface Definition Language)
que será utilizada pelo compilador ```protoc``` para geração de códigos necessários para interagir
com os dados.

Abaixo um exemplo de definição da mensagem **DateTime**.

```java
syntax = "proto3";

option java_multiple_files = true;
option java_package = "br.com.rbcti.protobuf.artifacts";

package common;

message DateTime {
    optional int32 day = 1;
    optional int32 month = 2;
    optional int32 year = 3;
    optional int32 hour = 4;
    optional int32 minute = 5;
    optional int32 second = 6;
}
```


### Exemplo de uso

Este projeto contém uma classe chamada [BankTransactionProtoBufWriter.java](https://github.com/renatocunha216/protobuf-bank/blob/main/java/src/main/java/br/com/rbcti/protobuf/BankTransactionProtoBufWriter.java)
que cria um arquivo binário com os dados construídos pelo Protocol Buffers.

Executando o método **main** da classe BankTransactionProtoBufWriter temos o seguinte resultado.

**Maven** `mvn compile exec:java -Dexec.mainClass="br.com.rbcti.protobuf.BankTransactionProtoBufWriter"`

**Processor:** Intel Core i7-3632QM CPU @ 2.20GHz<br>
**OS:** Windows 7 Professional 64 bits<br>
**JVM:** OpenJDK Runtime Environment Temurin-11.0.19+7 (build 11.0.19+7)<br>

Com verificação dos dados.
```
Start test.
Check data        : true
Total transactions: 1.000.000
C:\Users\renato\bankTransactionProtoBuf.bin file was successfully created.
File lenght             : 100.300.024 bytes
Buffer build time       : 2091 ms
Read time and check time: 2766 ms
End test.
```
Sem a verificação dos dados.
```
Start test.
Check data        : false
Total transactions: 1.000.000
C:\Users\renato\bankTransactionProtoBuf.bin file was successfully created.
File lenght             : 100.300.079 bytes
Buffer build time       : 2111 ms
Reading time            : 2236 ms
End test.
```

**Processor:** Intel(R) Core(TM) i7-12700H<br>
**OS:** Windows 11 Pro 22H2<br>
**JVM:** OpenJDK Runtime Environment Temurin-17.0.8+7 (build 17.0.8+7)<br>
**SSD:** NVMe

Com verificação dos dados.
```
Start test.
Check data        : true
Total transactions: 1.000.000
C:\Users\rbcun\bankTransactionProtoBuf.bin file was successfully created.
File lenght             : 100.300.054 bytes
Buffer build time       : 649 ms
Read time and check time: 794 ms
End test.
```
Sem a verificação dos dados.
```
Start test.
Check data        : false
Total transactions: 1.000.000
C:\Users\rbcun\bankTransactionProtoBuf.bin file was successfully created.
File lenght             : 100.300.059 bytes
Buffer build time       : 666 ms
Reading time            : 665 ms
End test.
```

Para propósito de comparação veja o exemplo [flatbuffers-bank](https://github.com/renatocunha216/flatbuffers-bank) que utiliza a bilbioteca FlatBuffers.
