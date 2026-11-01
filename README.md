# Google Gemini GG — Android

Projeto independente em Kotlin que abre o Gemini oficial em uma WebView Android, com controles de navegação e zoom restritos.

## Código-fonte
O projeto Android Studio completo está na pasta [android/](./android/). Abra essa pasta no Android Studio.

## Recursos
- Tela imersiva, sem barra de endereço ou botões de navegador.
- Zoom por pinça e menu de contexto desativados.
- Navegação HTTPS restrita ao Gemini e aos endpoints essenciais de autenticação Google.
- Pop-ups e links externos bloqueados.
- Certificados TLS inválidos e conteúdo misto HTTP rejeitados.
- Seletor nativo de arquivos e permissões de microfone/câmera sob autorização do Android.

## Compilação
Requisitos: Android Studio recente, JDK 17, Android SDK Platform 36 e Android 7.0 (API 24) ou superior. Consulte [android/README.md](./android/README.md).

## Aviso
Este projeto não é oficial nem afiliado ao Google. A interface e a IA são carregadas dos servidores oficiais do Gemini. O Google pode limitar o login em navegadores incorporados, portanto a autenticação dentro da WebView não é garantida.
