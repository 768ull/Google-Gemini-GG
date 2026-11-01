# Google Gemini GG — Android

Aplicativo Android nativo independente, escrito em Kotlin, que abre o Gemini oficial em uma WebView Android.

## Projeto Android Studio
Abra a pasta [android/](./android/) no Android Studio.

## Ícone do aplicativo
O ícone usa o símbolo multicolorido enviado para o projeto, reproduzido em vetor com gradiente. Ele possui:
- Camada de primeiro plano com o símbolo colorido.
- Camada de fundo branca independente.
- Camada monocromática para ícones temáticos do Android 13 ou superior.
- Recursos adaptativos em `mipmap-anydpi-v26` e vetores de compatibilidade para Android 7.x.

O launcher aplica automaticamente a máscara disponível no dispositivo (círculo, quadrado arredondado ou outra forma).

## Recursos do aplicativo
- Tela imersiva, sem barra de endereço ou botões de navegador.
- Zoom por pinça e menu de contexto desativados.
- Navegação principal HTTPS restrita ao Gemini e aos endpoints necessários de autenticação Google.
- Pop-ups e links externos bloqueados.
- Certificados TLS inválidos e conteúdo misto HTTP rejeitados.
- Seletor de arquivos e permissões de câmera/microfone sob autorização do Android.

## Compilação
Consulte [android/README.md](./android/README.md). Requisitos principais: Android Studio recente, JDK 17 e Android SDK Platform 36.

## Aviso
Este é um projeto independente e não oficial, sem afiliação com o Google. O Gemini e os serviços de IA são carregados dos servidores oficiais pela internet. O Google pode limitar autenticação em navegadores incorporados.
