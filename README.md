# Aplicativo Formulário com Câmera e Localização em Kotlin

Este projeto consiste em um aplicativo Android que permite ao usuário preencher um formulário, tirar uma foto usando a câmera do celular e salvar esses dados em um banco de dados SQLite. Além disso, o aplicativo exibe a localização atual do usuário utilizando o GPS do dispositivo.

## Funcionalidades

- **Formulário**:
  - Campos:
    - Nome (EditText)
    - Email (EditText)
    - Comentário (EditText)

- **Captura de Imagem**:
  - Um botão que abre a câmera do celular.
  - Após tirar a foto, a imagem é exibida no aplicativo.

- **Armazenamento em SQLite**:
  - Tabela chamada `FormData` que armazena:
    - Nome (String)
    - Email (String)
    - Comentário (String)
    - Caminho da foto no armazenamento do dispositivo

- **Localização GPS**:
  - Um botão que, ao ser clicado, exibe a latitude e longitude do usuário na tela.

## Tecnologias Utilizadas

- **Kotlin**: Linguagem de programação para desenvolvimento do aplicativo.
- **SQLite**: Banco de dados local para armazenamento de dados.
- **FusedLocationProviderClient**: API para acessar a localização do dispositivo.

## Instruções de Uso

1. Preencher o formulário com Nome, Email e Comentário.
2. Capturar a imagem usando a câmera.
3. Salvar os dados no banco de dados SQLite.
4. Obter e exibir a localização ao clicar no botão correspondente.

## Permissões Necessárias

Adicione as seguintes permissões ao `AndroidManifest.xml`:

```xml
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
