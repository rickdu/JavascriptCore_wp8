package com.appcelerator.javascriptcore.opaquetypes;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;

import com.appcelerator.javascriptcore.util.LongSparseArray;

import com.appcelerator.javascriptcore.JavaScriptCoreLibrary;
import com.appcelerator.javascriptcore.JavaScriptException;
import com.appcelerator.javascriptcore.enums.JSClassAttribute;
import com.appcelerator.javascriptcore.enums.JSType;
import com.appcelerator.javascriptcore.callbacks.JSObjectCallAsConstructorCallback;
import com.appcelerator.javascriptcore.callbacks.JSObjectCallAsFunctionCallback;
import com.appcelerator.javascriptcore.callbacks.JSObjectConvertToTypeCallback;
import com.appcelerator.javascriptcore.callbacks.JSObjectDeletePropertyCallback;
import com.appcelerator.javascriptcore.callbacks.JSObjectFinalizeCallback;
import com.appcelerator.javascriptcore.callbacks.JSObjectGetPropertyCallback;
import com.appcelerator.javascriptcore.callbacks.JSObjectGetPropertyNamesCallback;
import com.appcelerator.javascriptcore.callbacks.JSObjectHasInstanceCallback;
import com.appcelerator.javascriptcore.callbacks.JSObjectHasPropertyCallback;
import com.appcelerator.javascriptcore.callbacks.JSObjectInitializeCallback;
import com.appcelerator.javascriptcore.callbacks.JSObjectSetPropertyCallback;
import com.appcelerator.javascriptcore.callbacks.JSObjectGetPropertyCallback;

/**
 * This structure contains properties and callbacks that define a type of
 * object. All fields other than the version field are optional. Any pointer may
 * be NULL.
 */
public class JSClassDefinition {

    /**
     * The version number of this structure. The current version is 0.
     */
    public int version;
    /**
     * A logically ORed set of JSClassAttributes to give to the class.
     */
    public JSClassAttribute attributes  = JSClassAttribute.None;
    /**
     * A null-terminated UTF8 string containing the class's name.
     */
    public String className;
    /**
     * A JSClass to set as the class's parent class. Pass NULL use the default
     * object class.
     */ 
    public JSClassRef parentClass;

    /**
     * A JSStaticValue array containing the class's statically declared value
     * properties. Pass NULL to specify no statically declared value properties.
     * The array must be terminated by a JSStaticValue whose name field is NULL.
     */
    public JSStaticValues staticValues;

    /**
     * A JSStaticFunction array containing the class's statically declared
     * function properties. Pass NULL to specify no statically declared function
     * properties. The array must be terminated by a JSStaticFunction whose name
     * field is NULL.
     */
    public JSStaticFunctions staticFunctions;

    /**
     * The callback invoked when an object is first created. Use this callback
     * to initialize the object.
     */
    public JSObjectInitializeCallback initialize;

   /**
     * The callback invoked when an object is finalized (prepared for garbage
     * collection). Use this callback to release resources allocated for the
     * object, and perform other cleanup.
     */
    public JSObjectFinalizeCallback finalize;

    /**
     * The callback invoked when determining whether an object has a property.
     * If this field is NULL, getProperty is called instead. The hasProperty
     * callback enables optimization in cases where only a property's existence
     * needs to be known, not its value, and computing its value is expensive.
     */
    public JSObjectHasPropertyCallback hasProperty;

    /**
     * The callback invoked when getting a property's value.
     */
    public JSObjectGetPropertyCallback getProperty;

    /**
     * The callback invoked when setting a property's value.
     */
    public JSObjectSetPropertyCallback setProperty;

    /**
     * The callback invoked when deleting a property.
     */
    public JSObjectDeletePropertyCallback deleteProperty;

    /**
     * The callback invoked when collecting the names of an object's properties.
     */
    public JSObjectGetPropertyNamesCallback getPropertyNames;

    /**
     * The callback invoked when an object is called as a function.
     */
    public JSObjectCallAsFunctionCallback callAsFunction;

    /**
     * The callback invoked when an object is used as a constructor in a 'new'
     * expression.
     */
    public JSObjectCallAsConstructorCallback callAsConstructor;

    /**
     * The callback invoked when an object is used as the target of an
     * 'instanceof' expression.
     */
    public JSObjectHasInstanceCallback hasInstance;

    /**
     * The callback invoked when converting an object to a particular JavaScript
     * type.
     */
    public JSObjectConvertToTypeCallback convertToType;

    private static final short LONG     = JavaScriptCoreLibrary.SizeOfLong;
    private static final short INT      = JavaScriptCoreLibrary.SizeOfInt;
    private static final short UNSIGNED = JavaScriptCoreLibrary.SizeOfUnsigned;

    private static long initializeFunction;
    private static long finalizeFunction;
    private static long hasPropertyFunction;
    private static long getPropertyFunction;
    private static long setPropertyFunction;
    private static long deletePropertyFunction;
    private static long getPropertyNamesFunction;
    private static long callAsFunctionFunction;
    private static long callAsConstructorFunction;
    private static long hasInstanceFunction;
    private static long convertToTypeFunction;

    private static int versionIndex = 0;
    private static int attributesIndex;
    private static int classNameIndex; 
    private static int parentClassIndex;
    private static int staticValuesIndex;
    private static int staticFunctionsIndex;
    private static int initializeIndex;
    private static int finalizeIndex;
    private static int hasPropertyIndex;
    private static int getPropertyIndex;
    private static int setPropertyIndex;
    private static int deletePropertyIndex;
    private static int getPropertyNamesIndex;
    private static int callAsFunctionIndex;
    private static int callAsConstructorIndex;
    private static int hasInstanceIndex;
    private static int convertToTypeIndex;
 
    private static final ByteOrder nativeOrder = ByteOrder.nativeOrder();
    private static ByteBuffer bufferTemplate = null;

    private ByteBuffer buffer;
    private boolean hasParent = false;
    private boolean forceCallAsConstructor = false;
    private boolean forceFinaizeCall       = true;

    public JSClassDefinition() {
        constructBufferTemplate();
    }

    public ByteBuffer getStaticValues() {
        if (staticValues == null) {
            return null;
        } else {
            return staticValues.commit();
        }
    }

    public ByteBuffer getStaticFunctions() {
        if (staticFunctions == null) {
            return null;
        } else {
            return staticFunctions.commit();
        }
    }

    public int getStaticFunctionCount() {
        if (staticFunctions == null) {
            return 0;
        } else {
            return staticFunctions.size();
        }
    }

    public ByteBuffer commit() {
        if (buffer == null) {
            buffer = ByteBuffer.allocateDirect(JavaScriptCoreLibrary.SizeOfJSClassDefinition).order(nativeOrder);

            buffer.putInt(versionIndex, version);
            buffer.putInt(attributesIndex, attributes.getValue());

            if (parentClass != null) JavaScriptCoreLibrary.putLong(buffer, parentClassIndex, parentClass.p());
            if (initialize  != null) JavaScriptCoreLibrary.putLong(buffer, initializeIndex, initializeFunction);
            if (hasProperty != null) JavaScriptCoreLibrary.putLong(buffer, hasPropertyIndex, hasPropertyFunction);
            if (getProperty != null) JavaScriptCoreLibrary.putLong(buffer, getPropertyIndex, getPropertyFunction);
            if (setProperty != null) JavaScriptCoreLibrary.putLong(buffer, setPropertyIndex, setPropertyFunction);
            if (deleteProperty    != null) JavaScriptCoreLibrary.putLong(buffer, deletePropertyIndex, deletePropertyFunction);
            if (getPropertyNames  != null) JavaScriptCoreLibrary.putLong(buffer, getPropertyNamesIndex, getPropertyNamesFunction);
            if (callAsFunction    != null) JavaScriptCoreLibrary.putLong(buffer, callAsFunctionIndex, callAsFunctionFunction);
            if (forceCallAsConstructor || callAsConstructor != null) JavaScriptCoreLibrary.putLong(buffer, callAsConstructorIndex, callAsConstructorFunction);
            if (hasInstance   != null) JavaScriptCoreLibrary.putLong(buffer, hasInstanceIndex, hasInstanceFunction);
            if (convertToType != null) JavaScriptCoreLibrary.putLong(buffer, convertToTypeIndex, convertToTypeFunction);

            // finalize callback should be fired on every objects (other than global object)
            // to release all associated Java object. Note that firing finalize callback 
            // onto global object causes crash at JSGlobalContextRelease. 
            if (forceFinaizeCall) {
                JavaScriptCoreLibrary.putLong(buffer, finalizeIndex, finalizeFunction);
            }

            hasParent = (parentClass != null && parentClass.getDefinition() != null);
        }

        return buffer;

    }

    public void registerStaticFunctionCallback(long ctx, long object) {
        if (staticFunctions != null) {
            staticFunctions.registerFunctions(object, NativeGetStaticFunctions(ctx, object, staticFunctions.size(), staticFunctions.commit()));
        }
    }
    private void clearStaticFunctions(long object) {
        if (staticFunctions != null) {
            staticFunctions.removeObject(object);
        }
    }

    public void JSObjectInitializeCallback(long ctx, long object) {
        if (hasParent) {
            parentClass.getDefinition().JSObjectInitializeCallback(ctx, object);
        }
        if (initialize != null) {
            JSContextRef context = new JSContextRef(ctx);
            initialize.initialize(context, new JSObjectRef(context, object));
        }
    }

    public void JSObjectFinalizeCallback(long object) {
        if (finalize != null) {
            finalize.finalize(new JSObjectRef(object));
            if (hasParent) {
                parentClass.getDefinition().JSObjectFinalizeCallback(object);
            }
        }
        clearPrototypeChain(object);
        clearStaticFunctions(object);
    }

    private static LongSparseArray<JSClassDefinition> setPropertyChain = new LongSparseArray<JSClassDefinition>(JavaScriptCoreLibrary.numberOfPrototypeHierarchy);
    public boolean JSObjectSetPropertyCallback(long ctx, long object, String propertyName, long value, long exception) {
        if (setPropertyChain.get(object) != null && !this.equals(setPropertyChain.get(object))) {
            return setPropertyChain.get(object).JSObjectSetPropertyCallback(ctx, object, propertyName, value, exception);
        }
        JSContextRef context = new JSContextRef(ctx);
        if (setProperty != null && setProperty.setProperty(context, new JSObjectRef(context, object), propertyName,
                                        new JSValueRef(context, value), new Pointer(exception))) {
            setPropertyChain.remove(object);
            return true;
        }
        if (hasParent) {
            setPropertyChain.put(object, parentClass.getDefinition());
            if (setProperty == null) {
                return JSObjectSetPropertyCallback(ctx, object, propertyName, value, exception);
            }
        } else {
            setPropertyChain.remove(object);
        }
        return false;
    }

    private static LongSparseArray<JSClassDefinition> getPropertyChain = new LongSparseArray<JSClassDefinition>(JavaScriptCoreLibrary.numberOfPrototypeHierarchy);
    public long JSObjectGetPropertyCallback(long ctx, long object, String propertyName, long exception) {
        if (getPropertyChain.get(object) != null && !this.equals(getPropertyChain.get(object))) {
            return getPropertyChain.get(object).JSObjectGetPropertyCallback(ctx, object, propertyName, exception);
        }
        if (getProperty != null) {
            JSContextRef context = new JSContextRef(ctx);
            JSValueRef prop = getProperty.getProperty(context, new JSObjectRef(context, object), propertyName, new Pointer(exception));
            if (p(prop) != 0) {
                getPropertyChain.remove(object);
                return p(prop);
            }
        }
        if (hasParent) {
            getPropertyChain.put(object, parentClass.getDefinition());
            if (getProperty == null) {
                return JSObjectGetPropertyCallback(ctx, object, propertyName, exception);
            }
        } else {
            getPropertyChain.remove(object);
        }
        return 0;
    }

    public long JSObjectCallAsFunctionCallback(long ctx, long func, long thisObject, int argc, ByteBuffer argv, long exception) {
        if (callAsFunction != null) {
            JSContextRef context = new JSContextRef(ctx);
            JSValueArrayRef jargv = new JSValueArrayRef(argc, argv);
            return p(callAsFunction.callAsFunction(context, new JSObjectRef(context, func), new JSObjectRef(context, thisObject),
                                        argc, jargv, new Pointer(exception)));
        }
        if (hasParent) {
            return parentClass.getDefinition().JSObjectCallAsFunctionCallback(ctx, func, thisObject, argc, argv, exception);
        }
        throw new JavaScriptException(String.format("CallAsFunction callback is not found for %d", thisObject));
    }

    public long JSObjectStaticFunctionCallback(long ctx, long func, long thisObject, int argc, ByteBuffer argv, long exception) {
        if (staticFunctions != null) {
            if (staticFunctions.requestFunctions(thisObject)) {
                registerStaticFunctionCallback(ctx, thisObject);
            }
            JSContextRef context = new JSContextRef(ctx);
            JSValueArrayRef jargv = new JSValueArrayRef(argc, argv);
            JSObjectCallAsFunctionCallback callback = staticFunctions.getFunction(thisObject, func);
            if (callback != null) return p(callback.callAsFunction(context, new JSObjectRef(context, func),
                                     new JSObjectRef(context, thisObject), argc, jargv, new Pointer(exception)));
        }
        if (hasParent) {
            return parentClass.getDefinition().JSObjectStaticFunctionCallback(ctx, func, thisObject, argc, argv, exception);
        }
        throw new JavaScriptException(String.format("Static function callback is not found for %d", thisObject));
    }

    private static LongSparseArray<JSClassDefinition> callAsConstructorChain = new LongSparseArray<JSClassDefinition>(JavaScriptCoreLibrary.numberOfPrototypeHierarchy);
    public long JSObjectCallAsConstructorCallback(long ctx, long constructor, int argc, ByteBuffer argv, long exception) {
        if (callAsConstructorChain.get(constructor) != null && !this.equals(callAsConstructorChain.get(constructor))) {
            return callAsConstructorChain.get(constructor).JSObjectCallAsConstructorCallback(ctx, constructor, argc, argv, exception);
        }
        if (callAsConstructor != null) {
            JSContextRef context = new JSContextRef(ctx);
            JSValueArrayRef jargv = new JSValueArrayRef(argc, argv);
            JSValueRef object = callAsConstructor.callAsConstructor(context, 
                                            new JSObjectRef(context, constructor),
                                             argc, jargv, new Pointer(exception));
            callAsConstructorChain.remove(constructor);
            return p(object);
        }
        if (hasParent) {
            callAsConstructorChain.put(constructor, parentClass.getDefinition());
            if (callAsConstructor == null) {
                return JSObjectCallAsConstructorCallback(ctx, constructor, argc, argv, exception);
            }
        } else {
            callAsConstructorChain.remove(constructor);
        }
        throw new JavaScriptException(String.format("CallAsConstructor callback is not found for %d", constructor));
    }

    /*
     * Static CallAsFunction callbacks for JSObjectMakeFunctionWithCallback
     */
    private static HashMap<Long, JSObjectCallAsFunctionCallback> functionCallbacks = new HashMap<Long, JSObjectCallAsFunctionCallback>(JavaScriptCoreLibrary.numberOfJSObjectBuckets);
    public static void registerMakeFunctionCallback(long function, JSObjectCallAsFunctionCallback callback) {
        functionCallbacks.put(function, callback);
    }
    public static long JSObjectMakeFunctionCallback(long ctx, long function, long thisObject, int argc, ByteBuffer argv, long exception) {
        if (functionCallbacks.get(function) != null) {
            JSContextRef context = new JSContextRef(ctx);
            JSValueArrayRef jargv = new JSValueArrayRef(argc, argv);
            return p(functionCallbacks.get(function).callAsFunction(
                                            context, new JSObjectRef(context, function),
                                            new JSObjectRef(context, thisObject),
                                            argc, jargv, new Pointer(exception)));
        }
        throw new JavaScriptException(String.format("JSObjectMakeFunctionWithCallback callback is not found for %d", thisObject));
    }

    /* 
     * Static CallAsConstructor callbacks for JSObjectMakeConstructor
     */
    private static HashMap<Long, JSObjectCallAsConstructorCallback> constructorCallbacks = new HashMap<Long, JSObjectCallAsConstructorCallback>(JavaScriptCoreLibrary.numberOfJSObjectBuckets);
    public static void registerMakeConstructorCallback(long constructor, JSObjectCallAsConstructorCallback callback) {
        constructorCallbacks.put(constructor, callback);
    }
    public static long JSObjectMakeConstructorCallback(long ctx, long constructor, int argc, ByteBuffer argv, long exception) {
        if (constructorCallbacks.get(constructor) != null) {
            JSContextRef context = new JSContextRef(ctx);
            JSValueArrayRef jargv = new JSValueArrayRef(argc, argv);
            return p(constructorCallbacks.get(constructor).callAsConstructor(
                                            context, new JSObjectRef(context, constructor),
                                            argc, jargv, new Pointer(exception)));
        }
        throw new JavaScriptException(String.format("JSObjectMakeConstructor callback is not found for %d", constructor));
    }

    private static LongSparseArray<JSClassDefinition> convertToTypeChain = new LongSparseArray<JSClassDefinition>(JavaScriptCoreLibrary.numberOfPrototypeHierarchy);
    public long JSObjectConvertToTypeCallback(long ctx, long object, int type, long exception) {
        if (convertToTypeChain.get(object) != null && !this.equals(convertToTypeChain.get(object))) {
            return convertToTypeChain.get(object).JSObjectConvertToTypeCallback(ctx, object, type, exception);
        }
        if (convertToType != null) {
            JSContextRef context = new JSContextRef(ctx);
            JSValueRef prop = convertToType.convertToType(context, new JSObjectRef(context, object), JSType.request(type), new Pointer(exception));
            if (p(prop) != 0) {
                convertToTypeChain.remove(object);
                return p(prop);
            }
        }
        if (hasParent) {
            convertToTypeChain.put(object, parentClass.getDefinition());
            if (convertToType == null) {
                return JSObjectConvertToTypeCallback(ctx, object, type, exception);
            }
        } else {
            convertToTypeChain.remove(object);
        }
        return 0;
    }
    
    private static LongSparseArray<JSClassDefinition> deletePropertyChain = new LongSparseArray<JSClassDefinition>(JavaScriptCoreLibrary.numberOfPrototypeHierarchy);
    public boolean JSObjectDeletePropertyCallback(long ctx, long object, String name, long exception) {
        if (deletePropertyChain.get(object) != null && !this.equals(deletePropertyChain.get(object))) {
            return deletePropertyChain.get(object).JSObjectDeletePropertyCallback(ctx, object, name, exception);
        }
        JSContextRef context = new JSContextRef(ctx);
        if (deleteProperty != null && deleteProperty.deleteProperty(context, new JSObjectRef(context, object), name, new Pointer(exception))) {
            deletePropertyChain.remove(object);
            return true;
        }
        if (hasParent) {
            deletePropertyChain.put(object, parentClass.getDefinition());
            if (deleteProperty == null) {
                return JSObjectDeletePropertyCallback(ctx, object, name, exception);
            }
        } else {
            deletePropertyChain.remove(object);
        }
        return false;
    }

    private static LongSparseArray<JSClassDefinition> getPropertyNamesChain = new LongSparseArray<JSClassDefinition>(JavaScriptCoreLibrary.numberOfPrototypeHierarchy);
    public void JSObjectGetPropertyNamesCallback(long ctx, long object, long propertyNames) {
        if (getPropertyNamesChain.get(object) != null && !this.equals(getPropertyNamesChain.get(object))) {
            getPropertyNamesChain.get(object).JSObjectGetPropertyNamesCallback(ctx, object, propertyNames);
            return;
        }
        if (getPropertyNames != null) {
            JSContextRef context = new JSContextRef(ctx);
            getPropertyNames.getPropertyNames(context, new JSObjectRef(context, object),
                                              new JSPropertyNameAccumulatorRef(propertyNames));
        }
        if (hasParent) {
            getPropertyNamesChain.put(object, parentClass.getDefinition());
            if (getPropertyNames == null) {
                JSObjectGetPropertyNamesCallback(ctx, object, propertyNames);
                return;
            }
        } else {
            getPropertyNamesChain.remove(object);
        }
        return;
    }

    private static LongSparseArray<JSClassDefinition> hasInstanceChain = new LongSparseArray<JSClassDefinition>(JavaScriptCoreLibrary.numberOfPrototypeHierarchy);
    public boolean JSObjectHasInstanceCallback(long ctx, long constructor, long possibleInstance, long exception) {
        if (hasInstanceChain.get(constructor) != null && !this.equals(hasInstanceChain.get(constructor))) {
            return hasInstanceChain.get(constructor).JSObjectHasInstanceCallback(ctx, constructor, possibleInstance, exception);
        }
        JSContextRef context = new JSContextRef(ctx);
        if (hasInstance != null && hasInstance.hasInstance(context, new JSObjectRef(context, constructor),
                                            new JSValueRef(context, possibleInstance), new Pointer(exception))) {
            hasInstanceChain.remove(constructor);
            return true;
        }
        if (hasParent) {
            hasInstanceChain.put(constructor, parentClass.getDefinition());
            if (hasInstance == null) {
                return JSObjectHasInstanceCallback(ctx, constructor, possibleInstance, exception);
            }
        } else {
            hasInstanceChain.remove(constructor);
        }
        return false;
    }

    private static LongSparseArray<JSClassDefinition> hasPropertyChain = new LongSparseArray<JSClassDefinition>(JavaScriptCoreLibrary.numberOfPrototypeHierarchy);
    public boolean JSObjectHasPropertyCallback(long ctx, long object, String name) {
        if (hasPropertyChain.get(object) != null && !this.equals(hasPropertyChain.get(object))) {
            return hasPropertyChain.get(object).JSObjectHasPropertyCallback(ctx, object, name);
        }
        JSContextRef context = new JSContextRef(ctx);
        if (hasProperty != null && hasProperty.hasProperty(context, new JSObjectRef(context, object), name)) {
            hasPropertyChain.remove(object);
            return true;
        }
        if (hasParent) {
            hasPropertyChain.put(object, parentClass.getDefinition());
            if (hasProperty == null) {
                return JSObjectHasPropertyCallback(ctx, object, name);
            }
        } else {
            hasPropertyChain.remove(object);
        }
        return false;
    }

    public boolean JSObjectSetStaticValueCallback(long ctx, long object, String propertyName, long value, long exception) {
        if (staticValues != null && staticValues.containsSetter(propertyName)) {
            JSObjectSetPropertyCallback callback = staticValues.getSetPropertyCallback(propertyName);
            if (callback == null) {
                return false;
            }
            JSContextRef context = new JSContextRef(ctx);
            return callback.setProperty(context, new JSObjectRef(context, object), propertyName,
                new JSValueRef(context, value), new Pointer(exception));
        }
        if (hasParent) {
            return parentClass.getDefinition().JSObjectSetStaticValueCallback(ctx, object, propertyName, value, exception);
        }
        return false;
    }

    public long JSObjectGetStaticValueCallback(long ctx, long object, String propertyName, long exception) {
        if (staticValues != null && staticValues.containsGetter(propertyName)) {
            JSObjectGetPropertyCallback callback = staticValues.getGetPropertyCallback(propertyName);
            if (callback == null) {
                return 0;
            }
            JSContextRef context = new JSContextRef(ctx);
            return p(callback.getProperty(context, new JSObjectRef(context, object),
                        propertyName, new Pointer(exception)));
        }
        if (hasParent) {
            return parentClass.getDefinition().JSObjectGetStaticValueCallback(ctx, object, propertyName, exception);
        }
        throw new JavaScriptException(String.format("Static value '%s' callback is not found for %d", propertyName, object));
    }

    public JSClassDefinition copy() {
        JSClassDefinition copy = new JSClassDefinition();
        copy.version = this.version;
        copy.attributes = this.attributes;
        copy.className = this.className;
        copy.parentClass = this.parentClass;
        copy.staticValues = this.staticValues;
        copy.staticFunctions = this.staticFunctions;
        copy.initialize = this.initialize;
        copy.finalize = this.finalize;
        copy.hasProperty = this.hasProperty;
        copy.getProperty = this.getProperty;
        copy.setProperty = this.setProperty;
        copy.deleteProperty = this.deleteProperty;
        copy.getPropertyNames = this.getPropertyNames;
        copy.callAsFunction = this.callAsFunction;
        copy.callAsConstructor = this.callAsConstructor;
        copy.hasInstance = this.hasInstance;
        copy.convertToType = this.convertToType;
        return copy;
    }

    public void enableConstructor(boolean enabled) {
        this.forceCallAsConstructor = enabled;
    }

    public void enableFinalize(boolean enabled) {
        this.forceFinaizeCall = enabled;
    }

    public void dispose() {
        if (buffer != null) {
            buffer.clear();
            buffer = null;
        }
        if (staticFunctions != null) {
            staticFunctions.dispose();
            staticFunctions = null;
        }
        if (staticValues != null) {
            staticValues.dispose();
            staticValues = null;
        }
        
        setPropertyChain.clear();
        getPropertyChain.clear();
        convertToTypeChain.clear();
        deletePropertyChain.clear();
        getPropertyNamesChain.clear();
        hasPropertyChain.clear();
        functionCallbacks.clear();
        hasInstanceChain.clear();
        constructorCallbacks.clear();
        functionCallbacks.clear();
        callAsConstructorChain.clear();

        parentClass = null;
        initialize  = null;
        finalize    = null;
        hasProperty = null;
        getProperty = null;
        setProperty = null;
        deleteProperty = null;
        getPropertyNames = null;
        callAsFunction = null;
        callAsConstructor = null;
        hasInstance = null;
        convertToType = null;

        hasParent = false;
        forceCallAsConstructor = false;
        forceFinaizeCall       = true;
    }

    private void constructBufferTemplate() {
        if (bufferTemplate == null) {
            bufferTemplate = NativeGetClassDefinitionTemplate().order(nativeOrder);
            attributesIndex   = INT;
            classNameIndex    = attributesIndex + UNSIGNED;
            parentClassIndex  = classNameIndex + LONG;
            staticValuesIndex = parentClassIndex + LONG;
            staticFunctionsIndex = staticValuesIndex + LONG;
            initializeIndex    = staticFunctionsIndex + LONG;
            finalizeIndex      = initializeIndex + LONG;
            hasPropertyIndex   = finalizeIndex + LONG;
            getPropertyIndex   = hasPropertyIndex + LONG;
            setPropertyIndex   = getPropertyIndex + LONG;
            deletePropertyIndex   = setPropertyIndex + LONG;
            getPropertyNamesIndex = deletePropertyIndex + LONG;
            callAsFunctionIndex   = getPropertyNamesIndex + LONG;
            callAsConstructorIndex = callAsFunctionIndex + LONG;
            hasInstanceIndex     = callAsConstructorIndex + LONG;
            convertToTypeIndex   = hasInstanceIndex + LONG;

            initializeFunction  = JavaScriptCoreLibrary.getLong(bufferTemplate, initializeIndex);
            finalizeFunction    = JavaScriptCoreLibrary.getLong(bufferTemplate, finalizeIndex);
            hasPropertyFunction = JavaScriptCoreLibrary.getLong(bufferTemplate, hasPropertyIndex);
            getPropertyFunction = JavaScriptCoreLibrary.getLong(bufferTemplate, getPropertyIndex);
            setPropertyFunction = JavaScriptCoreLibrary.getLong(bufferTemplate, setPropertyIndex);
            deletePropertyFunction    = JavaScriptCoreLibrary.getLong(bufferTemplate, deletePropertyIndex);
            getPropertyNamesFunction  = JavaScriptCoreLibrary.getLong(bufferTemplate, getPropertyNamesIndex);
            callAsFunctionFunction    = JavaScriptCoreLibrary.getLong(bufferTemplate, callAsFunctionIndex);
            callAsConstructorFunction = JavaScriptCoreLibrary.getLong(bufferTemplate, callAsConstructorIndex);
            hasInstanceFunction   = JavaScriptCoreLibrary.getLong(bufferTemplate, hasInstanceIndex);
            convertToTypeFunction = JavaScriptCoreLibrary.getLong(bufferTemplate, convertToTypeIndex);
        }
    }

    private static long p(PointerType p) {
        if (p == null) return 0;
        return p.pointer();
    }

    /*
     * Just in case make sure to remove all references associated with the object
     */
    private static void clearPrototypeChain(long object) {
        setPropertyChain.remove(object);
        getPropertyChain.remove(object);
        convertToTypeChain.remove(object);
        deletePropertyChain.remove(object);
        getPropertyNamesChain.remove(object);
        hasPropertyChain.remove(object);
        hasInstanceChain.remove(object);
        callAsConstructorChain.remove(object);
        functionCallbacks.remove(object);
        constructorCallbacks.remove(object);
    }

    private static native ByteBuffer NativeGetClassDefinitionTemplate();
    private native long[] NativeGetStaticFunctions(long context, long object, int size, ByteBuffer functions);
}
