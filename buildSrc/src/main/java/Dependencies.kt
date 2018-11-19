object Versions {
  val androidPlugin = "3.3.0-beta04"
  val kotlin = "1.3.0"
  val appCompat = "28.0.0"
  val glide = "4.8.0"
  val retrofit = "2.4.0"
  val room = "1.1.1"
  val constraintLayout = "1.1.3"
  val logger = "3.9.0"
  val archComponents = "1.1.1"
  val paging = "1.0.0"
  val threeten = "1.0.5"
  val koin = "1.0.1"
  val circleImageView = "2.2.0"
  val coroutinesAdapter = "0.9.2"
  val coroutines = "1.0.0"
  val timber = "4.7.1"

  val junit = "4.12"
  val mockitoKotlin = "1.5.0"
  val mockito = "2.12.0"
  val robolectric = "3.4.2"
  val runner = "1.0.2"
  val rules = "1.0.1"
  val espresso = "3.0.2"
}

object GradlePlugins {
  val android = "com.android.tools.build:gradle:${Versions.androidPlugin}"
  val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
}

object CoreDependencies {
  val kotlin = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
  val koin = "org.koin:koin-android:${Versions.koin}"
  val koinViewModel = "org.koin:koin-android-viewmodel:${Versions.koin}"
  val koinScope = "org.koin:koin-android-scope:${Versions.koin}"
  val threeten = "com.jakewharton.threetenabp:threetenabp:${Versions.threeten}"
  val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
  val coroutinesAndroid = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
  val timber = "com.jakewharton.timber:timber:${Versions.timber}"
}

object UiDependencies {
  val appCompat = "com.android.support:appcompat-v7:${Versions.appCompat}"
  val constraintLayout = "com.android.support.constraint:constraint-layout:${Versions.constraintLayout}"
  val recyclerView = "com.android.support:recyclerview-v7:${Versions.appCompat}"
  val glide = "com.github.bumptech.glide:glide:${Versions.glide}"
  val glideCompiler = "com.github.bumptech.glide:compiler:${Versions.glide}"
  val dataBinding = "com.android.databinding:compiler:${Versions.androidPlugin}"
  val design = "com.android.support:design:${Versions.appCompat}"
  val circleImageView = "de.hdodenhof:circleimageview:${Versions.circleImageView}"
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
  val coroutinesAdapter = "com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:${Versions.coroutinesAdapter}"
}

object ArchitectureComponents {
  val lifecycle = "android.arch.lifecycle:extensions:${Versions.archComponents}"
  val lifeCycleCompiler = "android.arch.lifecycle:compiler:${Versions.archComponents}"
  val liveData = "android.arch.lifecycle:livedata:${Versions.archComponents}"
}