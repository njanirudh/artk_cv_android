# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

# ======================OpenCV stuff===============================
add_library( lib_opencv SHARED IMPORTED )
set_target_properties(lib_opencv PROPERTIES IMPORTED_LOCATION
${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libopencv_java3.so)

add_library(lib_opencv_core STATIC IMPORTED)
set_target_properties(lib_opencv_core PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libopencv_core.a)


add_library(lib_opencv_imgproc STATIC IMPORTED)
set_target_properties(lib_opencv_imgproc PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libopencv_imgproc.a)


add_library(lib_opencv_imgcodecs STATIC IMPORTED)
set_target_properties(lib_opencv_imgcodecs PROPERTIES IMPORTED_LOCATION
          ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libopencv_imgcodecs.a)

add_library(lib_opencv_objdetect STATIC IMPORTED)
set_target_properties(lib_opencv_objdetect PROPERTIES IMPORTED_LOCATION
          ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libopencv_objdetect.a)

add_library(lib_opencv_tracking STATIC IMPORTED)
set_target_properties(lib_opencv_tracking PROPERTIES IMPORTED_LOCATION
          ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libopencv_tracking.a)

# ====================AR-TOOLKIT============================
add_library(libz STATIC IMPORTED)
set_target_properties(libz PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libz.a)

add_library(libEGL SHARED IMPORTED)
set_target_properties(libEGL PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libEGL.so)

add_library(libGLESv1_CM SHARED IMPORTED)
set_target_properties(libGLESv1_CM PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libGLESv1_CM.so)


add_library(libcurl STATIC IMPORTED)
set_target_properties(libcurl PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libcurl.a)

add_library(libkpm STATIC IMPORTED)
set_target_properties(libkpm PROPERTIES IMPORTED_LOCATION
${CMAKE_CURRENT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libkpm.a)

add_library(libc++_shared SHARED IMPORTED)
set_target_properties(libc++_shared PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libc++_shared.so)

add_library(libar STATIC IMPORTED)
set_target_properties(libar PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libar.a)


add_library(libar2 STATIC IMPORTED)
set_target_properties(libar2 PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libar2.a)

add_library(libutil STATIC IMPORTED)
set_target_properties(libutil PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libutil.a)

add_library(libOpenThreads STATIC IMPORTED)
set_target_properties(libOpenThreads PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libOpenThreads.a)


add_library(libarmulti STATIC IMPORTED)
set_target_properties(libarmulti PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libarmulti.a)

add_library(libeden STATIC IMPORTED)
set_target_properties(libeden PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libeden.a)

add_library(libargsub_es STATIC IMPORTED)
set_target_properties(libargsub_es PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libargsub_es.a)

add_library(libcpufeatures STATIC IMPORTED)
set_target_properties(libcpufeatures PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libcpufeatures.a)


add_library(libaricp STATIC IMPORTED)
set_target_properties(libaricp PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libaricp.a)

add_library(libjpeg STATIC IMPORTED)
set_target_properties(libjpeg PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libjpeg.a)

add_library(libarvideo STATIC IMPORTED)
set_target_properties(libarvideo PROPERTIES IMPORTED_LOCATION
         ${CMAKE_SOURCE_DIR}/src/main/jniLibs/armeabi-v7a/libarvideo.a)


add_library( # Sets the name of the library.
             native-lib

             # Sets the library as a shared library.
             SHARED

             # Provides a relative path to your source file(s).
             src/main/cpp/native-lib.cpp

             # AR-Toolkit code
             src/main/cpp/ar_toolkit.hpp
             src/main/cpp/ARMarkerNFT.cpp
             src/main/cpp/ARMarkerNFT.hpp
             src/main/cpp/trackingSub.cpp
             src/main/cpp/trackingSub.hpp
             src/main/cpp/utility.hpp

             )


include_directories(/home/anirudh/NJ/Git/artk_cv_android/app/src/main/include)

# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log
              libz
              libEGL
              libGLESv1_CM
              )

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

target_link_libraries( # Specifies the target library.
                       native-lib

                       #AR Toolkit Libs
                       libEGL
                       libGLESv1_CM
                       libc++_shared
                       libar2
                       libkpm
                       libutil
                       libeden
                       libargsub_es
                       libarmulti
                       libaricp
                       libar
                       libjpeg
                       libarvideo
                       libcurl
                       libOpenThreads
                       libcpufeatures

                       # OpenCV lib
                       lib_opencv
                       lib_opencv_core
                       lib_opencv_imgproc
                       lib_opencv_imgcodecs

                       # Links the target library to the log library
                       # included in the NDK.
                       ${log-lib}
                        libz
                        )