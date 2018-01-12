# Kryptoscope
A wrapper based on Facebook Stetho to view encrypted (SQLCipher) database of Android Project.

Once you complete the set-up instructions below, just start your app and point
your laptop browser to `chrome://inspect`.  Click the "Inspect" button to
begin.

## Set-up

### Download
Gradle:

Edit project level build.gradle
```groovy
allprojects {
    repositories {
        maven {
            url 'https://dl.bintray.com/therahulmeena/maven/'
        }
    }
}
```

Edit app/build.gradle
```groovy
compile 'com.therahulmeena.kryptoscope:kryptoscope:1.1'
```
or Maven:
```xml
<dependency>
  <groupId>com.therahulmeena.kryptoscope</groupId>
  <artifactId>kryptoscope</artifactId>
  <version>1.1</version>
  <type>pom</type>
</dependency>
```

### Putting it together
Integrating with Kryptoscope is intended to be seamless and straightforward for
most existing Android applications.  There is a simple initialization step
which occurs in your `Application` class:

```java
public class MyApplication extends Application {
  public void onCreate() {
    super.onCreate();
    Kryptoscope.initializeWithDefaults(this,"test123"); //test123 is sqlCipher database key
  }
}
```
