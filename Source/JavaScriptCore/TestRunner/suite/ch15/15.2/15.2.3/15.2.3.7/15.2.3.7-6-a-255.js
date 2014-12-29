/// Copyright (c) 2012 Ecma International.  All rights reserved. 
/// Ecma International makes this code available under the terms and conditions set
/// forth on http://hg.ecmascript.org/tests/test262/raw-file/tip/LICENSE (the 
/// "Use Terms").   Any redistribution of this code must retain the above 
/// copyright and this notice and otherwise comply with the Use Terms.
/**
 * @path ch15/15.2/15.2.3/15.2.3.7/15.2.3.7-6-a-255.js
 * @description Object.defineProperties - 'O' is an Array, 'P' is an array index named property that already exists on 'O' is accessor property and 'desc' is accessor descriptor, test setting the [[Get]] attribute value of 'P' as undefined  (15.4.5.1 step 4.c)
 */


function testcase() {

        var arr = [];

        Object.defineProperty(arr, "0", {
            get: function () {
                return 12;
            },
            configurable: true
        });

        Object.defineProperties(arr, {
            "0": {
                get: undefined
            }
        });
        return accessorPropertyAttributesAreCorrect(arr, "0", undefined, undefined, undefined, false, true);
    }
runTestCase(testcase);
