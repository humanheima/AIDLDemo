// DataTestAidlInterface.aidl
package com.hm.aidlserver;
import com.hm.aidlserver.Person;
// Declare any non-default types here with import statements

interface DataTestAidlInterface {
   List<Person> getPersonListIn(in Person person);
}
