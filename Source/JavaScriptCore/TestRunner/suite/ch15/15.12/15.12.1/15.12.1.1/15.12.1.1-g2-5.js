/// Copyright (c) 2012 Ecma International.  All rights reserved. 
/// Ecma International makes this code available under the terms and conditions set
/// forth on http://hg.ecmascript.org/tests/test262/raw-file/tip/LICENSE (the 
/// "Use Terms").   Any redistribution of this code must retain the above 
/// copyright and this notice and otherwise comply with the Use Terms.
/**
 * @path ch15/15.12/15.12.1/15.12.1.1/15.12.1.1-g2-5.js
 * @description A JSONStrings can contain no JSONStringCharacters (Empty JSONStrings)
 */


function testcase() {
  return JSON.parse('""')===""; 
  }
runTestCase(testcase);
