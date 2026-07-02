plugins {
    java
    application
    id("org.beryx.runtime") version "1.13.1"
}

group = "io.github.segateekb"
version = "0.1.0"

repositories {
    mavenCentral()
    // Нужен для транзитивной зависимости bsl-parser 0.23.3 -> com.github.1c-syntax:utils:0.5.1,
    // которая опубликована только на jitpack (сам bsl-parser 0.23.3 - на Maven Central).
    maven { url = uri("https://jitpack.io") }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

dependencies {
    // 0.23.3 - последняя версия на Maven Central, где ещё есть API BSLParserRuleContext
    // и грамматика совпадает с замером отладчика 1С (правила Coverage41C проходят verbatim).
    // Новее: BSLParserRuleContext удалён (>=~0.24-0.30), а с ~0.37 многострочные конструкции
    // парсятся иначе (каскад ошибок на искусственных фикстурах) - паритет ломается.
    implementation("io.github.1c-syntax:bsl-parser:0.23.3")

    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("io.github.segateekb.linestocover.Main")
    applicationName = "lines-to-cover"
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}

// Самодостаточный рантайм (jlink): свой JRE + приложение, без установленной Java.
// Модули - минимальный набор, достаточный для парсинга (проверено: без java.desktop/compiler).
runtime {
    options.set(listOf("--strip-debug", "--no-header-files", "--no-man-pages", "--compress", "zip-9"))
    modules.set(listOf("java.base", "java.logging", "java.prefs", "java.xml"))
}
