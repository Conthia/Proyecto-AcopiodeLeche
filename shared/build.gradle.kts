plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.kotlinSerialization)
}

kotlin {
    jvmToolchain(21)

    android {
        namespace = "pe.edu.upeu.acopioleche.shared"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()

        withHostTestBuilder {}.configure {}
    }

    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            // api, no implementation: Flow/StateFlow y LocalDate/LocalDateTime aparecen en las
            // firmas públicas del dominio y de los ViewModels, así que androidApp/desktopApp
            // necesitan verlas en su classpath de compilación.
            api(libs.kotlinx.coroutines.core)
            api(libs.kotlinx.datetime)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines)
            // Cliente HTTP: Android y Desktop son ambos JVM, así que el motor CIO (puro Kotlin,
            // sin dependencias nativas) sirve para los dos targets sin expect/actual por
            // plataforma. Si en el futuro se agrega un target no-JVM (iOS/JS), esto sí
            // necesitará un motor por plataforma (Darwin/Js) inyectado del mismo modo que
            // DatabaseDriverFactory.
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.cio)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlinx.serialization.json)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.core)
        }
        androidMain.dependencies {
            implementation(libs.sqldelight.android.driver)
        }
        getByName("desktopMain") {
            dependencies {
                implementation(libs.sqldelight.sqlite.driver)
            }
        }
    }
}

sqldelight {
    databases {
        create("AcopioLecheDatabase") {
            packageName.set("pe.edu.upeu.acopioleche.data.sqldelight")
            // Falla el build si el esquema resultante de aplicar todos los .sqm en orden no
            // coincide con el de los .sq actuales — evita repetir el desfase de versión que
            // causó "Can't downgrade database from version 2 to 1" (ver 1.sqm). Requiere las
            // fotos de esquema por versión que genera `generateCommonMainAcopioLecheDatabaseSchema`.
            verifyMigrations.set(true)
            schemaOutputDirectory.set(file("src/commonMain/sqldelight/databases"))
        }
    }
}
