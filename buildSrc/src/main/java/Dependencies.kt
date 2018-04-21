object Versions {
    val androidPlugin = "3.1.0"
    val kotlin = "1.2.40"
    val appCompat = "27.1.1"
    val glide = "4.7.1"
    val retrofit = "2.4.0"
    val room = "1.0.0"
    val constraintLayout = "1.0.2"
    val logger = "3.9.0"
    val archComponents = "1.1.1"
    val dagger = "2.15"
    val paging = "1.0.0-alpha7"
    val threeten = "1.0.5"

    val junit = "4.12"
    val mockitoKotlin = "1.5.0"
    val mockito = "2.12.0"
    val robolectric = "3.4.2"
    val runner = "1.0.1"
    val rules = "1.0.1"
    val espresso = "3.0.1"
}

object GradlePlugins {
    val android = "com.android.tools.build:gradle:${Versions.androidPlugin}"
    val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object CoreDependencies {
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jre7:${Versions.kotlin}"
    val dagger = "com.google.dagger:dagger:${Versions.dagger}"
    val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
    val daggerAndroid = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    val daggerAndroidSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
    val daggerAndroidCompiler = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    val threeten = "com.jakewharton.threetenabp:threetenabp:${Versions.threeten}"
}

object UiDependencies {
    val appCompat = "com.android.support:appcompat-v7:${Versions.appCompat}"
    val constraintLayout = "com.android.support.constraint:constraint-layout:${Versions.constraintLayout}"
    val recyclerView = "com.android.support:recyclerview-v7:${Versions.appCompat}"
    val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
    val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
    val dataBinding = "com.android.databinding:compiler:${Versions.androidPlugin}"
    val design = "com.android.support:design:${Versions.appCompat}"
}

object TvDependencies {
    val leanback = "com.android.support:leanback-v17:${Versions.appCompat}"
}

object DataDependencies {
    val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
    val retrofitGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    val room = "android.arch.persistence.room:runtime:${Versions.room}"
    val roomCompiler = "android.arch.persistence.room:compiler:${Versions.room}"
    val logger = "com.squareup.okhttp3:logging-interceptor:${Versions.logger}"
    val paging = "android.arch.paging:runtime:${Versions.paging}"
}

object ArchitectureComponents {
    val lifecycle = "android.arch.lifecycle:extensions:${Versions.archComponents}"
    val lifeCycleCompiler = "android.arch.lifecycle:compiler:${Versions.archComponents}"
    val liveData = "android.arch.lifecycle:livedata:${Versions.archComponents}"
}