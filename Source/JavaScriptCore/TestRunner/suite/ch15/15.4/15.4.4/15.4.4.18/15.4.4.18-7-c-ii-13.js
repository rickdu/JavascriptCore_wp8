/// Copyright (c) 2012 Ecma International.  All rights reserved. 
/// Ecma International makes this code available under the terms and conditions set
/// forth on http://hg.ecmascript.org/tests/test262/raw-file/tip/LICENSE (the 
/// "Use Terms").   Any redistribution of this code must retain the above 
/// copyright and this notice and otherwise comply with the Use Terms.
/**
 * @path ch15/15.4/15.4.4/15.4.4.18/15.4.4.18-7-c-ii-13.js
 * @description Array.prototype.forEach - callbackfn that uses arguments
 */


function testcase() {

        var result = false;
        function callbackfn() {
            result = (arguments[2][arguments[1]] === arguments[0]);
        }

        [11].forEach(callbackfn);
        return result;
    }
runTestCase(testcase);
