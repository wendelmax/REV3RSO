# Configurando o Lombok no Projeto REV3RSO

Este projeto utiliza o [Lombok](https://projectlombok.org/) para reduzir o código boilerplate nos modelos. Para que sua IDE reconheça corretamente as anotações do Lombok, você precisa instalar o plugin correspondente.

## Instalação do Plugin

### IntelliJ IDEA

1. Abra as **Configurações** (`Ctrl+Alt+S` ou `⌘,` no macOS)
2. Vá para **Plugins**
3. Pesquise por "Lombok" na aba **Marketplace**
4. Clique em **Instalar**
5. Reinicie a IDE quando solicitado

### Eclipse

1. Baixe o jar do Lombok em [https://projectlombok.org/download](https://projectlombok.org/download)
2. Execute o jar baixado (`java -jar lombok-1.18.38.jar`)
3. A interface de instalação do Lombok será aberta
4. Especifique a localização do seu Eclipse e clique em **Install/Update**
5. Reinicie o Eclipse

### VS Code

1. Instale a extensão "Lombok Annotations Support for VS Code"
2. Reinicie o VS Code

## Verificação da Instalação

Para verificar se o Lombok está funcionando corretamente:

1. Abra um arquivo modelo que use anotações Lombok, como `Usuario.java`
2. Verifique se não há erros de importação nas anotações como `@Getter`, `@Setter`, etc.
3. Tente usar os métodos gerados pelo Lombok em algum outro lugar do código (como chamar um getter)

## Solução de Problemas

Se você ainda estiver tendo problemas com o Lombok:

### IntelliJ IDEA

1. Certifique-se de que o processamento de anotações está habilitado:
   - Vá para **Configurações** > **Build, Execution, Deployment** > **Compiler**
   - Marque a opção **Enable annotation processing**

### Eclipse

1. Verifique se o Lombok está listado em **Help** > **About Eclipse** > **Installation Details**
2. Se não estiver, tente reinstalar seguindo as instruções acima

### Maven

1. Verifique se o Lombok está corretamente declarado no `pom.xml`
2. Execute o comando Maven para baixar as dependências: `mvn clean install -DskipTests`

### Limpar o Projeto

Se os problemas persistirem, tente:

1. Limpar o projeto na sua IDE (Clean Project)
2. Para o Maven: `mvn clean`
3. Invalidar caches e reiniciar a IDE (IntelliJ: **File** > **Invalidate Caches**)

## Configuração no pom.xml

Confirme que o Lombok está configurado corretamente no `pom.xml` como uma dependência direta:
