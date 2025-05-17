plugins {
    kotlin("js") version "1.9.0"
    id("com.github.node-gradle.node") version "3.5.0"
}

repositories {
    mavenCentral()
}

kotlin {
    js {
        browser { 
           
        }
        binaries.executable()
    }
}

node {
    version = "22.15.1"
    npmVersion = "11.4.0"
    download = true
}

tasks {
    val generatePackageJson by registering {
        file("package.json").writeText("{\"dependencies\":{\"@gamely/gly-cli\": \"^0.0.20\"}}")
    }

    val buildGlyEngine by creating(Exec::class) {
        dependsOn("npmInstall")
        val nodeDir = file(".gradle/nodejs/node-v22.15.1-linux-x64/bin")
        environment("PATH", "${nodeDir.absolutePath}:${System.getenv("PATH")}")
        commandLine("$nodeDir/npx", "gly-cli", "build", "@pong", "--enginecdn", "--core", "html5_webos")
    }

    val copyJavascript by registering(Copy::class) {
        from("build/dist/js/productionExecutable/app.js")
        into("dist")
        rename("app.js", "index.js")
    }

    val setGameInHtml by registering {
        doLast {
            val expression = Regex("""\.set_game\([^\)]*\)""")
            val file = file("dist/index.html")
            val content = file.readText()
                .replace(expression, ".set_game({meta: {title:'', version:''}, callbacks: new app.Game})")
                .replace("gly-engine@", "gly-engine-micro@")
            file.writeText(content)
        }
    }

    npmInstall {
        dependsOn(generatePackageJson)
    }

    build {
        dependsOn(buildGlyEngine)
        finalizedBy(setGameInHtml)
        finalizedBy(copyJavascript)
    }
}