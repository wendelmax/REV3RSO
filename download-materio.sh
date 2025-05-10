#!/bin/bash

# Criar diretório temporário
mkdir -p temp
cd temp

# Baixar o template Materio
wget https://github.com/themeselection/materio-bootstrap-html-admin-template-free/archive/refs/heads/main.zip

# Extrair o arquivo
unzip main.zip

# Copiar arquivos CSS
cp -r materio-bootstrap-html-admin-template-free-main/assets/vendor/css/* ../src/main/resources/static/assets/vendor/css/
cp -r materio-bootstrap-html-admin-template-free-main/assets/css/* ../src/main/resources/static/assets/css/

# Copiar arquivos JS
cp -r materio-bootstrap-html-admin-template-free-main/assets/vendor/js/* ../src/main/resources/static/assets/vendor/js/
cp -r materio-bootstrap-html-admin-template-free-main/assets/js/* ../src/main/resources/static/assets/js/

# Copiar fontes
cp -r materio-bootstrap-html-admin-template-free-main/assets/vendor/fonts/* ../src/main/resources/static/assets/vendor/fonts/

# Copiar imagens
cp -r materio-bootstrap-html-admin-template-free-main/assets/img/favicon/* ../src/main/resources/static/assets/img/favicon/
cp -r materio-bootstrap-html-admin-template-free-main/assets/img/illustrations/* ../src/main/resources/static/assets/img/illustrations/

# Copiar bibliotecas
cp -r materio-bootstrap-html-admin-template-free-main/assets/vendor/libs/* ../src/main/resources/static/assets/vendor/libs/

# Limpar
cd ..
rm -rf temp 