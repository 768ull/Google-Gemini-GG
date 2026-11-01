# Gemini GG para Android

Aplicativo Android nativo (Kotlin) que abre a versão web oficial do Gemini em uma WebView do Android.

## Ícone adaptativo

O ícone reproduz o símbolo multicolorido da imagem fornecida como vetor escalável. Os arquivos ficam em:

- `app/src/main/res/drawable/ic_launcher_foreground.xml`: símbolo colorido com gradiente.
- `app/src/main/res/drawable/ic_launcher_monochrome.xml`: camada monocromática para ícones temáticos.
- `app/src/main/res/values/ic_launcher_background.xml`: plano de fundo branco.
- `app/src/main/res/mipmap-anydpi-v26/`: ícones adaptativos para Android 8.0+.
- `app/src/main/res/mipmap-anydpi/`: fallback vetorial para Android 7.x.

O `AndroidManifest.xml` declara `android:icon` e `android:roundIcon`. O Android aplica a máscara do launcher ao ícone adaptativo, por isso o símbolo foi mantido dentro da safe zone central.

## Recursos

- Inicializa `https://gemini.google.com/app?hl=pt-BR`.
- Tela imersiva, sem barra de endereço, abas ou botões de navegador.
- Zoom por pinça e controles de zoom desativados.
- Menu de contexto e janelas `target=_blank` bloqueados.
- Navegação principal HTTPS permitida somente para Gemini e endpoints de autenticação Google.
- Erros de certificado TLS são sempre cancelados; conteúdo misto HTTP é bloqueado.
- Cookies, JavaScript e armazenamento DOM habilitados para a sessão web.
- Seletor nativo de arquivos e permissões de microfone/câmera sob autorização do Android.

## Requisitos

- Android Studio recente.
- JDK 17.
- Android SDK Platform 36.
- Android 7.0 (API 24) ou superior.
- Acesso à internet.

## Compilar

Abra esta pasta no Android Studio e aguarde a sincronização do Gradle. Ou execute:

- Linux/macOS: `chmod +x gradlew` e depois `./gradlew assembleDebug`.
- Windows: `gradlew.bat assembleDebug`.

O APK de depuração, após compilação, fica em `app/build/outputs/apk/debug/app-debug.apk`.

## Observações importantes

1. O app bloqueia a navegação principal para sites fora da allowlist. As chamadas de rede internas do Gemini continuam funcionando; bloquear todos os pedidos de rede quebraria o serviço.
2. O Google pode limitar login em navegadores incorporados por políticas de segurança. Se a conta recusar login na WebView, este projeto não contorna essa política; use o app oficial ou um navegador compatível.
3. O modo imersivo oculta as barras do Android, mas o usuário pode revelá-las com gestos do sistema.
4. Desativar o menu de contexto também impede selecionar/compartilhar texto por toque longo.
5. Alterações futuras do site Gemini podem exigir ajustes na allowlist.

## Configuração

- applicationId: `com.gg.gemini`
- versionName: `1.0.0`
- minSdk: `24`
- compileSdk / targetSdk: `36`
- AndroidX WebKit: `1.18.0`
