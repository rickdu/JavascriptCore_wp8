
This is JavascriptCore for wp8. As it's too hard to migrate spidermonkey to wp8 platform, I turned to webkit and it works fine on wp8.
Find the .sln file under Source/JavaScriptCore/JavaScriptCore-WP8.vxcproj

To build the project on windows 8, you need:
1. cygwin with bash, perl, python, ruby, make installed in it, and then add cygwin/bin to PATH.
2. build project JavaScriptCoreGenerated first, and then build the whole sulution.
3. Except LLInt* and TesetRunner*, other projects are built under <sysdrive>:/Debug or <sysdrive>:/Release. It is caused by cygwin, which cannot recognize %CONFIGURATIONBUILDDIR% well.

If you get error when building the project, pay attention to pre-build event first.
As I lost the building environment(I removed my cygwin when debuging an error which shows I lost NULL device under win8), if any configuration error exists in it, please correct it. 
