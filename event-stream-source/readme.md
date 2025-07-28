# Event Stream Source Example
This example shows how to add a source to the Ignition Event Stream.  The source takes comma separated values 
and generates events from them. Values are sent every second and loops back to the beginning after the last value.

For instance, if the source is configured with the values `A,B,C`, the source will generate events with the 
values `A`, `B`, `C`, `A`, `B`, `C` sending `A` at time 0, `B` at time 1, `C` at time 2, `A` at time 3, and so on.



