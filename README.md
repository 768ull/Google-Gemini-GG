# Google Gemini GG — Android

Projeto Android nativo em Kotlin que encapsula a versão web oficial do Gemini usando Android WebView e AndroidX WebKit.

## Projeto Android Studio

Baixe **Google-Gemini-GG-Android-Project.zip** neste repositório e extraia o conteúdo. O arquivo contém o projeto completo, incluindo código Kotlin, Manifest, recursos, configuração Gradle e instruções em português.

## Recursos

- Tela imersiva sem barras ou botões de navegador.
- Zoom e menu de contexto desativados.
- Navegação principal restrita ao Gemini e aos endpoints necessários de login Google.
- Pop-ups e links externos bloqueados.
- HTTPS obrigatório e erros TLS rejeitados.
- Seletor de arquivos e permissões de microfone/câmera sob autorização do Android.

## Requisitos de compilação

Android Studio recente, JDK 17, Android SDK Platform 36 e Android 7.0 (API 24) ou superior. O projeto utiliza AndroidX WebKit 1.18.0.

## Aviso

Este é um projeto independente e não oficial. Google Gemini e suas marcas pertencem ao Google. O conteúdo e a IA são carregados dos serviços oficiais pela internet. O Google pode limitar a autenticação em navegadores incorporados; não é possível garantir que toda conta aceite login dentro de uma WebView.
