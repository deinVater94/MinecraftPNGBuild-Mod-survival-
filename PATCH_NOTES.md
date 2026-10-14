# PixelGuide 26.3 API-Korrektur (Kompilier-Versuch)

Ausgehend vom GitHub-Actions-Log vom 14.10.2026 wurden die 41 gemeldeten Kompilierungsfehlergruppen bearbeitet.

- `ResourceLocation` → `Identifier`
- `GuiGraphics` → `GuiGraphicsExtractor` und `render` → `extractRenderState`
- `drawString` → `text` und `drawCenteredString` → `centeredText`
- `client.screen`/`client.setScreen` → `client.gui.screen()`/`client.gui.setScreen()`
- `displayClientMessage` → `sendSystemMessage` bzw. `sendOverlayMessage`
- `InputConstants.Type.KEYSYM` → `KEYBOARD`
- Verschobene RenderPearl-Pakete für `RenderPipeline`, `PrimitiveTopology`, GPU-Typen, etc.
- Beim Zeichnen: `RenderSystem.getCompiledPipeline(pipeline)`
- GitHub Action: `actions/setup-java@v5`

**ACHTUNG**: Diese Korrektur wurde mit der veröffentlichten 26.3-API verglichen, aber **nicht erfolgreich kompiliert**: In der verfügbaren Umgebung ist nur Java 21 vorhanden und keine Gradle-Distribution oder Minecraft-Abhängigkeiten. Der GitHub Actions-Build ist der nächste Test. Mehr Fehler können verbleiben. Keine fertige JAR.

Upload: Bitte den *Inhalt* dieses Ordners als Projekt-Root hochladen, inkl. `.github`-Ordner. Alte Dateien im GitHub-Repository dabei ersetzen. Danach Actions → Build ausführen. Bei roten Fehlern den neuen Build-Log bereitstellen.
