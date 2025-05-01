# Recursos Utilitários no Projeto REV3RSO

Este documento lista os recursos utilitários disponíveis no projeto, incluindo classes próprias e bibliotecas de terceiros.

## Bibliotecas de Terceiros

### Apache Commons Lang 3

Apache Commons Lang é uma biblioteca que fornece utilitários para manipulação de strings, números, datas, reflexão e muito mais. É altamente recomendado usar suas classes ao invés de reimplementar funcionalidades comuns.

#### Principais Classes:

1. **StringUtils**: Operações com strings que tratam nulos com segurança
   ```java
   // Verificar se uma string é vazia ou nula
   if (StringUtils.isBlank(texto)) { ... }
   
   // Capitalizar uma string
   String textoCapitalizado = StringUtils.capitalize(texto);
   
   // Abreviar uma string longa
   String resumo = StringUtils.abbreviate(textoLongo, 100);
   ```

2. **ObjectUtils**: Operações com objetos em geral
   ```java
   // Retornar um valor padrão se for nulo
   String valor = ObjectUtils.defaultIfNull(valorPossivementeNulo, "valor padrão");
   ```

3. **NumberUtils**: Operações com números
   ```java
   // Converter string para int com valor padrão
   int numero = NumberUtils.toInt(textoNumerico, 0);
   ```

4. **DateUtils**: Operações com datas
   ```java
   // Adicionar dias a uma data
   Date novaData = DateUtils.addDays(dataOriginal, 5);
   ```

## Classes Utilitárias do Projeto

### DateUtil

Utilitário para manipulação de datas específicas para o projeto.
