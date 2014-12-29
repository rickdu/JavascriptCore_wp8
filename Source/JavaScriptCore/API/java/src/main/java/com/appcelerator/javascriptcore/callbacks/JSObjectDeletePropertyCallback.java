package com.appcelerator.javascriptcore.callbacks;

import com.appcelerator.javascriptcore.opaquetypes.JSContextRef;
import com.appcelerator.javascriptcore.opaquetypes.JSObjectRef;
import com.appcelerator.javascriptcore.opaquetypes.JSValueRef;
import com.appcelerator.javascriptcore.opaquetypes.Pointer;

/**
 * The callback invoked when deleting a property.
 *
 * If this function returns false, the delete request forwards to object's
 * statically declared properties, then its parent class chain (which includes
 * the default object class).
 * 
 * @param ctx
 *            The execution context to use.
 * @param object
 *            The JSObject in which to delete the property.
 * @param propertyName
 *            A String containing the name of the property to delete.
 * @param exception
 *            A pointer to a JSValueRef in which to return an exception, if any.
 * @return true if propertyName was successfully deleted, otherwise false.
 */
public interface JSObjectDeletePropertyCallback  {
    boolean deleteProperty(JSContextRef ctx, JSObjectRef object,
            String propertyName, Pointer exception);
}