// ISecurityCenter.aidl
package com.hm.aidlserver;

// Declare any non-default types here with import statements

interface ISecurityCenter {

   String encrypt(String content);
   String decrypt(String password);
}
