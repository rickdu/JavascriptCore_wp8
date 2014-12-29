package com.appcelerator.javascriptcore.callbacks;

import com.appcelerator.javascriptcore.opaquetypes.JSContextRef;
import com.appcelerator.javascriptcore.opaquetypes.JSObjectRef;
import com.appcelerator.javascriptcore.opaquetypes.JSValueRef;
import com.appcelerator.javascriptcore.opaquetypes.JSValueArrayRef;
import com.appcelerator.javascriptcore.opaquetypes.Pointer;

/**
 * The callback invoked when an object is used as a constructor in a 'new'
 * expression.
 * 
 * If your callback were invoked by the JavaScript expression 'new
 * myConstructor()', constructor would be set to myConstructor. If this callback
 * is NULL, using your object as a constructor in a 'new' expression will throw
 * an exception.
 * 
 * @param ctx
 *            The execution context to use.
 * @param constructor
 *            A JSObject that is the constructor being called.
 * @param argumentCount
 *            An integer count of the number of arguments in arguments.
 * @param arguments
 *            A JSValue array of the arguments passed to the function.
 * @param exception
 *            A pointer to a JSValueRef in which to return an exception, if any.
 * @return A JSObject that is the constructor's return value.
 */
public interface JSObjectCallAsConstructorCallback {
    JSObjectRef callAsConstructor(JSContextRef ctx, JSObjectRef constructor,
            int argumentCount, JSValueArrayRef arguments,
            Pointer exception);
}