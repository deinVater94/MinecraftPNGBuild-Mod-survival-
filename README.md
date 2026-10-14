# PixelGuide – Fabric 26.3 – v0.3.0 (QUELLCODE, KEINE GETESTETE MOD-JAR)

## Für dein Pixel-Art-Bauprojekt

- Minecraft Java **26.3**, clientseitige Fabric-Mod (Fabric API erforderlich).
- 96 × 96 = **9.216** Blöcke in **16** Bildfarbgruppen.
- Rechts unten **X=228, Y=71, Z=-95**; links unten X=133, Y=71, Z=-95.
- **H**: Hologramm an/aus. **J**: Vorschläge/Blockpalette ändern. **K**: Materialbedarf.
- Mehrere Blöcke je Bildfarbe sind gültig, z. B. schwarze Wolle und Obsidian gleichzeitig.
- Die selbst zugewiesenen Block-IDs werden lokal in `config/pixelguide.json` gespeichert.
- Alle Prüfungen lesen ausschließlich aus der Client-Welt. Keine Serverinstallation und kein automatisches Platzieren von Blöcken.

## NEU: Materialbedarf (K)

Die Materialansicht gibt für jede der 16 Farben folgende Werte aus:

- **Benötigt insgesamt**: Zahl der Bildpixel dieser Farbe.
- **Bereits richtig**: belegte Positionen mit einem akzeptierten Block (auch alternative Blöcke).
- **Noch leer**: nachweisbar leere Positionen in aktuell geladenen Chunks.
- **Falsch belegt**: besetzte Positionen mit nicht akzeptiertem Block.
- **Nicht prüfbar**: Positionen in nicht geladenen Chunks.
- **Maximal noch nötig** = insgesamt − bereits richtig. Das ist eine **Obergrenze**, denn
  „nicht prüfbar“ kann bereits korrekt gebaute Blöcke enthalten.

Die Ansicht aktualisiert sich ungefähr alle zwei Sekunden. Mit „Neu prüfen“ lässt sie
sich sofort aktualisieren. Sie zeigt das bevorzugte Material je Bildfarbe, das du
mit J ändern kannst. **Alle gültigen Materialien zählen weiter als richtig**;
bei gemischter Verwendung gibt es keine feste Stückzahl pro einzelnen Blocktyp.
`materialbedarf.csv` enthält die anfänglichen Materialmengen, inklusive 64er-Stapel.

## Was aktuell bestätigt ist (und was nicht)

**Dies ist weiterhin nur ein Quellprojekt.** Es wurde **nicht** vollständig gegen
Minecraft 26.3 und Fabric API kompiliert und **nicht** im Spiel getestet. Die
in Java programmierten GUI-/Rendering-APIs können noch Anpassungen benötigen.

In der ursprünglichen Erstellungsumgebung sind nur Java 21 und kein Gradle vorhanden;
externe Download-Abhängigkeiten konnten hier nicht geladen werden. Deshalb wird
**keine installierbare oder „fertige“ JAR behauptet oder beigelegt**.
Die `gradle.properties` verwendet nun Fabric Loom 1.17.21 und das
nach offizieller Fabric-Anleitung für 26.3 empfohlene Gradle 9.6.0.

## Echte JAR bauen – automatisiert über GitHub Actions

Eine Workflow-Datei `.github/workflows/build.yml` ist im Projekt enthalten.

1. Lege ein GitHub-Repository an und lade **den entpackten Projektinhalt** ins
   Stammverzeichnis dieses Repositorys hoch, einschließlich `.github/workflows/build.yml`.
2. Unter **Actions** den Workflow „Fabric Mod bauen“ aufrufen. Er startet bei Push
   normalerweise automatisch; sonst per `Run workflow`.
3. Wenn der Build **erfolgreich** ist, unter dem Workflow-Run bei **Artifacts** die
   `pixelguide-fabric-26.3-jar` herunterladen und entpacken.
4. Die tatsächlich gebaute `pixelguide-0.3.0.jar` kommt zusammen mit der passenden
   Fabric API in den **clientseitigen** Minecraft-`mods`-Ordner.

**Bei einem roten Build ist die Mod noch nicht bereit.** Die Logausgabe zeigt dann
Kompilierfehler, die zunächst im Java-Code behoben werden müssen. Eine grüne
GitHub-Buildprüfung beweist zudem noch nicht, dass Rendering und GUI im Spiel laufen;
dafür ist ein Praxistest erforderlich.

## Lokal bauen

JDK 25 und Gradle 9.6.0 installieren, dann im Projektverzeichnis:

```sh
gradle build
```

Bei erfolgreichem Build entsteht unter `build/libs/` eine Mod-JAR. Keine bloß
umbenannte ZIP-Datei als JAR verwenden.

## Dateiübersicht

- `src/client/java/de/pixelguide/client/MaterialProgress.java`: sichere Zählung der Weltblöcke.
- `src/client/java/de/pixelguide/client/MaterialScreen.java`: Materialübersicht im Spiel.
- `src/client/java/de/pixelguide/client/Palette*.java`: Farben und Blockalternativen.
- `src/client/resources/assets/pixelguide/plan.bin`: originaler 96×96-Bauplan.
- `materialbedarf.csv`: initialer Überblick pro Farbe mit Stapelzahlen.
- `.github/workflows/build.yml`: automatischer Build-Versuch.
