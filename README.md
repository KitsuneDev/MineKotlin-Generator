# MineKotlin-Generator
Have you ever wanted to write a plugin in Kotlin, but the complexity of setting one up made you give up? No more.

MKGenerator is a simple command-line app which lets you create a Gradle Minecraft Plugin with Kotlin, only by answering a few questions in an interactive command-line interface.

[Downloads available here (by clicking on Assets, and downloading the .jar](https://github.com/GabrielTK/MineKotlin-Generator/releases)

**NOTE**: At the moment, the generated project will come preconfigured with the Paper API, since it provides more options than Spigot's.
However, **generated plugins will work fine on Spigot**, as long as you don't reference 'com.destroystokyo.paper.*' or depend on any Paper patches.

## Usage:
To invoke the app, simply head to your "Projects" directory (since the app will create a new directory), and invoke it with:
`java -jar MKScaffold-(version)-SNAPSHOT-all.jar`.
