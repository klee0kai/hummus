# Design Storybook App

Interactive desktop application and HTTP server for viewing and testing design components from `DesignComponentsRegistry`.

## Features

- 🎨 Browse all registered design components
- 🔍 Search and filter components by name or package
- ⚙️ Edit component parameters with type-aware inputs
- 📋 Select predefined parameter presets
- 👁️ Live component preview
- 🎯 Parameter introspection and type information
- 💾 Save and load parameter configurations
- 🌐 HTTP server mode with embedded WASM artifacts
- 🖥️ Desktop application with customizable theme and window size

## Building

### Desktop Application

```bash
./gradlew design-storybook-app:run
```

### Fat JAR

```bash
./gradlew design-storybook-app:fatJar
```

## Usage

### CLI Commands

```bash
# Show help
java -jar design-storybook-app-1.0.0-all.jar

# Launch desktop viewer with default settings
java -jar design-storybook-app-1.0.0-all.jar viewer

# Launch desktop viewer with custom theme and window size
java -jar design-storybook-app-1.0.0-all.jar viewer -t DARK -w 1600 -h 1000

# Launch HTTP server with default port (8080)
java -jar design-storybook-app-1.0.0-all.jar server

# Launch HTTP server on custom port
java -jar design-storybook-app-1.0.0-all.jar server -p 3000
```

### Desktop Viewer Options

For the `viewer` command:
- `-t, --theme`: Theme to use (LIGHT, DARK) - default: LIGHT
- `-w, --width`: Window width in pixels - default: 1400
- `-h, --height`: Window height in pixels - default: 900

### Server Options

For the `server` command:
- `-p, --port`: HTTP server port - default: 8080

## Architecture

### Structure

```
design-storybook-app/
├── src/
│   ├── desktopMain/
│   │   └── kotlin/com/github/klee0kai/hummus/storybook/
│   │       ├── DesignStorybookApp.kt (Main app entry point with picocli)
│   │       └── cmd/
│   │           ├── DesignComponentsViewerCmd.kt (Desktop UI command)
│   │           └── DesignComponentsServerCmd.kt (HTTP server command)
│   └── commonMain/
│       └── dependencies (shared with design-storybook)
└── build.gradle.kts
```

### Components

- **DesignStorybookApp**: Main application class with picocli command structure
- **DesignComponentsViewerCmd**: Desktop viewer command that launches the Compose UI with HummusTheme
- **DesignComponentsServerCmd**: HTTP server command that serves WASM artifacts via Ktor/Netty
- **DesignComponentsBrowser**: Full UI for browsing and editing components (in design-storybook)
- **ParametersEditor**: Parameter editing panel with preset selection
- **ComponentPresetsRegistry**: Database of predefined parameter presets

## Development

### Adding New Commands

Create a new command class in the `cmd` package:

```kotlin
@CommandLine.Command(name = "export", description = ["Export component definitions"])
class ExportComponentsCmd : Runnable {
    // Implementation
}
```

Then register it in `DesignStorybookApp`:

```kotlin
@CommandLine.Command(
    subcommands = [
        DesignComponentsViewerCmd::class,
        ExportComponentsCmd::class,  // Add here
    ]
)
```

## Dependencies

- Compose Multiplatform (Desktop UI)
- Ktor Server (HTTP server with Netty engine)
- picocli (CLI framework)
- Design-Kit (Components)
- Design-Core (Component registry and presets)
- Design-Storybook (UI components and WASM artifacts)

## How It Works

### Server Mode
The server command packages and serves WASM artifacts from the `design-storybook` module:

1. **WASM Packaging** (`design-storybook/build.gradle.kts`):
   - `wasmJs` target compiles Kotlin/Compose to WebAssembly
   - `wasmJsBrowserProductionJar` task bundles compiled artifacts (`.wasm`, `.js`, `index.html`, etc.)
   - `wasmArchives` configuration exposes the JAR for downstream consumption

2. **Ktor Server** (`DesignComponentsServerCmd.kt`):
   - Embedded HTTP server using Netty engine
   - Serves WASM artifacts from classpath resources
   - Handles routing: `/` serves `index.html`, all other paths resolved from WASM JAR
   - Auto-detects content types (`.wasm`, `.js`, `.css`, `.html`)
   - SPA pattern: unknown paths fallback to `index.html` for client-side routing
