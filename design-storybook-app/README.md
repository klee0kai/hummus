# Design Storybook App

Interactive desktop application for viewing and testing design components from `DesignComponentsRegistry`.

## Features

- 🎨 Browse all registered design components
- 🔍 Search and filter components by name or package
- ⚙️ Edit component parameters with type-aware inputs
- 📋 Select predefined parameter presets
- 👁️ Live component preview
- 🎯 Parameter introspection and type information
- 💾 Save and load parameter configurations

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
java -jar design-storybook-app-all.jar

# Launch desktop viewer with default settings
java -jar design-storybook-app-all.jar viewer

# Launch with custom theme and window size
java -jar design-storybook-app-all.jar viewer -t DARK -w 1600 -h 1000
```

### Command Line Options

For the `viewer` command:
- `-t, --theme`: Theme to use (LIGHT, DARK) - default: LIGHT
- `-w, --width`: Window width in pixels - default: 1400
- `-h, --height`: Window height in pixels - default: 900

## Architecture

### Structure

```
design-storybook-app/
├── src/
│   ├── desktopMain/
│   │   └── kotlin/com/github/klee0kai/hummus/storybook/
│   │       ├── DesignStorybookApp.kt (Main app entry point with picocli)
│   │       └── cmd/
│   │           └── DesignComponentsViewerCmd.kt (Viewer command implementation)
│   └── commonMain/
│       └── dependencies (shared with design-storybook)
└── build.gradle.kts
```

### Components

- **DesignStorybookApp**: Main application class with picocli command structure
- **DesignComponentsViewerCmd**: Desktop viewer command that launches the UI
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

- Compose Multiplatform (UI)
- picocli (CLI)
- Design-Kit (Components)
- Design-Core (Component registry and presets)
- Design-Storybook (UI Components)
