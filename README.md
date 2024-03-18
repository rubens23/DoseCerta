English:
<h1 align="center">RightDose</h1>

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=26"><img src="https://img.shields.io/badge/API-26%2B-brightgreen.svg?style=flat" border="0" alt="API"></a>
  <br>
  <a href="https://wa.me/+5511961422254"><img alt="WhatsApp" src="https://img.shields.io/badge/WhatsApp-25D366?style=for-the-badge&logo=whatsapp&logoColor=white"/></a>
  <a href="https://www.linkedin.com/in/rubens-francisco-125529162/"><img alt="Linkedin" src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white"/></a>
  <a href="mailto:rubens_assis@outlook.com.br"><img alt="Outlook" src="https://img.shields.io/badge/Microsoft_Outlook-0078D4?style=for-the-badge&logo=microsoft-outlook&logoColor=white"/></a>
</p>

<p align="center">  

⭐ This is a project to demonstrate my technical knowledge in native Android development with Kotlin. More technical information below.

:pill: Application that helps people remember the times of the medications they are taking. The application has a detail screen for each medication where the person can see information such as dose times, duration of treatment, doses already taken, as well as a button to activate the medication reminder alarm.

</p>

</br>

<p float="left" align="center">

<img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171255_RightDose_google-pixel4-ohsoorange-portrait.png"/>
  <img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171334_RightDose_google-pixel4-ohsoorange-portrait.png"/>
  <img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171357_RightDose_google-pixel4-ohsoorange-portrait.png"/>
  <img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171529_RightDose_google-pixel4-ohsoorange-portrait.png"/>

  


</p>

## Download

Download the app on the store:
<a href="https://play.google.com/store/apps/details?id=com.rubens.applembretemedicamento&hl=pt_BR&gl=BR"><img alt="Linkedin" src="https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=google-play&logoColor=white"/></a>

Download the app <a href="apk/app-debug.apk?raw=true">APK directly</a>. You can see <a href="https://www.google.com/search?q=como+instalar+um+apk+no+android">here</a> how to install an apk on an android device!

  


## Technologies and open source code used

- Minimum SDK level 26
- [Linguagem Kotlin](https://kotlinlang.org/)

- Android SDK components that were used:
   - Espresso: Used to perform UI tests.
   - Navigation: Used to simplify screen navigation in my app.
   - NavigationTesting: Used for testing the Navigation Component in my UI tests.
   - Room: Used to persist data locally on the user's device.
   - Dagger Hilt: Used to facilitate the injection of dependencies in my classes.
   - ViewModel: Used to provide a little decoupling between the data layer and the view. It was also used to communicate between activity-fragment or fragment-fragment.
   - Fragment: Fragments were used to provide better organization for my app screens. In addition to working very well with the NavigationComponent.
   - SharedFlow: used to keep an eye on asynchronous methods and collect this data for the view layer.
   - LiveData: used to provide observers for collecting data in asynchronous methods in addition to keeping the last data saved in cache in case the view is destroyed and recreated again.
   - ViewBinding: Provides a simple way to reference view elements in classes that need to manipulate these elements in some way.
   - DataStore: Used to store some simple configuration data for my app's theme.

- Arquitetura 
  - Eu estou usando como rumo a arquitetura MVVM, porém ainda tem alguns métodos que estão na camada errada. Em breve farei uma limpeza no meu código para melhorar isso.
  - Eu também utilizei repositories e classes managers para separar a logica de obtenção dos dados da camada de viewModel.
(Fragments -> ViewModel -> Repository(Manager) -> daos do room)
  
- Libraries
  - [Maskara](https://github.com/santalu/maskara): I used this library to provide a mask that I needed in one of my edit texts. With it I can better control what the user provides as input.
  - [CircleImageView](https://github.com/hdodenhof/CircleImageView):
  Library used to obtain customized imageView with rounded format and customizable edges. I used it in the circle that represents the dose taken or not taken.
  



## Architecture
**RightDose** uses the [MVVM] architecture
(https://developer.android.com/topic/architecture)
</br></br>
<img width="60%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/arquiteturadosecerta.png">
<br>


## Features

### Users can view today's medications and see the time of the next dose.
<img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171334_RightDose_google-pixel4-ohsoorange-portrait.png"/>



### Users can add new medications and inform the time and day they will start taking the medication so that dose times are calculated automatically.
  <img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171255_RightDose_google-pixel4-ohsoorange-portrait.png"/>


### A settings screen with changing the color of the app's theme and switches so the person has better control over the alarm behavior in the app
<img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230714-182920_RightDose_google-pixel4-clearlywhite-portrait.png"/>


Português:
<h1 align="center">DoseCerta</h1>

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

:pill: Aplicativo que serve para as pessoas lembrarem dos horários dos medicamentos que elas estão tomando. O aplicativo conta com uma tela de detalhe para cada medicamento onde a pessoa pode ver informações como horários das doses, duração do tratamento, doses já tomadas além de um botão para ativar o alarme de lembrete de medicamentos.

</p>

</br>

<p float="left" align="center">

<img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171255_RightDose_google-pixel4-ohsoorange-portrait.png"/>
  <img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171334_RightDose_google-pixel4-ohsoorange-portrait.png"/>
  <img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171357_RightDose_google-pixel4-ohsoorange-portrait.png"/>
  <img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171529_RightDose_google-pixel4-ohsoorange-portrait.png"/>

  


</p>

## Download

Faça o download na loja:
<a href="https://play.google.com/store/apps/details?id=com.rubens.applembretemedicamento&hl=pt_BR&gl=BR"><img alt="Linkedin" src="https://img.shields.io/badge/Google_Play-414141?style=for-the-badge&logo=google-play&logoColor=white"/></a>

Faça o download da <a href="apk/app-debug.apk?raw=true">APK diretamente</a>. Você pode ver <a href="https://www.google.com/search?q=como+instalar+um+apk+no+android">aqui</a> como instalar uma APK no seu aparelho android.

  


## Tecnologias usadas e bibliotecas de código aberto

- Minimum SDK level 26
- [Linguagem Kotlin](https://kotlinlang.org/)

- Componentes da SDK do android que foram utilizados:
  - Espresso: Utilizado para fazer testes de UI. 
  - Navigation: Utilizado para simplificar a navegação das telas no meu app.
  - NavigationTesting: Utilizado para os testes do Navigation Component nos meus testes de UI.
  - Room: Utilizado para persistir os dados localmente no device do usuário.
  - Dagger Hilt: Utilizado para facilitar a injeção de dependências nas minhas classes.
  - ViewModel: Utilizado para fornecer um pouco de desacoplamento entre a camada de dados e a view. Também foi utilizada para fazer a comunicação entre activity-fragment ou fragment-fragment.
  - Fragment: Os fragments foram utilizados para fornecer uma organização melhor para as telas do meu app. Além de funcionar muito bem com o NavigationComponent.
  - SharedFlow: utilizado para ficar de olho em metodos assincronos e coletar esses dados para a camada de view.
  - LiveData: utilizado para fornecer observers para a coleta de dados em métodos assíncronos além de manter o último dado salvo em cache para caso a view seja destruida e recriada novamente.
  - ViewBinding: Fornece uma maneira simples de referenciar os elementos da view nas classes que precisam manipular de alguma forma esses elementos.
  - DataStore: Utilizado para guardar alguns dados simples de configuração do tema do meu app.

- Arquitetura 
  - Eu estou usando como rumo a arquitetura MVVM, porém ainda tem alguns métodos que estão na camada errada. Em breve farei uma limpeza no meu código para melhorar isso.
  - Eu também utilizei repositories e classes managers para separar a logica de obtenção dos dados da camada de viewModel.
(Fragments -> ViewModel -> Repository(Manager) -> daos do room)
  
- Bibliotecas 
  - [Maskara](https://github.com/santalu/maskara): Utilizei essa biblioteca para fornecer uma mascara que eu precisei em uma das minhas edit texts. Com ela eu consigo controlar melhor o que o user fornece como input.
  - [CircleImageView](https://github.com/hdodenhof/CircleImageView): Biblioteca utilizada para obter imageView customizadas com formato arredondado e bordas customizaveis. Utilizei ela no circulo que representa a dose tomada ou não tomada.
  



## Arquitetura
**DoseCerta** utiliza a arquitetura [MVVM]
(https://developer.android.com/topic/architecture).
</br></br>
<img width="60%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/arquiteturadosecerta.png">
<br>


## Features

### Usuários podem ver os medicamentos do dia de hoje e ver o horário da próxima dose.
<img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171334_RightDose_google-pixel4-ohsoorange-portrait.png"/>



### Usuários conseguem adicionar novos medicamentos e informar sobre o horario e dia que vai começar a tomar o medicamento para que os horários das doses sejam calculados automaticamente.
  <img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230701-171255_RightDose_google-pixel4-ohsoorange-portrait.png"/>


### Uma tela de configurações com mudança de cor do tema do app e switches para a pessoa ter um controle melhor sobre o comportamento do alarme no app
<img alt="screenshot" width="30%" src="https://github.com/rubens23/App-Lembrete-Medicamento/raw/main/app/src/main/appscreenshots/Screenshot_20230714-182920_RightDose_google-pixel4-clearlywhite-portrait.png"/>






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



