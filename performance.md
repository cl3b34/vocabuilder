# Statistics on poor RMS performance #

Tests were done by loading the list 'longlist\_fin\_en.txt' that contains 1827 flashcards.
As we can see by the results, aparently switching the processing from a method to another in J2ME takes a lot of time.

If we take the Load (original) method as an example, the same card instantiating and loading code, when moved to another method (load card)
and used from there takes about 75 times longer to finish loading the set in the Handset.

The results in the simulator are not so obvious but it is still slower.

Turning the methods static didn't improve the performance significantly.

As a result, I'm forced to repeat the card loading code in 'load card' and 'load set' methods in order to achieve acceptable performance.

|Operation|Simulator|Nokia E50| Change |
|:--------|:--------|:--------|:-------|
| Load (original) | 22s | 300s | Loading loop became a method |
| Save (original) | 7s | 51s |  |
| Load Card (original) | 15ms | 135ms |  |
| Load card (new file format) | 15ms | 144ms |  |
| Load file format version | 15ms | 135ms | file format on 1st record |
| Load set metadata | 15ms | 153ms | metadata on 2nd record |
| save set (new file format)| 6s | 59s |  |
| Update card | 0ms | 31ms |  |
| Add card | 0ms | 131ms |  |
| Get record count | 0ms | 1ms |  |
| Add metadata | 0ms | 131ms |  |
| Update metadata | 0ms | 31ms |  |
| Reset state | 0ms | 457ms |  |
| Load set (new file format) | 28s | 884s | metadata separated from card data |
| Load set (only one record) | 4s | 297s | all card data in a single record (3rd) |
| Load card (only one record) | 0ms | 133ms |  |
| Add file Format Version | 0ms | 131ms |  |
| Load set metadata (original) | 16ms | 140ms |  |
| Save Set (only one record) | 171ms | 979ms |  |
| Add card (only one record) | 0ms | 140ms |  |


Load Set refactored to contain the card loading code.

|Operation|Simulator|Nokia E50| Change |
|:--------|:--------|:--------|:-------|
| Load (original) | 17s | 4s |  |
| Save (original) | 6s | 51s |  |
| Load Card (original) | 15ms | 142ms |  |
| Load card (new file format) | 15ms | 137ms |  |
| Load file format version | 16ms | 141ms |  |
| Load set metadata | 32ms | 162ms |  |
| save set (new file format)| 5s | 60s |  |
| Update card | 0ms | 54ms |  |
| Add card | 0ms | 165ms |  |
| Get record count | 0ms | 1ms |  |
| Add metadata | 0ms | 131ms |  |
| Update metadata | 0ms | 31ms |  |
| Reset state | 0ms | 457ms |  |
| Load set (new file format) | 17s | 3s |  |
| Load set (only one record) | 641ms | 523ms |  |
| Load card (only one record) | 0ms | 134ms |  |
| Add file Format Version | 16ms | 131ms |  |
| Load set metadata (original) | 31ms | 140ms |  |
| Save Set (only one record) | 156ms | 1s |  |
| Add card (only one record) | 0ms | 140ms |  |


Load Set, load card and load set metadata refactored to be static

|Operation|Simulator|Nokia E50| Change |
|:--------|:--------|:--------|:-------|
| Load (original) | 23s | 239s |  |
| Save (original) | 6s | 51s |  |
| Load Card (original) | 15ms | 117ms |  |
| Load card (new file format) | 15ms | 123ms |  |
| Load file format version | 16ms | 118ms |  |
| Load set metadata | 32ms | 162ms |  |
| save set (new file format)| 5s | 60s |  |
| Update card | 0ms | 54ms |  |
| Add card | 0ms | 165ms |  |
| Get record count | 0ms | 1ms |  |
| Add metadata | 0ms | 131ms |  |
| Update metadata | 0ms | 31ms |  |
| Reset state | 0ms | 457ms |  |
| Load set (new file format) | 33s | 760s |  |
| Load set (only one record) | 4s | 237s |  |
| Load card (only one record) | 0ms | 134ms |  |
| Add file Format Version | 16ms | 131ms |  |
| Load set metadata (original) | 31ms | 140ms |  |
| Save Set (only one record) | 156ms | 1s |  |
| Add card (only one record) | 0ms | 140ms |  |