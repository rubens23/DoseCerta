
<h1 align="center">App Lembrete Medicamento</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=26"><img src="https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat" border="0" alt="API"></a>
  <br>
  <a href="https://wa.me/+5511961422254"><img alt="WhatsApp" src="https://img.shields.io/badge/WhatsApp-25D366?style=for-the-badge&logo=whatsapp&logoColor=white"/></a>
  <a href="https://www.linkedin.com/in/rubens-francisco-125529162/"><img alt="Linkedin" src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"/></a>
  <a href="mailto:rubens_assis@outlook.com.br"><img alt="Outlook" src="https://img.shields.io/badge/Microsoft_Outlook-0078D4?style=for-the-badge&logo=microsoft-outlook&logoColor=white"/></a>
</p>

<p align="center">  

⭐ Esse é um projeto para demonstrar meu conhecimento técnico no desenvolvimento Android nativo com Kotlin. Mais informações técnicas abaixo.

:pill: Aplicativo que serve para as pessoas lembrarem dos horários dos medicamentos que elas estão tomando.

</p>

</br>

<p float="left" align="center">

<img alt="screenshot" width="30%" src="https://github.com/rubens23/appDenuncia/raw/master/app/src/main/appscreenshots/Screenshot_20230616_194322.png"/>
  <img alt="screenshot" width="30%" src="https://github.com/rubens23/appDenuncia/raw/master/app/src/main/appscreenshots/Screenshot_20230616_194432.png"/>
  <img alt="screenshot" width="30%" src="https://github.com/rubens23/appDenuncia/raw/master/app/src/main/appscreenshots/Screenshot_20230616_194458.png"/>
<img alt="screenshot" width="30%" src="https://github.com/rubens23/appDenuncia/raw/master/app/src/main/appscreenshots/Screenshot_20230616_194549.png"/>
<img alt="screenshot" width="30%" src="https://github.com/rubens23/appDenuncia/raw/master/app/src/main/appscreenshots/Screenshot_20230616_194628.png"/>
<img alt="screenshot" width="30%" src="https://github.com/rubens23/appDenuncia/raw/master/app/src/main/appscreenshots/Screenshot_20230616_195725.png"/>
  


</p>

## Download

Faça o download da <a href="apk/app-debug.apk?raw=true">APK diretamente</a>. Você pode ver <a href="https://www.google.com/search?q=como+instalar+um+apk+no+android">aqui</a> como instalar uma APK no seu aparelho android.

  


## Tecnologias usadas e bibliotecas de código aberto

- Minimum SDK level 21
- [Linguagem Kotlin](https://kotlinlang.org/)

- Componentes da SDK do android que foram utilizados:
  - ViewModel: Utilizado para fornecer um pouco de desacoplamento entre a camada de dados e a view.
  - Navigation: Utilizado para facilitar o código da navegação entre telas no app.
  - Fragment: Os fragments foram utilizados para fornecer uma organização melhor para as telas do meu app. Além de funcionar muito bem com o NavigationComponent.
  - SharedFlow: utilizado como um observer para pegar os valores assim que eles terminam de serem coletados atraves de um método assíncrono. Utilizei ele ao invés do liveData porque queria que o valor não ficasse guardado depois que o fragment fosse reiniciado.
  - ViewBinding: Fornece uma maneira simples de referenciar os elementos da view nas classes que precisam manipular de alguma forma esses elementos.
  - Espresso: biblioteca utilizada para fazer testes de ui no meu aplicativo.

- Arquitetura 
  - MVVM (View - ViewModel - Model): Utilizada para colocar um intermediário entre a view e a lógica de negócio. No meu app a viewModel presente na arquitetura mvvm me ajuda a separar a lógica de obtenção de dados da camada de view(fragments e activity).
  - Eu também utilizei repositories e classes managers para separar a logica de obtenção dos dados da camada de viewModel.
(Fragments -> ViewModel -> Repository(Manager) -> apis do firebase)
  
- Bibliotecas 
  - [Firebase](https://firebase.google.com/?gad=1&gclid=CjwKCAjws7WkBhBFEiwAIi16864LHiVEI5b4n9MrQ5qNyuOaIxlcwvBNFIalIJSvH4lAkYL5sw54qRoCPR8QAvD_BwE&gclsrc=aw.ds&hl=pt-br): Utilizei o serviço de banco de dados do firebase, o serviço de autenticação do firebase e o serviço de storage.
  - [Picasso](https://square.github.io/picasso/): Biblioteca para carregar imagens através da url e mostrá-las na tela, além de armazená-las em cache.
  - [Dagger Hilt](https://dagger.dev/hilt/gradle-setup): Biblioteca usada para injetar dependencias nas classes de maneira mais fácil, centralizando a inicialização de classes necessárias em outras classes de maneira única em uma classe só.



## Arquitetura
**App Denúncia** utiliza a arquitetura [MVVM]
(https://developer.android.com/topic/architecture).
</br></br>
<img width="60%" src="https://github.com/rubens23/appDenuncia/raw/master/app/src/main/appscreenshots/arquitetura app denuncia.png">
<br>


## Features

### Usuários podem se cadastrar para terem acesso ao app.
<img alt="screenshot" width="30%" src="https://github.com/rubens23/appDenuncia/raw/master/app/src/main/appscreenshots/Screenshot_20230616_194322.png"/>


### Usuários podem postar novas denúncias, ver as denúncias que ele já postou ou ver todas as denúncias circulando na comunidade que usa o app.
<img alt="screenshot" width="30%" src="https://github.com/rubens23/appDenuncia/raw/master/app/src/main/appscreenshots/Screenshot_20230616_194432.png"/>

### Usuários podem colocar uma foto para representar sua denúncia e também podem curtir ou comentar as denúncias.
<img alt="screenshot" width="30%" src="https://github.com/rubens23/appDenuncia/raw/master/app/src/main/appscreenshots/Screenshot_20230616_194628.png"/>
<img alt="screenshot" width="30%" src="https://github.com/rubens23/appDenuncia/raw/master/app/src/main/appscreenshots/Screenshot_20230616_195725.png"/>





# Licença



```xml
    Copyright [2023] [Rubens Francisco de Assis]

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

```



