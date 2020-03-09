# :dollar: Simple Count

An Android App to help users to handle group expenses more efficiently by minimizing cash flows among people in group.

The app was written in Kotlin and using RxKotlin and RxBinding to observe and handle Edittext changes

Download the app via this link : https://github.com/vietanhle97/SimpleCount/blob/master/simple_count.apk

<p align="center">
  <img width="40%" src="app_start.gif">
</p>

| What the app do                                                                                                                                 | Complete |                   Image                       |
| ------------------------------------------------------------------------------------------------------------------------------------------------| :------: | :-------------------------------------------: |
| Event creation with title, description and participants                                                                                         | :dollar: | <img width="100%" src="event_creation.gif">    |
| Expense creation with title, amount, date, payer and participants. Allow users to handle the portion as well as amount they spent in an expense | :dollar: | <img width="100%" src="expense_creation_1.gif">|
| Calculate the amount users have to pay each other and live update it after the expenses created                                                 | :dollar: | <img width="100%" src="expense_creation_2.gif">|
| Allow user to delete and undo delete expenses and event| :dollar: | <img width="100%" src="handle_delete.gif"> |
                                     



## Libraries

### Jetpack

| Library      | Use                                                                           |
| ------------ | ----------------------------------------------------------------------------- |
| Data Binding | For accessing XML layouts without findViewById()                              |
| Live Data    | For observing changes in Fragments                                            | 
| Room         | For storing app data into the database                                        |
| ViewModel    | For MVVM architecture                                                         |

### Third Party

| Library    | Use                                                          |
| ---------- | ------------------------------------------------------------ |
| Gson       | For converting complex data into string to store in database |
| Coroutines | For asynchronous calls                                       |
| Lottie     | For app icon animations                                      |
