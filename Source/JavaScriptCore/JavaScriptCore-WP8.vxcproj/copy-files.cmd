@echo on

set PublicHeadersDirectory=%CONFIGURATIONBUILDDIR%\include\JavaScriptCore
set PrivateHeadersDirectory=%CONFIGURATIONBUILDDIR%\include\private\JavaScriptCore
set ResourcesDirectory=%CONFIGURATIONBUILDDIR%\bin32\JavaScriptCore.resources

if "%1" EQU "clean" goto :clean
if "%1" EQU "rebuild" call :clean

echo Copying public headers...
mkdir "%PublicHeadersDirectory%"
for %%f in (
    APICast.h
    APIShims.h
    JSBase.h
    JSClassRef.h
    JSContextRef.h
    JSContextRefPrivate.h
	JSCTestRunnerUtils.h
    JSObjectRef.h
    JSObjectRefPrivate.h
    JSRetainPtr.h
    JSRetainPtr.h
    JSStringRef.h
    JSStringRefBSTR.h
    JSStringRefCF.h
    JSValueRef.h
    JSWeakObjectMapRefInternal.h
    JSWeakObjectMapRefPrivate.h
    JavaScript.h
    JavaScriptCore.h
    OpaqueJSString.h
    WebKitAvailability.h
) do (
    xcopy /y /d ..\API\%%f "%PublicHeadersDirectory%"
)

echo Copying private headers...
mkdir "%PrivateHeadersDirectory%"
for %%d in (
    assembler
    bytecode
    dfg
    disassembler
    heap
    debugger
    interpreter
    jit
    llint
    parser
    profiler
    runtime
    yarr
) do (
    xcopy /y /d ..\%%d\*.h "%PrivateHeadersDirectory%"
)

goto :EOF

:clean

echo Deleting copied files...
if exist "%PublicHeadersDirectory%" rmdir /s /q "%PublicHeadersDirectory%"
if exist "%PrivateHeadersDirectory%" rmdir /s /q "%PrivateHeadersDirectory%"
if exist "%ResourcesDirectory%" rmdir /s /q "%ResourcesDirectory%"
