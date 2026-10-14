# PixelGuide 0.3.2 – Behebung des H-Absturzes

**Grund:** Der alte `GhostRenderer` hat einen GPU-Render-Pass während eines bereits laufenden Render-Passes geöffnet. Außerdem lud er seinen Vertexbuffer im Callback hoch.

**Änderungen:**
- GPU-Uploads und manuelle Render-Passes aus `GhostRenderer.java` entfernt.
- `LevelRenderEvents.AFTER_TRANSLUCENT_TERRAIN` durch `LevelRenderEvents.COLLECT_SUBMITS` ersetzt.
- Über `submitNodeCollector().submitCustomGeometry(...)` bei `RenderTypes.debugFilledBox()` die Farbpixel in Minecrafts Render-Queue eingereiht.
- Sichtbarkeit, Blockprüfung, rechter Koordinatenanker und Materialliste unverändert.
- Version auf 0.3.2 erhöht; GitHub-Workflow lädt `build/libs/*.jar` hoch.

**Status:** Quellcodepatch auf Basis Fabric 26.3 APIs überprüft, **nicht** kompiliert oder in Minecraft getestet.

**Installation:** Projektinhalt (nicht das ZIP) auf GitHub hochladen → grünen Actions-Build abwarten → `pixelguide-0.3.2.jar` aus dem Artefakt extrahieren → alte PixelGuide-JAR aus `mods` entfernen → neue JAR hinein → Welt-Backup → Spiel starten und `H` testen.

**Wenn es weiter crasht:** vollständige Datei aus `crash-reports/` oder `logs/latest.log` senden, insbesondere die Zeilen `Caused by:` und `at de.pixelguide...`.
